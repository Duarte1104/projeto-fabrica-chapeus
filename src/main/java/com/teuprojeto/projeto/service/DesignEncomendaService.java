package com.teuprojeto.projeto.service;

import com.teuprojeto.projeto.dto.design.CriarDesignEncomendaRequest;
import com.teuprojeto.projeto.entity.DesignEncomenda;
import com.teuprojeto.projeto.entity.Encomenda;
import com.teuprojeto.projeto.repository.DesignEncomendaRepository;
import com.teuprojeto.projeto.repository.EncomendaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class DesignEncomendaService {

    private final DesignEncomendaRepository designEncomendaRepository;
    private final EncomendaRepository encomendaRepository;

    public DesignEncomendaService(
            DesignEncomendaRepository designEncomendaRepository,
            EncomendaRepository encomendaRepository
    ) {
        this.designEncomendaRepository = designEncomendaRepository;
        this.encomendaRepository = encomendaRepository;
    }

    @Transactional
    public DesignEncomenda criar(CriarDesignEncomendaRequest request) {
        Encomenda encomenda = encomendaRepository.findById(request.getIdEncomenda())
                .orElseThrow(() -> new IllegalArgumentException("Encomenda não encontrada."));

        if (encomenda.getDesign() == null || !encomenda.getDesign()) {
            throw new IllegalArgumentException("Esta encomenda não precisa de design.");
        }

        DesignEncomenda design = new DesignEncomenda();
        design.setIdEncomenda(request.getIdEncomenda());
        design.setDescricaoDesigner(request.getDescricaoDesigner());
        design.setFicheiroDesign(request.getFicheiroDesign());
        design.setEstadoDesign("ENVIADO_CLIENTE");
        design.setDataCriacao(LocalDateTime.now());

        return designEncomendaRepository.save(design);
    }

    public List<DesignEncomenda> listarTodos() {
        return designEncomendaRepository.findAll();
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
            Encomenda encomenda = encomendaRepository.findById(design.getIdEncomenda())
                    .orElseThrow(() -> new IllegalArgumentException("Encomenda não encontrada."));
            encomenda.setIdestado(2L); // PREPARACAO
            encomendaRepository.save(encomenda);
        }

        return designGuardado;
    }
}