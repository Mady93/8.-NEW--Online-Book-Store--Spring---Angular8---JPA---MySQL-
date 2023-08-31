package com.javainuse.services;

import java.nio.file.AccessDeniedException;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserService {

    @PersistenceContext
    private EntityManager entityManager;

    @Transactional
    public void deleteUserAndRelatedData(Long userId) throws AccessDeniedException {

        // Conta il numero di utenti con tipo "Admin"
        long adminUserCount = entityManager
                .createQuery("SELECT COUNT(u) FROM User u WHERE u.type = 'Admin'", Long.class)
                .getSingleResult();

        // Verifica se l'utente da eliminare è un admin
        boolean isUserAdmin = entityManager
                .createQuery(
                        "SELECT CASE WHEN u.type = 'Admin' THEN TRUE ELSE FALSE END FROM User u WHERE u.id = :userId",
                        Boolean.class)
                .setParameter("userId", userId)
                .getSingleResult();

        if (isUserAdmin && adminUserCount <= 1) {
            throw new AccessDeniedException("");
        } else {
            // Elimina le righe dalla tabella di intersezione OrderBook
            entityManager.createQuery("DELETE FROM OrderBook jt WHERE jt.id.orderId IN " +
                    "(SELECT o.id FROM Order o WHERE o.user.id = :userId)")
                    .setParameter("userId", userId)
                    .executeUpdate();

            // Elimina gli ordini associati all'utente
            entityManager.createQuery("DELETE FROM Order o WHERE o.user.id = :userId")
                    .setParameter("userId", userId)
                    .executeUpdate();

            // Elimina l'utente stesso
            entityManager.createQuery("DELETE FROM User u WHERE u.id = :userId")
                    .setParameter("userId", userId)
                    .executeUpdate();
        }
    }

    @Transactional
    public void deleteAllExceptAdmin() throws RuntimeException {
        // Ottieni il numero di utenti con type diverso da "Admin"
        long nonAdminUserCount = entityManager
                .createQuery("SELECT COUNT(u) FROM User u WHERE u.type <> 'Admin'", Long.class)
                .getSingleResult();

        if (nonAdminUserCount <= 0) {
            throw new RuntimeException();

        }

        // Ottieni gli ID degli utenti con type "Admin"
        List<Long> adminUserIds = entityManager
                .createQuery("SELECT u.id FROM User u WHERE u.type = 'Admin'", Long.class)
                .getResultList();

        // Elimina tutti i record dalla tabella OrderBook associati agli utenti non
        // admin
        entityManager.createQuery("DELETE FROM OrderBook ob WHERE ob.id.orderId IN " +
                "(SELECT o.id FROM Order o WHERE o.user.id NOT IN :adminUserIds)")
                .setParameter("adminUserIds", adminUserIds)
                .executeUpdate();

        // Elimina tutti i record dalla tabella Order associati agli utenti non admin
        entityManager.createQuery("DELETE FROM Order o WHERE o.user.id NOT IN :adminUserIds")
                .setParameter("adminUserIds", adminUserIds)
                .executeUpdate();

        // Elimina tutti gli utenti eccetto quelli con type "Admin"
        entityManager.createQuery("DELETE FROM User u WHERE u.type <> 'Admin'")
                .executeUpdate();
    }

}
