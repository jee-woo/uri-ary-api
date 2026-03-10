package com.diary.shared_diary.repository;

import com.diary.shared_diary.domain.Diary;
import com.diary.shared_diary.domain.DiaryKey;
import com.diary.shared_diary.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface DiaryKeyRepository extends JpaRepository<DiaryKey, Long> {
    List<DiaryKey> findByDiary_Group_IdAndUser_Id(Long groupId, Long userId);
    Optional<DiaryKey> findByDiaryAndUser(Diary diary, User user);
    void deleteByDiary(Diary diary);
}
