package com.study.board3.service;

import com.study.board3.dto.LoginDto;
import com.study.board3.dto.MemberDto;
import com.study.board3.entity.Board;
import com.study.board3.entity.Member;

public interface MemberService {
    Member saveEntity(Member member);
    Member saveDto(MemberDto memberDto);
    Member findByUsername(String username);

    Member findByUsernameAndLoginId(String username, String loginId);
    boolean login(LoginDto loginDto);

}
