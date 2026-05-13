package com.teuprojeto.projeto.service;

import com.teuprojeto.projeto.entity.Chapeu;
import com.teuprojeto.projeto.repository.ChapeuRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class ChapeuService {

    private final ChapeuRepository chapeuRepository;

    private static final String UPLOAD_DIR = "uploads/chapeus";
    private static final String BASE_URL = "http://localhost:8080";

    public ChapeuService(ChapeuRepository chapeuRepository) {
        this.chapeuRepository = chapeuRepository;
    }

    public List<Chapeu> listarTodos() {
        return chapeuRepository.findAll();
    }

    public Optional<Chapeu> procurarPorId(Long id) {
        return chapeuRepository.findById(id);
    }

    public Chapeu guardar(Chapeu chapeu) {
        if (chapeu.getCod() == null) {
            Long proximoCodigo = chapeuRepository.obterMaiorCodigo() + 1;
            chapeu.setCod(proximoCodigo);
        }

        return chapeuRepository.save(chapeu);
    }

    public Chapeu criarComImagem(String nome, BigDecimal preco, MultipartFile imagem) {
        if (imagem == null || imagem.isEmpty()) {
            throw new IllegalArgumentException("A imagem do chapéu é obrigatória.");
        }

        try {
            Files.createDirectories(Paths.get(UPLOAD_DIR));

            String nomeOriginal = imagem.getOriginalFilename() == null
                    ? "imagem"
                    : imagem.getOriginalFilename().replaceAll("[^a-zA-Z0-9._-]", "_");

            String nomeFicheiro = UUID.randomUUID() + "_" + nomeOriginal;
            Path caminho = Paths.get(UPLOAD_DIR, nomeFicheiro);

            Files.copy(imagem.getInputStream(), caminho);

            Chapeu chapeu = new Chapeu();
            chapeu.setCod(chapeuRepository.obterMaiorCodigo() + 1);
            chapeu.setNome(nome);
            chapeu.setPrecoactvenda(preco);
            chapeu.setImagemUrl(BASE_URL + "/uploads/chapeus/" + nomeFicheiro);

            return chapeuRepository.save(chapeu);

        } catch (IOException e) {
            throw new RuntimeException("Erro ao guardar imagem do chapéu.", e);
        }
    }

    public void apagar(Long id) {
        chapeuRepository.deleteById(id);
    }
}