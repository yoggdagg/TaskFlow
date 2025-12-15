package com.taskflow.security;

import java.nio.charset.StandardCharsets;
import java.util.Date;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.taskflow.dto.Member;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class JWTUtil {

	
	private final SecretKey secretKey;
	private final long accessExpiration;
	private final long refreshExpiration;
	
	// jjwt의 최신 버전에서는 key를 String이 아니라 SecretKey 객체를 사용해야한다.
	public JWTUtil(@Value("${jwt.secretKey}") String secretKey, @Value("${jwt.expiration.access}") long accessExpiration, @Value("${jwt.expiration.refresh}") long refreshExpiration) {
		// Keys.hmacShaKeyFor()는 secretKey의 길이에 따라 암호화 알고리즘을 자동으로 선택해 암호화한다.
		// e.g. secretKey가 512비트라면 자동으로 HS512암호화를 사용
		this.secretKey =  Keys.hmacShaKeyFor(
							secretKey.getBytes(StandardCharsets.UTF_8)
						);
		// 암호화 알고리즘 강제 지정 방법
		// new SecretKeySpec()은 알고리즘 명시 필수!!
//	    this.secretKey = new SecretKeySpec(
//	         secretKey.getBytes(StandardCharsets.UTF_8),
//	         "HmacSHA256"
//	    );
		
		this.accessExpiration = accessExpiration;
		this.refreshExpiration =refreshExpiration;
	}
	
	
	
	
	// 엑세스 토큰 생성
	public String generateAccessToken(Member member) {
		long now = System.currentTimeMillis();
		Date issuedAt = new Date(now);
		Date expiration = new Date(now + accessExpiration);
		
		String token = Jwts.builder()
				.subject(member.getUsername()) // jwt에 정의된 표준 클레임이 subject
				.claim("role", member.getRole()) // 커스텀 클레임 role 추가
				.claim("email", member.getEmail()) // username이 아니라 email로 인증할거니 email 추가
				.issuedAt(issuedAt)
				.expiration(expiration)
				.signWith(secretKey)
				.compact();
		return token;
	}
	
	// 리프레시 토큰 생성
		public String generateRefreshToken(Member member) {
			long now = System.currentTimeMillis();
			Date issuedAt = new Date(now);
			Date expiration = new Date(now + refreshExpiration);
			
			// 리프레시 토큰은 엑세스 토큰 갱신을 위한 것이니 최소한의 정보만 담는다.
			String token = Jwts.builder()
					.subject(member.getUsername()) // jwt에 정의된 표준 클레임이 subject
					.claim("email", member.getEmail()) // username이 아니라 email로 인증할거니 email 추가
					.issuedAt(issuedAt)
					.expiration(expiration)
					.signWith(secretKey)
					.compact();
			return token;
		}
	
	// 토큰에서 username 추출
	public String getUsernameFromJWT(String jwt) {
		Claims claims = Jwts.parser()
				.verifyWith(secretKey)
				.build()
				.parseSignedClaims(jwt)
				.getPayload();
		// claims객체에 jwt의 페이로드 정보를 담는다.
		return claims.getSubject(); // 페이로드에서 email이라는 이름의 클레임 반환
	}
	
	// 토큰에서 이메일 추출
	public String getEmailFromJWT(String jwt) {
		Claims claims = Jwts.parser()
				.verifyWith(secretKey)
				.build()
				.parseSignedClaims(jwt)
				.getPayload();
		
		// claims객체에 jwt의 페이로드 정보를 담는다.
		// Claims.get()은 인자를 2개 받을 경우 2번째 인자는 타입 클래스
		return claims.get("email", String.class); // 페이로드에서 email이라는 이름의 클레임 반환
	}
	
	// JWT 유효성 검증
	public boolean validateJWT(String jwt) {
		try {
			Jwts.parser()
				.verifyWith(secretKey)
				.build()
				.parseSignedClaims(jwt);
				// parseSignedClaims메서드는 다음 동작을 수행한다.
				// 1. JWT 파싱   2. 서명 검증   3. 만료시간 검증
				// 모든 동작을 수행하면 Claims객체를 반환하고 문제가 생기면 예외를 던진다.
			return true;
		} catch(JwtException | IllegalArgumentException e) {
			log.error("JWT 검증 실패 {}", e.getMessage());
			return false;
		}
	}
}
