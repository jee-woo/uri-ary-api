package com.diary.shared_diary.controller;

import com.diary.shared_diary.auth.CustomUserDetails;
import com.diary.shared_diary.dto.comment.CommentRequestDto;
import com.diary.shared_diary.dto.comment.CommentResponseDto;
import com.diary.shared_diary.service.CommentService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/diaries/{diaryId}/comments")
public class CommentController {

    private final CommentService commentService;

    public CommentController(CommentService commentService) {
        this.commentService = commentService;
    }

    @GetMapping
    public List<CommentResponseDto> getComments(@PathVariable Long diaryId,
                                                @AuthenticationPrincipal CustomUserDetails userDetails) {
        log.info("[COMMENT-GET] Request user: {}, diary: {}", userDetails.getUsername(), diaryId);
        return commentService.getComments(diaryId, userDetails.getUsername());
    }

    @PostMapping
    public CommentResponseDto createComment(@PathVariable Long diaryId,
                                            @AuthenticationPrincipal CustomUserDetails userDetails,
                                            @RequestBody CommentRequestDto dto) {
        log.info("[COMMENT-CREATE] Request user: {}, diary: {}", userDetails.getUsername(), diaryId);
        return commentService.createComment(diaryId, userDetails.getUsername(), dto);
    }
}
