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

    public FaturaService(
            FaturaRepository faturaRepository,
            EncomendaRepository encomendaRepository
    ) {
        this.faturaRepository = faturaRepository;
        this.encomendaRepository = encomendaRepository;
    }

    @Transactional
    public Fatura criar(CriarFaturaRequest request) {
        validarRequest(request);

        Encomenda encomenda = encomendaRepository.findById(request.getIdEncomenda().longValue())
                .orElseThrow(() -> new IllegalArgumentException("Encomenda não encontrada."));

        if (!Long.valueOf(3L).equals(encomenda.getIdestado())) {
            throw new IllegalArgumentException("Só é possível faturar encomendas prontas.");
        }

        List<Fatura> faturasDaEncomenda = faturaRepository.findByIdEncomenda(request.getIdEncomenda());

        if (!faturasDaEncomenda.isEmpty()) {
            throw new IllegalArgumentException("Esta encomenda já tem uma fatura emitida.");
        }

        return criarFaturaDaEncomenda(encomenda, request.getObservacoes());
    }

    @Transactional
    public Fatura emitirAutomaticamenteSeNecessario(Encomenda encomenda) {
        if (encomenda == null || encomenda.getNum() == null) {
            throw new IllegalArgumentException("Encomenda inválida para emissão automática de fatura.");
        }

        if (!Long.valueOf(3L).equals(encomenda.getIdestado())) {
            throw new IllegalArgumentException("Só é possível emitir fatura automática para encomendas prontas.");
        }

        BigDecimal idEncomenda = BigDecimal.valueOf(encomenda.getNum());

        List<Fatura> faturasExistentes = faturaRepository.findByIdEncomenda(idEncomenda);

        if (!faturasExistentes.isEmpty()) {
            return faturasExistentes.get(0);
        }

        return criarFaturaDaEncomenda(
                encomenda,
                "Fatura emitida automaticamente quando a encomenda ficou pronta."
        );
    }

    private Fatura criarFaturaDaEncomenda(Encomenda encomenda, String observacoes) {
        Fatura fatura = new Fatura();
        fatura.setIdEncomenda(BigDecimal.valueOf(encomenda.getNum()));
        fatura.setData(LocalDateTime.now());
        fatura.setValor(encomenda.getValortotal());
        fatura.setObservacoes(observacoes);

        return faturaRepository.save(fatura);
    }

    public List<Fatura> listarTodas() {
        return faturaRepository.findAll();
    }

    public List<Fatura> listarPorEncomenda(BigDecimal idEncomenda) {
        return faturaRepository.findByIdEncomenda(idEncomenda);
    }

    private void validarRequest(CriarFaturaRequest request) {
        if (request.getIdEncomenda() == null) {
            throw new IllegalArgumentException("A encomenda é obrigatória.");
        }
    }
}