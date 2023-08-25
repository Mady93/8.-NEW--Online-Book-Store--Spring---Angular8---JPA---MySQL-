package com.javainuse.services;

//import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@Service
public class OrderBookService {

    @PersistenceContext
    private EntityManager entityManager;

   /* @Transactional
    public void deleteByBookId(Long bookId) {
        entityManager.createQuery("DELETE FROM OrderBook jt WHERE jt.id.bookId = :bookId")
            .setParameter("bookId", bookId)
            .executeUpdate();
    }

    

    @Transactional
    public void deleteAll() {
        entityManager.createQuery("DELETE FROM OrderBook").executeUpdate();
    }

    
    @Transactional
    public void deleteByOrderId(Long orderId) {
        entityManager.createQuery("DELETE FROM OrderBook jt WHERE jt.id.orderId = :orderId")
            .setParameter("bookId", orderId)
            .executeUpdate();
    }
    */
}