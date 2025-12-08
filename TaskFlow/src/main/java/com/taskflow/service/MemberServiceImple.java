package com.taskflow.service;

import org.springframework.stereotype.Service;

import com.taskflow.dto.Member;
import com.taskflow.repositories.MemberMapper;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@AllArgsConstructor
public class MemberServiceImple implements MemberService {
	private final MemberMapper memberMapper;
	
	@Override
	public void registerMember(Member member) {
		memberMapper.registerMember(member);
	}
}
