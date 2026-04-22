package com.teuprojeto.projeto.service;

import com.teuprojeto.projeto.dto.producao.AtualizarProducaoEncomendaRequest;
import com.teuprojeto.projeto.entity.Encomenda;
import com.teuprojeto.projeto.entity.ProducaoEncomenda;
import com.teuprojeto.projeto.repository.EncomendaRepository;
import com.teuprojeto.projeto.repository.ProducaoEncomendaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Optional;

@Service
public class ProducaoEncomendaService {

    private final ProducaoEncomendaRepository producaoEncomendaRepository;
    private final EncomendaRepository encomendaRepository;

    public ProducaoEncomendaService(
            ProducaoEncomendaRepository producaoEncomendaRepository,
            EncomendaRepository encomendaRepository
    ) {
        this.producaoEncomendaRepository = producaoEncomendaRepository;
        this.encomendaRepository = encomendaRepository;
    }

    @Transactional
    public ProducaoEncomenda atualizar(AtualizarProducaoEncomendaRequest request) {
        Encomenda encomenda = encomendaRepository.findById(request.getIdEncomenda())
                .orElseThrow(() -> new IllegalArgumentException("Encomenda não encontrada."));

        if (!Long.valueOf(2L).equals(encomenda.getIdestado())) {
            throw new IllegalArgumentException("A encomenda não está em preparação.");
        }

        Optional<ProducaoEncomenda> existente = producaoEncomendaRepository.findByIdEncomenda(request.getIdEncomenda());

        ProducaoEncomenda producao = existente.orElseGet(ProducaoEncomenda::new);

        producao.setIdEncomenda(request.getIdEncomenda());
        producao.setMontagemConcluida(request.getMontagemConcluida());
        producao.setMontagemComentario(request.getMontagemComentario());
        producao.setCosturasConcluidas(request.getCosturasConcluidas());
        producao.setCosturasComentario(request.getCosturasComentario());
        producao.setPersonalizacaoConcluida(request.getPersonalizacaoConcluida());
        producao.setPersonalizacaoComentario(request.getPersonalizacaoComentario());
        producao.setObservacoes(request.getObservacoes());
        producao.setConcluida(request.getConcluida());

        ProducaoEncomenda producaoGuardada = producaoEncomendaRepository.save(producao);

        if (Boolean.TRUE.equals(request.getConcluida())) {
            encomenda.setIdestado(3L); // PRONTA
            encomendaRepository.save(encomenda);
        }

        return producaoGuardada;
    }

    public ProducaoEncomenda procurarPorEncomenda(BigDecimal idEncomenda) {
        return producaoEncomendaRepository.findByIdEncomenda(idEncomenda)
                .orElseThrow(() -> new IllegalArgumentException("Produção da encomenda não encontrada."));
    }
}