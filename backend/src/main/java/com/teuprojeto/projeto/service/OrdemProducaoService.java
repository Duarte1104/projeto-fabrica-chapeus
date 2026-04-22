package com.teuprojeto.projeto.service;

import com.teuprojeto.projeto.entity.OrdemProducao;
import com.teuprojeto.projeto.repository.OrdemProducaoRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class OrdemProducaoService {

    private final OrdemProducaoRepository ordemProducaoRepository;

    public OrdemProducaoService(OrdemProducaoRepository ordemProducaoRepository) {
        this.ordemProducaoRepository = ordemProducaoRepository;
    }

    public List<OrdemProducao> listarTodos() {
        return ordemProducaoRepository.findAll();
    }

    public Optional<OrdemProducao> procurarPorId(Long id) {
        return ordemProducaoRepository.findById(id);
    }

    public OrdemProducao guardar(OrdemProducao ordemProducao) {
        return ordemProducaoRepository.save(ordemProducao);
    }

    public void apagar(Long id) {
        ordemProducaoRepository.deleteById(id);
    }
}