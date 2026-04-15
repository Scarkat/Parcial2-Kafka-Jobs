package com.queries.querie.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.queries.querie.entities.postgres.OrderRetryJob;

@Repository
public interface OrderRetryJobRepository extends JpaRepository<OrderRetryJob, UUID> {
    OrderRetryJob findByEntityIdAndAction(String entityId, String action);
    List<OrderRetryJob> findByGlobalStatus(String globalStatus);
}