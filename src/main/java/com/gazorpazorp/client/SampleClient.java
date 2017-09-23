package com.gazorpazorp.client;

import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

import com.gazorpazorp.model.Sample;

@FeignClient(name="sample-client") //Name can be custom name defined in bootstrap.yml, or the name of a service registered with eureka
public interface SampleClient {
	
	@GetMapping(value="/sample", consumes = "application/json")
	Sample getSample();
}

