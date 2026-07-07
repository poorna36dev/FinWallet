package com.poorna.fintech;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableCaching
@EnableAsync
@EnableScheduling
public class FintechApplication {

	public static void main(String[] args) {
		var context=SpringApplication.run(FintechApplication.class, args);

		System.out.println(context.getBeanDefinitionCount());
	}

}
