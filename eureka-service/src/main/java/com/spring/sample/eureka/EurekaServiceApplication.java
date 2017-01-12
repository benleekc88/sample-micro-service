package com.spring.sample.eureka;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;

@EnableDiscoveryClient
@EnableEurekaServer
@SpringBootApplication
public class EurekaServiceApplication {

	public static void main(String[] args) {
		String is_remote = System.getProperty("IS_REMOTE");
		if (is_remote != null) {
			if (!System.getProperty("IS_REMOTE").equalsIgnoreCase("true") && !System.getProperty("IS_REMOTE").equalsIgnoreCase("yes")) {
				System.out.println("LOCAL-APPLICATION.PROPERTIES is loaded.");					
				System.setProperty("spring.config.name", "local-application");
			}
		}
		SpringApplication.run(EurekaServiceApplication.class, args);
	}
}
