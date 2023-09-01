package com.javainuse.services;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class BookService {

    @PersistenceContext
    private EntityManager entityManager;

    @Transactional
    public void deleteAllBooks() {
        entityManager.createQuery("UPDATE Book SET isActive = false").executeUpdate();
    }

}
