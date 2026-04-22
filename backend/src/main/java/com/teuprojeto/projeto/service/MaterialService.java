package com.teuprojeto.projeto.service;

import com.teuprojeto.projeto.dto.gestor.CriarMaterialRequest;
import com.teuprojeto.projeto.entity.Material;
import com.teuprojeto.projeto.repository.MaterialRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
public class MaterialService {

    private final MaterialRepository materialRepository;

    public MaterialService(MaterialRepository materialRepository) {
        this.materialRepository = materialRepository;
    }

    public Material criar(CriarMaterialRequest request) {
        Material material = new Material();
        material.setNome(request.getNome());
        material.setStockAtual(request.getStockAtual());
        material.setStockMinimo(request.getStockMinimo());
        material.setUnidade(request.getUnidade());
        material.setCustoUnitario(request.getCustoUnitario());

        return materialRepository.save(material);
    }

    public List<Material> listarTodos() {
        return materialRepository.findAll();
    }

    public Material procurarPorId(Long id) {
        return materialRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Material não encontrado."));
    }

    public Material atualizarStock(Long id, BigDecimal novoStock) {
        Material material = materialRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Material não encontrado."));

        material.setStockAtual(novoStock);
        return materialRepository.save(material);
    }

    public List<Material> listarAbaixoMinimo() {
        return materialRepository.findAll().stream()
                .filter(m -> m.getStockAtual().compareTo(m.getStockMinimo()) < 0)
                .toList();
    }
}