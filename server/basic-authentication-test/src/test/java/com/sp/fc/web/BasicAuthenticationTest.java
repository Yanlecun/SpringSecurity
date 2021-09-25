package com.sp.fc.web;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.Base64;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class BasicAuthenticationTest {
    @LocalServerPort
    int port;

    RestTemplate client = new RestTemplate();
    private String greetingUrl() {
        return "http://localhost:"+port+"/greeting";
    }
    @DisplayName("1. 인증실패")
    @Test
    void test_1(){
//        String res = client.getForObject(greetingUrl(), String.class);
//        System.out.println(res);

        HttpClientErrorException exception = assertThrows(HttpClientErrorException.class, () -> {
            client.getForObject(greetingUrl(), String.class);
        });
        assertEquals(401, exception.getRawStatusCode()); // 이런 에러를 만들어 내는 것이 맞는지 확인
    }

    @DisplayName("2. 인증 성공1")
    @Test
    void test_2(){
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.AUTHORIZATION, "Basic " + Base64.getEncoder().encodeToString(
                "user1:1111".getBytes()
        ));
        HttpEntity entity = new HttpEntity(null, headers);
        ResponseEntity<String> res = client.exchange(greetingUrl(), HttpMethod.GET, entity, String.class);
        System.out.println(res.getBody());
        assertEquals("hello", res.getBody());  // Basic 토큰을 이용해 secured 상황에서 값을 얻어올 수 있었음
    }

    @DisplayName("3. 인증성공2")
    @Test
    void test_3(){
        TestRestTemplate testClient = new TestRestTemplate("user1", "1111"); //BASIC 토큰 지원
        String res = testClient.getForObject(greetingUrl(), String.class);
        assertEquals("hello", res);
    }

    // post방식도 동작할까 ?
    @DisplayName("4. POST 성공")
    @Test
    void test_4(){
        TestRestTemplate testClient = new TestRestTemplate("user1", "1111"); //BASIC 토큰 지원
        ResponseEntity<String> res = testClient.postForEntity(greetingUrl(),"songwoo", String.class);
        assertEquals("hello songwoo", res.getBody()); // 에러 ! -> post방식은 csrf 필터 작동하기 때문에 disable하자

    }


    
}
