package com.sp.fc.web.config;

import com.sp.fc.web.student.StudentAuthenticationToken;
import com.sp.fc.web.teacher.TeacherAuthenticationToken;
import org.springframework.security.authentication.AuthenticationManager;
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
        String type = request.getParameter("type");
        if (type == null || !type.equals("teacher")) {
            StudentAuthenticationToken token = StudentAuthenticationToken.builder()
                    .credentials(username).build();  // 비밀번호도 넘겨야하지만, 태스트라서 그냥 진행하자
            return this.getAuthenticationManager().authenticate(token);

        } else {
            TeacherAuthenticationToken token = TeacherAuthenticationToken.builder()
                    .credentials(username).build();
            return this.getAuthenticationManager().authenticate(token);

        }
        //UsernamePasswordAuthenticationToken authRequest = new UsernamePasswordAuthenticationToken(username, password);
//        setDetails(request, authRequest); details는ㄴㄴ
        //return this.getAuthenticationManager().authenticate(authRequest);
    }
}
