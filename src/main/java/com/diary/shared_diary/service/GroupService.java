package com.diary.shared_diary.service;

import com.diary.shared_diary.domain.*;
import com.diary.shared_diary.dto.group.GroupDetailResponseDto;
import com.diary.shared_diary.dto.group.GroupRequestDto;
import com.diary.shared_diary.dto.group.GroupResponseDto;
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
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class GroupService {

    private final GroupRepository groupRepository;
    private final UserRepository userRepository;
    private final DiaryRepository diaryRepository;
    private final S3Uploader s3Uploader;
    private final GroupMemberRepository groupMemberRepository;

    public Group getGroupByCode(String code) {
        return groupRepository.getByCodeOrThrow(code);
    }

    public List<GroupResponseDto> getGroupsByUserEmail(String email) {
        log.info("Fetching groups for user: {}", email);
        User user = userRepository.getByEmailOrThrow(email);
        List<GroupMember> groupMembers = groupMemberRepository.findByUser(user);
        log.info("Found {} groups for user: {}", groupMembers.size(), email);
        Map<Long, LocalDateTime> joinedAtByGroupId = groupMembers.stream()
                .collect(Collectors.toMap(gm -> gm.getGroup().getId(), GroupMember::getJoinedAt));

        List<GroupResponseDto> dtos = groupMembers.stream()
                .map(groupMember -> {
                    Group group = groupMember.getGroup();
                    int memberCount = (int) group.getGroupMembers().stream()
                            .filter(gm -> gm.getStatus() == MemberStatus.ACCEPTED)
                            .count();
                    LocalDateTime lastDiaryAt = diaryRepository.findTop1ByGroupOrderByCreatedAtDesc(group)
                            .map(Diary::getCreatedAt)
                            .orElse(null);
                    return new GroupResponseDto(group.getId(), group.getName(), group.getCode(), groupMember.getStatus(), memberCount, lastDiaryAt);
                })
                .toList();

        return dtos.stream()
                .sorted(Comparator
                        .comparing((GroupResponseDto dto) -> dto.status() == MemberStatus.ACCEPTED ? 0 : 1)
                        .thenComparing(dto -> {
                            if (dto.status() == MemberStatus.ACCEPTED) {
                                LocalDateTime lastDiaryAt = dto.lastDiaryAt();
                                return lastDiaryAt != null ? lastDiaryAt : LocalDateTime.MIN;
                            }
                            return joinedAtByGroupId.getOrDefault(dto.id(), LocalDateTime.MIN);
                        }, Comparator.reverseOrder()))
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

        Group group = groupRepository.save(Group.builder()
                .name(dto.name())
                .code(code)
                .createdAt(LocalDateTime.now())
                .build());

        groupMemberRepository.save(GroupMember.builder()
                .user(creator)
                .group(group)
                .status(MemberStatus.ACCEPTED)
                .joinedAt(LocalDateTime.now())
                .build());
        log.info("Group created with id: {}", group.getId());
        return new GroupResponseDto(group.getId(), group.getName(), code, MemberStatus.ACCEPTED, 1, null);
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
}