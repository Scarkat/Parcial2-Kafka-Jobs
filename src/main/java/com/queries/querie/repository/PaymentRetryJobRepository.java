package com.queries.querie.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.queries.querie.entities.postgres.PaymentRetryJob;

@Repository
public interface PaymentRetryJobRepository extends JpaRepository<PaymentRetryJob, UUID> {
    PaymentRetryJob findByEntityIdAndAction(String entityId, String action);
    List<PaymentRetryJob> findByGlobalStatus(String globalStatus);
    
}