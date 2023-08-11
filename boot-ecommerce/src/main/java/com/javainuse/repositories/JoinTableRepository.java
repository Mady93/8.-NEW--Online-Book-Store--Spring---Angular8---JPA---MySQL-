package com.javainuse.repositories;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.javainuse.entities.JoinTable;
import com.javainuse.entities.JoinTable.JoinTableId;
import org.springframework.stereotype.Repository;

@Repository
public interface JoinTableRepository extends JpaRepository<JoinTable, JoinTableId>{

	//Long countJoinTablesByBookIdAndOrderId(Long bookId, Long orderId);
	Long countJoinTablesByCompositeId(JoinTable.JoinTableId joinTableId);
	
	Page<JoinTable> findJoinTablesByBookIdAndOrderId(Long bookId, Long orderId, Pageable pageable);
	Page<JoinTable> findJoinTablesByCompositeId(JoinTable.JoinTableId joinTableId, Pageable pageable);
	List<JoinTable> findJoinTablesListByCompositeId(JoinTable.JoinTableId joinTableId);
	}


