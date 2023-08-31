package com.javainuse.repositories;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.javainuse.entities.Book;

@Repository
public interface BookRepository extends JpaRepository<Book, Long> {

    Page<Book> findByNotDeleted(org.springframework.data.domain.Pageable pageable);

    long countNotDeleted();

    List<Book> findByNameContaining(String name);

}