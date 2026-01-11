package com.example.SmartLogi.services;

import com.example.SmartLogi.dto.PasswordChangeDTO;
import com.example.SmartLogi.dto.UserRequestDTO;
import com.example.SmartLogi.dto.UserResponseDTO;
import com.example.SmartLogi.entities.User;
import com.example.SmartLogi.enums.UserRole;
import com.example.SmartLogi.mapper.UserMapper;
import com.example.SmartLogi.repositories.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import com.example.SmartLogi.util.PasswordUtils;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserMapper userMapper;

    @Mock
    private UserDetailsService userDetailsService;

    @InjectMocks
    private UserService userService;

    private User user;
    private UserResponseDTO responseDTO;

    @BeforeEach
    void setUp() {
        user = User.builder()
                .id(1L)
                .firstName("John")
                .lastName("Doe")
                .email("john.doe@example.com")
                .password("hashedPassword")
                .role(UserRole.ADMIN)
                .enabled(true)
                .build();

        // UserResponseDTO: id, firstName, lastName, email
        responseDTO = new UserResponseDTO(1L, "John", "Doe", "john.doe@example.com");
    }

    @Test
    void findUserByEmailAndByPassword_ShouldReturnUser_WhenCredentialsValid() {
        UserRequestDTO requestDTO = new UserRequestDTO("john.doe@example.com", "password123");

        when(userRepository.findByEmail("john.doe@example.com")).thenReturn(Optional.of(user));
        when(userMapper.toDto(user)).thenReturn(responseDTO);

        try (MockedStatic<PasswordUtils> mockedPasswordUtils = mockStatic(PasswordUtils.class)) {
            mockedPasswordUtils.when(() -> PasswordUtils.verify("password123", "hashedPassword"))
                    .thenReturn(true);

            UserResponseDTO result = userService.findUserByEmailAndByPassword(requestDTO);

            assertNotNull(result);
            assertEquals("john.doe@example.com", result.email());
        }
    }

    @Test
    void findUserByEmailAndByPassword_ShouldThrowException_WhenUserNotFound() {
        UserRequestDTO requestDTO = new UserRequestDTO("unknown@example.com", "password123");

        when(userRepository.findByEmail("unknown@example.com")).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, 
            () -> userService.findUserByEmailAndByPassword(requestDTO));
    }

    @Test
    void findUserByEmailAndByPassword_ShouldThrowException_WhenPasswordInvalid() {
        UserRequestDTO requestDTO = new UserRequestDTO("john.doe@example.com", "wrongPassword");

        when(userRepository.findByEmail("john.doe@example.com")).thenReturn(Optional.of(user));

        try (MockedStatic<PasswordUtils> mockedPasswordUtils = mockStatic(PasswordUtils.class)) {
            mockedPasswordUtils.when(() -> PasswordUtils.verify("wrongPassword", "hashedPassword"))
                    .thenReturn(false);

            assertThrows(RuntimeException.class, 
                () -> userService.findUserByEmailAndByPassword(requestDTO));
        }
    }

    @Test
    void changePassword_ShouldReturnSuccess_WhenPasswordChanged() {
        PasswordChangeDTO dto = new PasswordChangeDTO("oldPassword", "newPassword");

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenReturn(user);

        try (MockedStatic<PasswordUtils> mockedPasswordUtils = mockStatic(PasswordUtils.class)) {
            mockedPasswordUtils.when(() -> PasswordUtils.verify("oldPassword", "hashedPassword"))
                    .thenReturn(true);
            mockedPasswordUtils.when(() -> PasswordUtils.hash("newPassword"))
                    .thenReturn("newHashedPassword");

            String result = userService.changePassword(1L, dto);

            assertEquals("Password updated successfully", result);
            verify(userRepository, times(1)).save(user);
        }
    }

    @Test
    void changePassword_ShouldReturnError_WhenUserNotFound() {
        PasswordChangeDTO dto = new PasswordChangeDTO("oldPassword", "newPassword");

        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        String result = userService.changePassword(99L, dto);

        assertEquals("User not found", result);
        verify(userRepository, never()).save(any());
    }

    @Test
    void changePassword_ShouldReturnError_WhenOldPasswordIncorrect() {
        PasswordChangeDTO dto = new PasswordChangeDTO("wrongOldPassword", "newPassword");

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        try (MockedStatic<PasswordUtils> mockedPasswordUtils = mockStatic(PasswordUtils.class)) {
            mockedPasswordUtils.when(() -> PasswordUtils.verify("wrongOldPassword", "hashedPassword"))
                    .thenReturn(false);

            String result = userService.changePassword(1L, dto);

            assertEquals("Old password is incorrect", result);
            verify(userRepository, never()).save(any());
        }
    }

    @Test
    void loadUserRole_ShouldReturnRole_WhenUserExists() {
        UserDetails userDetails = mock(UserDetails.class);
        List<SimpleGrantedAuthority> authorities = List.of(new SimpleGrantedAuthority("ROLE_ADMIN"));
        doReturn(authorities).when(userDetails).getAuthorities();
        when(userDetailsService.loadUserByUsername("john.doe@example.com")).thenReturn(userDetails);

        String result = userService.loadUserRole("john.doe@example.com");

        assertEquals("ROLE_ADMIN", result);
    }
}
