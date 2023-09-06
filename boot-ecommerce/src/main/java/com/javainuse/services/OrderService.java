package com.javainuse.services;

//import java.util.Calendar;
//import java.util.Date;

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


    // update automatico per gli ordini cancelled -- isActive = false (hidden)
   /* public void updateInactiveOrders(Date sevenDaysAgo) {
        String jpql = "UPDATE Order o SET o.isActive = false WHERE o.cancelledDate > :sevenDaysAgo AND o.isActive = true AND o.state = 'Cancelled'";
        entityManager.createQuery(jpql)
                     .setParameter("sevenDaysAgo", sevenDaysAgo)
                     .executeUpdate();
    }*/



    @Transactional
    @Scheduled(fixedRate = 3600000)//cron = "0 0 0 * * ?") // Esegui tutti i giorni a mezzanotte
    public void hideInactiveOrders() {
        /*
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DAY_OF_MONTH, -7); // Sottrai 7 giorni dalla data corrente
        Date sevenDaysAgo = cal.getTime();
        
        // Aggiorna gli ordini che sono stati cancellati piu di 7 giorni fa
        updateInactiveOrders(sevenDaysAgo);
        */

        System.out.println("task of delete");


        //1 ora = 10k
        //1 minuto = 100
        entityManager.createQuery("UPDATE Order o SET o.isActive = false WHERE now() > (o.cancelledDate+1680000) AND o.isActive = true AND o.state = 'Cancelled'").executeUpdate();


        
        //String jpql = "UPDATE Order o SET o.isActive = false WHERE now() > (o.cancelledDate+60) AND o.isActive = true AND o.state = 'Cancelled'";
        //entityManager.createQuery(jpql)
        //             .executeUpdate();

    }
}
