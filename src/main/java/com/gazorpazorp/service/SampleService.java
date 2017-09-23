package com.gazorpazorp.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.gazorpazorp.client.SampleClient;
import com.gazorpazorp.model.Sample;
import com.gazorpazorp.repository.SampleRepository;

@Service
public class SampleService {

	@Autowired
	SampleRepository sampleRepo;
	@Autowired
	SampleClient sampleClient;
	
	
	public List<Sample> getAllSamples() {
		return sampleRepo.findAll();
	}

	public Sample getSampleById(Long sampleId) {
		return sampleRepo.findById(sampleId).get();
	}	
	
	public Sample createSample (Sample sample) throws Exception {
		return sampleRepo.save(sample);
	}
}
