package com.gazorpazorp.model.dto;

import java.util.Date;

public class SampleMinimalDto {
	private String sample;

	public String getSample() {
		return sample;
	}

	public void setSample(String sample) {
		this.sample = sample;
	}

	@Override
	public String toString() {
		return "SampleMinimalDto [sample=" + sample + "]";
	}
	
	
	
}
