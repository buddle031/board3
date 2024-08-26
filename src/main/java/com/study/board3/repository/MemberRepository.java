package com.study.board3.repository;

import com.study.board3.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MemberRepository extends JpaRepository<Member, Long> {
    Member findByUsername(String username);
    Member findByUsernameAndLoginId(String username,String LoginId);
    Member findByLoginId(String loginId);
}
