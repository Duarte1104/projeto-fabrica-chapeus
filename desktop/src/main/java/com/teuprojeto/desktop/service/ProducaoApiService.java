package com.teuprojeto.desktop.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.teuprojeto.desktop.api.ApiConfig;
import com.teuprojeto.desktop.dto.AtualizarProducaoEncomendaRequestDto;
import com.teuprojeto.desktop.dto.ProducaoEncomendaDto;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class ProducaoApiService {

    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;

    public ProducaoApiService() {
        this.httpClient = HttpClient.newHttpClient();
        this.objectMapper = new ObjectMapper();
    }

    public ProducaoEncomendaDto procurarPorEncomendaOuNull(Long idEncomenda) {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(ApiConfig.BASE_URL + "/producao/encomenda/" + idEncomenda))
                .GET()
                .header("Accept", "application/json")
                .build();

        try {
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() < 200 || response.statusCode() >= 300) {
                return null;
            }

            return objectMapper.readValue(response.body(), ProducaoEncomendaDto.class);
        } catch (Exception e) {
            return null;
        }
    }

    public ProducaoEncomendaDto atualizar(AtualizarProducaoEncomendaRequestDto dto) {
        try {
            String body = objectMapper.writeValueAsString(dto);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(ApiConfig.BASE_URL + "/producao"))
                    .POST(HttpRequest.BodyPublishers.ofString(body))
                    .header("Content-Type", "application/json")
                    .header("Accept", "application/json")
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() < 200 || response.statusCode() >= 300) {
                throw new RuntimeException("Erro ao atualizar produção. HTTP " + response.statusCode());
            }

            return objectMapper.readValue(response.body(), ProducaoEncomendaDto.class);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Não foi possível atualizar a produção.", e);
        } catch (IOException e) {
            throw new RuntimeException("Não foi possível atualizar a produção.", e);
        }
    }
}