package com.diary.shared_diary.dto.group;

import com.diary.shared_diary.domain.Diary;
import com.diary.shared_diary.domain.Group;
import com.diary.shared_diary.dto.diary.DiaryResponseDto;
import com.diary.shared_diary.dto.user.UserResponseDto;
import lombok.Getter;

import java.util.List;
import java.util.stream.Collectors;

@Getter
public class GroupDetailResponseDto {
    private Long id;
    private String name;
    private String code;
    private List<UserResponseDto> members;
    private List<DiaryResponseDto> diaries;

    public GroupDetailResponseDto(Group group, List<Diary> diaryList) {
        this.id = group.getId();
        this.name = group.getName();
        this.code = group.getCode();
        this.members = group.getMembers().stream()
                .map(UserResponseDto::new)
                .collect(Collectors.toList());
        this.diaries = diaryList.stream()
                .map(DiaryResponseDto::new)
                .toList();
    }
}
