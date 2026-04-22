package com.teuprojeto.projeto.controller;

import com.teuprojeto.projeto.dto.producao.CriarGastoMaterialRequest;
import com.teuprojeto.projeto.entity.GastoMaterial;
import com.teuprojeto.projeto.service.GastoMaterialService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/gastos-material")
public class GastoMaterialController {

    private final GastoMaterialService gastoMaterialService;

    public GastoMaterialController(GastoMaterialService gastoMaterialService) {
        this.gastoMaterialService = gastoMaterialService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public GastoMaterial criar(@RequestBody CriarGastoMaterialRequest request) {
        return gastoMaterialService.criar(request);
    }

    @GetMapping("/encomenda/{idEncomenda}")
    public List<GastoMaterial> listarPorEncomenda(@PathVariable BigDecimal idEncomenda) {
        return gastoMaterialService.listarPorEncomenda(idEncomenda);
    }
}