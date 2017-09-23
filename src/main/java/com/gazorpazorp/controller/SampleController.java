package com.gazorpazorp.controller;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.gazorpazorp.model.Sample;
import com.gazorpazorp.model.dto.SampleMinimalDto;
import com.gazorpazorp.model.dtoMapper.SampleMapper;
import com.gazorpazorp.service.SampleService;

@RestController
@RequestMapping("/samples")
public class SampleController {
	
	@Autowired
	SampleService sampleService;
	
	@PostMapping
	public ResponseEntity<Sample> createSample (@RequestBody Sample sample) throws Exception {
		return Optional.ofNullable(sampleService.createSample(sample))
				.map(o -> new ResponseEntity<Sample>(o, HttpStatus.OK))
				.orElseThrow(() -> new Exception("Could not create sample!"));
	}
	
	@GetMapping
	public ResponseEntity<List<SampleMinimalDto>> getAll() throws Exception{
		return Optional.ofNullable(sampleService.getAllSamples())
				.map(s -> new ResponseEntity<List<SampleMinimalDto>>
						(s.stream()
						.map(sample -> SampleMapper.INSTANCE.sampleToSampleMinimalDto(sample))
						.collect(Collectors.toList()), HttpStatus.OK)
					)
				.orElseThrow(() -> new Exception("Account does not exist"));
	}
	
	@GetMapping("/{sampleId}")
	public ResponseEntity getSampleById (@PathVariable Long sampleId) throws Exception {
		return Optional.ofNullable(sampleService.getSampleById(sampleId))
				.map(s -> new ResponseEntity<Sample>(s, HttpStatus.OK))
				.orElseThrow(() -> new Exception("An unknown error has occured."));
	}

	
	
}
