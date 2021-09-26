package com.sp.fc.web.student;


import org.springframework.beans.factory.InitializingBean;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

// 통행증 발급 Authentication provider
@Component
public class StudentManager implements AuthenticationProvider, InitializingBean {
    // Student에 대한 리스트를 가지고 있어야하며 원래는 DB연동해야함
    private HashMap<String, Student> studentDB = new HashMap<>();

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        if(authentication instanceof UsernamePasswordAuthenticationToken) {
            UsernamePasswordAuthenticationToken token = (UsernamePasswordAuthenticationToken) authentication;
            if(studentDB.containsKey(token.getName())){
                return getStudentAuthenticationToken(token.getName());
            }
            return null;
        }
        StudentAuthenticationToken token = (StudentAuthenticationToken) authentication;
        if(studentDB.containsKey(token.getCredentials())) { // 이름이 아이디와 같다면
            // studentDB에 있다면, 인증 토큰을 발행하는 Authentication provider
            //Student student = studentDB.get(token.getName());
            return getStudentAuthenticationToken(token.getCredentials());
        }
        return null;  // 처리할 수 없는 Auth는 null로 처리하지 않는다.
    }

    private StudentAuthenticationToken getStudentAuthenticationToken(String id) {
        Student student = studentDB.get(id);
        return StudentAuthenticationToken.builder()
                .principal(student)
                .details(student.getUsername())
                .authenticated(true)
                .build();
    }

    @Override
    public boolean supports(Class<?> authentication) {
        // UsernamePasswordAuthentication필터를 통해 토큰을 받기 때문에
        // 이 클래스의 형태의 토큰을 받으면 검증을 하는 provier로서 동작하겠다고 알림

        //return authentication == UsernamePasswordAuthenticationToken.class; radio체크로 관리자/학생 구현
        return authentication == StudentAuthenticationToken.class ||
                authentication == UsernamePasswordAuthenticationToken.class;
    }

    // 빈이 초기화 되었을 때 Student DB
    @Override
    public void afterPropertiesSet() throws Exception {
        Set.of(
                new Student("user1", "홍구", Set.of(new SimpleGrantedAuthority("ROLE_STUDENT")), "teacher1"),
                new Student("user2", "강강이", Set.of(new SimpleGrantedAuthority("ROLE_STUDENT")), "teacher1"),
                new Student("user3", "뭉치", Set.of(new SimpleGrantedAuthority("ROLE_STUDENT")), "teacher1")
        ).forEach(s ->
                studentDB.put(s.getId(), s)
        );
    }

    public List<Student> myStudents(String teacherId) {
        return studentDB.values().stream().filter(s -> s.getTeacherId().equals(teacherId))
                .collect(Collectors.toList());
    }
}
