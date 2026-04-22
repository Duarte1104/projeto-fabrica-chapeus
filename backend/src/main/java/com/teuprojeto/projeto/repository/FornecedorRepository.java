package com.teuprojeto.projeto.repository;

import com.teuprojeto.projeto.entity.Fornecedor;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FornecedorRepository extends JpaRepository<Fornecedor, Long> {
}