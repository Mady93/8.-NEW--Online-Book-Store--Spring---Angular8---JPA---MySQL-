package com.javainuse.services;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.springframework.stereotype.Service;

@Service
public class DiscountService {

    @PersistenceContext
    private EntityManager entityManager;
}
