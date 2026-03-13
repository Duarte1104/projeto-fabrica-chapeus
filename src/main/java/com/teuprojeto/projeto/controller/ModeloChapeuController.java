package com.teuprojeto.projeto.controller;

import com.teuprojeto.projeto.entity.ModeloChapeu;
import com.teuprojeto.projeto.service.ModeloChapeuService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/modelos-chapeu")
public class ModeloChapeuController {

    private final ModeloChapeuService modeloChapeuService;

    public ModeloChapeuController(ModeloChapeuService modeloChapeuService) {
        this.modeloChapeuService = modeloChapeuService;
    }

    @GetMapping
    public List<ModeloChapeu> listarTodos() {
        return modeloChapeuService.listarTodos();
    }

    @PostMapping
    public ModeloChapeu guardar(@RequestBody ModeloChapeu modeloChapeu) {
        return modeloChapeuService.guardar(modeloChapeu);
    }
}