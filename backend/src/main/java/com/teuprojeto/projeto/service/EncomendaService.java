package com.teuprojeto.projeto.service;

import com.teuprojeto.projeto.dto.encomenda.CriarEncomendaRequest;
import com.teuprojeto.projeto.dto.encomenda.LinhaEncomendaRequest;
import com.teuprojeto.projeto.entity.Encomenda;
import com.teuprojeto.projeto.entity.LinhaEncomenda;
import com.teuprojeto.projeto.repository.ClienteRepository;
import com.teuprojeto.projeto.repository.EncomendaRepository;
import com.teuprojeto.projeto.repository.FuncionarioRepository;
import com.teuprojeto.projeto.repository.LinhaEncomendaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Service
public class EncomendaService {

    private final EncomendaRepository encomendaRepository;
    private final LinhaEncomendaRepository linhaEncomendaRepository;
    private final ClienteRepository clienteRepository;
    private final FuncionarioRepository funcionarioRepository;

    public EncomendaService(
            EncomendaRepository encomendaRepository,
            LinhaEncomendaRepository linhaEncomendaRepository,
            ClienteRepository clienteRepository,
            FuncionarioRepository funcionarioRepository
    ) {
        this.encomendaRepository = encomendaRepository;
        this.linhaEncomendaRepository = linhaEncomendaRepository;
        this.clienteRepository = clienteRepository;
        this.funcionarioRepository = funcionarioRepository;
    }

    @Transactional
    public Encomenda criar(CriarEncomendaRequest request) {

        if (!clienteRepository.existsById(request.getIdCliente())) {
            throw new IllegalArgumentException("Cliente não encontrado.");
        }

        if (request.getLinhas() == null || request.getLinhas().isEmpty()) {
            throw new IllegalArgumentException("A encomenda deve ter pelo menos uma linha.");
        }

        BigDecimal valorTotal = BigDecimal.ZERO;

        for (LinhaEncomendaRequest linha : request.getLinhas()) {
            BigDecimal subtotal = linha.getPrecoUnitario()
                    .multiply(BigDecimal.valueOf(linha.getQuantidade()));
            valorTotal = valorTotal.add(subtotal);
        }

        Encomenda encomenda = new Encomenda();
        encomenda.setData(LocalDate.now());
        encomenda.setHora(LocalTime.now());
        encomenda.setDataEntrega(request.getDataEntrega());
        encomenda.setObservacoes(request.getObservacoes());
        encomenda.setIdcliente(request.getIdCliente());
        encomenda.setValortotal(valorTotal);
        encomenda.setDesign(request.getDesign());
        encomenda.setDescricaoDesign(request.getDescricaoDesign());
        encomenda.setIdfuncionario(null);

        // 1 = AGUARDA_DESIGN | 2 = PREPARACAO
        if (Boolean.TRUE.equals(request.getDesign())) {
            encomenda.setIdestado(1L);
        } else {
            encomenda.setIdestado(2L);
        }

        Encomenda encomendaGuardada = encomendaRepository.save(encomenda);

        for (LinhaEncomendaRequest linhaRequest : request.getLinhas()) {
            LinhaEncomenda linha = new LinhaEncomenda();
            linha.setNumencomenda(encomendaGuardada.getNum());
            linha.setCodchapeu(linhaRequest.getCodChapeu());
            linha.setQuantidade(linhaRequest.getQuantidade());
            linhaEncomendaRepository.save(linha);
        }

        return encomendaGuardada;
    }

    public List<Encomenda> listarTodas() {
        return encomendaRepository.findAll();
    }

    public List<Encomenda> listarComDesign() {
        return encomendaRepository.findByDesignTrue();
    }

    public List<Encomenda> listarPorCliente(Integer idCliente) {
        return encomendaRepository.findByIdcliente(idCliente);
    }

    public List<Encomenda> listarDisponiveisParaFuncionario() {
        return encomendaRepository.findByIdfuncionarioIsNullAndIdestado(2L);
    }

    public List<Encomenda> listarPorFuncionario(Long idFuncionario) {
        return encomendaRepository.findByIdfuncionario(idFuncionario);
    }

    public Encomenda procurarPorId(BigDecimal id) {
        return encomendaRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Encomenda não encontrada."));
    }

    public void apagar(BigDecimal id) {
        if (!encomendaRepository.existsById(id)) {
            throw new IllegalArgumentException("Encomenda não encontrada.");
        }
        encomendaRepository.deleteById(id);
    }

    @Transactional
    public Encomenda mudarEstado(BigDecimal idEncomenda, Long idNovoEstado) {
        Encomenda encomenda = encomendaRepository.findById(idEncomenda)
                .orElseThrow(() -> new IllegalArgumentException("Encomenda não encontrada."));

        encomenda.setIdestado(idNovoEstado);
        return encomendaRepository.save(encomenda);
    }

    @Transactional
    public Encomenda aceitarEncomenda(BigDecimal idEncomenda, Long idFuncionario) {
        Encomenda encomenda = encomendaRepository.findById(idEncomenda)
                .orElseThrow(() -> new IllegalArgumentException("Encomenda não encontrada."));

        if (!funcionarioRepository.existsById(idFuncionario)) {
            throw new IllegalArgumentException("Funcionário não encontrado.");
        }

        if (encomenda.getIdfuncionario() != null) {
            throw new IllegalArgumentException("Esta encomenda já foi atribuída a um funcionário.");
        }

        if (!Long.valueOf(2L).equals(encomenda.getIdestado())) {
            throw new IllegalArgumentException("Só é possível aceitar encomendas em preparação.");
        }

        encomenda.setIdfuncionario(idFuncionario);
        return encomendaRepository.save(encomenda);
    }
}