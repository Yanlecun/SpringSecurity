package com.sp.fc.web.controller;

import com.sp.fc.web.service.SecurityMessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.security.access.prepost.PreAuthorize;

@RestController
public class HomeController {

    @Autowired
    SecurityMessageService securityMessageService;

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping("/greeting")
    public String greeting() {
        return "hello";
    }

    @PreAuthorize("@nameCheck.check(#name) ")
    @GetMapping("/greeting/{name}")
    public String greeting(@PathVariable String name) {
        return "hello " + securityMessageService.message(name);  // 서비스에서 hasRole가 admin이라서 x
    }
}
