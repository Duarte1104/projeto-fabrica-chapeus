package com.teuprojeto.desktop.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.teuprojeto.desktop.api.ApiConfig;
import com.teuprojeto.desktop.dto.DashboardRececionistaResponseDto;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class DashboardApiService {

    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;

    public DashboardApiService() {
        this.httpClient = HttpClient.newHttpClient();
        this.objectMapper = new ObjectMapper();
    }

    public DashboardRececionistaResponseDto obterDashboardRececionista() {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(ApiConfig.BASE_URL + "/dashboard/rececionista"))
                .GET()
                .header("Accept", "application/json")
                .build();

        try {
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() < 200 || response.statusCode() >= 300) {
                throw new RuntimeException("Erro ao obter dashboard da rececionista. HTTP " + response.statusCode());
            }

            return objectMapper.readValue(response.body(), DashboardRececionistaResponseDto.class);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Não foi possível obter o dashboard da rececionista.", e);
        } catch (IOException e) {
            throw new RuntimeException("Não foi possível obter o dashboard da rececionista.", e);
        }
    }
}