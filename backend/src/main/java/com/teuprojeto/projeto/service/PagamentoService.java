package com.teuprojeto.projeto.service;

import com.teuprojeto.projeto.entity.Pagamento;
import com.teuprojeto.projeto.repository.PagamentoRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class PagamentoService {

    private final PagamentoRepository pagamentoRepository;

    public PagamentoService(PagamentoRepository pagamentoRepository) {
        this.pagamentoRepository = pagamentoRepository;
    }

    public List<Pagamento> listarTodos() {
        return pagamentoRepository.findAll();
    }

    public Optional<Pagamento> procurarPorId(Long id) {
        return pagamentoRepository.findById(id);
    }

    public Pagamento guardar(Pagamento pagamento) {
        return pagamentoRepository.save(pagamento);
    }

    public void apagar(Long id) {
        pagamentoRepository.deleteById(id);
    }
}