package com.diary.shared_diary.service;

import com.diary.shared_diary.domain.Group;
import com.diary.shared_diary.domain.User;
import com.diary.shared_diary.dto.group.GroupRequestDto;
import com.diary.shared_diary.dto.group.GroupResponseDto;
import com.diary.shared_diary.repository.GroupRepository;
import com.diary.shared_diary.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class GroupService {

    private final GroupRepository groupRepository;
    private final UserRepository userRepository;

    public GroupService(GroupRepository groupRepository, UserRepository userRepository) {
        this.groupRepository = groupRepository;
        this.userRepository = userRepository;
    }

    public List<GroupResponseDto> getGroupsByUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        List<Group> groups = groupRepository.findAllByMembersContaining(user);

        return groups.stream()
                .map(group -> new GroupResponseDto(group.getId(), group.getName()))
                .toList();
    }


    public GroupResponseDto createGroup(GroupRequestDto dto) {
        User creator = userRepository.findById(dto.getCreatorId())
                .orElseThrow(() -> new RuntimeException("Creator not found"));

        Group group = Group.builder()
                .name(dto.getName())
                .createdAt(LocalDateTime.now())
                .members(List.of(creator))
                .build();

        Group saved = groupRepository.save(group);
        return new GroupResponseDto(saved.getId(), saved.getName());
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
