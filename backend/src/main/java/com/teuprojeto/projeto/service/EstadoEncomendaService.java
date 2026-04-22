package com.teuprojeto.projeto.service;

import com.teuprojeto.projeto.entity.EstadoEncomenda;
import com.teuprojeto.projeto.repository.EstadoEncomendaRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class EstadoEncomendaService {

    private final EstadoEncomendaRepository estadoEncomendaRepository;

    public EstadoEncomendaService(EstadoEncomendaRepository estadoEncomendaRepository) {
        this.estadoEncomendaRepository = estadoEncomendaRepository;
    }

    public List<EstadoEncomenda> listarTodos() {
        return estadoEncomendaRepository.findAll();
    }

    public Optional<EstadoEncomenda> procurarPorId(Long id) {
        return estadoEncomendaRepository.findById(id);
    }

    public EstadoEncomenda guardar(EstadoEncomenda estadoEncomenda) {
        return estadoEncomendaRepository.save(estadoEncomenda);
    }

    public void apagar(Long id) {
        estadoEncomendaRepository.deleteById(id);
    }
}