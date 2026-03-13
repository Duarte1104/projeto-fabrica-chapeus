package com.teuprojeto.projeto.service;

import com.teuprojeto.projeto.entity.LinhaEncomenda;
import com.teuprojeto.projeto.entity.LinhaEncomendaId;
import com.teuprojeto.projeto.repository.LinhaEncomendaRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class LinhaEncomendaService {

    private final LinhaEncomendaRepository linhaEncomendaRepository;

    public LinhaEncomendaService(LinhaEncomendaRepository linhaEncomendaRepository) {
        this.linhaEncomendaRepository = linhaEncomendaRepository;
    }

    public List<LinhaEncomenda> listarTodos() {
        return linhaEncomendaRepository.findAll();
    }

    public Optional<LinhaEncomenda> procurarPorId(LinhaEncomendaId id) {
        return linhaEncomendaRepository.findById(id);
    }

    public LinhaEncomenda guardar(LinhaEncomenda linhaEncomenda) {
        return linhaEncomendaRepository.save(linhaEncomenda);
    }

    public void apagar(LinhaEncomendaId id) {
        linhaEncomendaRepository.deleteById(id);
    }
}