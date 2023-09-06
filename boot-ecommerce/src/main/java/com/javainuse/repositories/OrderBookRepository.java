package com.javainuse.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.javainuse.entities.OrderBook;
import com.javainuse.entities.OrderBook.OrderBooksId;

import org.springframework.stereotype.Repository;

@Repository
public interface OrderBookRepository extends JpaRepository<OrderBook, OrderBooksId> {

    List<OrderBook> getOrderBooksByOrderId(Long orderId);

   

    // Quety native
   /*@Modifying
    @Query("DELETE FROM OrderBook jt WHERE jt.id.bookId = :bookId")
    void deleteOrderBooksById_BookId(@Param("bookId") Long bookId);

    @Modifying
    @Query(value = "DELETE FROM order_books", nativeQuery = true)
    void deleteAllRecords();*/

    


    
    // Query native fanno la stessa cosa delle due 2 query sopra
    /* In entrambi i casi, l'annotazione @Modifying indica a Spring Data JPA che questi metodi eseguiranno operazioni di modifica sul database, consentendo a Spring di gestire le transazioni e l'esecuzione delle query in modo appropriato */
    /*@Modifying
    void deleteByBookId(Long bookId);

    @Modifying
    void deleteAll();*/

}
