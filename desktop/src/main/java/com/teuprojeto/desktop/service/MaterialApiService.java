package com.teuprojeto.desktop.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.teuprojeto.desktop.api.ApiConfig;
import com.teuprojeto.desktop.dto.MaterialDto;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class MaterialApiService {

    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;

    public MaterialApiService() {
        this.httpClient = HttpClient.newHttpClient();
        this.objectMapper = new ObjectMapper();
    }

    public List<MaterialDto> listarTodos() {
        return getLista("/materiais", "Não foi possível obter os materiais.");
    }

    public List<MaterialDto> listarAbaixoMinimo() {
        return getLista("/materiais/abaixo-minimo", "Não foi possível obter os materiais abaixo do mínimo.");
    }

    public MaterialDto procurarPorId(Long id) {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(ApiConfig.BASE_URL + "/materiais/" + id))
                .GET()
                .header("Accept", "application/json")
                .build();

        try {
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() < 200 || response.statusCode() >= 300) {
                throw new RuntimeException("Erro ao obter material. HTTP " + response.statusCode());
            }
            return objectMapper.readValue(response.body(), MaterialDto.class);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Não foi possível obter o material.", e);
        } catch (IOException e) {
            throw new RuntimeException("Não foi possível obter o material.", e);
        }
    }

    public MaterialDto criar(MaterialDto dto) {
        try {
            String body = objectMapper.writeValueAsString(dto);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(ApiConfig.BASE_URL + "/materiais"))
                    .POST(HttpRequest.BodyPublishers.ofString(body))
                    .header("Content-Type", "application/json")
                    .header("Accept", "application/json")
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() < 200 || response.statusCode() >= 300) {
                throw new RuntimeException(response.body() == null || response.body().isBlank()
                        ? "Erro ao criar material. HTTP " + response.statusCode()
                        : response.body());
            }

            return objectMapper.readValue(response.body(), MaterialDto.class);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Não foi possível criar o material.", e);
        } catch (IOException e) {
            throw new RuntimeException("Não foi possível criar o material.", e);
        }
    }

    public MaterialDto atualizarStock(Long id, BigDecimal novoStock) {
        String valor = URLEncoder.encode(novoStock.toPlainString(), StandardCharsets.UTF_8);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(ApiConfig.BASE_URL + "/materiais/" + id + "/stock?novoStock=" + valor))
                .method("PATCH", HttpRequest.BodyPublishers.noBody())
                .header("Accept", "application/json")
                .build();

        try {
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() < 200 || response.statusCode() >= 300) {
                throw new RuntimeException(response.body() == null || response.body().isBlank()
                        ? "Erro ao atualizar stock. HTTP " + response.statusCode()
                        : response.body());
            }

            return objectMapper.readValue(response.body(), MaterialDto.class);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Não foi possível atualizar o stock.", e);
        } catch (IOException e) {
            throw new RuntimeException("Não foi possível atualizar o stock.", e);
        }
    }

    private List<MaterialDto> getLista(String path, String errorMessage) {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(ApiConfig.BASE_URL + path))
                .GET()
                .header("Accept", "application/json")
                .build();

        try {
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() < 200 || response.statusCode() >= 300) {
                throw new RuntimeException("Erro HTTP " + response.statusCode());
            }
            return objectMapper.readValue(response.body(), new TypeReference<>() {});
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException(errorMessage, e);
        } catch (IOException e) {
            throw new RuntimeException(errorMessage, e);
        }
    }
}