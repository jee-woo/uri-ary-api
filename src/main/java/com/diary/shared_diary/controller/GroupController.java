package com.diary.shared_diary.controller;

import com.diary.shared_diary.auth.CustomUserDetails;
import com.diary.shared_diary.dto.group.*;
import com.diary.shared_diary.service.GroupMemberService;
import com.diary.shared_diary.service.GroupService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/groups")
@RequiredArgsConstructor
public class GroupController {

    private final GroupService groupService;
    private final GroupMemberService groupMemberService;

    @Operation(summary = "그룹에 가입 요청")
    @PostMapping("/join-requests")
    public void requestToJoinGroup(@AuthenticationPrincipal CustomUserDetails userDetails, @RequestBody @Valid JoinGroupRequestDto dto) {
        log.info("[GROUP-JOIN] Request user: {}, group code: {}", userDetails.getId(), dto.code());
        Long groupId = groupService.getGroupByCode(dto.code()).getId();
        groupMemberService.requestToJoinGroup(userDetails.getId(), groupId);
    }

    @Operation(summary = "그룹 가입 요청 승인")
    @PostMapping("/join-requests/{targetId}/approve")
    public void approveJoinRequest(@AuthenticationPrincipal CustomUserDetails userDetails, @PathVariable Long targetId) {
        groupMemberService.approveJoinRequest(userDetails.getId(), targetId);
    }

    @Operation(summary = "그룹 가입 대기자 목록 조회")
    @GetMapping("/{groupId}/pending-members")
    public List<PendingMemberResponseDto> getPendingMembers(@AuthenticationPrincipal CustomUserDetails userDetails, @PathVariable Long groupId) {
        return groupMemberService.getPendingMembers(userDetails.getId(), groupId);
    }

    @GetMapping("/user")
    public List<GroupResponseDto> getUserGroups(@AuthenticationPrincipal CustomUserDetails userDetails) {
        String email = userDetails.getUsername(); // 또는 userDetails.getEmail();
        log.info("[GROUP-GET-USER] Request user: {}", email);
        return groupService.getGroupsByUserEmail(email);
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
}
