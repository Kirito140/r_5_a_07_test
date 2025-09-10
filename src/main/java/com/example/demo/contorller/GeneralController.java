package com.example.demo.contorller;

import org.springframework.web.bind.annotation.GetMapping;

import org.springframework.stereotype.Controller;

@Controller
public class GeneralController {

	@GetMapping("/")
	public String index() {
		return "index";
	}

}
