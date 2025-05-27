package com.fx.exchange.persistance.repository;

import com.fx.exchange.persistance.entity.ConversionEntity;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ConversionRepository extends JpaRepository<ConversionEntity, Integer> {
    Optional<ConversionEntity> findByTransactionId(UUID transactionId);

    List<ConversionEntity> findByConvertedAtBetween(LocalDate start, LocalDate end, Pageable pageable);
}
