package com.elearning.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.elearning.entity.CourseMaterial;

public interface CourseMaterialRepository extends JpaRepository<CourseMaterial, Long> {

	List<CourseMaterial> findAllByCourse_id(long id);
	
}
