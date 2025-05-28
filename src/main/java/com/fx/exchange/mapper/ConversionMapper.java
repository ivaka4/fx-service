package com.fx.exchange.mapper;

import com.fx.exchange.model.ConversionRequest;
import com.fx.exchange.model.ConversionResponse;
import com.fx.exchange.persistance.entity.ConversionEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.math.BigDecimal;
import java.util.List;

@Mapper(componentModel = "spring")
public interface ConversionMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "fromCurrency", source = "request.from")
    @Mapping(target = "toCurrency", source = "request.to")
    @Mapping(target = "transactionId", expression = "java(java.util.UUID.randomUUID())")
    @Mapping(target = "ratePrecision", source = "rate")
    @Mapping(target = "convertedAmount", source = "converted")
    @Mapping(target = "convertedAt", expression = "java(java.time.OffsetDateTime.now())")
    ConversionEntity toConversionEntity(ConversionRequest request,
                              BigDecimal rate,
                              BigDecimal converted);

    @Mapping(target = "timestamp", source = "convertedAt")
    @Mapping(target = "from", source = "entity.fromCurrency")
    @Mapping(target = "to", source = "entity.toCurrency")
    ConversionResponse toConversionResponse(ConversionEntity entity);

    List<ConversionResponse> toConversionResponseList(List<ConversionEntity> entities);
}