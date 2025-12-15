package com.taskflow.service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.taskflow.dto.Member;
import com.taskflow.repositories.MemberMapper;
import com.taskflow.security.JWTUtil;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@AllArgsConstructor
public class MemberServiceImple implements MemberService {
    private final MemberMapper memberMapper;
    private final JWTUtil jwtUtil;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void registerMember(Member member) {
        // 비밀번호 암호화
        String encodedPassword = passwordEncoder.encode(member.getPw());
        member.setPw(encodedPassword);
        
        memberMapper.registerMember(member);
    }

    /**
     * 일반 로그인
     */
    @Override
    public Map<String, Object> login(String email, String pw, HttpServletResponse response) {
        Optional<Member> optional = memberMapper.findByEmail(email);

        // 1. 사용자 존재 여부 및 비밀번호 확인
        if (optional.isEmpty() || !passwordEncoder.matches(pw, optional.get().getPw())) {
            throw new IllegalArgumentException("이메일 또는 비밀번호가 일치하지 않습니다");
        }

        Member member = optional.get();

        // 2. 계정 상태 확인
        if (!member.getIsActive()) {
            throw new IllegalArgumentException("비활성화된 계정입니다.");
        }
        
        // 3. JWT 토큰 생성
        String accessToken = jwtUtil.generateAccessToken(member);
        String refreshToken = jwtUtil.generateRefreshToken(member);
        
        // 4. Refresh Token을 DB에 저장
        LocalDateTime tokenExpiresAt = LocalDateTime.now().plusDays(7);
        memberMapper.updateRefreshToken(member.getId(), refreshToken, tokenExpiresAt);
        
        // 5. 마지막 로그인 시간 업데이트
        memberMapper.updateLastLoginAt(member.getId());
        
        // 6. Refresh Token을 HttpOnly 쿠키로 설정
        Cookie refreshTokenCookie = new Cookie("refreshToken", refreshToken);
        refreshTokenCookie.setHttpOnly(true);
        refreshTokenCookie.setSecure(true); // HTTPS 환경에서만 전송
        refreshTokenCookie.setPath("/");
        refreshTokenCookie.setMaxAge(7 * 24 * 60 * 60); // 7일
        response.addCookie(refreshTokenCookie);
        
        log.info("✅ 일반 로그인 성공: {}", email);
        
        // 7. 응답 데이터 구성 (refreshToken은 쿠키로만 전달)
        HashMap<String, Object> responseData = new HashMap<>();
        responseData.put("accessToken", accessToken);
        responseData.put("member", Map.of(
            "id", member.getId(),
            "email", member.getEmail(),
            "username", member.getUsername(),
            "phone", member.getPhone() != null ? member.getPhone() : "",
            "address", member.getAddress() != null ? member.getAddress() : "",
            "profileImage", member.getProfileImage() != null ? member.getProfileImage() : "",
            "provider", member.getProvider(),
            "role", member.getRole()
        ));
        
        return responseData;
    }

    /**
     * 로그아웃 (일반 로그인 + OAuth 공통)
     */
    @Override
    public ResponseEntity<Map<String, String>> logout(HttpServletRequest request, HttpServletResponse response) {
        try {
            // 1. Authorization 헤더에서 JWT 토큰 추출
            String authHeader = request.getHeader("Authorization");
            
            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                String token = authHeader.substring(7);
                
                try {
                    // 2. 토큰 유효성 검증
                    if (jwtUtil.validateJWT(token)) {
                        // 3. 토큰에서 사용자 이메일 추출
                        String email = jwtUtil.getEmailFromJWT(token);
                        
                        // 4. 이메일로 회원 조회
                        Optional<Member> member = memberMapper.findByEmail(email);
                        
                        if (member.isPresent()) {
                            // 5. DB에서 Refresh Token 삭제
                            memberMapper.deleteRefreshToken(member.get().getId());
                            log.info("✅ DB에서 Refresh Token 삭제 완료: {}", email);
                        }
                    }
                } catch (Exception e) {
                    log.warn("⚠️ 토큰 처리 중 오류 (무시하고 계속): {}", e.getMessage());
                }
            }
            
            // 6. Refresh Token 쿠키 삭제 (MaxAge=0으로 설정)
            Cookie refreshTokenCookie = new Cookie("refreshToken", null);
            refreshTokenCookie.setHttpOnly(true);
            refreshTokenCookie.setSecure(true);
            refreshTokenCookie.setPath("/");
            refreshTokenCookie.setMaxAge(0); // 즉시 만료
            response.addCookie(refreshTokenCookie);
            
            log.info("✅ 로그아웃 성공");
            
            return ResponseEntity.ok(Map.of(
                "message", "로그아웃되었습니다.",
                "success", "true"
            ));
            
        } catch (Exception e) {
            log.error("❌ 로그아웃 처리 실패: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of(
                    "message", "로그아웃 처리 중 오류가 발생했습니다.",
                    "success", "false"
                ));
        }
    }
}
