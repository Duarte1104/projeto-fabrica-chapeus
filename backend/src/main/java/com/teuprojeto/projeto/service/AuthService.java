package com.teuprojeto.projeto.service;

import com.teuprojeto.projeto.dto.auth.CriarUtilizadorRequest;
import com.teuprojeto.projeto.dto.auth.LoginRequest;
import com.teuprojeto.projeto.dto.auth.LoginResponse;
import com.teuprojeto.projeto.entity.Utilizador;
import com.teuprojeto.projeto.repository.UtilizadorRepository;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final UtilizadorRepository utilizadorRepository;

    public AuthService(UtilizadorRepository utilizadorRepository) {
        this.utilizadorRepository = utilizadorRepository;
    }

    public LoginResponse login(LoginRequest request) {
        Utilizador utilizador = utilizadorRepository.findByEmailIgnoreCase(request.getEmail())
                .orElseThrow(() -> new IllegalArgumentException("Credenciais inválidas."));

        if (!Boolean.TRUE.equals(utilizador.getAtivo())) {
            throw new IllegalArgumentException("Utilizador inativo.");
        }

        if (!utilizador.getPassword().equals(request.getPassword())) {
            throw new IllegalArgumentException("Credenciais inválidas.");
        }

        return new LoginResponse(utilizador.getId(), utilizador.getEmail(), utilizador.getRole());
    }

    public LoginResponse criar(CriarUtilizadorRequest request) {
        if (utilizadorRepository.existsByEmailIgnoreCase(request.getEmail())) {
            throw new IllegalArgumentException("Já existe um utilizador com esse email.");
        }

        Utilizador utilizador = new Utilizador();
        utilizador.setEmail(request.getEmail().trim());
        utilizador.setPassword(request.getPassword());
        utilizador.setRole(request.getRole().trim().toUpperCase());
        utilizador.setAtivo(true);

        Utilizador guardado = utilizadorRepository.save(utilizador);
        return new LoginResponse(guardado.getId(), guardado.getEmail(), guardado.getRole());
    }
}