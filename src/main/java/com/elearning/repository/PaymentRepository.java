package com.elearning.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.elearning.entity.Payment;
import com.elearning.entity.User;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
	List<Payment> findAllByUser(User user);
	
	long count();
	
	@Query("SELECT SUM(p.course.coursePrice) FROM Payment p")
	Long calculateTotalRevenue();
}
