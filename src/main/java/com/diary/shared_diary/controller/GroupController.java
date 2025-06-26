package com.diary.shared_diary.controller;

import com.diary.shared_diary.dto.group.GroupMemberAddRequestDto;
import com.diary.shared_diary.dto.group.GroupRequestDto;
import com.diary.shared_diary.dto.group.GroupResponseDto;
import com.diary.shared_diary.service.GroupService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/groups")
public class GroupController {

    private final GroupService groupService;

    public GroupController(GroupService groupService) {
        this.groupService = groupService;
    }

    @GetMapping("/user")
    public List<GroupResponseDto> getUserGroups(@AuthenticationPrincipal String email) {
        return groupService.getGroupsByUserEmail(email);
    }



    @PostMapping
    public GroupResponseDto createGroup(
            @RequestBody GroupRequestDto dto,
            org.springframework.security.core.Authentication authentication
    ) {
        String email = (String) authentication.getPrincipal();
        return groupService.createGroup(dto, email);
    }

    @PostMapping("/{groupId}/members")
    public void addGroupMembers(
            @PathVariable Long groupId,
            @RequestBody GroupMemberAddRequestDto dto
    ) {
        groupService.addMembers(groupId, dto.getUserIds());
    }
}
