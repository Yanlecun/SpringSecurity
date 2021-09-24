package com.sp.fc.web.controller;

import lombok.* ;
import org.springframework.security.core.Authentication;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SecurityMessage {
    private Authentication authentication;
    private String message; // 어떤 페이지인지 ?
}
