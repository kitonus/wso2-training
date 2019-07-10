package com.jatis.demo.demoapi.dto;

import java.util.List;

public class TokenDTO {

	private String user;
	private List<String> roles;
	public String getUser() {
		return user;
	}
	public void setUser(String user) {
		this.user = user;
	}
	public List<String> getRoles() {
		return roles;
	}
	public void setRoles(List<String> roles) {
		this.roles = roles;
	}
	
	
}
