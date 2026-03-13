package com.teuprojeto.projeto.service;

import com.teuprojeto.projeto.entity.Chapeu;
import com.teuprojeto.projeto.repository.ChapeuRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ChapeuService {

    private final ChapeuRepository chapeuRepository;

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
        return chapeuRepository.save(chapeu);
    }

    public void apagar(Long id) {
        chapeuRepository.deleteById(id);
    }
}