package com.teuprojeto.projeto.service;

import com.teuprojeto.projeto.entity.DesignEncomenda;
import com.teuprojeto.projeto.entity.DesignEncomendaImagem;
import com.teuprojeto.projeto.entity.Encomenda;
import com.teuprojeto.projeto.repository.DesignEncomendaImagemRepository;
import com.teuprojeto.projeto.repository.DesignEncomendaRepository;
import com.teuprojeto.projeto.repository.EncomendaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class DesignEncomendaService {

    private final DesignEncomendaRepository designEncomendaRepository;
    private final DesignEncomendaImagemRepository designEncomendaImagemRepository;
    private final EncomendaRepository encomendaRepository;

    private static final String UPLOAD_DIR = "uploads/designs";
    private static final String BASE_URL = "http://localhost:8080";

    public DesignEncomendaService(
            DesignEncomendaRepository designEncomendaRepository,
            DesignEncomendaImagemRepository designEncomendaImagemRepository,
            EncomendaRepository encomendaRepository
    ) {
        this.designEncomendaRepository = designEncomendaRepository;
        this.designEncomendaImagemRepository = designEncomendaImagemRepository;
        this.encomendaRepository = encomendaRepository;
    }

    @Transactional
    public DesignEncomenda criarComImagens(
            BigDecimal idEncomenda,
            String descricaoDesigner,
            List<MultipartFile> imagens
    ) {
        Encomenda encomenda = encomendaRepository.findById(idEncomenda.longValue())
                .orElseThrow(() -> new IllegalArgumentException("Encomenda não encontrada."));

        if (encomenda.getDesign() == null || !encomenda.getDesign()) {
            throw new IllegalArgumentException("Esta encomenda não precisa de design.");
        }

        if (imagens == null || imagens.isEmpty()) {
            throw new IllegalArgumentException("Adiciona pelo menos uma imagem da proposta.");
        }

        DesignEncomenda design = new DesignEncomenda();
        design.setIdEncomenda(idEncomenda);
        design.setDescricaoDesigner(descricaoDesigner);
        design.setFicheiroDesign(null);
        design.setEstadoDesign("ENVIADO_CLIENTE");
        design.setDataCriacao(LocalDateTime.now());

        DesignEncomenda designGuardado = designEncomendaRepository.save(design);

        guardarImagens(designGuardado.getId(), imagens);

        return designGuardado;
    }

    private void guardarImagens(Long idDesign, List<MultipartFile> imagens) {
        try {
            Files.createDirectories(Paths.get(UPLOAD_DIR));

            for (MultipartFile imagem : imagens) {
                if (imagem == null || imagem.isEmpty()) {
                    continue;
                }

                String nomeOriginal = imagem.getOriginalFilename() == null
                        ? "imagem"
                        : imagem.getOriginalFilename().replaceAll("[^a-zA-Z0-9._-]", "_");

                String nomeFicheiro = UUID.randomUUID() + "_" + nomeOriginal;
                Path caminho = Paths.get(UPLOAD_DIR, nomeFicheiro);

                Files.copy(imagem.getInputStream(), caminho);

                DesignEncomendaImagem designImagem = new DesignEncomendaImagem();
                designImagem.setIdDesignEncomenda(idDesign);
                designImagem.setUrlImagem(BASE_URL + "/uploads/designs/" + nomeFicheiro);

                designEncomendaImagemRepository.save(designImagem);
            }
        } catch (IOException e) {
            throw new RuntimeException("Erro ao guardar imagens do design.", e);
        }
    }

    public List<DesignEncomenda> listarTodos() {
        return designEncomendaRepository.findAll();
    }

    public List<DesignEncomendaImagem> listarImagens(Long idDesign) {
        return designEncomendaImagemRepository.findByIdDesignEncomenda(idDesign);
    }

    public List<DesignEncomenda> listarPorEncomenda(BigDecimal idEncomenda) {
        return designEncomendaRepository.findByIdEncomenda(idEncomenda);
    }

    public List<DesignEncomenda> listarPorEstado(String estadoDesign) {
        return designEncomendaRepository.findByEstadoDesign(estadoDesign);
    }

    public DesignEncomenda procurarPorId(Long id) {
        return designEncomendaRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Design não encontrado."));
    }

    @Transactional
    public DesignEncomenda mudarEstado(Long id, String novoEstado) {
        DesignEncomenda design = designEncomendaRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Design não encontrado."));

        design.setEstadoDesign(novoEstado);
        DesignEncomenda designGuardado = designEncomendaRepository.save(design);

        if ("APROVADO_CLIENTE".equalsIgnoreCase(novoEstado)) {
            Encomenda encomenda = encomendaRepository.findById(design.getIdEncomenda().longValue())
                    .orElseThrow(() -> new IllegalArgumentException("Encomenda não encontrada."));
            encomenda.setIdestado(2L);
            encomendaRepository.save(encomenda);
        }

        return designGuardado;
    }
}