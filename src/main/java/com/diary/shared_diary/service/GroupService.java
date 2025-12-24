package com.diary.shared_diary.service;

import com.diary.shared_diary.domain.Diary;
import com.diary.shared_diary.domain.Group;
import com.diary.shared_diary.domain.User;
import com.diary.shared_diary.dto.group.GroupDetailResponseDto;
import com.diary.shared_diary.dto.group.GroupRequestDto;
import com.diary.shared_diary.dto.group.GroupResponseDto;
import com.diary.shared_diary.exception.NotFoundException;
import com.diary.shared_diary.repository.DiaryRepository;
import com.diary.shared_diary.repository.GroupRepository;
import com.diary.shared_diary.repository.UserRepository;
import com.diary.shared_diary.util.CodeGenerator;
import com.diary.shared_diary.util.S3Uploader;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Slf4j
@Service
public class GroupService {

    private final GroupRepository groupRepository;
    private final UserRepository userRepository;
    private final DiaryRepository diaryRepository;
    private final S3Uploader s3Uploader;

    public GroupService(GroupRepository groupRepository, UserRepository userRepository, DiaryRepository diaryRepository, S3Uploader s3Uploader) {
        this.groupRepository = groupRepository;
        this.userRepository = userRepository;
        this.diaryRepository = diaryRepository;
        this.s3Uploader = s3Uploader;
    }

    public List<GroupResponseDto> getGroupsByUserEmail(String email) {
        log.info("Fetching groups for user: {}", email);
        User user = userRepository.getByEmailOrThrow(email);
        List<Group> groups = groupRepository.findAllByMembersContains(user);
        log.info("Found {} groups for user: {}", groups.size(), email);
        return groups.stream()
                .map(group -> new GroupResponseDto(group.getId(), group.getName(), group.getCode()))
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

    public GroupResponseDto createGroup(String userEmail, GroupRequestDto dto) {
        log.info("Creating group for user: {}", userEmail);
        User creator = userRepository.getByEmailOrThrow(userEmail);

        String code;
        do {
            code = CodeGenerator.generateGroupCode();
        } while (groupRepository.findByCode(code).isPresent());
        log.info("Generated unique group code: {}", code);

        Group group = Group.builder()
                .name(dto.name())
                .code(code)
                .createdAt(LocalDateTime.now())
                .members(new HashSet<>(Set.of(creator)))
                .build();

        Group saved = groupRepository.save(group);
        log.info("Group created with id: {}", saved.getId());
        return new GroupResponseDto(saved.getId(), saved.getName(), code);
    }

    @Transactional
    public void joinGroupByCode(String code, String email) {
        log.info("User {} attempts to join group with code: {}", email, code);
        Group group = groupRepository.getByCodeOrThrow(code);
        User user = userRepository.getByEmailOrThrow(email);

        if (!group.getMembers().contains(user)) {
            group.addMember(user);
            groupRepository.save(group);
            log.info("User {} successfully joined group with code: {}", email, code);
        } else {
            log.info("User {} is already a member of group with code: {}", email, code);
        }
    }


    @Transactional
    public void addMembers(Long groupId, List<Long> userIds) {
        log.info("Adding members to group: {}", groupId);
        Group group = groupRepository.getOrThrow(groupId);
        List<User> usersToAdd = userRepository.findAllById(userIds);

        for (User user : usersToAdd) {
            group.addMember(user);
        }

        log.info("Successfully added {} members to group: {}", usersToAdd.size(), groupId);
    }

}
