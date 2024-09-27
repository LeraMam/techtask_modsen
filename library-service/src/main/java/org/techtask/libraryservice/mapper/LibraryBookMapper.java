package org.techtask.libraryservice.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;
import org.techtask.libraryservice.db.entity.LibraryBookEntity;
import org.techtask.libraryservice.dto.LibraryBookDTO;
import org.techtask.libraryservice.request.UpdateLibraryBookRequest;

import java.util.List;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING, unmappedTargetPolicy = ReportingPolicy.ERROR)
public interface LibraryBookMapper {

    LibraryBookDTO mapEntityToDto(LibraryBookEntity book);

    List<LibraryBookDTO> mapEntitiesToDtoList(List<LibraryBookEntity> entities);

    @Mapping(target = "id", ignore = true)
    LibraryBookEntity mapRequestToEntity(UpdateLibraryBookRequest createOrUpdateBookRequest);

    @Mapping(target = "id", ignore = true)
    LibraryBookEntity updateBook(@MappingTarget LibraryBookEntity entity, UpdateLibraryBookRequest request);
}
