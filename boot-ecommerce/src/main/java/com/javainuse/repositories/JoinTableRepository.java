package com.javainuse.repositories;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.javainuse.entities.JoinTable;
import com.javainuse.entities.JoinTable.JoinTableId;
import org.springframework.stereotype.Repository;

@Repository
public interface JoinTableRepository extends JpaRepository<JoinTable, JoinTableId>{

	List<JoinTable> getJoinTablesByOrderId(Long orderId);

//Quety native
	@Modifying
    @Query("DELETE FROM JoinTable jt WHERE jt.id.bookId = :bookId")
    void deleteJoinTablesById_BookId(@Param("bookId") Long bookId);

	@Modifying
    @Query(value = "DELETE FROM join_table", nativeQuery = true)
    void deleteAllRecords();
	}


