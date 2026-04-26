package com.teuprojeto.desktop.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.teuprojeto.desktop.api.ApiConfig;
import com.teuprojeto.desktop.dto.ContaEmpresaDto;
import com.teuprojeto.desktop.dto.MovimentoFinanceiroDto;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;

public class FinanceiroApiService {

    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;

    public FinanceiroApiService() {
        this.httpClient = HttpClient.newHttpClient();
        this.objectMapper = new ObjectMapper();
    }

    public ContaEmpresaDto obterConta() {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(ApiConfig.BASE_URL + "/financeiro/conta"))
                .GET()
                .header("Accept", "application/json")
                .build();

        try {
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() < 200 || response.statusCode() >= 300) {
                throw new RuntimeException("Erro ao obter conta da empresa. HTTP " + response.statusCode());
            }

            return objectMapper.readValue(response.body(), ContaEmpresaDto.class);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Não foi possível obter a conta da empresa.", e);
        } catch (IOException e) {
            throw new RuntimeException("Não foi possível obter a conta da empresa.", e);
        }
    }

    public List<MovimentoFinanceiroDto> listarMovimentos() {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(ApiConfig.BASE_URL + "/financeiro/movimentos"))
                .GET()
                .header("Accept", "application/json")
                .build();

        try {
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() < 200 || response.statusCode() >= 300) {
                throw new RuntimeException("Erro ao obter movimentos financeiros. HTTP " + response.statusCode());
            }

            return objectMapper.readValue(response.body(), new TypeReference<>() {});
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Não foi possível obter os movimentos financeiros.", e);
        } catch (IOException e) {
            throw new RuntimeException("Não foi possível obter os movimentos financeiros.", e);
        }
    }
}