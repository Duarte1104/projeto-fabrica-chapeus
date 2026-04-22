package com.teuprojeto.projeto.repository;

import com.teuprojeto.projeto.entity.ContaEmpresa;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ContaEmpresaRepository extends JpaRepository<ContaEmpresa, Long> {
}