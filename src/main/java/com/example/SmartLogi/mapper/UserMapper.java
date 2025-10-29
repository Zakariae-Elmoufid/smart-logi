package com.example.SmartLogi.mapper;

import com.example.SmartLogi.dto.ClientRequestDTO;
import com.example.SmartLogi.dto.ClientResponseDTO;
import com.example.SmartLogi.dto.UserRequestDTO;
import com.example.SmartLogi.dto.UserResponseDTO;
import com.example.SmartLogi.entities.Client;
import com.example.SmartLogi.entities.User;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapper {

    UserResponseDTO toDto(User user);
    User toEntity(UserRequestDTO DTO);
}
