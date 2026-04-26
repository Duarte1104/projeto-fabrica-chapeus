package com.teuprojeto.desktop.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.teuprojeto.desktop.api.ApiConfig;
import com.teuprojeto.desktop.dto.AlterarPasswordRequestDto;
import com.teuprojeto.desktop.dto.AtualizarUtilizadorRequestDto;
import com.teuprojeto.desktop.dto.CriarUtilizadorRequestDto;
import com.teuprojeto.desktop.dto.LoginRequestDto;
import com.teuprojeto.desktop.dto.LoginResponseDto;
import com.teuprojeto.desktop.dto.UtilizadorDto;
import com.teuprojeto.desktop.model.AppUser;
import com.teuprojeto.desktop.model.UserRole;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;

public class AuthApiService {

    private final HttpClient httpClient = HttpClient.newHttpClient();
    private final ObjectMapper objectMapper = new ObjectMapper();

    public AppUser login(String email, String password) {
        try {
            String body = objectMapper.writeValueAsString(new LoginRequestDto(email, password));

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(ApiConfig.BASE_URL + "/auth/login"))
                    .POST(HttpRequest.BodyPublishers.ofString(body))
                    .header("Content-Type", "application/json")
                    .header("Accept", "application/json")
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() < 200 || response.statusCode() >= 300) {
                throw new RuntimeException(response.body());
            }

            LoginResponseDto dto = objectMapper.readValue(response.body(), LoginResponseDto.class);
            return new AppUser(dto.getEmail(), password, UserRole.valueOf(dto.getRole()));
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Não foi possível fazer login.", e);
        } catch (IOException e) {
            throw new RuntimeException("Não foi possível fazer login.", e);
        }
    }

    public void criarUtilizador(CriarUtilizadorRequestDto dto) {
        try {
            String body = objectMapper.writeValueAsString(dto);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(ApiConfig.BASE_URL + "/auth/utilizadores"))
                    .POST(HttpRequest.BodyPublishers.ofString(body))
                    .header("Content-Type", "application/json")
                    .header("Accept", "application/json")
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() < 200 || response.statusCode() >= 300) {
                throw new RuntimeException(response.body());
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Não foi possível criar o utilizador.", e);
        } catch (IOException e) {
            throw new RuntimeException("Não foi possível criar o utilizador.", e);
        }
    }

    public void alterarPassword(String email, String passwordAtual, String novaPassword) {
        try {
            AlterarPasswordRequestDto dto = new AlterarPasswordRequestDto();
            dto.setEmail(email);
            dto.setPasswordAtual(passwordAtual);
            dto.setNovaPassword(novaPassword);

            String body = objectMapper.writeValueAsString(dto);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(ApiConfig.BASE_URL + "/auth/alterar-password"))
                    .POST(HttpRequest.BodyPublishers.ofString(body))
                    .header("Content-Type", "application/json")
                    .header("Accept", "application/json")
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() < 200 || response.statusCode() >= 300) {
                throw new RuntimeException(
                        response.body() == null || response.body().isBlank()
                                ? "Erro ao alterar palavra-passe. HTTP " + response.statusCode()
                                : response.body()
                );
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Não foi possível alterar a palavra-passe.", e);
        } catch (IOException e) {
            throw new RuntimeException("Não foi possível alterar a palavra-passe.", e);
        }
    }

    public List<UtilizadorDto> listarUtilizadores() {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(ApiConfig.BASE_URL + "/auth/utilizadores"))
                .GET()
                .header("Accept", "application/json")
                .build();

        try {
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() < 200 || response.statusCode() >= 300) {
                throw new RuntimeException("Erro ao listar utilizadores. HTTP " + response.statusCode());
            }

            return objectMapper.readValue(response.body(), new TypeReference<>() {});
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Não foi possível obter os utilizadores.", e);
        } catch (IOException e) {
            throw new RuntimeException("Não foi possível obter os utilizadores.", e);
        }
    }

    public void atualizarUtilizador(Long id, String email, String novaPassword) {
        try {
            AtualizarUtilizadorRequestDto dto = new AtualizarUtilizadorRequestDto();
            dto.setEmail(email);
            dto.setNovaPassword(novaPassword);

            String body = objectMapper.writeValueAsString(dto);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(ApiConfig.BASE_URL + "/auth/utilizadores/" + id))
                    .PUT(HttpRequest.BodyPublishers.ofString(body))
                    .header("Content-Type", "application/json")
                    .header("Accept", "application/json")
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() < 200 || response.statusCode() >= 300) {
                throw new RuntimeException(
                        response.body() == null || response.body().isBlank()
                                ? "Erro ao atualizar utilizador. HTTP " + response.statusCode()
                                : response.body()
                );
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Não foi possível atualizar o utilizador.", e);
        } catch (IOException e) {
            throw new RuntimeException("Não foi possível atualizar o utilizador.", e);
        }
    }

    public void apagarUtilizador(Long id) {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(ApiConfig.BASE_URL + "/auth/utilizadores/" + id))
                .DELETE()
                .header("Accept", "application/json")
                .build();

        try {
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() < 200 || response.statusCode() >= 300) {
                throw new RuntimeException(
                        response.body() == null || response.body().isBlank()
                                ? "Erro ao apagar utilizador. HTTP " + response.statusCode()
                                : response.body()
                );
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Não foi possível apagar o utilizador.", e);
        } catch (IOException e) {
            throw new RuntimeException("Não foi possível apagar o utilizador.", e);
        }
    }
}