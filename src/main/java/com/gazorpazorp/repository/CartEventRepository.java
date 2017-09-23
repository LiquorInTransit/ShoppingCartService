package com.gazorpazorp.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.gazorpazorp.model.CartEvent;

@Repository
public interface CartEventRepository extends JpaRepository<CartEvent, Long>{

}
