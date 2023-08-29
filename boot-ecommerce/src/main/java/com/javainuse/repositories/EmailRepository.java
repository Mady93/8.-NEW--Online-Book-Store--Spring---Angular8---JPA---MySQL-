package com.javainuse.repositories;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.javainuse.entities.Email;

@Repository
public interface EmailRepository extends JpaRepository<Email, Long> {
    List<Email> getEmailByUserId(Long userId);
}
