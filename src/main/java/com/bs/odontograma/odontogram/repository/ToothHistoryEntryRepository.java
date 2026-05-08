package com.bs.odontograma.odontogram.repository;

import com.bs.odontograma.odontogram.entity.ToothHistoryEntry;
import com.bs.odontograma.odontogram.enums.TreatmentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ToothHistoryEntryRepository extends JpaRepository<ToothHistoryEntry, UUID> {

    List<ToothHistoryEntry> findByToothRecordIdOrderByCreatedAtDesc(UUID toothRecordId);

    /**
     * Full plan-de-tratamiento view: every history entry of every tooth in the odontogram,
     * filtered by treatment status. Used by the front to render the table shown under
     * "Plan de tratamiento" in the patient screen.
     */
    @Query("""
            SELECT h FROM ToothHistoryEntry h
            WHERE h.toothRecord.odontogram.id = :odontogramId
              AND (:status IS NULL OR h.treatmentStatus = :status)
            ORDER BY h.createdAt DESC
            """)
    List<ToothHistoryEntry> findPlanForOdontogram(
            @Param("odontogramId") UUID odontogramId,
            @Param("status") TreatmentStatus status
    );
}
