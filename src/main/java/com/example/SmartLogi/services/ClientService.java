package com.example.SmartLogi.services;

import com.example.SmartLogi.dto.ClientRequestDTO;
import com.example.SmartLogi.dto.ClientResponseDTO;
import com.example.SmartLogi.entities.Client;
import com.example.SmartLogi.enums.UserRole;
import com.example.SmartLogi.mapper.ClientMapper;
import com.example.SmartLogi.repositories.ClientRepository;
import com.example.SmartLogi.util.PasswordUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;


@Service
public class ClientService {


    @Autowired
    private ClientRepository clientRepository;

    @Autowired
    private  ClientMapper clientMapper;



    public ClientResponseDTO createClient(ClientRequestDTO dto) {
//        Client client = clientMapper.toEntity(dto);
        Client client = Client.builder()
                .firstName(dto.firstName())
                .lastName(dto.lastName())
                .email(dto.email())
                .password(PasswordUtils.hash(dto.password()))
                .phoneNumber(dto.phoneNumber())
                .role(UserRole.CLIENT)
                .createdAt(LocalDateTime.now())
                .active(true)
                .build();
        return clientMapper.toDto(clientRepository.save(client));
    }
}
