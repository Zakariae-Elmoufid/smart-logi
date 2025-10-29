package com.example.SmartLogi.services;

import com.example.SmartLogi.dto.ClientRequestDTO;
import com.example.SmartLogi.dto.ClientResponseDTO;
import com.example.SmartLogi.entities.Client;
import com.example.SmartLogi.enums.UserRole;
import com.example.SmartLogi.mapper.ClientMapper;
import com.example.SmartLogi.repositories.ClientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ClientService {


    @Autowired
    private ClientRepository clientRepository;

    @Autowired
    private  ClientMapper clientMapper;


    public ClientResponseDTO createClient(ClientRequestDTO dto) {
        Client client = clientMapper.toEntity(dto);

        return clientMapper.toDto(clientRepository.save(client));
    }
}
