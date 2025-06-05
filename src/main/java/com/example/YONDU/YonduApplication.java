package com.example.YONDU;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EnableJpaRepositories(basePackages = "com.example.YONDU.repository")  // 이 줄 추가
public class YonduApplication {

	public static void main(String[] args) {
		SpringApplication.run(YonduApplication.class, args);
	}

}
