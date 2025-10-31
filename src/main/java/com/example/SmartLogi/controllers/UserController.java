package com.example.SmartLogi.controllers;


import com.example.SmartLogi.dto.ApiResponse;
import com.example.SmartLogi.dto.PasswordChangeDTO;
import com.example.SmartLogi.services.UserService;
import jakarta.persistence.Access;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService userService;

    @PutMapping("/{id}/change-password")
    public ResponseEntity<ApiResponse> changePassword(
            @PathVariable Long id,
            @Valid @RequestBody PasswordChangeDTO dto) {

        String message = userService.changePassword(id, dto);

        HttpStatus status;
        if (message.equals("Password updated successfully")) {
            status = HttpStatus.OK;
        } else {
            status = HttpStatus.BAD_REQUEST;
        }

        return ResponseEntity.status(status).body(
                ApiResponse.builder()
                        .message(message)
                        .data(null)
                        .status(status.value())
                        .build()
        );
    }
}
