package com.gazorpazorp.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.gazorpazorp.model.Sample;


public interface SampleRepository extends JpaRepository<Sample, Long> {
	public List<Sample> findByCustomerId(@Param("customerId") Long customerId);
}
