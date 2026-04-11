package com.queries.querie.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.queries.querie.entities.postgres.ProductRetryJob;
import java.util.List;
import java.util.UUID;

@Repository
public interface ProductRetryJobRepository extends JpaRepository<ProductRetryJob, UUID> {
    ProductRetryJob findByEntityIdAndAction(String entityId, String action);
    List<ProductRetryJob> findByGlobalStatus(String globalStatus);
}