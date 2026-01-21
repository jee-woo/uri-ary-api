package com.diary.shared_diary.controller;

import com.diary.shared_diary.auth.CustomUserDetails;
import com.diary.shared_diary.dto.group.*;
import com.diary.shared_diary.repository.GroupRepository;
import com.diary.shared_diary.service.GroupService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/groups")
@RequiredArgsConstructor
public class GroupController {

    private final GroupService groupService;
    private final GroupRepository groupRepository;


    @GetMapping("/user")
    public List<GroupResponseDto> getUserGroups(@AuthenticationPrincipal CustomUserDetails userDetails) {
        String email = userDetails.getUsername(); // 또는 userDetails.getEmail();
        log.info("[GROUP-GET-USER] Request user: {}", email);
        return groupService.getGroupsByUserEmail(email);
    }


    @PostMapping("/join")
    public void joinGroup(@RequestBody @Valid JoinGroupRequestDto dto, @AuthenticationPrincipal CustomUserDetails userDetails) {
        log.info("[GROUP-JOIN] Request user: {}, group code: {}", userDetails.getUsername(), dto.code());
        groupService.joinGroupByCode(dto.code(), userDetails.getUsername());
    }

    @GetMapping("/{groupId}/pending-members")
    public ResponseEntity<List<PendingMemberResponseDto>> getPendingMembers(
            @PathVariable Long groupId,
            @AuthenticationPrincipal CustomUserDetails userDetails) { // 현재 로그인 유저 정보

        List<PendingMemberResponseDto> pendingMembers =
                groupService.getPendingMembers(groupId, userDetails.getUsername());

        return ResponseEntity.ok(pendingMembers);
    }


    @GetMapping("/{groupId}")
    public GroupDetailResponseDto getGroupDetail(@PathVariable Long groupId, @AuthenticationPrincipal CustomUserDetails userDetails) {
        String email = userDetails.getUsername();
        log.info("[GROUP-GET-DETAIL] Request user: {}, group: {}", email, groupId);
        return groupService.getGroupDetail(groupId, email);
    }


    @PostMapping
    public GroupResponseDto createGroup(
            @RequestBody GroupRequestDto dto,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        String email = userDetails.getUsername();
        log.info("[GROUP-CREATE] Request user: {}", email);
        return groupService.createGroup(email, dto);
    }

    @PostMapping("/{groupId}/members")
    public void addGroupMembers(
            @PathVariable Long groupId,
            @RequestBody GroupMemberAddRequestDto dto
    ) {
        log.info("[GROUP-ADD-MEMBER] Add members to group: {}", groupId);
        groupService.addMembers(groupId, dto.userIds());
    }
}
