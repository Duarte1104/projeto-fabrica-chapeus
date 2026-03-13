package com.teuprojeto.projeto.repository;

import com.teuprojeto.projeto.entity.Fatura;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FaturaRepository extends JpaRepository<Fatura, Long> {
}