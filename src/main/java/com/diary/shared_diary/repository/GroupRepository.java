package com.diary.shared_diary.repository;

import com.diary.shared_diary.domain.Group;
import com.diary.shared_diary.domain.User;
import io.micrometer.common.lang.NonNull;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.List;

public interface GroupRepository extends JpaRepository<Group, Long> {
    List<Group> findAllByMembersContains(User user);

    Optional<Group> findByCode(String code);

    default @NonNull Group getById(Long id) {
        return findById(id)
                .orElseThrow(() -> new RuntimeException("Group not found"));
    }
}
