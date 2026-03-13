package com.teuprojeto.projeto.service;

import com.teuprojeto.projeto.entity.Fatura;
import com.teuprojeto.projeto.repository.FaturaRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class FaturaService {

    private final FaturaRepository faturaRepository;

    public FaturaService(FaturaRepository faturaRepository) {
        this.faturaRepository = faturaRepository;
    }

    public List<Fatura> listarTodos() {
        return faturaRepository.findAll();
    }

    public Optional<Fatura> procurarPorId(Long id) {
        return faturaRepository.findById(id);
    }

    public Fatura guardar(Fatura fatura) {
        return faturaRepository.save(fatura);
    }

    public void apagar(Long id) {
        faturaRepository.deleteById(id);
    }
}