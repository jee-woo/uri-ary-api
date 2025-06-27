package com.diary.shared_diary.controller;

import com.diary.shared_diary.auth.CustomUserDetails;
import com.diary.shared_diary.dto.diary.DiaryDetailResponseDto;
import com.diary.shared_diary.dto.diary.DiaryRequestDto;
import com.diary.shared_diary.dto.diary.DiaryResponseDto;
import com.diary.shared_diary.service.DiaryService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

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
            @RequestPart("diary") DiaryRequestDto dto,
            @RequestPart(value = "image", required = false) MultipartFile image,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        DiaryResponseDto result = diaryService.createDiary(groupId, userDetails.getUsername(), dto, image);
        return ResponseEntity.ok(result);
    }


    @GetMapping("/{diaryId}")
    public DiaryDetailResponseDto getDiaryDetail(
            @PathVariable Long diaryId,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        String email = userDetails.getUsername();
        return diaryService.getDiaryDetail(diaryId, email);
    }
}
