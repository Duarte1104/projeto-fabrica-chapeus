package com.teuprojeto.projeto.controller;

import com.teuprojeto.projeto.entity.Funcionario;
import com.teuprojeto.projeto.service.FuncionarioService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/funcionarios")
public class FuncionarioController {

    private final FuncionarioService funcionarioService;

    public FuncionarioController(FuncionarioService funcionarioService) {
        this.funcionarioService = funcionarioService;
    }

    @GetMapping
    public List<Funcionario> listarTodos() {
        return funcionarioService.listarTodos();
    }

    @GetMapping("/{id}")
    public Optional<Funcionario> procurarPorId(@PathVariable Long id) {
        return funcionarioService.procurarPorId(id);
    }

    @PostMapping
    public Funcionario guardar(@RequestBody Funcionario funcionario) {
        return funcionarioService.guardar(funcionario);
    }

    @DeleteMapping("/{id}")
    public void apagar(@PathVariable Long id) {
        funcionarioService.apagar(id);
    }
}