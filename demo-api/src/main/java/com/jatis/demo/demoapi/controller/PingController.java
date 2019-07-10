package com.jatis.demo.demoapi.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class PingController {

	@GetMapping(path="/", produces="text/plain")
	public String ping() {
		return "OK";
	}
}
