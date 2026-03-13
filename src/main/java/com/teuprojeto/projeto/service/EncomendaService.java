package com.teuprojeto.projeto.service;

import com.teuprojeto.projeto.entity.Encomenda;
import com.teuprojeto.projeto.repository.EncomendaRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
public class EncomendaService {

    private final EncomendaRepository encomendaRepository;

    public EncomendaService(EncomendaRepository encomendaRepository) {
        this.encomendaRepository = encomendaRepository;
    }

    public List<Encomenda> listarTodos() {
        return encomendaRepository.findAll();
    }

    public Optional<Encomenda> procurarPorId(BigDecimal id) {
        return encomendaRepository.findById(id);
    }

    public Encomenda guardar(Encomenda encomenda) {
        return encomendaRepository.save(encomenda);
    }

    public void apagar(BigDecimal id) {
        encomendaRepository.deleteById(id);
    }
}