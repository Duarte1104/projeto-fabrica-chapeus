package com.teuprojeto.desktop.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.teuprojeto.desktop.api.ApiConfig;
import com.teuprojeto.desktop.dto.CriarFaturaRequestDto;
import com.teuprojeto.desktop.dto.EncomendaDto;
import com.teuprojeto.desktop.dto.FaturaDto;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;

public class FaturaApiService {

    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;

    public FaturaApiService() {
        this.httpClient = HttpClient.newHttpClient();
        this.objectMapper = new ObjectMapper();
    }

    public List<FaturaDto> listarFaturas() {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(ApiConfig.BASE_URL + "/faturas"))
                .GET()
                .header("Accept", "application/json")
                .build();

        try {
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() < 200 || response.statusCode() >= 300) {
                throw new RuntimeException("Erro ao listar faturas. HTTP " + response.statusCode());
            }

            return objectMapper.readValue(response.body(), new TypeReference<>() {});

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Não foi possível obter as faturas do backend.", e);
        } catch (IOException e) {
            throw new RuntimeException("Não foi possível obter as faturas do backend.", e);
        }
    }

    public List<EncomendaDto> listarEncomendas() {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(ApiConfig.BASE_URL + "/encomendas"))
                .GET()
                .header("Accept", "application/json")
                .build();

        try {
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() < 200 || response.statusCode() >= 300) {
                throw new RuntimeException("Erro ao listar encomendas. HTTP " + response.statusCode());
            }

            return objectMapper.readValue(response.body(), new TypeReference<>() {});

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Não foi possível obter as encomendas do backend.", e);
        } catch (IOException e) {
            throw new RuntimeException("Não foi possível obter as encomendas do backend.", e);
        }
    }

    public FaturaDto criarFatura(CriarFaturaRequestDto dto) {
        try {
            String body = objectMapper.writeValueAsString(dto);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(ApiConfig.BASE_URL + "/faturas"))
                    .POST(HttpRequest.BodyPublishers.ofString(body))
                    .header("Content-Type", "application/json")
                    .header("Accept", "application/json")
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() < 200 || response.statusCode() >= 300) {
                String erro = response.body() == null || response.body().isBlank()
                        ? "Erro ao criar fatura. HTTP " + response.statusCode()
                        : response.body();

                throw new RuntimeException(erro);
            }

            return objectMapper.readValue(response.body(), FaturaDto.class);

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Não foi possível criar a fatura no backend.", e);
        } catch (IOException e) {
            throw new RuntimeException("Não foi possível criar a fatura no backend.", e);
        }
    }
}