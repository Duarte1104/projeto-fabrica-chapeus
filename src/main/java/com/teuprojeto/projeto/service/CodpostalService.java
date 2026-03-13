package com.teuprojeto.projeto.service;

import com.teuprojeto.projeto.entity.Codpostal;
import com.teuprojeto.projeto.repository.CodpostalRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CodpostalService {

    private final CodpostalRepository codpostalRepository;

    public CodpostalService(CodpostalRepository codpostalRepository) {
        this.codpostalRepository = codpostalRepository;
    }

    public List<Codpostal> listarTodos() {
        return codpostalRepository.findAll();
    }

    public Optional<Codpostal> procurarPorId(String id) {
        return codpostalRepository.findById(id);
    }

    public Codpostal guardar(Codpostal codpostal) {
        return codpostalRepository.save(codpostal);
    }

    public void apagar(String id) {
        codpostalRepository.deleteById(id);
    }
}