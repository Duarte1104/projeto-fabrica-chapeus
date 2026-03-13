package com.teuprojeto.projeto.controller;

import com.teuprojeto.projeto.entity.Material;
import com.teuprojeto.projeto.service.MaterialService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/materiais")
public class MaterialController {

    private final MaterialService materialService;

    public MaterialController(MaterialService materialService) {
        this.materialService = materialService;
    }

    @GetMapping
    public List<Material> listarTodos() {
        return materialService.listarTodos();
    }

    @GetMapping("/{id}")
    public Optional<Material> procurarPorId(@PathVariable Long id) {
        return materialService.procurarPorId(id);
    }

    @PostMapping
    public Material guardar(@RequestBody Material material) {
        return materialService.guardar(material);
    }

    @DeleteMapping("/{id}")
    public void apagar(@PathVariable Long id) {
        materialService.apagar(id);
    }
}