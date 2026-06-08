package com.teuprojeto.projeto.service;

import com.teuprojeto.projeto.dto.rececionista.CriarPagamentoRequest;
import com.teuprojeto.projeto.entity.Encomenda;
import com.teuprojeto.projeto.entity.Fatura;
import com.teuprojeto.projeto.entity.Pagamento;
import com.teuprojeto.projeto.repository.EncomendaRepository;
import com.teuprojeto.projeto.repository.FaturaRepository;
import com.teuprojeto.projeto.repository.PagamentoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class PagamentoService {

    private final PagamentoRepository pagamentoRepository;
    private final FaturaRepository faturaRepository;
    private final EncomendaRepository encomendaRepository;
    private final FinanceiroService financeiroService;

    public PagamentoService(
            PagamentoRepository pagamentoRepository,
            FaturaRepository faturaRepository,
            EncomendaRepository encomendaRepository,
            FinanceiroService financeiroService
    ) {
        this.pagamentoRepository = pagamentoRepository;
        this.faturaRepository = faturaRepository;
        this.encomendaRepository = encomendaRepository;
        this.financeiroService = financeiroService;
    }

    public List<Pagamento> listarTodos() {
        return pagamentoRepository.findAll();
    }

    public List<Pagamento> listarPorFatura(Long idFatura) {
        return pagamentoRepository.findByNumfatura(idFatura);
    }

    public List<Pagamento> listarPorEncomenda(BigDecimal idEncomenda) {
        return pagamentoRepository.findByIdencomenda(idEncomenda);
    }

    public Pagamento procurarPorId(Long id) {
        return pagamentoRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Pagamento não encontrado."));
    }

    public BigDecimal calcularTotalPagoPorFatura(Long idFatura) {
        return pagamentoRepository.somarTotalPagoPorFatura(idFatura);
    }

    public BigDecimal calcularValorEmDivida(Long idFatura) {
        Fatura fatura = faturaRepository.findById(idFatura)
                .orElseThrow(() -> new IllegalArgumentException("Fatura não encontrada."));

        BigDecimal totalPago = calcularTotalPagoPorFatura(idFatura);
        return fatura.getValor().subtract(totalPago);
    }

    @Transactional
    public Pagamento criar(CriarPagamentoRequest request) {
        validarRequest(request);

        Fatura fatura = faturaRepository.findById(request.getIdFatura())
                .orElseThrow(() -> new IllegalArgumentException("Fatura não encontrada."));

        Encomenda encomenda = encomendaRepository.findById(fatura.getIdEncomenda().longValue())
                .orElseThrow(() -> new IllegalArgumentException("Encomenda associada à fatura não encontrada."));

        BigDecimal totalPagoAntes = pagamentoRepository.somarTotalPagoPorFatura(fatura.getId());
        BigDecimal valorEmDivida = fatura.getValor().subtract(totalPagoAntes);

        if (valorEmDivida.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Esta fatura já se encontra totalmente paga.");
        }

        if (request.getValorPago().compareTo(valorEmDivida) > 0) {
            throw new IllegalArgumentException(
                    "O valor pago não pode ser superior ao valor em dívida. Valor em dívida: " + valorEmDivida
            );
        }

        Pagamento pagamento = new Pagamento();
        pagamento.setNumfatura(fatura.getId());
        pagamento.setIdencomenda(fatura.getIdEncomenda());
        pagamento.setValorpago(request.getValorPago());
        pagamento.setDatapagamento(LocalDateTime.now());
        pagamento.setMetodopagamento(request.getMetodoPagamento());
        pagamento.setObservacoes(request.getObservacoes());

        Pagamento pagamentoGuardado = pagamentoRepository.save(pagamento);

        financeiroService.registarEntrada(
                pagamentoGuardado.getValorpago(),
                "Pagamento da fatura " + fatura.getId() + " da encomenda " + encomenda.getNum(),
                "PAGAMENTO"
        );

        BigDecimal totalPagoDepois = totalPagoAntes.add(pagamentoGuardado.getValorpago());

        if (totalPagoDepois.compareTo(fatura.getValor()) >= 0) {
            encomenda.setIdestado(4L); // PAGA
            encomendaRepository.save(encomenda);
        }

        return pagamentoGuardado;
    }

    @Transactional
    public void apagar(Long id) {
        Pagamento pagamento = pagamentoRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Pagamento não encontrado."));

        pagamentoRepository.delete(pagamento);
    }

    private void validarRequest(CriarPagamentoRequest request) {
        if (request.getIdFatura() == null) {
            throw new IllegalArgumentException("A fatura é obrigatória.");
        }

        if (request.getValorPago() == null || request.getValorPago().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("O valor pago deve ser superior a zero.");
        }
    }
}