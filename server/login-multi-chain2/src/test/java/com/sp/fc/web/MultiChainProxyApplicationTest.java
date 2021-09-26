package com.sp.fc.web;

import com.sp.fc.web.student.Student;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class MultiChainProxyApplicationTest {

    @LocalServerPort
    int port;

    TestRestTemplate testClient = new TestRestTemplate("teacher1","1");

    @DisplayName("1. teacher1:1 로 로그인해서 조회")
    @Test
    void test_1(){
        String url = String.format("http://localhost:%d/api/teacher/students",port);
        ResponseEntity<List<Student>> res = testClient.exchange(url, HttpMethod.GET, null
                ,new ParameterizedTypeReference<List<Student>>() {
                });
        System.out.println(res.getBody());
        assertEquals(3,res.getBody().size());
    }

}