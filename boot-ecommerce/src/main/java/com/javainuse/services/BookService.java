package com.javainuse.services;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class BookService {

    @PersistenceContext
    private EntityManager entityManager;

    @Transactional
    public void deleteBookAndRelatedData(Long bookId) {

        //ci sarebbe da fare una logica diversa in base allo stato dell'ordine, ma ci vorebbe del tempo aggiuntivo...

        // Elimina le righe dalla tabella di intersezione OrderBook
        entityManager.createQuery("DELETE FROM OrderBook jt WHERE jt.id.bookId = :bookId")
                .setParameter("bookId", bookId)
                .executeUpdate();

        // Elimina il libro stesso
        entityManager.createQuery("DELETE FROM Book b WHERE b.id = :bookId")
                .setParameter("bookId", bookId)
                .executeUpdate();

        // Elimina gli ordini che non contengono libri
        entityManager.createQuery("DELETE FROM Order o WHERE o.id NOT IN " +
                "(SELECT ob.id.orderId FROM OrderBook ob)")
                .executeUpdate();
    }

    @Transactional
    public void deleteAll() {
        // Elimina tutti i record dalla tabella OrderBook
        entityManager.createQuery("DELETE FROM OrderBook").executeUpdate();

        // Elimina tutti i record dalla tabella Book
        entityManager.createQuery("DELETE FROM Book").executeUpdate();

        // Eliminate all records from the Order table
        entityManager.createQuery("DELETE FROM Order").executeUpdate();

    }

}
