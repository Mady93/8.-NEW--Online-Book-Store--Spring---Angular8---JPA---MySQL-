package com.javainuse.repositories;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.javainuse.entities.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
	
	boolean existsByEmail(String email);
    User findUserByEmail(String email);
    User findUserByType(String code);
    long countByType(String type);

    // aggiunto
    long countByNotDeleted();
    Page<User> findByNotDeleted(Pageable pageable);
    List<User> getUsersByRole(String role);

   
}