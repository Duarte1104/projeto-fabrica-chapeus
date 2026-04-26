package com.teuprojeto.projeto.controller;

import com.teuprojeto.projeto.dto.auth.AlterarPasswordRequest;
import com.teuprojeto.projeto.dto.auth.AtualizarUtilizadorRequest;
import com.teuprojeto.projeto.dto.auth.CriarUtilizadorRequest;
import com.teuprojeto.projeto.dto.auth.LoginRequest;
import com.teuprojeto.projeto.dto.auth.LoginResponse;
import com.teuprojeto.projeto.dto.auth.UtilizadorResponse;
import com.teuprojeto.projeto.service.AuthService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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

    @PostMapping("/alterar-password")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void alterarPassword(@RequestBody AlterarPasswordRequest request) {
        authService.alterarPassword(request);
    }

    @GetMapping("/utilizadores")
    public List<UtilizadorResponse> listarUtilizadores() {
        return authService.listarUtilizadores();
    }

    @PutMapping("/utilizadores/{id}")
    public UtilizadorResponse atualizarUtilizador(
            @PathVariable Long id,
            @RequestBody AtualizarUtilizadorRequest request
    ) {
        return authService.atualizarUtilizador(id, request);
    }

    @DeleteMapping("/utilizadores/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void apagarUtilizador(@PathVariable Long id) {
        authService.apagarUtilizador(id);
    }
}