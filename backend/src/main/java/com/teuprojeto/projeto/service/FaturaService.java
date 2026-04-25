package com.teuprojeto.projeto.service;

import com.teuprojeto.projeto.dto.rececionista.CriarFaturaRequest;
import com.teuprojeto.projeto.entity.Encomenda;
import com.teuprojeto.projeto.entity.Fatura;
import com.teuprojeto.projeto.repository.EncomendaRepository;
import com.teuprojeto.projeto.repository.FaturaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class FaturaService {

    private final FaturaRepository faturaRepository;
    private final EncomendaRepository encomendaRepository;
    private final FinanceiroService financeiroService;

    public FaturaService(
            FaturaRepository faturaRepository,
            EncomendaRepository encomendaRepository,
            FinanceiroService financeiroService
    ) {
        this.faturaRepository = faturaRepository;
        this.encomendaRepository = encomendaRepository;
        this.financeiroService = financeiroService;
    }

    @Transactional
    public Fatura criar(CriarFaturaRequest request) {
        Encomenda encomenda = encomendaRepository.findById(request.getIdEncomenda().longValue())
                .orElseThrow(() -> new IllegalArgumentException("Encomenda não encontrada."));

        if (!Long.valueOf(3L).equals(encomenda.getIdestado())) {
            throw new IllegalArgumentException("Só é possível faturar encomendas prontas.");
        }

        Fatura fatura = new Fatura();
        fatura.setIdEncomenda(request.getIdEncomenda());
        fatura.setData(LocalDateTime.now());
        fatura.setValor(encomenda.getValortotal());
        fatura.setObservacoes(request.getObservacoes());

        encomenda.setIdestado(4L); // PAGA
        encomendaRepository.save(encomenda);

        financeiroService.registarEntrada(
                fatura.getValor(),
                "Pagamento da encomenda " + encomenda.getNum(),
                "FATURA"
        );

        return faturaRepository.save(fatura);
    }

    public List<Fatura> listarTodas() {
        return faturaRepository.findAll();
    }

    public List<Fatura> listarPorEncomenda(BigDecimal idEncomenda) {
        return faturaRepository.findByIdEncomenda(idEncomenda);
    }
}