package com.diary.shared_diary.repository;

import com.diary.shared_diary.domain.Diary;
import com.diary.shared_diary.domain.User;
import com.diary.shared_diary.domain.Group;
import com.diary.shared_diary.exception.NotFoundException;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DiaryRepository extends JpaRepository<Diary, Long> {
    List<Diary> findByAuthor(User author);

    List<Diary> findByGroup(Group group);
    List<Diary> findByGroupOrderByCreatedAtDesc(Group group);

    default Diary getOrThrow(Long id) {
        return findById(id)
                .orElseThrow(() -> new NotFoundException("ID가 [" + id + "]인 일기를 찾을 수 없습니다."));
    }
}
