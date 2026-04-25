package com.teuprojeto.projeto.controller;

import com.teuprojeto.projeto.dto.auth.CriarUtilizadorRequest;
import com.teuprojeto.projeto.dto.auth.LoginRequest;
import com.teuprojeto.projeto.dto.auth.LoginResponse;
import com.teuprojeto.projeto.service.AuthService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public LoginResponse login(@RequestBody LoginRequest request) {
        return authService.login(request);
    }

    @PostMapping("/utilizadores")
    @ResponseStatus(HttpStatus.CREATED)
    public LoginResponse criar(@RequestBody CriarUtilizadorRequest request) {
        return authService.criar(request);
    }
}