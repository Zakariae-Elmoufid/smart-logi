package com.example.SmartLogi.services;


import com.example.SmartLogi.dto.PasswordChangeDTO;
import com.example.SmartLogi.dto.UserRequestDTO;
import com.example.SmartLogi.dto.UserResponseDTO;
import com.example.SmartLogi.entities.User;
import com.example.SmartLogi.mapper.UserMapper;
import com.example.SmartLogi.repositories.UserRepository;
import com.example.SmartLogi.util.PasswordUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserMapper userMapper;

    public UserResponseDTO findUserByEmailAndByPassword(UserRequestDTO dto){
        User user = userRepository.findByEmail(dto.email())
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!PasswordUtils.verify(dto.password(), user.getPassword())) {
            throw new RuntimeException("Invalid password");
        }

        return userMapper.toDto(user);
    }

    public String changePassword(Long id, PasswordChangeDTO dto) {
        Optional<User> optionalUser = userRepository.findById(id);

        if (optionalUser.isEmpty()) {
            return "User not found"; // message personnalisé
        }

        User user = optionalUser.get();

        if (!PasswordUtils.verify(dto.oldPassword(), user.getPassword())) {
            return "Old password is incorrect"; // message personnalisé
        }

        user.setPassword(PasswordUtils.hash(dto.newPassword()));
        userRepository.save(user);

        return "Password updated successfully";
    }

}
