package com.javainuse.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.javainuse.entities.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
	
	boolean existsByEmail(String email);

	// aggiunti
    User findUserByEmail(String email);
    User findUserByType(String code);

    //void deleteByTypeIn(List<String> types);

    
}