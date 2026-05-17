package com.teuprojeto.projeto.controller;

import com.teuprojeto.projeto.entity.DesignEncomenda;
import com.teuprojeto.projeto.entity.DesignEncomendaImagem;
import com.teuprojeto.projeto.service.DesignEncomendaService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/designs")
@CrossOrigin("*")
public class DesignEncomendaController {

    private final DesignEncomendaService designEncomendaService;

    public DesignEncomendaController(DesignEncomendaService designEncomendaService) {
        this.designEncomendaService = designEncomendaService;
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public DesignEncomenda criarComImagens(
            @RequestParam BigDecimal idEncomenda,
            @RequestParam String descricaoDesigner,
            @RequestParam("imagens") List<MultipartFile> imagens
    ) {
        return designEncomendaService.criarComImagens(idEncomenda, descricaoDesigner, imagens);
    }

    @GetMapping
    public List<DesignEncomenda> listarTodos() {
        return designEncomendaService.listarTodos();
    }

    @GetMapping("/{id}")
    public DesignEncomenda procurarPorId(@PathVariable Long id) {
        return designEncomendaService.procurarPorId(id);
    }

    @GetMapping("/{id}/imagens")
    public List<DesignEncomendaImagem> listarImagens(@PathVariable Long id) {
        return designEncomendaService.listarImagens(id);
    }

    @GetMapping("/encomenda/{idEncomenda}")
    public List<DesignEncomenda> listarPorEncomenda(@PathVariable BigDecimal idEncomenda) {
        return designEncomendaService.listarPorEncomenda(idEncomenda);
    }

    @PatchMapping("/{id}/estado/{novoEstado}")
    public DesignEncomenda mudarEstado(@PathVariable Long id, @PathVariable String novoEstado) {
        return designEncomendaService.mudarEstado(id, novoEstado);
    }

    @PostMapping("/{id}/aprovar")
    public DesignEncomenda aprovar(@PathVariable Long id) {
        return designEncomendaService.mudarEstado(id, "APROVADO_CLIENTE");
    }

    @PostMapping("/{id}/rejeitar")
    public DesignEncomenda rejeitar(@PathVariable Long id) {
        return designEncomendaService.mudarEstado(id, "REJEITADO_CLIENTE");
    }
}