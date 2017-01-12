package works.service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.cloud.netflix.zuul.EnableZuulProxy;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;

@EnableZuulProxy
@EnableDiscoveryClient
@SpringBootApplication
public class WorkDispatcherMain {

	@LoadBalanced
	@Bean
	public RestTemplate restTemplate() {
	    return new RestTemplate();
	}
	
    public static void main(String[] args) {
		String is_remote = System.getProperty("IS_REMOTE");
		if (is_remote != null) {
			if (!System.getProperty("IS_REMOTE").equalsIgnoreCase("true") && !System.getProperty("IS_REMOTE").equalsIgnoreCase("yes")) {
				System.out.println("LOCAL-APPLICATION.PROPERTIES is loaded.");					
				System.setProperty("spring.config.name", "local-application");
			}
		}
        SpringApplication.run(WorkDispatcherMain.class, args);
    }

}
