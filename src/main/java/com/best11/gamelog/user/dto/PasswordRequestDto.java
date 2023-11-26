package com.best11.gamelog.user.dto;

import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PasswordRequestDto {

    private String currentPassword;

    @Size(min = 8,max = 15, message ="비밀번호는 8자 이상 15자 이하만 가능합니다.")
    @Pattern(regexp = "^[a-zA-Z_0-9]*$", message = "비밀번호는 알파벳 대소문자, 숫자만 사용가능합니다.")
    private String changePassword;

    private String changePasswordCheck;
}
