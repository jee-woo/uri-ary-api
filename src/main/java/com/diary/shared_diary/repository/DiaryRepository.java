package com.diary.shared_diary.repository;

import com.diary.shared_diary.domain.Diary;
import com.diary.shared_diary.domain.User;
import com.diary.shared_diary.domain.Group;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DiaryRepository extends JpaRepository<Diary, Long> {
    List<Diary> findByAuthor(User author);

    List<Diary> findByGroup(Group group);
    List<Diary> findByGroupOrderByCreatedAtDesc(Group group);
}
