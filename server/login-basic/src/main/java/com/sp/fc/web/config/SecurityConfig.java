package com.sp.fc.web.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.security.access.hierarchicalroles.RoleHierarchy;
import org.springframework.security.access.hierarchicalroles.RoleHierarchyImpl;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.csrf.CsrfFilter;

@EnableWebSecurity(debug = true)
@EnableGlobalMethodSecurity(prePostEnabled = true) // @PreAuthorize in HomeController
public class SecurityConfig extends WebSecurityConfigurerAdapter {

//    @Autowired
//    CustomAuthDetails customAuthDetails;  // private final로 만든 뒤 생성자로 주입 받는 게 트렌드긴 함

    private final CustomAuthDetails customAuthDetails;
    public SecurityConfig(CustomAuthDetails customAuthDetails) {
        this.customAuthDetails = customAuthDetails;
    }

    UsernamePasswordAuthenticationFilter filter;
    CsrfFilter filter2;

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth
                .inMemoryAuthentication()
                .withUser(
                        User.withDefaultPasswordEncoder()  // 안정하지 않으므로 테스트 한정으로 사용
                                .username("user1")
                                .password("1111")
                                .roles("USER")
                ).withUser(
                        User.withDefaultPasswordEncoder()
                                .username("admin1")
                                .password("1111")
                                .roles("ADMIN")
                );
    }

    @Bean
    RoleHierarchy roleHierarchy() {
        RoleHierarchyImpl roleHierarchy = new RoleHierarchyImpl();
        roleHierarchy.setHierarchy("ROLE_ADMIN > ROLE_USER");  // ADMIN은 USER가 가진 모든 권한 사용 가능
        return roleHierarchy;
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .authorizeRequests(request -> {
                    request
                            .antMatchers("/").permitAll() // main페이지 제외 막기
                            .anyRequest().authenticated()    // 그 외에는 인증권한이 필요함
                    ;
                })
                .formLogin( // 로그인 페이지 특정해주자
                        login -> login.loginPage("/login").permitAll()  // 로그인 페이지에 접근할 수 있도록하자
                                .defaultSuccessUrl("/", false) // 로그인 성공 시 이동할 페이지 설정, 원래 가려던 곳으로 가기
                                .failureUrl("/login-error") // 로그인 실패 시 라우팅 !
                                .authenticationDetailsSource(customAuthDetails) // 디테일 정보 담기
                )
                .logout(logout -> logout.logoutSuccessUrl("/"))
                .exceptionHandling(exception -> exception.accessDeniedPage("/access-denied"))  // user가 관리자 페이지로 들어갔을 때
        ;
    }

    @Override  // CSS,js 파일들 마저 시큐리티에 의해 막힘
    public void configure(WebSecurity web) throws Exception {
        web.ignoring()
                .requestMatchers(
                        PathRequest.toStaticResources().atCommonLocations()  // static 리소스들을 무시하도록 함
                );

    }
}
