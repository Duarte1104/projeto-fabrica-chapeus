package com.teuprojeto.projeto.controller;

import com.teuprojeto.projeto.entity.Fornecedor;
import com.teuprojeto.projeto.service.FornecedorService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/fornecedores")
public class FornecedorController {

    private final FornecedorService fornecedorService;

    public FornecedorController(FornecedorService fornecedorService) {
        this.fornecedorService = fornecedorService;
    }

    @GetMapping
    public List<Fornecedor> listarTodos() {
        return fornecedorService.listarTodos();
    }

    @GetMapping("/{id}")
    public Optional<Fornecedor> procurarPorId(@PathVariable Long id) {
        return fornecedorService.procurarPorId(id);
    }

    @PostMapping
    public Fornecedor guardar(@RequestBody Fornecedor fornecedor) {
        return fornecedorService.guardar(fornecedor);
    }

    @DeleteMapping("/{id}")
    public void apagar(@PathVariable Long id) {
        fornecedorService.apagar(id);
    }
}