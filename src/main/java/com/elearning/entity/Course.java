package com.elearning.entity;

import java.util.List;
import java.util.Set;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;

@Entity
public class Course {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private long id;

	private String courseName;

	private long coursePrice;

	@OneToMany(mappedBy = "course", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<CourseMaterial> courseMaterials;

	@OneToMany(mappedBy = "course", cascade = CascadeType.ALL)
    private Set<Payment> payments;
	
	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getCourseName() {
		return courseName;
	}

	public void setCourseName(String courseName) {
		this.courseName = courseName;
	}

	public long getCoursePrice() {
		return coursePrice;
	}

	public void setCoursePrice(long coursePrice) {
		this.coursePrice = coursePrice;
	}

	
	public List<CourseMaterial> getCourseMaterials() {
		return courseMaterials;
	}

	public void setCourseMaterials(List<CourseMaterial> courseMaterials) {
		this.courseMaterials = courseMaterials;
	}

	public Set<Payment> getPayments() {
		return payments;
	}

	public void setPayments(Set<Payment> payments) {
		this.payments = payments;
	}

	@Override
	public String toString() {
		return "Course [id=" + id + ", courseName=" + courseName + ", coursePrice=" + coursePrice + "]";
	}

}
