package com.teuprojeto.desktop.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.teuprojeto.desktop.api.ApiConfig;
import com.teuprojeto.desktop.dto.CriarUtilizadorRequestDto;
import com.teuprojeto.desktop.dto.LoginRequestDto;
import com.teuprojeto.desktop.dto.LoginResponseDto;
import com.teuprojeto.desktop.model.AppUser;
import com.teuprojeto.desktop.model.UserRole;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

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
}