package com.diary.shared_diary.controller;

import com.diary.shared_diary.auth.CustomUserDetails;
import com.diary.shared_diary.dto.diary.DiaryRequestDto;
import com.diary.shared_diary.dto.diary.DiaryResponseDto;
import com.diary.shared_diary.service.DiaryService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/groups/{groupId}/diaries")
public class DiaryController {

    private final DiaryService diaryService;

    public DiaryController(DiaryService diaryService) {
        this.diaryService = diaryService;
    }

    @PostMapping
    public DiaryResponseDto createDiary(@PathVariable Long groupId,
                                        @AuthenticationPrincipal CustomUserDetails userDetails,
                                        @RequestBody DiaryRequestDto dto) {
        return diaryService.createDiary(groupId, userDetails.getUsername(), dto);
    }

    @GetMapping
    public List<DiaryResponseDto> getDiaries(@PathVariable Long groupId,
                                             @AuthenticationPrincipal CustomUserDetails userDetails) {
        return diaryService.getDiariesByGroup(groupId, userDetails.getUsername());
    }
}
