package com.teuprojeto.projeto.repository;

import com.teuprojeto.projeto.entity.ProducaoEncomenda;
import org.springframework.data.jpa.repository.JpaRepository;

import java.math.BigDecimal;
import java.util.Optional;

public interface ProducaoEncomendaRepository extends JpaRepository<ProducaoEncomenda, Long> {
    Optional<ProducaoEncomenda> findByIdEncomenda(BigDecimal idEncomenda);
}