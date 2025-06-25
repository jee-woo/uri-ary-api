package com.diary.shared_diary.service;

import com.diary.shared_diary.domain.Diary;
import com.diary.shared_diary.domain.Group;
import com.diary.shared_diary.domain.User;
import com.diary.shared_diary.dto.diary.DiaryDetailResponseDto;
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
    private final GroupRepository groupRepository;
    private final UserRepository userRepository;

    public DiaryService(DiaryRepository diaryRepository, GroupRepository groupRepository, UserRepository userRepository) {
        this.diaryRepository = diaryRepository;
        this.groupRepository = groupRepository;
        this.userRepository = userRepository;
    }

    public DiaryResponseDto createDiary(Long groupId, String email, DiaryRequestDto dto) {
        User author = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new RuntimeException("Group not found"));

        if (!group.getMembers().contains(author)) {
            throw new RuntimeException("그룹에 속한 사용자만 작성할 수 있습니다.");
        }

        Diary diary = Diary.builder()
                .title(dto.getTitle())
                .content(dto.getContent())
                .createdAt(LocalDateTime.now())
                .author(author)
                .group(group)
                .build();

        Diary saved = diaryRepository.save(diary);
        return new DiaryResponseDto(saved);
    }


    public DiaryDetailResponseDto getDiaryDetail(Long diaryId, String email) {
        Diary diary = diaryRepository.findById(diaryId)
                .orElseThrow(() -> new RuntimeException("Diary not found"));

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // 권한 체크: 그룹 멤버만 조회 가능
        if (!diary.getGroup().getMembers().contains(user)) {
            throw new RuntimeException("해당 그룹에 속한 사용자만 조회할 수 있습니다.");
        }

        return new DiaryDetailResponseDto(diary);
    }

}
