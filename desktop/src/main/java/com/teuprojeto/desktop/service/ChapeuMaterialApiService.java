package com.teuprojeto.desktop.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.teuprojeto.desktop.api.ApiConfig;
import com.teuprojeto.desktop.dto.ChapeuMaterialDto;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;

public class ChapeuMaterialApiService {

    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;

    public ChapeuMaterialApiService() {
        this.httpClient = HttpClient.newHttpClient();
        this.objectMapper = new ObjectMapper();
    }

    public List<ChapeuMaterialDto> listarPorChapeu(Long idChapeu) {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(ApiConfig.BASE_URL + "/chapeus-materiais/chapeu/" + idChapeu))
                .GET()
                .header("Accept", "application/json")
                .build();

        try {
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() < 200 || response.statusCode() >= 300) {
                throw new RuntimeException("Erro ao obter materiais do chapéu. HTTP " + response.statusCode());
            }

            return objectMapper.readValue(response.body(), new TypeReference<>() {});
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Não foi possível obter os materiais do chapéu.", e);
        } catch (IOException e) {
            throw new RuntimeException("Não foi possível obter os materiais do chapéu.", e);
        }
    }

    public ChapeuMaterialDto criar(ChapeuMaterialDto dto) {
        try {
            String body = objectMapper.writeValueAsString(dto);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(ApiConfig.BASE_URL + "/chapeus-materiais"))
                    .POST(HttpRequest.BodyPublishers.ofString(body))
                    .header("Content-Type", "application/json")
                    .header("Accept", "application/json")
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() < 200 || response.statusCode() >= 300) {
                throw new RuntimeException(response.body() == null || response.body().isBlank()
                        ? "Erro ao associar material. HTTP " + response.statusCode()
                        : response.body());
            }

            return objectMapper.readValue(response.body(), ChapeuMaterialDto.class);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Não foi possível associar o material ao chapéu.", e);
        } catch (IOException e) {
            throw new RuntimeException("Não foi possível associar o material ao chapéu.", e);
        }
    }

    public void apagar(Long id) {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(ApiConfig.BASE_URL + "/chapeus-materiais/" + id))
                .DELETE()
                .header("Accept", "application/json")
                .build();

        try {
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() < 200 || response.statusCode() >= 300) {
                throw new RuntimeException("Erro ao apagar associação. HTTP " + response.statusCode());
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Não foi possível apagar a associação.", e);
        } catch (IOException e) {
            throw new RuntimeException("Não foi possível apagar a associação.", e);
        }
    }
}