package com.teuprojeto.projeto.service;

import com.teuprojeto.projeto.dto.rececionista.CriarReciboRequest;
import com.teuprojeto.projeto.entity.Pagamento;
import com.teuprojeto.projeto.entity.Recibo;
import com.teuprojeto.projeto.repository.PagamentoRepository;
import com.teuprojeto.projeto.repository.ReciboRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ReciboService {

    private final ReciboRepository reciboRepository;
    private final PagamentoRepository pagamentoRepository;

    public ReciboService(
            ReciboRepository reciboRepository,
            PagamentoRepository pagamentoRepository
    ) {
        this.reciboRepository = reciboRepository;
        this.pagamentoRepository = pagamentoRepository;
    }

    public List<Recibo> listarTodos() {
        return reciboRepository.findAll();
    }

    public Recibo procurarPorId(Long id) {
        return reciboRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Recibo não encontrado."));
    }

    public Recibo procurarPorPagamento(Long idPagamento) {
        return reciboRepository.findByIdPagamento(idPagamento)
                .orElseThrow(() -> new IllegalArgumentException("Este pagamento ainda não tem recibo emitido."));
    }

    public List<Recibo> listarPorFatura(Long numFatura) {
        return reciboRepository.findByNumFatura(numFatura);
    }

    @Transactional
    public Recibo criar(CriarReciboRequest request) {
        validarRequest(request);

        Pagamento pagamento = pagamentoRepository.findById(request.getIdPagamento())
                .orElseThrow(() -> new IllegalArgumentException("Pagamento não encontrado."));

        if (reciboRepository.existsByIdPagamento(pagamento.getCod())) {
            throw new IllegalArgumentException("Este pagamento já tem recibo emitido.");
        }

        Recibo recibo = new Recibo();
        recibo.setIdPagamento(pagamento.getCod());
        recibo.setNumFatura(pagamento.getNumfatura());
        recibo.setIdEncomenda(pagamento.getIdencomenda());
        recibo.setValor(pagamento.getValorpago());
        recibo.setData(LocalDateTime.now());
        recibo.setObservacoes(request.getObservacoes());

        return reciboRepository.save(recibo);
    }

    private void validarRequest(CriarReciboRequest request) {
        if (request.getIdPagamento() == null) {
            throw new IllegalArgumentException("O pagamento é obrigatório para emitir o recibo.");
        }
    }
}