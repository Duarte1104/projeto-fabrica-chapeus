package com.teuprojeto.projeto.repository;

import com.teuprojeto.projeto.entity.Encomenda;
import org.springframework.data.jpa.repository.JpaRepository;

import java.math.BigDecimal;

public interface EncomendaRepository extends JpaRepository<Encomenda, BigDecimal> {
}