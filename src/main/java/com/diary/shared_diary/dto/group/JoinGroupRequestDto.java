package com.diary.shared_diary.dto.group;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class JoinGroupRequestDto {
    @NotBlank(message = "그룹 코드는 필수입니다.")
    private String code;
}
