package com.javainuse.services;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.javainuse.entities.Order;
import com.javainuse.entities.OrderBook;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

@Service
public class OrderBookService {

    @PersistenceContext
    private EntityManager entityManager;
    
}