package com.diary.shared_diary.controller;

import com.diary.shared_diary.auth.CustomUserDetails;
import com.diary.shared_diary.dto.group.*;
import com.diary.shared_diary.repository.GroupRepository;
import com.diary.shared_diary.service.GroupService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/groups")
@RequiredArgsConstructor
public class GroupController {

    private final GroupService groupService;
    private final GroupRepository groupRepository;


    @GetMapping("/user")
    public List<GroupResponseDto> getUserGroups(@AuthenticationPrincipal CustomUserDetails userDetails) {
        String email = userDetails.getUsername(); // 또는 userDetails.getEmail();
        return groupService.getGroupsByUserEmail(email);
    }


    @PostMapping("/join")
    public void joinGroup(@RequestBody @Valid JoinGroupRequestDto dto, @AuthenticationPrincipal CustomUserDetails userDetails) {
        groupService.joinGroupByCode(dto.getCode(), userDetails.getUsername());
    }


    @GetMapping("/{groupId}")
    public GroupDetailResponseDto getGroupDetail(@PathVariable Long groupId, @AuthenticationPrincipal CustomUserDetails userDetails) {
        String email = userDetails.getUsername();
        return groupService.getGroupDetail(groupId, email);
    }


    @PostMapping
    public GroupResponseDto createGroup(
            @RequestBody GroupRequestDto dto,
            org.springframework.security.core.Authentication authentication
    ) {
        String email = (String) authentication.getPrincipal();
        return groupService.createGroup(email, dto);
    }

    @PostMapping("/{groupId}/members")
    public void addGroupMembers(
            @PathVariable Long groupId,
            @RequestBody GroupMemberAddRequestDto dto
    ) {
        groupService.addMembers(groupId, dto.getUserIds());
    }
}
