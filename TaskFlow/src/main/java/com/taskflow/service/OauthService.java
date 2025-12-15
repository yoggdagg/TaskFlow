package com.taskflow.service;

import com.taskflow.dto.LoginResponse;
import com.taskflow.dto.Member;

public interface OauthService {
	Member googleLogin(String code);
	Member naverLogin(String code, String state);
	Member kakaoLogin(String code);
}
