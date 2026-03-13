package com.teuprojeto.projeto.service;

import com.teuprojeto.projeto.entity.Fornecedor;
import com.teuprojeto.projeto.repository.FornecedorRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class FornecedorService {

    private final FornecedorRepository fornecedorRepository;

    public FornecedorService(FornecedorRepository fornecedorRepository) {
        this.fornecedorRepository = fornecedorRepository;
    }

    public List<Fornecedor> listarTodos() {
        return fornecedorRepository.findAll();
    }

    public Optional<Fornecedor> procurarPorId(Long id) {
        return fornecedorRepository.findById(id);
    }

    public Fornecedor guardar(Fornecedor fornecedor) {
        return fornecedorRepository.save(fornecedor);
    }

    public void apagar(Long id) {
        fornecedorRepository.deleteById(id);
    }
}