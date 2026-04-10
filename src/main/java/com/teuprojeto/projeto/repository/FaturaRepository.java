package com.teuprojeto.projeto.repository;

import com.teuprojeto.projeto.entity.Fatura;
import org.springframework.data.jpa.repository.JpaRepository;

import java.math.BigDecimal;
import java.util.List;

public interface FaturaRepository extends JpaRepository<Fatura, Long> {
    List<Fatura> findByIdEncomenda(BigDecimal idEncomenda);
}