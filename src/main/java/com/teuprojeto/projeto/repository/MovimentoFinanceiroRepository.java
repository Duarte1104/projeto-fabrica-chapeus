package com.teuprojeto.projeto.repository;

import com.teuprojeto.projeto.entity.MovimentoFinanceiro;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MovimentoFinanceiroRepository extends JpaRepository<MovimentoFinanceiro, Long> {
    List<MovimentoFinanceiro> findByTipo(String tipo);
}