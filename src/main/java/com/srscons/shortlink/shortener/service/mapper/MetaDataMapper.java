package com.srscons.shortlink.shortener.service.mapper;

import com.srscons.shortlink.shortener.repository.entity.MetaDataEntity;
import com.srscons.shortlink.shortener.service.dto.MetaDataDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface MetaDataMapper {

    MetaDataDto fromEntityToDto(MetaDataEntity entity);

    MetaDataEntity fromDtoToEntity(MetaDataDto dto);
} 