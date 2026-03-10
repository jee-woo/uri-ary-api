package com.diary.shared_diary.service;

import com.diary.shared_diary.domain.*;
import com.diary.shared_diary.dto.diary.DiaryDetailResponseDto;
import com.diary.shared_diary.dto.diary.DiaryRequestDto;
import com.diary.shared_diary.dto.diary.DiaryResponseDto;
import com.diary.shared_diary.dto.diary.EncryptedDiaryKeyDto;
import com.diary.shared_diary.exception.NotFoundException;
import com.diary.shared_diary.repository.*;
import com.diary.shared_diary.util.S3Uploader;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class DiaryService {

    private final DiaryRepository diaryRepository;
    private final GroupRepository groupRepository;
    private final UserRepository userRepository;
    private final DiaryKeyRepository diaryKeyRepository;
    private final GroupMemberRepository groupMemberRepository;
    private final S3Uploader s3Uploader;
    private final GroupMemberService groupMemberService;

    @Transactional
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
                .createdAt(LocalDateTime.now())
                .author(author)
                .group(group)
                .imagePath(imagePath)
                .build();

        Diary savedDiary = diaryRepository.save(diary);
        log.info("Diary created with id: {}", savedDiary.getId());

        List<GroupMember> acceptedMembers = groupMemberRepository.findByGroupIdAndStatusWithUser(groupId, MemberStatus.ACCEPTED);

        Map<Long, User> memberMap = acceptedMembers.stream()
                .map(GroupMember::getUser)
                .collect(Collectors.toMap(User::getId, Function.identity()));

        if (dto.getKeys() != null) {
            for (EncryptedDiaryKeyDto keyDto : dto.getKeys()) {
                User member = memberMap.get(keyDto.getUserId());
                if (member != null) {
                    log.info("[DEBUG] Match found for userId {}. Saving key.", keyDto.getUserId());
                    DiaryKey diaryKey = DiaryKey.builder()
                            .diary(savedDiary)
                            .user(member)
                            .encryptedAesKey(keyDto.getEncryptedAesKey())
                            .build();
                    diaryKeyRepository.save(diaryKey);
                } else {
                    log.warn("[DEBUG] No matching accepted member found for userId {}. Skipping key.", keyDto.getUserId());
                }
            }
        }

        return new DiaryResponseDto(savedDiary, s3Uploader);
    }

    @Transactional
    public void deleteDiary(Long diaryId, String email) {
        log.info("Deleting diary for diaryId: {}, user: {}", diaryId, email);
        Diary diary = diaryRepository.getOrThrow(diaryId);
        User user = userRepository.getByEmailOrThrow(email);

        if (!diary.getAuthor().getId().equals(user.getId())) {
            throw new org.springframework.security.access.AccessDeniedException("본인이 작성한 일기만 삭제할 수 있습니다.");
        }

        if (diary.getImagePath() != null) {
            s3Uploader.delete(diary.getImagePath());
        }

        diaryKeyRepository.deleteByDiary(diary);
        diaryRepository.delete(diary);
        log.info("Diary deleted: {}", diaryId);
    }

    public DiaryDetailResponseDto getDiaryDetail(Long diaryId, String email) {
        log.info("Fetching diary detail for diaryId: {}, user: {}", diaryId, email);
        Diary diary = diaryRepository.getOrThrow(diaryId);
        User user = userRepository.getByEmailOrThrow(email);

        groupMemberService.validateAcceptedMember(user, diary.getGroup());

        DiaryKey diaryKey = diaryKeyRepository.findByDiaryAndUser(diary, user)
                .orElseThrow(() -> new NotFoundException("일기 키를 찾을 수 없습니다."));

        boolean isMine = diary.getAuthor().getId().equals(user.getId());

        log.info("Successfully fetched diary detail for diaryId: {}", diaryId);
        return new DiaryDetailResponseDto(diary, diaryKey, s3Uploader, isMine);
    }

}
