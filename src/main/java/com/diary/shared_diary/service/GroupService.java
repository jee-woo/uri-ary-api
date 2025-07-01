package com.diary.shared_diary.service;

import com.diary.shared_diary.domain.Diary;
import com.diary.shared_diary.domain.Group;
import com.diary.shared_diary.domain.User;
import com.diary.shared_diary.dto.group.GroupDetailResponseDto;
import com.diary.shared_diary.dto.group.GroupRequestDto;
import com.diary.shared_diary.dto.group.GroupResponseDto;
import com.diary.shared_diary.repository.DiaryRepository;
import com.diary.shared_diary.repository.GroupRepository;
import com.diary.shared_diary.repository.UserRepository;
import com.diary.shared_diary.util.CodeGenerator;
import com.diary.shared_diary.util.S3Uploader;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

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
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        List<Group> groups = groupRepository.findAllByMembersContains(user);

        return groups.stream()
                .map(group -> new GroupResponseDto(group.getId(), group.getName(), group.getCode()))
                .toList();
    }

    public GroupDetailResponseDto getGroupDetail(Long groupId, String email) {
        Group group = groupRepository.getById(groupId);
        User user = userRepository.getByEmail(email);

        if (!group.getMembers().contains(user)) {
            throw new RuntimeException("해당 그룹에 접근할 권한이 없습니다.");
        }
        List<Diary> diaries = diaryRepository.findByGroupOrderByCreatedAtDesc(group);

        return new GroupDetailResponseDto(group, diaries, s3Uploader);
    }

    public GroupResponseDto createGroup(String userEmail, GroupRequestDto dto) {
        User creator = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // ✅ 유일한 코드 생성
        String code;
        do {
            code = CodeGenerator.generateGroupCode();
        } while (groupRepository.findByCode(code).isPresent());

        Group group = Group.builder()
                .name(dto.name())
                .code(code)
                .createdAt(LocalDateTime.now())
                .members(List.of(creator))
                .build();

        Group saved = groupRepository.save(group);
        return new GroupResponseDto(saved.getId(), saved.getName(), code);
    }


    public void joinGroupByCode(String code, String email) {
        Group group = groupRepository.findByCode(code)
                .orElseThrow(() -> new RuntimeException("해당 코드의 그룹이 없습니다"));

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("사용자 정보를 찾을 수 없습니다"));

        if (!group.getMembers().contains(user)) {
            group.addMember(user);
            groupRepository.save(group);
        }
    }



    public void addMembers(Long groupId, List<Long> userIds) {
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new RuntimeException("Group not found"));

        List<User> usersToAdd = userRepository.findAllById(userIds);

        List<User> currentMembers = group.getMembers();
        for (User user : usersToAdd) {
            if (!currentMembers.contains(user)) {
                currentMembers.add(user);
            }
        }

        group.setMembers(currentMembers);
        groupRepository.save(group);
    }

}
