package com.javainuse.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.javainuse.entities.Book;

@Repository
public interface BookRepository extends JpaRepository<Book, Long> {
	

}