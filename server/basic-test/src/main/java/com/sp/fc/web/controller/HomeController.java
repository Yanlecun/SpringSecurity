package com.sp.fc.web.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/")
public class HomeController {

    @GetMapping("/")
    public String index() {
        return "홈페이지";
    }

    @GetMapping("/auth")
    public Authentication auth() {
        return SecurityContextHolder.getContext().getAuthentication();
    }

    @PreAuthorize("hasAnyAuthority('ROLE_USER')") // 권한 가진 사람만 접근하도록 하기
    @GetMapping("/user")
    public SecurityMessage user() {  //유저의 개인정보를 출력해보자
        return SecurityMessage.builder()
                .authentication(SecurityContextHolder.getContext().getAuthentication()) // 권한정보 출력
                .message("User 관련 정보")
                .build();
    }

    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN')")
    @GetMapping("/admin")
    public SecurityMessage admin() {
        return SecurityMessage.builder()
                .authentication(SecurityContextHolder.getContext().getAuthentication())
                .message("admin 관련 정보")
                .build();
    }
}
