package com.bs.odontograma.auth.mapper;

import com.bs.odontograma.auth.dto.RegisterRequest;
import com.bs.odontograma.auth.dto.UserResponse;
import com.bs.odontograma.auth.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserMapper {

    UserResponse toResponse(User user);

    /**
     * Maps RegisterRequest to User entity.
     * Only maps User-owned fields, not BaseEntity fields.
     */
    @Mapping(target = "passwordHash", ignore = true)
    @Mapping(target = "active", ignore = true)
    User toEntity(RegisterRequest request);
}
