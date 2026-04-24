package com.teuprojeto.desktop.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.teuprojeto.desktop.api.ApiConfig;
import com.teuprojeto.desktop.dto.ChapeuDto;
import com.teuprojeto.desktop.dto.CriarEncomendaRequestDto;
import com.teuprojeto.desktop.dto.EncomendaDto;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;

public class EncomendaApiService {

    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;

    public EncomendaApiService() {
        this.httpClient = HttpClient.newHttpClient();
        this.objectMapper = new ObjectMapper();
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

    public List<ChapeuDto> listarChapeus() {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(ApiConfig.BASE_URL + "/chapeus"))
                .GET()
                .header("Accept", "application/json")
                .build();

        try {
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() < 200 || response.statusCode() >= 300) {
                throw new RuntimeException("Erro ao listar chapéus. HTTP " + response.statusCode());
            }

            return objectMapper.readValue(response.body(), new TypeReference<>() {});
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Não foi possível obter os chapéus do backend.", e);
        } catch (IOException e) {
            throw new RuntimeException("Não foi possível obter os chapéus do backend.", e);
        }
    }

    public EncomendaDto criar(CriarEncomendaRequestDto dto) {
        try {
            String body = objectMapper.writeValueAsString(dto);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(ApiConfig.BASE_URL + "/encomendas"))
                    .POST(HttpRequest.BodyPublishers.ofString(body))
                    .header("Content-Type", "application/json")
                    .header("Accept", "application/json")
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() < 200 || response.statusCode() >= 300) {
                String erro = response.body() == null || response.body().isBlank()
                        ? "Erro ao criar encomenda. HTTP " + response.statusCode()
                        : response.body();

                throw new RuntimeException(erro);
            }

            return objectMapper.readValue(response.body(), EncomendaDto.class);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Não foi possível criar a encomenda no backend.", e);
        } catch (IOException e) {
            throw new RuntimeException("Não foi possível criar a encomenda no backend.", e);
        }
    }
}