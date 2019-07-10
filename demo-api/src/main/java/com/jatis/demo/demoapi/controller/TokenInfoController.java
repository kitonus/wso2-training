package com.jatis.demo.demoapi.controller;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.jatis.demo.demoapi.dto.TokenDTO;

@RestController
@RequestMapping("/api/token_info")
public class TokenInfoController {
	
	@GetMapping
	@PostMapping
	public TokenDTO info(HttpServletRequest request) {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		
		TokenDTO tokenDto = new TokenDTO();
		tokenDto.setUser(auth.getName());
		List<String> roles = new ArrayList<>(auth.getAuthorities().size());
		for (GrantedAuthority ga : auth.getAuthorities()) {
			roles.add(ga.getAuthority());
		}
		tokenDto.setRoles(roles);
		return tokenDto;
	}
}
