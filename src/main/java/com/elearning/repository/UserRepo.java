package com.elearning.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.elearning.entity.User;

public interface UserRepo extends JpaRepository<User, Integer> {

	public User findByEmail(String emaill);

	public User findByVerificationCode(String code);
	
	public List<User> findAllByRole(String role);
	
	@Query("SELECT COUNT(u) FROM User u WHERE u.role = 'ROLE_USER'")
    long countUsersWithUserRole();
}
