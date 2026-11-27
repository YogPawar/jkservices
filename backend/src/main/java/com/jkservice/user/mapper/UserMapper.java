package com.jkservice.user.mapper;

import com.jkservice.user.dto.response.UserResponse;
import com.jkservice.user.entity.UserEntity;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {

  public UserResponse toResponse(UserEntity entity) {

    return new UserResponse(
        entity.getId(),
        entity.getFullName(),
        entity.getEmail(),
        entity.isActive(),
        entity.getCreatedAt(),
        entity.getUpdatedAt());
  }
}
