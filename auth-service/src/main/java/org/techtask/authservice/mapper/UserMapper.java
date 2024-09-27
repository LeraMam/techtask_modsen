package org.techtask.authservice.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;
import org.techtask.authservice.db.entity.UserEntity;
import org.techtask.authservice.dto.UserDTO;
import org.techtask.authservice.request.LoginOrRegisterRequest;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING, unmappedTargetPolicy = ReportingPolicy.ERROR)
public interface UserMapper {
    UserDTO mapEntityToDto(UserEntity user);

    @Mapping(target = "id", ignore = true)
    UserEntity mapRequestToEntity(LoginOrRegisterRequest createOrUpdateBookRequest);
}
