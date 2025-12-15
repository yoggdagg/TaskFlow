package com.taskflow.service;

import java.util.Map;

import org.springframework.http.ResponseEntity;

import com.taskflow.dto.Member;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public interface MemberService {

	void registerMember(Member member);

	Map<String, Object> login(String email, String pw, HttpServletResponse response);

	ResponseEntity<Map<String, String>> logout(HttpServletRequest request, HttpServletResponse response);

}
