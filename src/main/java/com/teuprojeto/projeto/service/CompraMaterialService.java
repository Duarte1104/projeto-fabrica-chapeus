package com.teuprojeto.projeto.service;

import com.teuprojeto.projeto.dto.gestor.CriarCompraMaterialRequest;
import com.teuprojeto.projeto.entity.CompraMaterial;
import com.teuprojeto.projeto.entity.Material;
import com.teuprojeto.projeto.repository.CompraMaterialRepository;
import com.teuprojeto.projeto.repository.MaterialRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class CompraMaterialService {

    private final CompraMaterialRepository compraMaterialRepository;
    private final MaterialRepository materialRepository;
    private final FinanceiroService financeiroService;

    public CompraMaterialService(
            CompraMaterialRepository compraMaterialRepository,
            MaterialRepository materialRepository,
            FinanceiroService financeiroService
    ) {
        this.compraMaterialRepository = compraMaterialRepository;
        this.materialRepository = materialRepository;
        this.financeiroService = financeiroService;
    }

    @Transactional
    public CompraMaterial criar(CriarCompraMaterialRequest request) {
        Material material = materialRepository.findById(request.getIdMaterial())
                .orElseThrow(() -> new IllegalArgumentException("Material não encontrado."));

        CompraMaterial compra = new CompraMaterial();
        compra.setIdMaterial(request.getIdMaterial());
        compra.setQuantidade(request.getQuantidade());
        compra.setCustoTotal(material.getCustoUnitario().multiply(request.getQuantidade()));
        compra.setObservacoes(request.getObservacoes());
        compra.setData(LocalDateTime.now());

        material.setStockAtual(material.getStockAtual().add(request.getQuantidade()));
        materialRepository.save(material);

        financeiroService.registarSaida(
                compra.getCustoTotal(),
                "Compra de material: " + material.getNome(),
                "COMPRA_MATERIAL"
        );

        return compraMaterialRepository.save(compra);
    }

    public List<CompraMaterial> listarTodas() {
        return compraMaterialRepository.findAll();
    }

    public List<CompraMaterial> listarPorMaterial(Long idMaterial) {
        return compraMaterialRepository.findByIdMaterial(idMaterial);
    }
}