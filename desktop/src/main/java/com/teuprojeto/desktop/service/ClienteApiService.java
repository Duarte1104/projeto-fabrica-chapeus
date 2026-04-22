package com.teuprojeto.desktop.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.teuprojeto.desktop.api.ApiConfig;
import com.teuprojeto.desktop.dto.ClienteDto;
import com.teuprojeto.desktop.dto.CriarClienteRequestDto;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;

public class ClienteApiService {

    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;

    public ClienteApiService() {
        this.httpClient = HttpClient.newHttpClient();
        this.objectMapper = new ObjectMapper();
    }

    public List<ClienteDto> listarTodos() {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(ApiConfig.BASE_URL + "/clientes"))
                .GET()
                .header("Accept", "application/json")
                .build();

        try {
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() < 200 || response.statusCode() >= 300) {
                throw new RuntimeException("Erro ao listar clientes. HTTP " + response.statusCode());
            }

            return objectMapper.readValue(response.body(), new TypeReference<>() {
            });

        } catch (IOException | InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Não foi possível obter os clientes do backend.", e);
        }
    }

    public ClienteDto criar(CriarClienteRequestDto dto) {
        try {
            String body = objectMapper.writeValueAsString(dto);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(ApiConfig.BASE_URL + "/clientes"))
                    .POST(HttpRequest.BodyPublishers.ofString(body))
                    .header("Content-Type", "application/json")
                    .header("Accept", "application/json")
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() < 200 || response.statusCode() >= 300) {
                String erro = response.body() == null || response.body().isBlank()
                        ? "Erro ao criar cliente. HTTP " + response.statusCode()
                        : response.body();

                throw new RuntimeException(erro);
            }

            return objectMapper.readValue(response.body(), ClienteDto.class);

        } catch (IOException | InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Não foi possível criar o cliente no backend.", e);
        }
    }
}