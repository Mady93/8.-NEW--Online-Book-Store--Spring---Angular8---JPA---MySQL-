package com.javainuse.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.javainuse.entities.Book;
import com.javainuse.entities.Order;
import com.javainuse.entities.User;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

    //Page<Order> findOrdersByUserId(Long userId, Pageable pageable);

    // da vedere se servono queste 2
    long countOrdersByUserId(Long userId);
    List<Order> findOrdersByUser(User user);


    Page<Order> findByNotDeletedAndByUserId(Long userId, Pageable pageable);

    long countNotDeletedAndByUserId(Long userId);



    

    List<Book> getOrderBooksById(Long orderId);

    List<Order> findByUserId(Long userId);

    Long countTotalOrdersInWorkingState();

    Page<Order> getOrdersInWorkingStateWithDetails(Pageable pageable);

    // aggiunti mo
    Long countTotalOrdersInCancelledState();
    Page<Order> getOrdersInCancelledStatWithDetails(Pageable pageable);

}
