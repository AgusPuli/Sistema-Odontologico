package com.bs.odontograma.odontogram.repository;

import com.bs.odontograma.odontogram.entity.ToothRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ToothRecordRepository extends JpaRepository<ToothRecord, UUID> {

    List<ToothRecord> findByOdontogramId(UUID odontogramId);

    Optional<ToothRecord> findByOdontogramIdAndFdiNumber(UUID odontogramId, Integer fdiNumber);
}
