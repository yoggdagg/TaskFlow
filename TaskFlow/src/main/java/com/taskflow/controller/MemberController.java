package com.taskflow.controller;

import java.util.Map;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.taskflow.dto.Member;
import com.taskflow.security.JWTUtil;
import com.taskflow.service.MemberService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/api/member")
@AllArgsConstructor
public class MemberController {
	private final MemberService memberService;
	private final JWTUtil jwtUtil;
	
	// 일반 회원가입
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
                .body(Map.of("message", "회원가입이 실패했습니다."));
        }
    }
    
    // 일반 로그인
    @PostMapping("/login") 
    public ResponseEntity<?> login(@RequestBody Map<String, String> map, HttpServletResponse res) {
    		log.info("=== 로그인 요청 수신 ===");
    		log.info("Email : {}", map.get("email"));
    		
    		try {
    			String email = map.get("email");
    			String pw = map.get("password");
    			
    			// 로그인 처리 로직
    			Map<String, Object> response = memberService.login(email, pw, res);
    			
    			log.info("로그인 성공");
    			return ResponseEntity.ok(response);
    		} catch(IllegalArgumentException e) {
    			log.error("로그인 실패 - 인증 오류: 아이디 또는 비밀번호를 확인하세요. \n{}", e.getMessage());
    			
    			return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
    									.body(Map.of("message", e.getMessage()));
    		} catch(Exception e) {
    			// 기타 오류
    			log.error("로그인 실패 - 서버 오류: {}", e.getMessage());
    			
    			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
    					.body(Map.of("message", "로그인 처리 중 오류가 발생했습니다."));
    		}
    }
    
    /**
     * 로그아웃 (일반 로그인 + OAuth 공통)
     */
    @PostMapping("/logout")
    public ResponseEntity<Map<String, String>> logout(
        HttpServletRequest request,
        HttpServletResponse response) {
    		ResponseEntity<Map<String, String>> responseEntity = memberService.logout(request, response);
    		
    		return responseEntity;
    }
    
}
