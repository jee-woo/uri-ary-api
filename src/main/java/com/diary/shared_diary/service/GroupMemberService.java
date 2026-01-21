package com.diary.shared_diary.service;

import com.diary.shared_diary.domain.Group;
import com.diary.shared_diary.domain.GroupMember;
import com.diary.shared_diary.domain.MemberStatus;
import com.diary.shared_diary.domain.User;
import com.diary.shared_diary.repository.GroupMemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class GroupMemberService {

    private final GroupMemberRepository groupMemberRepository;

    /**
     * 사용자가 해당 그룹의 승인된 멤버(ACCEPTED)인지 검증합니다.
     */
    public void validateAcceptedMember(User user, Group group) {
        GroupMember membership = groupMemberRepository.findByUserAndGroup(user, group)
                .orElseThrow(() -> new AccessDeniedException("해당 그룹의 멤버가 아닙니다."));

        if (membership.getStatus() != MemberStatus.ACCEPTED) {
            log.warn("Access Denied: User {} is in {} status for Group {}",
                    user.getEmail(), membership.getStatus(), group.getId());
            throw new AccessDeniedException("그룹장의 승인이 완료되지 않은 상태입니다.");
        }
    }

    /**
     * 사용자가 해당 그룹에 가입 기록(PENDING 또는 ACCEPTED)이 있는지 확인합니다.
     */
    public boolean isAlreadyMember(User user, Group group) {
        return groupMemberRepository.existsByUserAndGroup(user, group);
    }
}