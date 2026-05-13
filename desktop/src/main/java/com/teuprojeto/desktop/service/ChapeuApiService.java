package com.teuprojeto.desktop.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.teuprojeto.desktop.api.ApiConfig;
import com.teuprojeto.desktop.dto.ChapeuDto;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class ChapeuApiService {

    private final HttpClient client = HttpClient.newHttpClient();
    private final ObjectMapper mapper = new ObjectMapper();

    public List<ChapeuDto> listar() {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(ApiConfig.BASE_URL + "/chapeus"))
                    .GET()
                    .header("Accept", "application/json")
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() < 200 || response.statusCode() >= 300) {
                throw new RuntimeException("Erro ao carregar chapéus. HTTP " + response.statusCode());
            }

            return mapper.readValue(response.body(), new TypeReference<>() {});
        } catch (Exception e) {
            throw new RuntimeException("Erro ao carregar chapéus.", e);
        }
    }

    public ChapeuDto criar(String nome, String preco, Path imagem) {
        try {
            String boundary = "----FabricaChapeusBoundary" + System.currentTimeMillis();

            HttpRequest.BodyPublisher body = criarMultipartBody(nome, preco, imagem, boundary);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(ApiConfig.BASE_URL + "/chapeus"))
                    .header("Content-Type", "multipart/form-data; boundary=" + boundary)
                    .header("Accept", "application/json")
                    .POST(body)
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() < 200 || response.statusCode() >= 300) {
                throw new RuntimeException(response.body() == null || response.body().isBlank()
                        ? "Erro ao criar chapéu. HTTP " + response.statusCode()
                        : response.body());
            }

            return mapper.readValue(response.body(), ChapeuDto.class);

        } catch (Exception e) {
            throw new RuntimeException("Erro ao criar chapéu.", e);
        }
    }

    private HttpRequest.BodyPublisher criarMultipartBody(
            String nome,
            String preco,
            Path imagem,
            String boundary
    ) throws IOException {

        List<byte[]> partes = new ArrayList<>();

        adicionarCampoTexto(partes, boundary, "nome", nome);
        adicionarCampoTexto(partes, boundary, "preco", preco);

        if (imagem != null) {
            String mimeType = Files.probeContentType(imagem);
            if (mimeType == null) {
                mimeType = "application/octet-stream";
            }

            partes.add((
                    "--" + boundary + "\r\n" +
                            "Content-Disposition: form-data; name=\"imagem\"; filename=\"" + imagem.getFileName() + "\"\r\n" +
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