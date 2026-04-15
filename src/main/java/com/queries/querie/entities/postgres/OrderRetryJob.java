package com.queries.querie.entities.postgres;

import java.time.OffsetDateTime;
import java.util.UUID;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.type.SqlTypes;

import com.fasterxml.jackson.databind.JsonNode;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "order_retry_jobs")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderRetryJob {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "entity_id", nullable = false)
    private String entityId;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "payload", nullable = false)
    private JsonNode payload;

    @Column(name = "action", nullable = false)
    private String action;

    @Column(nullable = false)
    private Integer attempt;

    @Column(name = "global_status", nullable = false)
    private String globalStatus;

    @Column(name = "next_run_at", nullable = false)
    private OffsetDateTime nextRunAt;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private OffsetDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private OffsetDateTime updatedAt;

    @PrePersist
    void applyDefaults() {
        if (nextRunAt == null) {
            nextRunAt = OffsetDateTime.now();
        }
        if (globalStatus == null) {
            globalStatus = "PENDING";
        }
        if (attempt == null) {
            attempt = 0;
        }
    }
}