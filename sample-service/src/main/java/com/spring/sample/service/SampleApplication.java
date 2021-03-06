package com.spring.sample.service;

import java.text.DecimalFormat;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import org.springframework.context.annotation.Bean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@EnableDiscoveryClient
@SpringBootApplication
public class SampleApplication {

	@Bean
	CommandLineRunner clr (StudentRepository studentRepository) {
		return new CommandLineRunner() {

			@Override
			public void run(String... args) throws Exception {
				// TODO Auto-generated method stub
				// default data here
				Stream.of("Student 1").forEach(s -> studentRepository.save(new Student(s)));
			}

		};
	}

	public static void main(String[] args) {
		String is_remote = System.getProperty("IS_REMOTE");
		if (is_remote != null) {
			if (!System.getProperty("IS_REMOTE").equalsIgnoreCase("true") && !System.getProperty("IS_REMOTE").equalsIgnoreCase("yes")) {
				System.out.println("LOCAL-APPLICATION.PROPERTIES is loaded.");					
				System.setProperty("spring.config.name", "local-application");
			}
		}
		SpringApplication.run(SampleApplication.class, args);
	}

}

@RestController
@RequestMapping(value="/services")
class SampleServiceController {

	@Autowired
	private StudentRepository studentRepository;

	@RequestMapping(method=RequestMethod.GET, value="/students")
	public Collection<Student> getStudents() {
		return studentRepository.findAll();
	}

	@RequestMapping(method=RequestMethod.POST, value="/students")
	public Collection<Student> writeStudents(@RequestParam(value="name") String name) {
		studentRepository.save(new Student(name));
		return studentRepository.findAll();
	}

	@RequestMapping(method=RequestMethod.GET, value="/hello")
	public String helloWorld(@RequestParam(value="name", defaultValue="") String name) {
		String messageTemplate = "Hello World%s";
		String message = null;
		if (!name.isEmpty()) {
			message = String.format(messageTemplate, ", " + name + "!");
		} else {
			message = String.format(messageTemplate, "!");
		}
		return message;
	}

	@RequestMapping(method=RequestMethod.GET, value="/sample")
	public Map<String, String> sampleData(@RequestParam(value="start", defaultValue="1") int start,
			@RequestParam(value="end", defaultValue="10") int end) {
		// function purpose:
		// generate a list of numbers between start and end

		DecimalFormat formatter = new DecimalFormat("0000");
		Map<String, String> map = new HashMap<String, String>();
		for(int i=start; i<=end; i++) {
			map.put("key_" + formatter.format(i), formatter.format(i));
		}
		return map;
	}

}
