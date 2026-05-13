package com.teuprojeto.projeto.service;

import com.teuprojeto.projeto.entity.ChapeuMaterial;
import com.teuprojeto.projeto.repository.ChapeuMaterialRepository;
import com.teuprojeto.projeto.repository.ChapeuRepository;
import com.teuprojeto.projeto.repository.MaterialRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ChapeuMaterialService {

    private final ChapeuMaterialRepository chapeuMaterialRepository;
    private final ChapeuRepository chapeuRepository;
    private final MaterialRepository materialRepository;

    public ChapeuMaterialService(
            ChapeuMaterialRepository chapeuMaterialRepository,
            ChapeuRepository chapeuRepository,
            MaterialRepository materialRepository
    ) {
        this.chapeuMaterialRepository = chapeuMaterialRepository;
        this.chapeuRepository = chapeuRepository;
        this.materialRepository = materialRepository;
    }

    public List<ChapeuMaterial> listarTodos() {
        return chapeuMaterialRepository.findAll();
    }

    public List<ChapeuMaterial> listarPorChapeu(Long idChapeu) {
        return chapeuMaterialRepository.findByIdChapeu(idChapeu);
    }

    public ChapeuMaterial criar(ChapeuMaterial request) {
        validar(request);

        if (!chapeuRepository.existsById(request.getIdChapeu())) {
            throw new IllegalArgumentException("Chapéu não encontrado.");
        }

        if (!materialRepository.existsById(request.getIdMaterial())) {
            throw new IllegalArgumentException("Material não encontrado.");
        }

        return chapeuMaterialRepository.save(request);
    }

    public void apagar(Long id) {
        if (!chapeuMaterialRepository.existsById(id)) {
            throw new IllegalArgumentException("Associação não encontrada.");
        }

        chapeuMaterialRepository.deleteById(id);
    }

    private void validar(ChapeuMaterial request) {
        if (request.getIdChapeu() == null) {
            throw new IllegalArgumentException("O chapéu é obrigatório.");
        }

        if (request.getIdMaterial() == null) {
            throw new IllegalArgumentException("O material é obrigatório.");
        }

        if (request.getQuantidadePorUnidade() == null ||
                request.getQuantidadePorUnidade().doubleValue() <= 0) {
            throw new IllegalArgumentException("A quantidade por unidade deve ser superior a zero.");
        }
    }
}