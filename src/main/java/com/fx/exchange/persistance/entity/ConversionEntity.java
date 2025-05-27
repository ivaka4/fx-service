package com.fx.exchange.persistance.entity;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Data
@Table(name = "conversion")
public class ConversionEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @JdbcTypeCode(SqlTypes.VARCHAR)
    @Column(name = "transaction_id", nullable = false, unique = true, length = 36)
    private UUID transactionId;

    @Column(name = "from_currency", nullable = false, length = 3)
    private String fromCurrency;

    @Column(name = "to_currency", nullable = false, length = 3)
    private String toCurrency;

    @Column(name = "rate_precision", nullable = false)
    private BigDecimal ratePrecision;

    @Column(name = "amount", nullable = false, precision = 19, scale = 6)
    private BigDecimal amount;

    @Column(name = "converted_amount", nullable = false, precision = 19, scale = 6)
    private BigDecimal convertedAmount;

    @Column(name = "converted_at", nullable = false)
    private OffsetDateTime convertedAt;
}
