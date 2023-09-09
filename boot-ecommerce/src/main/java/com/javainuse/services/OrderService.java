package com.javainuse.services;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@EnableScheduling
public class OrderService {

    @PersistenceContext
    private EntityManager entityManager;

    @Transactional
    @Scheduled(fixedRate = 3600000)
    public void hideInactiveOrders() {

        System.out.println("task of delete");
        //1 ora = 10k
        //1 minuto = 100
        entityManager.createQuery("UPDATE Order o SET o.isActive = false WHERE now() > (o.cancelledDate+1680000) AND o.isActive = true AND o.state = 'Cancelled'").executeUpdate();
    }
}
