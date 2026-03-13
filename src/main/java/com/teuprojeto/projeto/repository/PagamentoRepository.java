package com.teuprojeto.projeto.repository;

import com.teuprojeto.projeto.entity.Pagamento;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PagamentoRepository extends JpaRepository<Pagamento, Long> {
}