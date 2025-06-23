package com.diary.shared_diary.dto.group;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GroupRequestDto {
    private String name;
    private Long creatorId;
}
