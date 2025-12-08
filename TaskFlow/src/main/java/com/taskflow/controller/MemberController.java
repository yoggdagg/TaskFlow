package com.taskflow.controller;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.taskflow.dto.Member;
import com.taskflow.service.MemberService;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/api/member")
@AllArgsConstructor
public class MemberController {
	private final MemberService memberService;
	
    @PostMapping("/regist")
    public ResponseEntity<?> registerMember(@RequestBody Member member) {
        log.info("=== 회원가입 요청 수신 ===");
        log.info("Email: {}", member.getEmail());
        log.info("Name: {}", member.getUsername());
        
        try {
            memberService.registerMember(member);
            log.info("회원가입 성공: {}", member.getEmail());
            return ResponseEntity.status(HttpStatus.CREATED)
                .body(Map.of("message", "회원가입이 완료되었습니다."));
        } catch (Exception e) {
            log.error("회원가입 실패: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(Map.of("message", e.getMessage()));
        }
    }
}
