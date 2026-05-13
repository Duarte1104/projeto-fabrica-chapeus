package com.teuprojeto.projeto.controller;

import com.teuprojeto.projeto.entity.Chapeu;
import com.teuprojeto.projeto.service.ChapeuService;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/chapeus")
@CrossOrigin("*")
public class ChapeuController {

    private final ChapeuService chapeuService;

    public ChapeuController(ChapeuService chapeuService) {
        this.chapeuService = chapeuService;
    }

    @GetMapping
    public List<Chapeu> listarTodos() {
        return chapeuService.listarTodos();
    }

    @GetMapping("/{id}")
    public Optional<Chapeu> procurarPorId(@PathVariable Long id) {
        return chapeuService.procurarPorId(id);
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Chapeu criarComImagem(
            @RequestParam String nome,
            @RequestParam BigDecimal preco,
            @RequestParam MultipartFile imagem
    ) {
        return chapeuService.criarComImagem(nome, preco, imagem);
    }

    @DeleteMapping("/{id}")
    public void apagar(@PathVariable Long id) {
        chapeuService.apagar(id);
    }
}