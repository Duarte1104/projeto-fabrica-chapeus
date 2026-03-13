package com.teuprojeto.projeto.service;

import com.teuprojeto.projeto.entity.ModeloChapeu;
import com.teuprojeto.projeto.entity.ModeloChapeuId;
import com.teuprojeto.projeto.repository.ModeloChapeuRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ModeloChapeuService {

    private final ModeloChapeuRepository modeloChapeuRepository;

    public ModeloChapeuService(ModeloChapeuRepository modeloChapeuRepository) {
        this.modeloChapeuRepository = modeloChapeuRepository;
    }

    public List<ModeloChapeu> listarTodos() {
        return modeloChapeuRepository.findAll();
    }

    public Optional<ModeloChapeu> procurarPorId(ModeloChapeuId id) {
        return modeloChapeuRepository.findById(id);
    }

    public ModeloChapeu guardar(ModeloChapeu modeloChapeu) {
        return modeloChapeuRepository.save(modeloChapeu);
    }

    public void apagar(ModeloChapeuId id) {
        modeloChapeuRepository.deleteById(id);
    }
}