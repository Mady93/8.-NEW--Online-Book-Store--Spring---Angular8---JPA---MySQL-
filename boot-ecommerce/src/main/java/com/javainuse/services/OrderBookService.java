package com.javainuse.services;

import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@Service
public class OrderBookService {

    @PersistenceContext
    private EntityManager entityManager;
    
}