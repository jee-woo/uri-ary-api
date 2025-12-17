package com.diary.shared_diary.controller;

import com.diary.shared_diary.auth.CustomUserDetails;
import com.diary.shared_diary.dto.diary.DiaryDetailResponseDto;
import com.diary.shared_diary.dto.diary.DiaryRequestDto;
import com.diary.shared_diary.dto.diary.DiaryResponseDto;
import com.diary.shared_diary.service.DiaryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/groups/{groupId}/diaries")
public class DiaryController {

    private final DiaryService diaryService;

    public DiaryController(DiaryService diaryService) {
        this.diaryService = diaryService;
    }

    @PostMapping
    public ResponseEntity<DiaryResponseDto> createDiary(
            @PathVariable Long groupId,
            @ModelAttribute DiaryRequestDto dto,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        log.info("[DIARY-CREATE] Request user: {}, group: {}", userDetails.getUsername(), groupId);
        DiaryResponseDto result = diaryService.createDiary(groupId, userDetails.getUsername(), dto, dto.getImage());
        return ResponseEntity.ok(result);
    }


    @GetMapping("/{diaryId}")
    public DiaryDetailResponseDto getDiaryDetail(
            @PathVariable Long diaryId,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        log.info("[DIARY-GET] Request user: {}, diary: {}", userDetails.getUsername(), diaryId);
        String email = userDetails.getUsername();
        return diaryService.getDiaryDetail(diaryId, email);
    }
}
