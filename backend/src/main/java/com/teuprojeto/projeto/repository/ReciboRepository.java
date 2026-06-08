package com.teuprojeto.projeto.repository;

import com.teuprojeto.projeto.entity.Recibo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface ReciboRepository extends JpaRepository<Recibo, Long> {

    Optional<Recibo> findByIdPagamento(Long idPagamento);

    List<Recibo> findByNumFatura(Long numFatura);

    List<Recibo> findByIdEncomenda(BigDecimal idEncomenda);

    boolean existsByIdPagamento(Long idPagamento);
}