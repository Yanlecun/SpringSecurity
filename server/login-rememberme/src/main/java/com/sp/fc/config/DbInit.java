package com.sp.fc.config;

import com.sp.fc.user.domain.SpUser;
import com.sp.fc.user.service.SpUserService;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class DbInit implements InitializingBean {

    @Autowired
    private SpUserService userService;

    @Override
    public void afterPropertiesSet() throws Exception {
        if(!userService.findUser("user1@test.com").isPresent()){
            SpUser user = userService.save(SpUser.builder()
                    .email("user1@test.com")
                    .password("1111")
                    .enabled(true)
                    .build());
            userService.addAuthority(user.getUserId(), "ROLE_USER");
        }

        if(!userService.findUser("user2@test.com").isPresent()){
            SpUser user = userService.save(SpUser.builder()
                    .email("user2@test.com")
                    .password("1111")
                    .enabled(true)
                    .build());
            userService.addAuthority(user.getUserId(), "ROLE_USER");
        }

        if(!userService.findUser("admin@test.com").isPresent()){
            SpUser user = userService.save(SpUser.builder()
                    .email("admin@test.com")
                    .password("1111")
                    .enabled(true)
                    .build());
            userService.addAuthority(user.getUserId(), "ROLE_ADMIN");
        }
    }



}
