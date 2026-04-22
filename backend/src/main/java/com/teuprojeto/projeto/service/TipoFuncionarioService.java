package com.teuprojeto.projeto.service;

import com.teuprojeto.projeto.entity.TipoFuncionario;
import com.teuprojeto.projeto.repository.TipoFuncionarioRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class TipoFuncionarioService {

    private final TipoFuncionarioRepository tipoFuncionarioRepository;

    public TipoFuncionarioService(TipoFuncionarioRepository tipoFuncionarioRepository) {
        this.tipoFuncionarioRepository = tipoFuncionarioRepository;
    }

    public List<TipoFuncionario> listarTodos() {
        return tipoFuncionarioRepository.findAll();
    }

    public Optional<TipoFuncionario> procurarPorId(Long id) {
        return tipoFuncionarioRepository.findById(id);
    }

    public TipoFuncionario guardar(TipoFuncionario tipoFuncionario) {
        return tipoFuncionarioRepository.save(tipoFuncionario);
    }

    public void apagar(Long id) {
        tipoFuncionarioRepository.deleteById(id);
    }
}