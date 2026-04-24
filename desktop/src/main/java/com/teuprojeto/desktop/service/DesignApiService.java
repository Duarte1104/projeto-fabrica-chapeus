package com.teuprojeto.desktop.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.teuprojeto.desktop.api.ApiConfig;
import com.teuprojeto.desktop.dto.CriarDesignEncomendaRequestDto;
import com.teuprojeto.desktop.dto.DesignEncomendaDto;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;

public class DesignApiService {

    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;

    public DesignApiService() {
        this.httpClient = HttpClient.newHttpClient();
        this.objectMapper = new ObjectMapper();
    }

    public List<DesignEncomendaDto> listarTodos() {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(ApiConfig.BASE_URL + "/designs"))
                .GET()
                .header("Accept", "application/json")
                .build();

        try {
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() < 200 || response.statusCode() >= 300) {
                throw new RuntimeException("Erro ao listar designs. HTTP " + response.statusCode());
            }

            return objectMapper.readValue(response.body(), new TypeReference<>() {});
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Não foi possível obter os designs.", e);
        } catch (IOException e) {
            throw new RuntimeException("Não foi possível obter os designs.", e);
        }
    }

    public DesignEncomendaDto criar(CriarDesignEncomendaRequestDto dto) {
        try {
            String body = objectMapper.writeValueAsString(dto);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(ApiConfig.BASE_URL + "/designs"))
                    .POST(HttpRequest.BodyPublishers.ofString(body))
                    .header("Content-Type", "application/json")
                    .header("Accept", "application/json")
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() < 200 || response.statusCode() >= 300) {
                String erro = response.body() == null || response.body().isBlank()
                        ? "Erro ao criar design. HTTP " + response.statusCode()
                        : response.body();
                throw new RuntimeException(erro);
            }

            return objectMapper.readValue(response.body(), DesignEncomendaDto.class);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Não foi possível criar a proposta de design.", e);
        } catch (IOException e) {
            throw new RuntimeException("Não foi possível criar a proposta de design.", e);
        }
    }

    public DesignEncomendaDto mudarEstado(Long id, String novoEstado) {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(ApiConfig.BASE_URL + "/designs/" + id + "/estado/" + novoEstado))
                .method("PATCH", HttpRequest.BodyPublishers.noBody())
                .header("Accept", "application/json")
                .build();

        try {
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() < 200 || response.statusCode() >= 300) {
                String erro = response.body() == null || response.body().isBlank()
                        ? "Erro ao mudar estado do design. HTTP " + response.statusCode()
                        : response.body();
                throw new RuntimeException(erro);
            }

            return objectMapper.readValue(response.body(), DesignEncomendaDto.class);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Não foi possível mudar o estado do design.", e);
        } catch (IOException e) {
            throw new RuntimeException("Não foi possível mudar o estado do design.", e);
        }
    }
}