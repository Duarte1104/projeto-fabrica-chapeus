package com.teuprojeto.projeto.controller;

import com.teuprojeto.projeto.dto.gestor.CriarCompraMaterialRequest;
import com.teuprojeto.projeto.entity.CompraMaterial;
import com.teuprojeto.projeto.service.CompraMaterialService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/compras-material")
public class CompraMaterialController {

    private final CompraMaterialService compraMaterialService;

    public CompraMaterialController(CompraMaterialService compraMaterialService) {
        this.compraMaterialService = compraMaterialService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CompraMaterial criar(@RequestBody CriarCompraMaterialRequest request) {
        return compraMaterialService.criar(request);
    }

    @GetMapping
    public List<CompraMaterial> listarTodas() {
        return compraMaterialService.listarTodas();
    }

    @GetMapping("/material/{idMaterial}")
    public List<CompraMaterial> listarPorMaterial(@PathVariable Long idMaterial) {
        return compraMaterialService.listarPorMaterial(idMaterial);
    }
}