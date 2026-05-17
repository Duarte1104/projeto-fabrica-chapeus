package com.teuprojeto.desktop.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.teuprojeto.desktop.api.ApiConfig;
import com.teuprojeto.desktop.dto.DesignEncomendaDto;
import com.teuprojeto.desktop.dto.DesignEncomendaImagemDto;
import java.io.IOException;
import java.math.BigDecimal;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
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

    public DesignEncomendaDto criar(BigDecimal idEncomenda, String descricaoDesigner, List<Path> imagens) {
        try {
            String boundary = "----DesignBoundary" + System.currentTimeMillis();

            HttpRequest.BodyPublisher body = criarMultipartBody(
                    idEncomenda,
                    descricaoDesigner,
                    imagens,
                    boundary
            );

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(ApiConfig.BASE_URL + "/designs"))
                    .header("Content-Type", "multipart/form-data; boundary=" + boundary)
                    .header("Accept", "application/json")
                    .POST(body)
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

    public List<DesignEncomendaDto> listarPorEncomenda(Long idEncomenda) {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(ApiConfig.BASE_URL + "/designs/encomenda/" + idEncomenda))
                .GET()
                .header("Accept", "application/json")
                .build();

        try {
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() < 200 || response.statusCode() >= 300) {
                throw new RuntimeException("Erro ao listar designs da encomenda. HTTP " + response.statusCode());
            }

            return objectMapper.readValue(response.body(), new TypeReference<>() {});
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Não foi possível obter os designs da encomenda.", e);
        } catch (IOException e) {
            throw new RuntimeException("Não foi possível obter os designs da encomenda.", e);
        }
    }

    public List<DesignEncomendaImagemDto> listarImagens(Long idDesign) {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(ApiConfig.BASE_URL + "/designs/" + idDesign + "/imagens"))
                .GET()
                .header("Accept", "application/json")
                .build();

        try {
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() < 200 || response.statusCode() >= 300) {
                throw new RuntimeException("Erro ao listar imagens do design. HTTP " + response.statusCode());
            }

            return objectMapper.readValue(response.body(), new TypeReference<>() {});
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Não foi possível obter as imagens do design.", e);
        } catch (IOException e) {
            throw new RuntimeException("Não foi possível obter as imagens do design.", e);
        }
    }

    private HttpRequest.BodyPublisher criarMultipartBody(
            BigDecimal idEncomenda,
            String descricaoDesigner,
            List<Path> imagens,
            String boundary
    ) throws IOException {

        List<byte[]> partes = new ArrayList<>();

        adicionarCampoTexto(partes, boundary, "idEncomenda", idEncomenda.toPlainString());
        adicionarCampoTexto(partes, boundary, "descricaoDesigner", descricaoDesigner);

        for (Path imagem : imagens) {
            String mimeType = Files.probeContentType(imagem);
            if (mimeType == null) {
                mimeType = "application/octet-stream";
            }

            partes.add((
                    "--" + boundary + "\r\n" +
                            "Content-Disposition: form-data; name=\"imagens\"; filename=\"" + imagem.getFileName() + "\"\r\n" +
                            "Content-Type: " + mimeType + "\r\n\r\n"
            ).getBytes());

            partes.add(Files.readAllBytes(imagem));
            partes.add("\r\n".getBytes());
        }

        partes.add(("--" + boundary + "--\r\n").getBytes());

        return HttpRequest.BodyPublishers.ofByteArrays(partes);
    }

    private void adicionarCampoTexto(List<byte[]> partes, String boundary, String nomeCampo, String valor) {
        partes.add((
                "--" + boundary + "\r\n" +
                        "Content-Disposition: form-data; name=\"" + nomeCampo + "\"\r\n\r\n" +
                        valor + "\r\n"
        ).getBytes());
    }
}