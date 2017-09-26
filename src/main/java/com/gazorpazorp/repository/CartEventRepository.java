package com.gazorpazorp.repository;

import java.sql.Timestamp;
import java.util.List;
import java.util.stream.Stream;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.gazorpazorp.model.CartEvent;

@Repository
public interface CartEventRepository extends JpaRepository<CartEvent, Long>{

	
	//get most recent terminal event, and then get all the cart events created after that.
	
	 @Query(value = "SELECT c.*\n" +
	            "FROM (\n" +
	            "       SELECT *\n" +
	            "       FROM cart_event\n" +
	            "       WHERE customer_id = ?1 AND (cart_event_type = 3 OR cart_event_type = 2)\n" +
	            "       ORDER BY cart_event.created_at DESC\n" +
	            "       LIMIT 1\n" +
	            "     ) t\n" +
	            "  RIGHT JOIN cart_event c ON c.customer_id = t.customer_id\n" +
	            "WHERE c.created_at BETWEEN t.created_at AND '20000001-03-26 00:00:01' AND coalesce(t.id, -1) != c.id\n" +
	            "ORDER BY c.created_at ASC", nativeQuery = true)
	 Stream<CartEvent> getCartEventStreamByCustomer(Long customerId);
	 
	 
	 CartEvent findTopByCustomerIdAndCartEventTypeInOrderByCreatedAtDesc(@Param("customerId") Long customerId, @Param("cartEventType")List cartEvents);
	 Stream<CartEvent> findByCustomerIdAndCreatedAtAfterOrderByCreatedAtAsc(@Param("customerId") Long customerId, @Param("createdAt")Timestamp createdAt);
}
