package com.javainuse.services;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.javainuse.entities.OrderBook;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

@Service
public class OrderBookService {

    @PersistenceContext
    private EntityManager entityManager;

   /* @Transactional
    public List<OrderBook> traceDeletedAndNotByUserId(Long userId) {
        TypedQuery<OrderBook> query = entityManager.createQuery(
                "SELECT ob " +
                        "FROM OrderBook ob " +
                        "JOIN ob.order o " +
                        "WHERE (o.state = 'Send' AND o.deleted = true) OR o.state = 'Working' " +
                        "AND o.userId = :userId",
                OrderBook.class);
        
        query.setParameter("userId", userId);

        return query.getResultList();
    
    }


    @Transactional
    public long countDeletedAndNotByUserId(Long userId) {
        TypedQuery<Long> query = entityManager.createQuery(
                "SELECT COUNT(ob) " +
                        "FROM OrderBook ob " +
                        "JOIN ob.order o " +
                        "WHERE ((o.state = 'Send' AND o.deleted = true) OR o.state = 'Working') " +
                        "AND o.userId = :userId", Long.class);

        query.setParameter("userId", userId);

        return query.getSingleResult();
    }
 */
}