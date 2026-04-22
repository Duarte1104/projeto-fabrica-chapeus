package com.teuprojeto.projeto.service;

import com.teuprojeto.projeto.entity.ContaEmpresa;
import com.teuprojeto.projeto.entity.MovimentoFinanceiro;
import com.teuprojeto.projeto.repository.ContaEmpresaRepository;
import com.teuprojeto.projeto.repository.MovimentoFinanceiroRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class FinanceiroService {

    private final ContaEmpresaRepository contaEmpresaRepository;
    private final MovimentoFinanceiroRepository movimentoFinanceiroRepository;

    public FinanceiroService(
            ContaEmpresaRepository contaEmpresaRepository,
            MovimentoFinanceiroRepository movimentoFinanceiroRepository
    ) {
        this.contaEmpresaRepository = contaEmpresaRepository;
        this.movimentoFinanceiroRepository = movimentoFinanceiroRepository;
    }

    public ContaEmpresa obterContaEmpresa() {
        return contaEmpresaRepository.findAll().stream()
                .findFirst()
                .orElseGet(() -> {
                    ContaEmpresa conta = new ContaEmpresa();
                    conta.setSaldoAtual(BigDecimal.ZERO);
                    return contaEmpresaRepository.save(conta);
                });
    }

    public List<MovimentoFinanceiro> listarMovimentos() {
        return movimentoFinanceiroRepository.findAll();
    }

    public List<MovimentoFinanceiro> listarPorTipo(String tipo) {
        return movimentoFinanceiroRepository.findByTipo(tipo);
    }

    @Transactional
    public MovimentoFinanceiro registarEntrada(BigDecimal valor, String descricao, String origem) {
        ContaEmpresa conta = obterContaEmpresa();

        conta.setSaldoAtual(conta.getSaldoAtual().add(valor));
        contaEmpresaRepository.save(conta);

        MovimentoFinanceiro movimento = new MovimentoFinanceiro();
        movimento.setTipo("ENTRADA");
        movimento.setValor(valor);
        movimento.setDescricao(descricao);
        movimento.setOrigem(origem);
        movimento.setData(LocalDateTime.now());

        return movimentoFinanceiroRepository.save(movimento);
    }

    @Transactional
    public MovimentoFinanceiro registarSaida(BigDecimal valor, String descricao, String origem) {
        ContaEmpresa conta = obterContaEmpresa();

        conta.setSaldoAtual(conta.getSaldoAtual().subtract(valor));
        contaEmpresaRepository.save(conta);

        MovimentoFinanceiro movimento = new MovimentoFinanceiro();
        movimento.setTipo("SAIDA");
        movimento.setValor(valor);
        movimento.setDescricao(descricao);
        movimento.setOrigem(origem);
        movimento.setData(LocalDateTime.now());

        return movimentoFinanceiroRepository.save(movimento);
    }
}