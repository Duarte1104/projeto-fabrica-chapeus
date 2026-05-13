package com.teuprojeto.projeto.controller;

import com.teuprojeto.projeto.entity.ChapeuMaterial;
import com.teuprojeto.projeto.service.ChapeuMaterialService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/chapeus-materiais")
public class ChapeuMaterialController {

    private final ChapeuMaterialService chapeuMaterialService;

    public ChapeuMaterialController(ChapeuMaterialService chapeuMaterialService) {
        this.chapeuMaterialService = chapeuMaterialService;
    }

    @GetMapping
    public List<ChapeuMaterial> listarTodos() {
        return chapeuMaterialService.listarTodos();
    }

    @GetMapping("/chapeu/{idChapeu}")
    public List<ChapeuMaterial> listarPorChapeu(@PathVariable Long idChapeu) {
        return chapeuMaterialService.listarPorChapeu(idChapeu);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ChapeuMaterial criar(@RequestBody ChapeuMaterial request) {
        return chapeuMaterialService.criar(request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void apagar(@PathVariable Long id) {
        chapeuMaterialService.apagar(id);
    }
}