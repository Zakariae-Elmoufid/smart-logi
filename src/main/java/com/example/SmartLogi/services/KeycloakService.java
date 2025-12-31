package com.example.SmartLogi.services;

import com.example.SmartLogi.dto.ClientRequestDTO;
import com.example.SmartLogi.dto.ClientResponseDTO;
import com.example.SmartLogi.entities.Client;
import com.example.SmartLogi.enums.UserRole;
import com.example.SmartLogi.mapper.ClientMapper;
import com.example.SmartLogi.repositories.ClientRepository;
import org.keycloak.OAuth2Constants;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.stereotype.Service;

import javax.ws.rs.core.Response;
import java.util.Collections;

@Service
public class KeycloakService {

    private final ClientRepository clientRepository;
    private final ClientMapper clientMapper;

    public KeycloakService(ClientRepository clientRepository,ClientMapper clientMapper) {
        this.clientRepository = clientRepository;
        this.clientMapper =  clientMapper;
    }

    private Keycloak getKeycloakAdminClient() {
        return KeycloakBuilder.builder()
                .serverUrl("http://localhost:8080")
                .realm("master") // admin realm
                .grantType(OAuth2Constants.PASSWORD)
                .clientId("admin-cli")
                .username("admin") // admin username
                .password("admin123") // admin password
                .build();
    }

    public ClientResponseDTO registerClient(ClientRequestDTO dto) {
        Keycloak keycloak = getKeycloakAdminClient();
        RealmResource realmResource = keycloak.realm("logistics-realm");

        //  Créer l'utilisateur Keycloak
        UserRepresentation user = new UserRepresentation();
        user.setUsername(dto.email());
        user.setEmail(dto.email());
        user.setFirstName(dto.firstName());
        user.setLastName(dto.lastName());
        user.setEnabled(true);

        CredentialRepresentation credential = new CredentialRepresentation();
        credential.setTemporary(false);
        credential.setType(CredentialRepresentation.PASSWORD);
        credential.setValue(dto.password());
        user.setCredentials(Collections.singletonList(credential));

        Response response = realmResource.users().create(user);
        if (response.getStatus() != 201) {
            throw new RuntimeException("Failed to create user in Keycloak: " + response.getStatus());
        }

        //  Récupérer l'ID Keycloak créé
        String keycloakId = realmResource
                .users()
                .search(dto.email())
                .get(0)
                .getId();

        //  Sauvegarder dans la DB
        Client client = Client.builder()
                .firstName(dto.firstName())
                .lastName(dto.lastName())
                .email(dto.email())
                .phoneNumber(dto.phoneNumber())
                .role(UserRole.CLIENT)
                .enabled(true)
                .keycloakId(keycloakId) // Assure-toi que Client a ce champ
                .build();

        return clientMapper.toDto(clientRepository.save(client));

    }
}
