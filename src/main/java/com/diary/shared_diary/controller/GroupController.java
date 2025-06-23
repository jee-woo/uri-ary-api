package com.diary.shared_diary.controller;

import com.diary.shared_diary.dto.group.GroupMemberAddRequestDto;
import com.diary.shared_diary.dto.group.GroupRequestDto;
import com.diary.shared_diary.dto.group.GroupResponseDto;
import com.diary.shared_diary.service.GroupService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/groups")
public class GroupController {

    private final GroupService groupService;

    public GroupController(GroupService groupService) {
        this.groupService = groupService;
    }

    @GetMapping("/user/{userId}")
    public List<GroupResponseDto> getGroupsByUser(@PathVariable Long userId) {
        return groupService.getGroupsByUser(userId);
    }


    @PostMapping
    public GroupResponseDto createGroup(@RequestBody GroupRequestDto dto) {
        return groupService.createGroup(dto);
    }

    @PostMapping("/{groupId}/members")
    public void addGroupMembers(
            @PathVariable Long groupId,
            @RequestBody GroupMemberAddRequestDto dto
    ) {
        groupService.addMembers(groupId, dto.getUserIds());
    }
}
