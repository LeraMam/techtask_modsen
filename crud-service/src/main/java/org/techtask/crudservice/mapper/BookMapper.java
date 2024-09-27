package org.techtask.crudservice.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;
import org.techtask.crudservice.db.entity.BookEntity;
import org.techtask.crudservice.dto.BookDTO;
import org.techtask.crudservice.request.CreateOrUpdateBookRequest;

import java.util.List;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING, unmappedTargetPolicy = ReportingPolicy.ERROR)
public interface BookMapper {

    BookDTO mapEntityToDto(BookEntity book);

    List<BookDTO> mapEntitiesToDtoList(List<BookEntity> entities);

    @Mapping(target = "id", ignore = true)
    BookEntity mapRequestToEntity(CreateOrUpdateBookRequest createOrUpdateBookRequest);

    @Mapping(target = "id", ignore = true)
    BookEntity updateBook(@MappingTarget BookEntity entity, CreateOrUpdateBookRequest request);

}
