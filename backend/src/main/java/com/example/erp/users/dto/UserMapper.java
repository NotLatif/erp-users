package com.example.erp.users.dto;

import com.example.erp.users.User;

public class UserMapper {
    public static UserDTO toDTO(User user) {
        return new UserDTO(user.getId(), user.getUsername(), user.getEmail());
    }

    public static User toEntity(UserDTO dto) {
        return new User(dto.getId(), dto.getUsername(), dto.getEmail());
    }
}