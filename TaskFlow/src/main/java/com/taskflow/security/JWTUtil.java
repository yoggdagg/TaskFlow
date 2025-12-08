package com.taskflow.security;

import java.nio.charset.StandardCharsets;
import java.util.Date;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

@Component
public class JWTUtil {

	@Value("${jwt.secretKey}")
	private String secretKey;
	// jjwt의 최신 버전에서는 key를 String이 아니라 SecretKey 객체를 사용해야한다.
	public SecretKey getKey() {
		return Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8));
	}
	
	@Value("${jwt.expiration.access}")
	private long accessExpiration;
	
	@Value("${jwt.expiration.refresh}")
	private long refreshExpiration;
	
	// JWT 생성
	public String createJWT(String username, String email, String role) {
		long now = System.currentTimeMillis();
		Date issuedAt = new Date(now);
		Date expiration = new Date(now + accessExpiration);
		
		String jwt = Jwts.builder()
				.setSubject(username) // jwt에 정의된 표준 클레임이 subject
				.claim("role", role) // 커스텀 클레임 role 추가
				.claim("email", email) // username이 아니라 email로 인증할거니 email 추가
				.setIssuedAt(issuedAt)
				.setExpiration(expiration)
				.signWith(getKey(), io.jsonwebtoken.SignatureAlgorithm.HS512)
				.compact();
		return jwt;
	}
	
	// 토큰에서 username 추출
	public String getUsernameFromJWT(String jwt) {
		Claims claims = Jwts.parser()
				.verifyWith(getKey())
				.build()
				.parseSignedClaims(jwt)
				.getPayload();
		// claims객체에 jwt의 페이로드 정보를 담는다.
		return claims.getSubject(); // 페이로드에서 email이라는 이름의 클레임 반환
	}
	
	// 토큰에서 이메일 추출
	public String getEmailFromJWT(String jwt) {
		Claims claims = Jwts.parser()
				.verifyWith(getKey())
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
				.verifyWith(getKey())
				.build()
				.parseSignedClaims(jwt);
				// parseSignedClaims메서드는 다음 동작을 수행한다.
				// 1. JWT 파싱   2. 서명 검증   3. 만료시간 검증
				// 모든 동작을 수행하면 Claims객체를 반환하고 문제가 생기면 예외를 던진다.
			return true;
		} catch(JwtException e) {
			return false;
		}
	}
}
