package com.teuprojeto.projeto.service;

import com.teuprojeto.projeto.entity.Etapa;
import com.teuprojeto.projeto.repository.EtapaRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class EtapaService {

    private final EtapaRepository etapaRepository;

    public EtapaService(EtapaRepository etapaRepository) {
        this.etapaRepository = etapaRepository;
    }

    public List<Etapa> listarTodos() {
        return etapaRepository.findAll();
    }

    public Optional<Etapa> procurarPorId(Long id) {
        return etapaRepository.findById(id);
    }

    public Etapa guardar(Etapa etapa) {
        return etapaRepository.save(etapa);
    }

    public void apagar(Long id) {
        etapaRepository.deleteById(id);
    }
}