package com.teuprojeto.projeto.controller;

import com.teuprojeto.projeto.entity.ContaEmpresa;
import com.teuprojeto.projeto.entity.MovimentoFinanceiro;
import com.teuprojeto.projeto.service.FinanceiroService;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/financeiro")
public class FinanceiroController {

    private final FinanceiroService financeiroService;

    public FinanceiroController(FinanceiroService financeiroService) {
        this.financeiroService = financeiroService;
    }

    @GetMapping("/conta")
    public ContaEmpresa obterConta() {
        return financeiroService.obterContaEmpresa();
    }

    @GetMapping("/movimentos")
    public List<MovimentoFinanceiro> listarMovimentos() {
        return financeiroService.listarMovimentos();
    }

    @GetMapping("/movimentos/{tipo}")
    public List<MovimentoFinanceiro> listarPorTipo(@PathVariable String tipo) {
        return financeiroService.listarPorTipo(tipo);
    }

    @PostMapping("/entrada")
    public MovimentoFinanceiro registarEntrada(
            @RequestParam BigDecimal valor,
            @RequestParam String descricao,
            @RequestParam(required = false) String origem
    ) {
        return financeiroService.registarEntrada(valor, descricao, origem);
    }

    @PostMapping("/saida")
    public MovimentoFinanceiro registarSaida(
            @RequestParam BigDecimal valor,
            @RequestParam String descricao,
            @RequestParam(required = false) String origem
    ) {
        return financeiroService.registarSaida(valor, descricao, origem);
    }
}