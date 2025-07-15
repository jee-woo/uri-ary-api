package com.diary.shared_diary.repository;

import com.diary.shared_diary.domain.Comment;
import com.diary.shared_diary.domain.Diary;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findByDiaryOrderByCreatedAtAsc(Diary diary);
}
