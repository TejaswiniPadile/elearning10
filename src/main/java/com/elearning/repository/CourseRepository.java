package com.elearning.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.elearning.entity.Course;

public interface CourseRepository extends JpaRepository<Course, Long>{
	List<Course> findAllById(int id);
}
