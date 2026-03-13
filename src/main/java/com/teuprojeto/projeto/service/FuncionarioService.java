package com.teuprojeto.projeto.service;

import com.teuprojeto.projeto.entity.Funcionario;
import com.teuprojeto.projeto.repository.FuncionarioRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class FuncionarioService {

    private final FuncionarioRepository funcionarioRepository;

    public FuncionarioService(FuncionarioRepository funcionarioRepository) {
        this.funcionarioRepository = funcionarioRepository;
    }

    public List<Funcionario> listarTodos() {
        return funcionarioRepository.findAll();
    }

    public Optional<Funcionario> procurarPorId(Long id) {
        return funcionarioRepository.findById(id);
    }

    public Funcionario guardar(Funcionario funcionario) {
        return funcionarioRepository.save(funcionario);
    }

    public void apagar(Long id) {
        funcionarioRepository.deleteById(id);
    }
}