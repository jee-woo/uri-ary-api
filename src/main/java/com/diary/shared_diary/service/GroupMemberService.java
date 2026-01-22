package com.diary.shared_diary.service;

import com.diary.shared_diary.domain.*;

import com.diary.shared_diary.dto.group.PendingMemberResponseDto;
import com.diary.shared_diary.exception.NotFoundException;
import com.diary.shared_diary.repository.GroupMemberRepository;
import com.diary.shared_diary.repository.GroupRepository;
import com.diary.shared_diary.repository.NotificationRepository;
import com.diary.shared_diary.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
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

    @Transactional
    public void requestToJoinGroup(Long userId, Long groupId) {
        User user = userRepository.getByIdOrThrow(userId);
        Group group = groupRepository.getOrThrow(groupId);

        if (isAlreadyMember(user, group)) {
            throw new IllegalStateException("이미 가입 요청을 보냈거나 가입된 그룹입니다.");
        }

        groupMemberRepository.save(GroupMember.createPendingMember(user, group));

        List<GroupMember> acceptedMembers = groupMemberRepository.findByGroupAndStatus(group, MemberStatus.ACCEPTED);
        String message = user.getUsername() + "님이 '" + group.getName() + "' 그룹 참여를 요청했습니다.";

        for (GroupMember member : acceptedMembers) {
            notificationRepository.save(new Notification(member.getUser(), message, NotificationType.REQUEST, group.getId()));
        }
    }

    @Transactional
    public void approveJoinRequest(Long adminId, Long groupId, Long memberId) {
        User admin = userRepository.getByIdOrThrow(adminId);
        Group group = groupRepository.getOrThrow(groupId);

        validateAcceptedMember(admin, group);

        User targetUser = userRepository.getByIdOrThrow(memberId);
        GroupMember groupMember = groupMemberRepository.findByUserAndGroup(targetUser, group)
                .orElseThrow(() -> new NotFoundException("가입 요청이 존재하지 않습니다."));

        groupMember.approve();

        String message = "'" + group.getName() + "' 그룹 가입 요청이 승인되었습니다.";
        notificationRepository.save(new Notification(targetUser, message, NotificationType.SYSTEM, group.getId()));
    }

    public List<PendingMemberResponseDto> getPendingMembers(Long userId, Long groupId) {
        User user = userRepository.getByIdOrThrow(userId);
        Group group = groupRepository.getOrThrow(groupId);

        validateAcceptedMember(user, group);

        return groupMemberRepository.findByGroupAndStatus(group, MemberStatus.PENDING).stream()
                .map(gm -> PendingMemberResponseDto.from(gm.getUser()))
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