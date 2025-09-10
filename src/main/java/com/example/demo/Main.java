package com.example.demo;

import java.util.logging.Logger;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;



@SpringBootApplication
public class Main {
	public static final Logger log = Logger.getLogger("MyLog");
	
	public static void main(String[] args) {
		SpringApplication.run(Main.class, args);
	}

}
