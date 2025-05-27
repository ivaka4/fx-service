package com.fx.exchange.mapper;

import com.fx.exchange.model.ConversionRequest;
import com.fx.exchange.model.ConversionResponse;
import com.fx.exchange.persistance.entity.ConversionEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ConversionMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "convertedAt", expression = "java(java.time.OffsetDateTime.now())")
    ConversionEntity toConversionEntity(ConversionRequest request);

    @Mapping(target = "timestamp", source = "convertedAt")
    ConversionResponse toConversionResponse(ConversionEntity entity);

    List<ConversionResponse> toConversionResponseList(List<ConversionEntity> entities);
}