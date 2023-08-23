package com.javainuse.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.javainuse.entities.Book;
import com.javainuse.entities.Order;
import com.javainuse.entities.User;

import java.util.List;


import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;


import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

    Page<Order> findOrdersByUserId(Long userId, Pageable pageable);

    long countOrdersByUserId(Long userId);

    List<Order> findOrdersByUser(User user);

    List<Book> getOrderBooksById(Long orderId);

    long countOrders();

    /*
    @Transactional
    default void deleteAllOrders() {
        EntityManager em = getEntityManager();
        em.createQuery("DELETE FROM Orders").executeUpdate();
    }

    @PersistenceContext
    EntityManager getEntityManager();
    */
    

}
