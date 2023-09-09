package com.javainuse.services;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;

import org.springframework.stereotype.Service;

@Service
public class DiscountService {

    @PersistenceContext
    private EntityManager entityManager;

    @Transactional
    public void updateDiscountStatus(Long discountId, boolean isActive) {
        String queryString = "UPDATE Discount d SET d.isActive = :isActive WHERE d.id = :discountId";
        entityManager.createQuery(queryString)
            .setParameter("isActive", isActive)
            .setParameter("discountId", discountId)
            .executeUpdate();
    }


    @Transactional
    public void updateAllDiscountsStatus(boolean isActive) {
        String queryString = "UPDATE Discount d SET d.isActive = :isActive";
        entityManager.createQuery(queryString)
            .setParameter("isActive", isActive)
            .executeUpdate();
    }
}
