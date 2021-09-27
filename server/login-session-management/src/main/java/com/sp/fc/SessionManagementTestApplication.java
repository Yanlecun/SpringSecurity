package com.sp.fc;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = {
        "com.sp.fc.config", // config만 스캔하면 스캔해야할 정보들 정의해줌
        "com.sp.fc.web"
})
public class SessionManagementTestApplication {
    public static void main(String[] args) {
        SpringApplication.run(SessionManagementTestApplication.class, args);
    }
}
