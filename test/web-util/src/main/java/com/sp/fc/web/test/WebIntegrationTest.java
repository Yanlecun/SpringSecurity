package com.sp.fc.web.test;


import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;

import java.net.URI;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class WebIntegrationTest {
    @LocalServerPort
    int port;

    public URI uri(String path)  {
        try {
            return new URI(String.format("http://localhost:%d%s",port, path));
        } catch (Exception e){
            throw new RuntimeException();
        }

    }
}
