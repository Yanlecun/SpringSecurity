package com.sp.fc.web;

import com.sp.fc.web.test.WebIntegrationTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.assertEquals;


class AuthorityBasicTestApplicationTest extends WebIntegrationTest {
    TestRestTemplate client;

    @DisplayName("1. greeting")
    @Test
    void test_1(){
        client = new TestRestTemplate("user1", "1111");
        ResponseEntity<String> response = client.getForEntity(uri("/greeting"), String.class);

        assertEquals("hello", response.getBody());
        System.out.println(response.getBody()); // hello
    }

    @DisplayName("2. greeting PathVar") // ROLE_ADMIN
    @Test
    void test_2(){
        client = new TestRestTemplate("user1", "1111");
        ResponseEntity<String> response = client.getForEntity(uri("/greeting/songwoo"), String.class);

        //assertEquals("hello songwoo", response.getBody());
        System.out.println(response.getBody()); // hello songwoo
    }

}