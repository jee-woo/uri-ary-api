package com.diary.shared_diary.controller;

import com.diary.shared_diary.service.DiaryService;
import org.springframework.web.bind.annotation.*;
import com.diary.shared_diary.dto.diary.DiaryRequestDto;
import com.diary.shared_diary.dto.diary.DiaryResponseDto;

import java.util.List;

@RestController
@RequestMapping("/api/groups")
public class GroupDiaryController {

    private final DiaryService diaryService;

    public GroupDiaryController(DiaryService diaryService) {
        this.diaryService = diaryService;
    }

    @PostMapping("/{groupId}/diaries")
    public DiaryResponseDto createDiaryInGroup(
            @PathVariable Long groupId,
            @RequestBody DiaryRequestDto dto
    ) {
        return diaryService.createDiaryInGroup(groupId, dto);
    }

    @GetMapping("/{groupId}/diaries")
    public List<DiaryResponseDto> getGroupDiaries(@PathVariable Long groupId) {
        return diaryService.getDiariesByGroup(groupId);
    }

}
