package com.study.board3.controller;

import com.study.board3.dto.LoginDto;
import com.study.board3.dto.MemberDto;
import com.study.board3.entity.Member;
import com.study.board3.service.MemberService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
@RequiredArgsConstructor
@Slf4j
public class MemberController {
    private final MemberService memberService;

    @GetMapping("/login") // 실제 URL 경로
    public String getLogin(HttpServletRequest request, Model model) {
        String referer = request.getHeader("Referer");
        request.getSession().setAttribute("prevPage", referer);
        log.info("url={}", referer);
        model.addAttribute("login", new LoginDto());
        return "member/login"; // 여기 내부 경로
    }

    @PostMapping("/login")
    public String postLogin(@ModelAttribute("login") LoginDto loginDto, HttpServletRequest request,
                            HttpSession session, Model model) {
        boolean login = memberService.login(loginDto);

        if (login) {
            String username = loginDto.getUsername();
            Member member = memberService.findByUsername(username);
            session.setAttribute("loginMember", member);

            // 저장한 이전 페이지 주소 가져옴
            String prevPage = (String) request.getSession().getAttribute("prevPage");
            // 세션에 페이지 주소 삭제
            request.getSession().removeAttribute("prevPage");

            // 이전 페이지 존재: 거기로 / 없다면: 기본 페이지로
            return "redirect:" + (prevPage != null ? prevPage : "/");
        }
        model.addAttribute("error", "비밀번호 또는 아이디가 올바르지 않습니다");
        return "member/login";
    }

    @GetMapping("/createMember")
    public String getCreateMember(Model model) {
        model.addAttribute("member", new Member());
        return "member/joinMember";
    }

    @PostMapping("/createMember")
    public String postCreateMember(@ModelAttribute("member") MemberDto memberDto, Model model) {
        memberService.saveDto(memberDto);
        return "redirect:/";
    }

    @PostMapping("/logout")
    public String logout(HttpSession session) {
        session.removeAttribute("loginMember");
        return "redirect:/";
    }
}
