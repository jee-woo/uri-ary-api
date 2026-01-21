package com.diary.shared_diary.service;

import com.diary.shared_diary.domain.*;
import com.diary.shared_diary.dto.group.GroupDetailResponseDto;
import com.diary.shared_diary.dto.group.GroupRequestDto;
import com.diary.shared_diary.dto.group.GroupResponseDto;
import com.diary.shared_diary.dto.group.PendingMemberResponseDto;
import com.diary.shared_diary.exception.NotFoundException;
import com.diary.shared_diary.repository.DiaryRepository;
import com.diary.shared_diary.repository.GroupMemberRepository;
import com.diary.shared_diary.repository.GroupRepository;
import com.diary.shared_diary.repository.UserRepository;
import com.diary.shared_diary.util.CodeGenerator;
import com.diary.shared_diary.util.S3Uploader;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class GroupService {

    private final GroupRepository groupRepository;
    private final UserRepository userRepository;
    private final DiaryRepository diaryRepository;
    private final S3Uploader s3Uploader;
    private final GroupMemberRepository groupMemberRepository;
    private final GroupMemberService groupMemberService;

    public List<GroupResponseDto> getGroupsByUserEmail(String email) {
        log.info("Fetching groups for user: {}", email);
        User user = userRepository.getByEmailOrThrow(email);
        List<GroupMember> groupMembers = groupMemberRepository.findByUser(user);
        log.info("Found {} groups for user: {}", groupMembers.size(), email);
        return groupMembers.stream()
                .map(groupMember -> {
                    Group group = groupMember.getGroup();
                    return new GroupResponseDto(group.getId(), group.getName(), group.getCode(), groupMember.getStatus());
                })
                .toList();
    }

    public GroupDetailResponseDto getGroupDetail(Long groupId, String email) {
        log.info("Fetching group detail for groupId: {}, user: {}", groupId, email);
        Group group = groupRepository.getOrThrow(groupId);
        User user = userRepository.getByEmailOrThrow(email);

        group.validateMember(user);

        List<Diary> diaries = diaryRepository.findByGroupOrderByCreatedAtDesc(group);

        log.info("Successfully fetched group detail for groupId: {}", groupId);
        return new GroupDetailResponseDto(group, diaries, s3Uploader);
    }

    @Transactional
    public GroupResponseDto createGroup(String userEmail, GroupRequestDto dto) {
        log.info("Creating group for user: {}", userEmail);
        User creator = userRepository.getByEmailOrThrow(userEmail);

        String code;
        do {
            code = CodeGenerator.generateGroupCode();
        } while (groupRepository.findByCode(code).isPresent());

        Group group = Group.builder()
                .name(dto.name())
                .code(code)
                .createdAt(LocalDateTime.now())
                .build();

        group.addMember(creator, MemberStatus.ACCEPTED);

        Group saved = groupRepository.save(group);
        log.info("Group created with id: {}", saved.getId());
        return new GroupResponseDto(saved.getId(), saved.getName(), code, MemberStatus.ACCEPTED);
    }

    @Transactional
    public void joinGroupByCode(String code, String email) {
        log.info("User {} attempts to join group with code: {}", email, code);
        Group group = groupRepository.getByCodeOrThrow(code);
        User user = userRepository.getByEmailOrThrow(email);

        boolean alreadyExists = groupMemberRepository.existsByUserAndGroup(user, group);

        if (!alreadyExists) {
            group.addMember(user, MemberStatus.PENDING);
            groupRepository.save(group);
            log.info("User {} request to join group (PENDING) with code: {}", email, code);
        } else {
            log.info("User {} already has a membership record for group with code: {}", email, code);
        }
    }

    @Transactional
    public void addMembers(Long groupId, List<Long> userIds) {
        log.info("Adding members to group: {}", groupId);
        Group group = groupRepository.getOrThrow(groupId);
        List<User> usersToAdd = userRepository.findAllById(userIds);

        for (User user : usersToAdd) {
            group.addMember(user, MemberStatus.PENDING);
        }

        log.info("Successfully requested adding {} members to group: {}", usersToAdd.size(), groupId);
    }


    @Transactional(readOnly = true)
    public List<PendingMemberResponseDto> getPendingMembers(Long groupId, String email) {
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new NotFoundException("그룹을 찾을 수 없습니다."));
        User user = userRepository.getByEmailOrThrow(email);

        // 요청한 사람이 이 그룹의 승인된 멤버인지 확인 (보안)
        groupMemberService.validateAcceptedMember(user, group);

        return groupMemberRepository.findByGroupIdAndStatus(groupId, MemberStatus.PENDING).stream()
                .map(PendingMemberResponseDto::new)
                .toList();
    }
}
