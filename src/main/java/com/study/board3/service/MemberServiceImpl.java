package com.study.board3.service;

import com.study.board3.dto.LoginDto;
import com.study.board3.dto.MemberDto;
import com.study.board3.entity.Board;
import com.study.board3.entity.Member;
import com.study.board3.exception.Login_RestException;
import com.study.board3.exception.NotFindPageException;
import com.study.board3.repository.BoardRepository;
import com.study.board3.repository.MemberRepository;
import jakarta.persistence.Entity;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class MemberServiceImpl implements MemberService{

    @Autowired
    private final MemberRepository memberRepository;

    @Override
    public Member saveEntity(Member member){
        return memberRepository.save(member);
    }

    @Override
    public Member saveDto(MemberDto memberDto){
        Member member = Member.builder()
                .username(memberDto.getUsername())
                .password(memberDto.getPassword())
                .build();
        return saveEntity(member);
    }

    @Override
    public Member findByUsername(String username){
        return memberRepository.findByUsername(username);
    }


    @Override
    public boolean login(LoginDto loginDto){
        String username = loginDto.getUsername();
        String password = loginDto.getPassword();
        Member byUsername = memberRepository.findByUsername(username);
        if (byUsername != null) {
            if(byUsername.getPassword().equals(password)){
                return true;
            }
        }
        return false;
    }

    @Override
    public Member findByUsernameAndLoginId(String username, String loginId) {
        Member byUsernameAndLoginId = memberRepository.findByUsernameAndLoginId(username, loginId);
        if (byUsernameAndLoginId==null){
            throw new Login_RestException();
        }
        return byUsernameAndLoginId;
    }

}
