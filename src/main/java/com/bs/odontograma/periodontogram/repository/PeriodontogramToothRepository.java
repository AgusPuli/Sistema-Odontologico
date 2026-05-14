package com.bs.odontograma.periodontogram.repository;

import com.bs.odontograma.periodontogram.entity.PeriodontogramTooth;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface PeriodontogramToothRepository extends JpaRepository<PeriodontogramTooth, UUID> {

    Optional<PeriodontogramTooth> findByPeriodontogramIdAndFdiNumber(UUID periodontogramId, Integer fdiNumber);
}
