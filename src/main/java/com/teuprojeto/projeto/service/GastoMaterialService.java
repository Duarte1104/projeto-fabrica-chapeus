package com.teuprojeto.projeto.service;

import com.teuprojeto.projeto.dto.producao.CriarGastoMaterialRequest;
import com.teuprojeto.projeto.entity.Encomenda;
import com.teuprojeto.projeto.entity.GastoMaterial;
import com.teuprojeto.projeto.repository.EncomendaRepository;
import com.teuprojeto.projeto.repository.GastoMaterialRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
public class GastoMaterialService {

    private final GastoMaterialRepository gastoMaterialRepository;
    private final EncomendaRepository encomendaRepository;

    public GastoMaterialService(
            GastoMaterialRepository gastoMaterialRepository,
            EncomendaRepository encomendaRepository
    ) {
        this.gastoMaterialRepository = gastoMaterialRepository;
        this.encomendaRepository = encomendaRepository;
    }

    @Transactional
    public GastoMaterial criar(CriarGastoMaterialRequest request) {
        Encomenda encomenda = encomendaRepository.findById(request.getIdEncomenda())
                .orElseThrow(() -> new IllegalArgumentException("Encomenda não encontrada."));

        GastoMaterial gasto = new GastoMaterial();
        gasto.setIdEncomenda(request.getIdEncomenda());
        gasto.setMaterial(request.getMaterial());
        gasto.setQuantidade(request.getQuantidade());
        gasto.setObservacoes(request.getObservacoes());

        return gastoMaterialRepository.save(gasto);
    }

    public List<GastoMaterial> listarPorEncomenda(BigDecimal idEncomenda) {
        return gastoMaterialRepository.findByIdEncomenda(idEncomenda);
    }
}