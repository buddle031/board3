package com.study.board3.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MemberDto {
    @NotEmpty(message = "이름을 입력 해주세요")
    private String username;

    public String getLoginId() {
        return loginId;
    }

    public void setLoginId(String loginId) {
        this.loginId = loginId;
    }

    @NotEmpty(message = "아이디를 입력하세요")
    private String loginId;

    @NotEmpty(message = "비밀번호를 입력하세요")
    private String password;

    @NotEmpty(message = "비밀번호를 다시 입력해주세요")
    private String passwordRe;

    @Pattern(regexp = "^[a-zA-Z0-9]+@[a-zA-Z]+\\.[a-zA-Z]{2,}$", message = "이메일 형식이 올바르지 않습니다.")
    private String email;

    @Pattern(regexp = "^\\d{8}$", message = "전화번호를 정확히 입력해주세요")
    private String phoneNumber;

    @Min(1968)
    @Max(2020)
    private int year;

    @Min(1)
    @Max(12)
    private int month;

    @Min(1)
    @Max(31)
    private int day;

    @Valid
    //private AddressDto addressDto;

    private boolean verified;

    private String loginType;
}
