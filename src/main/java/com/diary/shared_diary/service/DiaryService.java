package com.diary.shared_diary.service;

import com.diary.shared_diary.domain.*;
import com.diary.shared_diary.dto.diary.DiaryDetailResponseDto;
import com.diary.shared_diary.dto.diary.DiaryRequestDto;
import com.diary.shared_diary.dto.diary.DiaryResponseDto;
import com.diary.shared_diary.dto.diary.EncryptedDiaryKeyDto;
import com.diary.shared_diary.exception.NotFoundException;
import com.diary.shared_diary.repository.DiaryKeyRepository;
import com.diary.shared_diary.repository.DiaryRepository;
import com.diary.shared_diary.repository.GroupRepository;
import com.diary.shared_diary.repository.UserRepository;
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

        List<GroupMember> acceptedMembers = group.getGroupMembers().stream()
                .filter(gm -> gm.getStatus() == MemberStatus.ACCEPTED)
                .toList();

        Map<Long, User> memberMap = acceptedMembers.stream()
                .map(GroupMember::getUser)
                .collect(Collectors.toMap(User::getId, Function.identity()));

        // [DEBUG] Log server-side member list
        log.info("[DEBUG] Found {} accepted members in the group.", memberMap.size());
        memberMap.keySet().forEach(id -> log.info("[DEBUG] Accepted member ID on server: {}", id));

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

    public DiaryDetailResponseDto getDiaryDetail(Long diaryId, String email) {
        log.info("Fetching diary detail for diaryId: {}, user: {}", diaryId, email);
        Diary diary = diaryRepository.getOrThrow(diaryId);
        User user = userRepository.getByEmailOrThrow(email);

        groupMemberService.validateAcceptedMember(user, diary.getGroup());

        DiaryKey diaryKey = diaryKeyRepository.findByDiaryAndUser(diary, user)
                .orElseThrow(() -> new NotFoundException("일기 키를 찾을 수 없습니다."));

        log.info("Successfully fetched diary detail for diaryId: {}", diaryId);
        return new DiaryDetailResponseDto(diary, diaryKey, s3Uploader);
    }

}
