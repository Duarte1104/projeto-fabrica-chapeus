package com.teuprojeto.projeto.controller;

import com.teuprojeto.projeto.entity.TipoFuncionario;
import com.teuprojeto.projeto.service.TipoFuncionarioService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/tipos-funcionario")
public class TipoFuncionarioController {

    private final TipoFuncionarioService tipoFuncionarioService;

    public TipoFuncionarioController(TipoFuncionarioService tipoFuncionarioService) {
        this.tipoFuncionarioService = tipoFuncionarioService;
    }

    @GetMapping
    public List<TipoFuncionario> listarTodos() {
        return tipoFuncionarioService.listarTodos();
    }

    @GetMapping("/{id}")
    public Optional<TipoFuncionario> procurarPorId(@PathVariable Long id) {
        return tipoFuncionarioService.procurarPorId(id);
    }

    @PostMapping
    public TipoFuncionario guardar(@RequestBody TipoFuncionario tipoFuncionario) {
        return tipoFuncionarioService.guardar(tipoFuncionario);
    }

    @DeleteMapping("/{id}")
    public void apagar(@PathVariable Long id) {
        tipoFuncionarioService.apagar(id);
    }
}