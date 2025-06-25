package com.diary.shared_diary.controller;

import com.diary.shared_diary.auth.CustomUserDetails;
import com.diary.shared_diary.dto.comment.CommentRequestDto;
import com.diary.shared_diary.dto.comment.CommentResponseDto;
import com.diary.shared_diary.service.CommentService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
        return commentService.getComments(diaryId, userDetails.getUsername());
    }

    @PostMapping
    public CommentResponseDto createComment(@PathVariable Long diaryId,
                                            @AuthenticationPrincipal CustomUserDetails userDetails,
                                            @RequestBody CommentRequestDto dto) {
        return commentService.createComment(diaryId, userDetails.getUsername(), dto);
    }
}
