package com.teuprojeto.projeto.service;

import com.teuprojeto.projeto.dto.producao.CriarGastoMaterialRequest;
import com.teuprojeto.projeto.entity.Encomenda;
import com.teuprojeto.projeto.entity.GastoMaterial;
import com.teuprojeto.projeto.entity.Material;
import com.teuprojeto.projeto.repository.EncomendaRepository;
import com.teuprojeto.projeto.repository.GastoMaterialRepository;
import com.teuprojeto.projeto.repository.MaterialRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
public class GastoMaterialService {

    private final GastoMaterialRepository gastoMaterialRepository;
    private final EncomendaRepository encomendaRepository;
    private final MaterialRepository materialRepository;

    public GastoMaterialService(
            GastoMaterialRepository gastoMaterialRepository,
            EncomendaRepository encomendaRepository,
            MaterialRepository materialRepository
    ) {
        this.gastoMaterialRepository = gastoMaterialRepository;
        this.encomendaRepository = encomendaRepository;
        this.materialRepository = materialRepository;
    }

    @Transactional
    public GastoMaterial criar(CriarGastoMaterialRequest request) {
        Encomenda encomenda = encomendaRepository.findById(request.getIdEncomenda())
                .orElseThrow(() -> new IllegalArgumentException("Encomenda não encontrada."));

        Material material = materialRepository.findById(request.getIdMaterial())
                .orElseThrow(() -> new IllegalArgumentException("Material não encontrado."));

        if (material.getStockAtual().compareTo(request.getQuantidade()) < 0) {
            throw new IllegalArgumentException("Stock insuficiente.");
        }

        GastoMaterial gasto = new GastoMaterial();
        gasto.setIdEncomenda(request.getIdEncomenda());
        gasto.setIdMaterial(request.getIdMaterial());
        gasto.setMaterial(material.getNome());
        gasto.setQuantidade(request.getQuantidade());
        gasto.setObservacoes(request.getObservacoes());

        material.setStockAtual(material.getStockAtual().subtract(request.getQuantidade()));
        materialRepository.save(material);

        return gastoMaterialRepository.save(gasto);
    }

    public List<GastoMaterial> listarPorEncomenda(BigDecimal idEncomenda) {
        return gastoMaterialRepository.findByIdEncomenda(idEncomenda);
    }
}