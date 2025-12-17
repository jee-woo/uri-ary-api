package com.diary.shared_diary.service;

import com.diary.shared_diary.domain.Comment;
import com.diary.shared_diary.domain.Diary;
import com.diary.shared_diary.domain.Group;
import com.diary.shared_diary.domain.User;
import com.diary.shared_diary.dto.comment.CommentRequestDto;
import com.diary.shared_diary.dto.comment.CommentResponseDto;
import com.diary.shared_diary.repository.CommentRepository;
import com.diary.shared_diary.repository.DiaryRepository;
import com.diary.shared_diary.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class CommentService {

    private final CommentRepository commentRepository;
    private final DiaryRepository diaryRepository;
    private final UserRepository userRepository;

    public CommentService(CommentRepository commentRepository,
                          DiaryRepository diaryRepository,
                          UserRepository userRepository) {
        this.commentRepository = commentRepository;
        this.diaryRepository = diaryRepository;
        this.userRepository = userRepository;
    }

    public List<CommentResponseDto> getComments(Long diaryId, String email) {
        log.info("Fetching comments for diaryId: {}, user: {}", diaryId, email);
        Diary diary = diaryRepository.findById(diaryId)
                .orElseThrow(() -> new RuntimeException("일기를 찾을 수 없습니다."));
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));

        Group group = diary.getGroup();
        if (!group.getMembers().contains(user)) {
            log.warn("User {} is not a member of group {}. Access denied for diary {}.", email, diary.getGroup().getId(), diaryId);
            throw new RuntimeException("댓글을 볼 수 있는 권한이 없습니다.");
        }

        log.info("Successfully fetched comments for diaryId: {}", diaryId);
        return commentRepository.findByDiaryOrderByCreatedAtAsc(diary).stream()
                .map(CommentResponseDto::new)
                .toList();
    }

    public CommentResponseDto createComment(Long diaryId, String email, CommentRequestDto dto) {
        log.info("Start creating comment for user: {}, diary: {}", email, diaryId);
        Diary diary = diaryRepository.findById(diaryId)
                .orElseThrow(() -> new RuntimeException("일기를 찾을 수 없습니다."));
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));

        Group group = diary.getGroup();
        if (!group.getMembers().contains(user)) {
            log.warn("User {} is not a member of group {}. Access denied for diary {}.", email, diary.getGroup().getId(), diaryId);
            throw new RuntimeException("해당 그룹 멤버만 댓글을 달 수 있습니다.");
        }

        Comment parent = Optional.ofNullable(dto.parentId())
                .map(id -> {
                    Comment p = commentRepository.findById(id)
                            .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 부모 댓글입니다."));

                    if (p.getParent() != null) {
                        throw new IllegalArgumentException("대댓글에는 다시 댓글을 달 수 없습니다.");
                    }

                    return p;
                })
                .orElse(null);

        Comment comment = Comment.builder()
                .content(dto.content())
                .author(user)
                .diary(diary)
                .createdAt(LocalDateTime.now())
                .parent(parent)
                .build();

        Comment saved = commentRepository.save(comment);
        log.info("Comment created with id: {}", saved.getId());
        return new CommentResponseDto(comment);
    }
}
