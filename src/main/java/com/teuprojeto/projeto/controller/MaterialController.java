package com.teuprojeto.projeto.controller;

import com.teuprojeto.projeto.dto.gestor.CriarMaterialRequest;
import com.teuprojeto.projeto.entity.Material;
import com.teuprojeto.projeto.service.MaterialService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/materiais")
public class MaterialController {

    private final MaterialService materialService;

    public MaterialController(MaterialService materialService) {
        this.materialService = materialService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Material criar(@RequestBody CriarMaterialRequest request) {
        return materialService.criar(request);
    }

    @GetMapping
    public List<Material> listarTodos() {
        return materialService.listarTodos();
    }

    @GetMapping("/{id}")
    public Material procurarPorId(@PathVariable Long id) {
        return materialService.procurarPorId(id);
    }

    @PatchMapping("/{id}/stock")
    public Material atualizarStock(@PathVariable Long id, @RequestParam BigDecimal novoStock) {
        return materialService.atualizarStock(id, novoStock);
    }

    @GetMapping("/abaixo-minimo")
    public List<Material> listarAbaixoMinimo() {
        return materialService.listarAbaixoMinimo();
    }
}