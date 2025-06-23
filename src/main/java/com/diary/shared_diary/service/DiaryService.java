package com.diary.shared_diary.service;

import com.diary.shared_diary.domain.Diary;
import com.diary.shared_diary.domain.Group;
import com.diary.shared_diary.domain.User;
import com.diary.shared_diary.dto.user.UserResponseDto;
import com.diary.shared_diary.dto.diary.DiaryRequestDto;
import com.diary.shared_diary.dto.diary.DiaryResponseDto;
import com.diary.shared_diary.repository.DiaryRepository;
import com.diary.shared_diary.repository.GroupRepository;
import com.diary.shared_diary.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class DiaryService {

    private final DiaryRepository diaryRepository;
    private final UserRepository userRepository;
    private final GroupRepository groupRepository;

    public DiaryService(
            DiaryRepository diaryRepository,
            UserRepository userRepository,
            GroupRepository groupRepository
    ) {
        this.diaryRepository = diaryRepository;
        this.userRepository = userRepository;
        this.groupRepository = groupRepository;
    }

    public DiaryResponseDto createDiaryInGroup(Long groupId, DiaryRequestDto dto) {
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new RuntimeException("Group not found"));

        User author = userRepository.findById(dto.getAuthorId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!group.getMembers().contains(author)) {
            throw new RuntimeException("User is not a member of this group");
        }

        Diary diary = Diary.builder()
                .title(dto.getTitle())
                .content(dto.getContent())
                .createdAt(LocalDateTime.now())
                .author(author)
                .group(group)
                .build();

        Diary saved = diaryRepository.save(diary);

        return new DiaryResponseDto(
                saved.getId(),
                saved.getTitle(),
                saved.getContent(),
                saved.getCreatedAt(),
                new UserResponseDto(
                        author.getId(),
                        author.getUsername(),
                        author.getEmail()
                )
        );
    }

    public List<DiaryResponseDto> getDiariesByGroup(Long groupId) {
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new RuntimeException("Group not found"));

        List<Diary> diaries = diaryRepository.findByGroup(group);

        return diaries.stream().map(diary -> new DiaryResponseDto(
                diary.getId(),
                diary.getTitle(),
                diary.getContent(),
                diary.getCreatedAt(),
                new UserResponseDto(
                        diary.getAuthor().getId(),
                        diary.getAuthor().getUsername(),
                        diary.getAuthor().getEmail()
                )
        )).toList();
    }

}
