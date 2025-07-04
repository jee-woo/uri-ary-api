package com.diary.shared_diary.dto.group;

import com.diary.shared_diary.domain.Group;
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

    public GroupDetailResponseDto(Group group) {
        this.id = group.getId();
        this.name = group.getName();
        this.code = group.getCode();
        this.members = group.getMembers().stream()
                .map(UserResponseDto::new)
                .toList();
    }
}
