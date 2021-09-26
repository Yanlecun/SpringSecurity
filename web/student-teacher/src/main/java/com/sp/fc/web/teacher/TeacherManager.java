package com.sp.fc.web.teacher;


import org.springframework.beans.factory.InitializingBean;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Set;

// 통행증 발급 Authentication provider
@Component
public class TeacherManager implements AuthenticationProvider, InitializingBean {
    private HashMap<String, Teacher> teacherDB = new HashMap<>();

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        if(authentication instanceof UsernamePasswordAuthenticationToken) {
            UsernamePasswordAuthenticationToken token = (UsernamePasswordAuthenticationToken)authentication;
            if(teacherDB.containsKey(token.getName())){
                return getTeacherAuthenticationToken(token.getName());
            }
            return null; // 처리할 수 없다면 null, 없으면 오류
        }
        TeacherAuthenticationToken token = (TeacherAuthenticationToken) authentication;
        if(teacherDB.containsKey(token.getCredentials())) {
            return getTeacherAuthenticationToken(token.getCredentials());
        }
        return null;
    }

    private TeacherAuthenticationToken getTeacherAuthenticationToken(String id) {
        Teacher teacher = teacherDB.get(id);
        return TeacherAuthenticationToken.builder()
                .principal(teacher)
                .details(teacher.getUsername())
                .authenticated(true)
                .build();
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return authentication == TeacherAuthenticationToken.class ||
                authentication == UsernamePasswordAuthenticationToken.class;
    }
    @Override
    public void afterPropertiesSet() throws Exception {
        Set.of(
                new Teacher("teacher1", "선생님1", Set.of(new SimpleGrantedAuthority("ROLE_TEACHER"))),
                new Teacher("teacher2", "선생님2", Set.of(new SimpleGrantedAuthority("ROLE_TEACHER")))
        ).forEach(s ->
                teacherDB.put(s.getId(), s)
        );
    }
}
