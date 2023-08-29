package com.javainuse.services;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.javainuse.entities.Order;;

@Service
public class OrderService {

    @PersistenceContext
    private EntityManager entityManager;
/* 
    @Transactional
    public void updateOrderState(Long orderId) {
        TypedQuery<Order> query = entityManager.createQuery(
            "UPDATE Order o SET o.state = 'Send' WHERE o.id = :orderId AND o.state = 'Working'",
            Order.class);

        query.setParameter("orderId", orderId);

        int updatedCount = query.executeUpdate();

        if (updatedCount > 0) {
            // Successfully updated
        } else {
            // No orders were updated
        }
    }


    */

}
