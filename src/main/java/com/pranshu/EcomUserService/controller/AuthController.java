package com.pranshu.EcomUserService.controller;

import com.pranshu.EcomUserService.dto.*;
import com.pranshu.EcomUserService.model.Session;
import com.pranshu.EcomUserService.model.SessionStatus;
import com.pranshu.EcomUserService.model.User;
import com.pranshu.EcomUserService.service.AuthService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/auth")
public class AuthController {
    private AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public ResponseEntity<UserDto> login(@RequestBody LoginRequestDto request) {
        return authService.login(request.getEmail(), request.getPassword());
    }

    @PostMapping("/logout/{id}")
    public ResponseEntity<Void> logout(@PathVariable("id") Long userId, @RequestHeader("token") String token) {
        return authService.logout(token, userId);
    }

    @PostMapping("/signup")
    public ResponseEntity<UserDto> signUp(@RequestBody SignUpRequestDto request) {
        UserDto userDto = authService.signUp(request.getEmail(), request.getPassword());
        return new ResponseEntity<>(userDto, HttpStatus.OK);
    }

    @PostMapping("/validate")
    public ResponseEntity<SessionStatus> validateToken(@RequestBody ValidateTokenRequestDto request) {
        SessionStatus sessionStatus = authService.validate(request.getToken(), request.getUserId());
        return new ResponseEntity<>(sessionStatus, HttpStatus.OK);
    }

    //below APIs are only for learning purposes, should not be present in actual systems
    @GetMapping("/session")
    public ResponseEntity<List<Session>> getAllSession(){
        return authService.getAllSession();
    }

    @GetMapping("/users")
    public ResponseEntity<List<User>> getAllUsers(){
        return authService.getAllUsers();
    }
}

/*
Evolution of Authentication
1. Basic login flow
2. Store password in encoded manner
    a. Imported Spring Security
    b. SpringSecurity Configuration class
    c. bCrypt matcher to compare passwords
<401 Unauthorised in Postman>
3.
 */
