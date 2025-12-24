package com.diary.shared_diary.repository;

import com.diary.shared_diary.domain.User;
import com.diary.shared_diary.exception.NotFoundException;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    boolean existsByEmail(String email);
    Optional<User> findByEmail(String email);
    Optional<User> findByRefreshToken(String refreshToken);
    Optional<User> findByAuthorizationCode(String authorizationCode);

    default User getByEmailOrThrow(String email) {
        return findByEmail(email)
                .orElseThrow(() -> new NotFoundException("이메일이 [" + email + "]인 사용자를 찾을 수 없습니다."));
    }

    default User getByIdOrThrow(Long id) {
        return findById(id)
                .orElseThrow(() -> new NotFoundException("ID가 [" + id + "]인 사용자를 찾을 수 없습니다."));
    }
}
