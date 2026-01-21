package com.diary.shared_diary.repository;

import com.diary.shared_diary.domain.Group;
import com.diary.shared_diary.domain.User;
import com.diary.shared_diary.exception.NotFoundException;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.List;

public interface GroupRepository extends JpaRepository<Group, Long> {
    @Query("SELECT g FROM Group g JOIN g.groupMembers gm WHERE gm.user = :user AND gm.status = 'ACCEPTED'")
    List<Group> findAllByAcceptedMember(@Param("user") User user);

    Optional<Group> findByCode(String code);

    default Group getOrThrow(Long id) {
        return findById(id)
                .orElseThrow(() -> new NotFoundException("해당 ID의 그룹을 찾을 수 없습니다: " + id));
    }

    default Group getByCodeOrThrow(String code) {
        return findByCode(code)
                .orElseThrow(() -> new NotFoundException("해당 코드의 그룹을 찾을 수 없습니다: " + code));
    }
}
