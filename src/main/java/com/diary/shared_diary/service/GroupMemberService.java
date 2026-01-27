package com.diary.shared_diary.service;

import com.diary.shared_diary.domain.*;

import com.diary.shared_diary.dto.group.*;
import com.diary.shared_diary.exception.NotFoundException;
import com.diary.shared_diary.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class GroupMemberService {

    private final GroupMemberRepository groupMemberRepository;
    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;
    private final GroupRepository groupRepository;
    private final DiaryKeyRepository diaryKeyRepository;
    private final DiaryRepository diaryRepository;

    @Transactional
    public void requestToJoinGroup(Long userId, Long groupId) {
        log.info("Request to join group: userId={}, groupId={}", userId, groupId);
        User user = userRepository.getByIdOrThrow(userId);
        Group group = groupRepository.getOrThrow(groupId);

        if (isAlreadyMember(user, group)) {
            log.warn("User {} is already a member of group {}", userId, groupId);
            throw new IllegalStateException("이미 가입 요청을 보냈거나, 가입된 그룹입니다.");
        }
        GroupMember pendingRequest = groupMemberRepository.save(
                GroupMember.createPendingMember(user, group)
        );
//        groupMemberRepository.save(GroupMember.createPendingMember(user, group));

        List<GroupMember> members = groupMemberRepository.findByGroupAndStatus(group, MemberStatus.ACCEPTED);
        log.info("Found {} accepted members in group {}", members.size(), groupId);
        String message = user.getUsername() + "님이 '" + group.getName() + "' 그룹 참여를 요청했습니다.";

        for (GroupMember member : members) {
            Notification notification = Notification.builder()
                    .receiver(member.getUser())
                    .message(message)
                    .type(NotificationType.REQUEST)
                    .targetId(pendingRequest.getId())
                    .isRead(false)
                    .createdAt(LocalDateTime.now())
                    .build();

            Notification savedNotification = notificationRepository.save(notification);
            log.info("Saved notification: {}", savedNotification);
        }
    }

    public ApprovalInfoResponse getApprovalInfo(Long adminId, Long groupMemberId) {
        User admin = userRepository.getByIdOrThrow(adminId);
        GroupMember groupMember = groupMemberRepository.findById(groupMemberId)
                .orElseThrow(() -> new NotFoundException("가입 요청을 찾을 수 없습니다."));
        Group group = groupMember.getGroup();
        validateAcceptedMember(admin, group);

        User newMember = groupMember.getUser();
        String newMemberPublicKey = newMember.getPublicKey();
        if (newMemberPublicKey == null || newMemberPublicKey.isEmpty()) {
            throw new IllegalStateException("새로운 멤버의 공개키가 등록되지 않았습니다.");
        }

        List<DiaryKey> diaryKeys = diaryKeyRepository.findByDiary_Group_IdAndUser_Id(group.getId(), adminId);
        List<DiaryKeyDto> diaryKeyDtos = diaryKeys.stream()
                .map(DiaryKeyDto::from)
                .collect(Collectors.toList());

        return new ApprovalInfoResponse(newMemberPublicKey, diaryKeyDtos);
    }

    @Transactional
    public void approveMemberWithKeys(Long adminId, Long groupMemberId, ApproveMemberRequest request) {
        User admin = userRepository.getByIdOrThrow(adminId);
        GroupMember groupMember = groupMemberRepository.findById(groupMemberId)
                .orElseThrow(() -> new NotFoundException("가입 요청을 찾을 수 없습니다."));
        Group group = groupMember.getGroup();
        validateAcceptedMember(admin, group);

        User newMember = groupMember.getUser();
        groupMember.approve();

        List<Diary> diariesInGroup = diaryRepository.findByGroup(group);
        Map<Long, Diary> diaryMap = diariesInGroup.stream()
                .collect(Collectors.toMap(Diary::getId, d -> d));

        for (ReEncryptedKeyDto keyDto : request.getReEncryptedKeys()) {
            Diary diary = diaryMap.get(keyDto.getDiaryId());
            if (diary != null) {
                DiaryKey diaryKey = DiaryKey.builder()
                        .diary(diary)
                        .user(newMember)
                        .encryptedAesKey(keyDto.getEncryptedAesKey())
                        .build();
                diaryKeyRepository.save(diaryKey);
            }
        }

        String message = "'" + group.getName() + "' 그룹 가입 요청이 승인되었습니다.";
        Notification notification = Notification.builder()
                .receiver(newMember)
                .message(message)
                .type(NotificationType.APPROVED)
                .targetId(group.getId())
                .isRead(false)
                .createdAt(LocalDateTime.now())
                .build();
        notificationRepository.save(notification);
    }

    public List<PendingMemberResponseDto> getPendingMembers(Long userId, Long groupId) {
        User user = userRepository.getByIdOrThrow(userId);
        Group group = groupRepository.getOrThrow(groupId);

        validateAcceptedMember(user, group);

        return groupMemberRepository.findByGroupAndStatus(group, MemberStatus.PENDING).stream()
                .map(gm -> PendingMemberResponseDto.from(gm.getUser()))
                .collect(Collectors.toList());
    }

    public List<GroupMemberResponseDto> getGroupMembers(Long groupId, Long requestingUserId) {
        User requestingUser = userRepository.getByIdOrThrow(requestingUserId);
        Group group = groupRepository.getOrThrow(groupId);
        validateAcceptedMember(requestingUser, group);

        List<GroupMember> acceptedMembers = groupMemberRepository.findByGroupAndStatus(group, MemberStatus.ACCEPTED);

        return acceptedMembers.stream()
                .map(GroupMemberResponseDto::from)
                .collect(Collectors.toList());
    }

    public void validateAcceptedMember(User user, Group group) {
        GroupMember membership = groupMemberRepository.findByUserAndGroup(user, group)
                .orElseThrow(() -> new AccessDeniedException("해당 그룹의 멤버가 아닙니다."));

        if (membership.getStatus() != MemberStatus.ACCEPTED) {
            throw new AccessDeniedException("그룹 승인이 완료되지 않았습니다.");
        }
    }

    public boolean isAlreadyMember(User user, Group group) {
        return groupMemberRepository.existsByUserAndGroupAndStatusIn(user, group, List.of(MemberStatus.PENDING, MemberStatus.ACCEPTED));
    }
}