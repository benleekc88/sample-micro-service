package com.spring.sample.edge.client;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.circuitbreaker.EnableCircuitBreaker;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.cloud.netflix.zuul.EnableZuulProxy;
//import org.springframework.cloud.stream.annotation.EnableBinding;
//import org.springframework.cloud.stream.annotation.Output;
//import org.springframework.cloud.stream.messaging.Source;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
//import org.springframework.messaging.MessageChannel;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
//import java.util.List;
//import java.util.Map;

//interface SampleClientChannels {
//
//	@Output
//	MessageChannel output();
//}

//@EnableBinding(SampleClientChannels.class)
@EnableCircuitBreaker
@EnableZuulProxy
@EnableDiscoveryClient
@SpringBootApplication
public class SampleClientApplication {

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
		SpringApplication.run(SampleClientApplication.class, args);
	}
	
}

// below is a complicated situation to use edge-client to 
// facade the micro-service

@RestController
@RequestMapping("/services")
class SampleServiceApiGateway {
	
	@Autowired
	private RestTemplate template;
	
	@SuppressWarnings("unchecked")
	private Collection<Map<String, String>> readStudents() {
		String urlString = String.format("http://SAMPLE-SERVICE/services/students");
		ResponseEntity<? extends Collection> responseEntity = template.getForEntity(urlString, Collection.class);		
		return (Collection<Map<String, String>>) responseEntity.getBody();
	}
	
	
	private Collection<Map<String, String>> addStudentFallback(@RequestParam(value="name") String name) {
		ArrayList<Map<String, String>> emptyData = new ArrayList<Map<String, String>>();
		return emptyData;
	}
	
	private Collection<Map<String, String>> getStudentsFallback() {
		ArrayList<Map<String, String>> emptyData = new ArrayList<Map<String, String>>();
		return emptyData;
	}

	//@InboundChannelAdapter(Source.OUTPUT)
	@SuppressWarnings("unchecked")
	@RequestMapping(method=RequestMethod.POST, value="/students")
	@HystrixCommand(fallbackMethod="addStudentFallback")
	public Collection<Map<String, String>> addStudent(@RequestParam(value="name") String name) {
		String urlString = String.format("http://SAMPLE-SERVICE/services/students?name=%s", name);		
		ResponseEntity<? extends Collection> responseEntity = template.postForEntity(urlString, null, Collection.class);		
		return (Collection<Map<String, String>>) responseEntity.getBody();		
	}
	
	@RequestMapping(method=RequestMethod.GET, value="/students")
	@HystrixCommand(fallbackMethod="getStudentsFallback")
	public Collection<Map<String, String>> getStudents() {
		return readStudents();
	}
	
	@SuppressWarnings("unchecked")
	private Map<String, String> readData(String start, String end) {
		String urlString = String.format("http://SAMPLE-SERVICE/services/sample?start=%s&end=%s", start, end);
		Map<String, String> sampleResult = new HashMap<String, String>();				
		ResponseEntity<? extends Map> responseEntity = template.getForEntity(urlString, sampleResult.getClass());
		
		return (Map<String, String>) responseEntity.getBody();
	}
	
	public Map<String, String> sampleDataFallback(String start, String end) {
		// assume returning empty map if service not available
		return new HashMap<String, String>();
	}
	
	public Collection<String> sampleDataKeysFallback(String start, String end) {
		// assume returning empty map if service not available
		return sampleDataFallback( start,  end).keySet();
	}
	
	public Collection<String> sampleDataValuesFallback(String start, String end) {
		// assume returning empty map if service not available
		return sampleDataFallback( start,  end).values();
	}
	
	@RequestMapping(method=RequestMethod.GET, value="/sample")
	@HystrixCommand(fallbackMethod="sampleDataFallback")
	public Map<String, String> sampleData(@RequestParam(value="start", defaultValue="1") String start, 
			@RequestParam(value="end", defaultValue="10") String end) {
		return this.readData(start, end);
	}
	
	@RequestMapping(method=RequestMethod.GET, value="/sample/keys")
	@HystrixCommand(fallbackMethod="sampleDataKeysFallback")
	public Collection<String> sampleDataKeys(@RequestParam(value="start", defaultValue="1") String start, 
			@RequestParam(value="end", defaultValue="10") String end) {
		Map<String, String> response = this.readData(start, end);
		return response.keySet();
	}
	
	@RequestMapping(method=RequestMethod.GET, value="/sample/values")
	@HystrixCommand(fallbackMethod="sampleDataValuesFallback")
	public Collection<String> sampleDataValues(@RequestParam(value="start", defaultValue="1") String start, 
			@RequestParam(value="end", defaultValue="10") String end) {
		Map<String, String> response = this.readData(start, end);
		return response.values();
	}
}