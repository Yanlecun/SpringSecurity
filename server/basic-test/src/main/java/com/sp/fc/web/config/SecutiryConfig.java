package com.sp.fc.web.config;

import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@EnableWebSecurity(debug = true)  // 디버그 모드
@EnableGlobalMethodSecurity(prePostEnabled = true) // 권한을 체크할 수 있도록 하겠음
public class SecutiryConfig extends WebSecurityConfigurerAdapter {
    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception { // Auth매니저는 나중에 알아보겠음
        auth.inMemoryAuthentication()    // 그냥 이걸 사용한다 정도
                .withUser(User.builder()
                        .username("user2")
                        //.password("2222")   // 그냥 실행하면 오류, 패스워드를 다른 사람이 못 알아보게 인코딩 해주여야함 (Node에서 따로 만든 것 처럼)
                        .password(passwordEncoder().encode("2222"))
                        .roles("USER"))
                .withUser(User.builder()
                        .username("admin1")
                        .password(passwordEncoder().encode("1111"))
                        .roles("ADMIN"));
    }

    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(); // 시큐리티가 기본으로 사용하는 인코더
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        //super.configure(http); // 상위 클래스의 메소드 보면 밑의 세 줄이 되어 있음
//        http.authorizeRequests((requests) -> requests.anyRequest().authenticated()); // 기본적으로 인증하도록 되어 있음
        http.authorizeRequests((requests) -> requests.antMatchers("/").permitAll() // 모든 사람에게 접근 허용하도록 하기
                .anyRequest().authenticated());
        http.formLogin();
        http.httpBasic();
    }
}
