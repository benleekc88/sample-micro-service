package works.service;

import java.util.Collection;
import java.util.HashMap;
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
@RequestMapping("/services")
public class SampleServiceController {

	@Autowired
	private RestTemplate template;

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
