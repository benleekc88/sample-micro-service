package works.service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;

@RestController
@RequestMapping("/services/students")
public class StudentRegistrationController {
	
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
	
}
