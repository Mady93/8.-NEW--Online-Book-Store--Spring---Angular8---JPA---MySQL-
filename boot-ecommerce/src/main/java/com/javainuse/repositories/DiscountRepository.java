package com.javainuse.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.javainuse.entities.Discount;

@Repository
public interface DiscountRepository extends JpaRepository<Discount, Long> {

	Page<Discount> findByNotDeleted(Pageable pageable);
	Long countByNotDeleted();
	
	
	

}
