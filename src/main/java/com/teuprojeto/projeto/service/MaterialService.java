package com.teuprojeto.projeto.service;

import com.teuprojeto.projeto.entity.Material;
import com.teuprojeto.projeto.repository.MaterialRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class MaterialService {

    private final MaterialRepository materialRepository;

    public MaterialService(MaterialRepository materialRepository) {
        this.materialRepository = materialRepository;
    }

    public List<Material> listarTodos() {
        return materialRepository.findAll();
    }

    public Optional<Material> procurarPorId(Long id) {
        return materialRepository.findById(id);
    }

    public Material guardar(Material material) {
        return materialRepository.save(material);
    }

    public void apagar(Long id) {
        materialRepository.deleteById(id);
    }
}