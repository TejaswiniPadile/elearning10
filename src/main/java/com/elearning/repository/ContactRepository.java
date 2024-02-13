package com.elearning.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.elearning.entity.Contact;

public interface ContactRepository extends JpaRepository<Contact, Long> {

}
