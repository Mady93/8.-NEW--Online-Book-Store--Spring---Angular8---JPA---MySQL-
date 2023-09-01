package com.javainuse.services;

import java.util.Calendar;
import java.util.Date;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
public class OrderService {

    @PersistenceContext
    private EntityManager entityManager;


    // update automatico per gli ordini cancelled -- isActive = false (hidden)
    public void updateInactiveOrders(Date sevenDaysAgo) {
        String jpql = "UPDATE Order o SET o.isActive = false WHERE o.cancelledDate > :sevenDaysAgo AND o.isActive = true AND o.state IN('Cancelled')";
        entityManager.createQuery(jpql)
                     .setParameter("sevenDaysAgo", sevenDaysAgo)
                     .executeUpdate();
    }

    @Scheduled(cron = "0 0 0 * * ?") // Esegui tutti i giorni a mezzanotte
    public void hideInactiveOrders() {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DAY_OF_MONTH, -7); // Sottrai 7 giorni dalla data corrente
        Date sevenDaysAgo = cal.getTime();
        
        // Aggiorna gli ordini che sono stati cancellati piu di 7 giorni fa
        updateInactiveOrders(sevenDaysAgo);

    }
}


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
