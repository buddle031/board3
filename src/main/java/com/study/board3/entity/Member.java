package com.study.board3.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "board3")
public class Member {

    @Id@GeneratedValue
    @Column(name = "member_Id")
    private Long id;

    private String username;
    private String loginId;

    private String password;

    private String email;

    //생년월일
    private int year;

    private int month;

    private int day;

    private String phoneNumber;

    private boolean verified;

    private String loginType;

    public String getLoginId() {
        return loginId;
    }

    public void setLoginId(String loginId) {
        this.loginId = loginId;
    }
}
