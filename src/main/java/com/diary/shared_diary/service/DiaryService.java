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
import com.diary.shared_diary.util.S3Uploader;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class DiaryService {

    private final DiaryRepository diaryRepository;
    private final GroupRepository groupRepository;
    private final UserRepository userRepository;
    private final S3Uploader s3Uploader;
    private final GroupMemberService groupMemberService;

    public DiaryResponseDto createDiary(Long groupId, String email, DiaryRequestDto dto, MultipartFile image) {
        log.info("Start creating diary for user: {}, group: {}", email, groupId);
        User author = userRepository.getByEmailOrThrow(email);
        Group group = groupRepository.getOrThrow(groupId);

        groupMemberService.validateAcceptedMember(author, group);

        String imagePath = null;
        if (image != null && !image.isEmpty()) {
            imagePath = s3Uploader.upload(image, "diary"); // S3 Key 저장
            log.info("Image uploaded to S3 with path: {}", imagePath);
        }

        Diary diary = Diary.builder()
                .title(dto.getTitle())
                .encryptedContent(dto.getEncryptedContent())
                .iv(dto.getIv())
                .authTag(dto.getAuthTag())
                .encryptedAesKey(dto.getEncryptedAesKey())
                .createdAt(LocalDateTime.now())
                .author(author)
                .group(group)
                .imagePath(imagePath)
                .build();

        Diary saved = diaryRepository.save(diary);
        log.info("Diary created with id: {}", saved.getId());
        return new DiaryResponseDto(saved, s3Uploader);
    }



    public DiaryDetailResponseDto getDiaryDetail(Long diaryId, String email) {
        log.info("Fetching diary detail for diaryId: {}, user: {}", diaryId, email);
        Diary diary = diaryRepository.getOrThrow(diaryId);
        User user = userRepository.getByEmailOrThrow(email);

        groupMemberService.validateAcceptedMember(user, diary.getGroup());

        log.info("Successfully fetched diary detail for diaryId: {}", diaryId);
        return new DiaryDetailResponseDto(diary, s3Uploader);
    }

}
