package com.fluxion.sote;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

import java.util.TimeZone;

@EnableJpaAuditing
@SpringBootApplication
@ComponentScan(basePackages = "com.fluxion.sote")
public class SoteApplication {

	public static void main(String[] args) {

		// 🔥 서버 내부 시간 기준을 UTC로 고정 (가장 중요)
		TimeZone.setDefault(TimeZone.getTimeZone("UTC"));

		SpringApplication.run(SoteApplication.class, args);
	}
}
