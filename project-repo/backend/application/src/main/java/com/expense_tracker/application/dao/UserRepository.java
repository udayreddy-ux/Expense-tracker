package com.expense_tracker.application.dao;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.expense_tracker.application.entity.Users;

@Repository
public interface UserRepository extends JpaRepository<Users,Long>{

	Users findByEmail(String email);
	
}
