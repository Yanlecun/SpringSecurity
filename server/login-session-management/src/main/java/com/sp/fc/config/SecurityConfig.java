package com.sp.fc.config;

import com.sp.fc.user.service.SpUserService;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.boot.web.servlet.ServletListenerRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.security.access.hierarchicalroles.RoleHierarchy;
import org.springframework.security.access.hierarchicalroles.RoleHierarchyImpl;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.core.session.SessionRegistryImpl;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.rememberme.*;
import org.springframework.security.web.session.HttpSessionEventPublisher;

import javax.servlet.http.HttpSessionEvent;
import javax.sql.DataSource;
import java.time.LocalDateTime;

@EnableWebSecurity(debug = true)
@EnableGlobalMethodSecurity(prePostEnabled = true) // @PreAuthorize in HomeController
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    RememberMeAuthenticationFilter rememberMeAuthenticationFilter;
    TokenBasedRememberMeServices tokenBasedRememberMeServices;
    PersistentTokenBasedRememberMeServices persistentTokenBasedRememberMeServices;

    private final SpUserService userService;
    private final DataSource dataSource;

    public SecurityConfig(DataSource dataSource, SpUserService userService) {
        this.dataSource = dataSource;
        this.userService = userService;
    }


    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userService); // user 설정하는 부분 이걸로 대체
    }

    @Bean
    PasswordEncoder passwordEncoder() {
        return NoOpPasswordEncoder.getInstance(); // 테스트용으로 인코더 잠시 생성
    }

    @Bean
    RoleHierarchy roleHierarchy() {
        RoleHierarchyImpl roleHierarchy = new RoleHierarchyImpl();
        roleHierarchy.setHierarchy("ROLE_ADMIN > ROLE_USER");
        return roleHierarchy;
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .authorizeRequests(request -> {
                    request
                            .antMatchers("/").permitAll()
                            .anyRequest().authenticated()
                    ;
                })
                .formLogin(
                        login -> login.loginPage("/login").permitAll()
                                .defaultSuccessUrl("/", false)
                                .failureUrl("/login-error")
                )
                .logout(logout -> logout.logoutSuccessUrl("/"))
                .exceptionHandling(exception -> exception.accessDeniedPage("/access-denied"))
                .rememberMe(r -> r
                        .rememberMeServices(rememberMeServices())) // persistanceTokenBased로 동작하게 된다.
                .sessionManagement(s-> s
                        //.sessionCreationPolicy(p-> SessionCreationPolicy.ALWAYS)
                        //.sessionFixation(sessionFixationConfigurer -> sessionFixationConfigurer.none())
                        .maximumSessions(1)  // 유저당 한 세션 허용
                        .maxSessionsPreventsLogin(false) // 새로 들어온 세션을 인정, 기존 세션을 만료시킴
                        .expiredUrl("/session-expired")  // 만료된 세션을 가진 유저에게 보낼 페이지
                )
                ;
    }

    @Override
    public void configure(WebSecurity web) throws Exception {
        web.ignoring()
                // 권한에서 체크하지 않는 권한으로 설정
                .antMatchers("/sessions", "/session/expire", "/session-expired")
                .requestMatchers(
                        PathRequest.toStaticResources().atCommonLocations(),
                        PathRequest.toH2Console() // 테스트를 위한 h2 콘솔을 열어줌
                );
    }

    @Bean
    public ServletListenerRegistrationBean<HttpSessionEventPublisher> httpSessionEventPublisher() {
        return new ServletListenerRegistrationBean<HttpSessionEventPublisher>(new HttpSessionEventPublisher() {
            @Override
            public void sessionCreated(HttpSessionEvent event) {
                super.sessionCreated(event);
                System.out.printf("===>> [%s] 세션 생성됨 %s \n", LocalDateTime.now(), event.getSession().getId());
            }

            @Override
            public void sessionDestroyed(HttpSessionEvent event) {
                super.sessionDestroyed(event);
                System.out.printf("===>> [%s] 세션 만료됨 %s \n", LocalDateTime.now(), event.getSession().getId());
            }

            @Override
            public void sessionIdChanged(HttpSessionEvent event, String oldSessionId) {
                super.sessionIdChanged(event, oldSessionId);
                System.out.printf("===>> [%s] 세션 아이디 변경  %s:%s \n", LocalDateTime.now(), oldSessionId, event.getSession().getId());
            }
        });
    }

    @Bean
    PersistentTokenRepository tokenRepository() {
        JdbcTokenRepositoryImpl repository = new JdbcTokenRepositoryImpl(); // datasource를 주입 받아야 함
        repository.setDataSource(dataSource);
        try {
            repository.removeUserTokens("1"); // 삭제를 했을 때 
        } catch (Exception e) {
            repository.setCreateTableOnStartup(true); // 문제가 생기면 table을 만들도록 함 (그냥 당연히 만들고 감)
        }
        return repository;
    }

    @Bean
    PersistentTokenBasedRememberMeServices rememberMeServices() {
        PersistentTokenBasedRememberMeServices services =
                new PersistentTokenBasedRememberMeServices("hello" // key값 아무거나,
                        , userService
                        , tokenRepository());
        services.setAlwaysRemember(true);  // 원래라면 httpSecurity에 붙여야하지만 커스텀해서 만들었으니 여기에 붙이자..매번 로그인하기 귀찮음
        return services;
    }

    @Bean
    SessionRegistry sessionRegistry() {  // Impl로 구현체 받아오기
        SessionRegistryImpl registry = new SessionRegistryImpl();
        return registry;
    }
}
