package com.diary.shared_diary.repository;

import com.diary.shared_diary.domain.Group;
import com.diary.shared_diary.domain.GroupMember;
import com.diary.shared_diary.domain.MemberStatus;
import com.diary.shared_diary.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface GroupMemberRepository extends JpaRepository<GroupMember, Long> {
    Optional<GroupMember> findByUserAndGroup(User user, Group group);
    List<GroupMember> findByGroupIdAndStatus(Long groupId, MemberStatus status);
    boolean existsByUserAndGroup(User user, Group group);
    boolean existsByUserAndGroupAndStatusIn(User user, Group group, List<MemberStatus> statuses);
    List<GroupMember> findByGroupAndStatus(Group group, MemberStatus status);
    List<GroupMember> findByUser(User user);

    @Query("SELECT gm FROM GroupMember gm JOIN FETCH gm.user WHERE gm.group.id = :groupId AND gm.status = :status")
    List<GroupMember> findByGroupIdAndStatusWithUser(@Param("groupId") Long groupId, @Param("status") MemberStatus status);
}