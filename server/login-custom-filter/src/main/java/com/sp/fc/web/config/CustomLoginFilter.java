package com.sp.fc.web.config;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class CustomLoginFilter extends UsernamePasswordAuthenticationFilter {
    public CustomLoginFilter(AuthenticationManager authenticationManager) {
        // 매니저를 할당해줘야 필터 적용되도록 함
        super(authenticationManager);
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        // 토큰 제작소
        // return super.attemptAuthentication(request, response); // 코드 가져와서 재정의
        String username = obtainUsername(request);
        username = (username != null) ? username : "";
        username = username.trim();
        String password = obtainPassword(request);
        password = (password != null) ? password : "";
        UsernamePasswordAuthenticationToken authRequest = new UsernamePasswordAuthenticationToken(username, password);
//        setDetails(request, authRequest); details는ㄴㄴ
        return this.getAuthenticationManager().authenticate(authRequest);
    }
}
