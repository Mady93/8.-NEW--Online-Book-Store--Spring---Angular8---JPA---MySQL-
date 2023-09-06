package com.javainuse.repositories;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.javainuse.entities.Book;

@Repository
public interface BookRepository extends JpaRepository<Book, Long> {

    Page<Book> findByNotDeleted(Pageable pageable);

    long countNotDeleted();

    List<Book> findByNameContaining(String name);

    // get discount books
    long countNotDeletedAndDiscountTrue();
    Page<Book> findByNotDeletedAndDiscountTrue(Pageable pageable);
}