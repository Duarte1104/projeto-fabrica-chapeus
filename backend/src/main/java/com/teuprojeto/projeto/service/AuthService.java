package com.teuprojeto.projeto.service;

import com.teuprojeto.projeto.dto.auth.AlterarPasswordRequest;
import com.teuprojeto.projeto.dto.auth.AtualizarUtilizadorRequest;
import com.teuprojeto.projeto.dto.auth.CriarUtilizadorRequest;
import com.teuprojeto.projeto.dto.auth.LoginRequest;
import com.teuprojeto.projeto.dto.auth.LoginResponse;
import com.teuprojeto.projeto.dto.auth.UtilizadorResponse;
import com.teuprojeto.projeto.entity.Utilizador;
import com.teuprojeto.projeto.repository.UtilizadorRepository;
import org.springframework.stereotype.Service;

import java.util.List;

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

    public void alterarPassword(AlterarPasswordRequest request) {
        Utilizador utilizador = utilizadorRepository.findByEmailIgnoreCase(request.getEmail())
                .orElseThrow(() -> new IllegalArgumentException("Utilizador não encontrado."));

        if (!Boolean.TRUE.equals(utilizador.getAtivo())) {
            throw new IllegalArgumentException("Utilizador inativo.");
        }

        if (request.getPasswordAtual() == null || !utilizador.getPassword().equals(request.getPasswordAtual())) {
            throw new IllegalArgumentException("A palavra-passe atual está incorreta.");
        }

        if (request.getNovaPassword() == null || request.getNovaPassword().isBlank()) {
            throw new IllegalArgumentException("A nova palavra-passe é obrigatória.");
        }

        utilizador.setPassword(request.getNovaPassword());
        utilizadorRepository.save(utilizador);
    }

    public List<UtilizadorResponse> listarUtilizadores() {
        return utilizadorRepository.findByRoleNotIgnoreCaseOrderByIdAsc("ADMIN")
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public UtilizadorResponse atualizarUtilizador(Long id, AtualizarUtilizadorRequest request) {
        Utilizador utilizador = utilizadorRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Utilizador não encontrado."));

        String email = request.getEmail() == null ? "" : request.getEmail().trim();

        if (email.isBlank()) {
            throw new IllegalArgumentException("O email é obrigatório.");
        }

        if (utilizadorRepository.existsByEmailIgnoreCaseAndIdNot(email, id)) {
            throw new IllegalArgumentException("Já existe outro utilizador com esse email.");
        }

        utilizador.setEmail(email);

        if (request.getNovaPassword() != null && !request.getNovaPassword().isBlank()) {
            utilizador.setPassword(request.getNovaPassword());
        }

        Utilizador guardado = utilizadorRepository.save(utilizador);
        return toResponse(guardado);
    }

    public void apagarUtilizador(Long id) {
        Utilizador utilizador = utilizadorRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Utilizador não encontrado."));

        if ("ADMIN".equalsIgnoreCase(utilizador.getRole())) {
            throw new IllegalArgumentException("O utilizador admin não pode ser apagado.");
        }

        utilizadorRepository.delete(utilizador);
    }

    private UtilizadorResponse toResponse(Utilizador utilizador) {
        return new UtilizadorResponse(
                utilizador.getId(),
                utilizador.getEmail(),
                utilizador.getRole(),
                utilizador.getAtivo()
        );
    }
}