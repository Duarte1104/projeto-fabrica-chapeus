package com.teuprojeto.projeto.repository;

import com.teuprojeto.projeto.entity.Funcionario;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FuncionarioRepository extends JpaRepository<Funcionario, Long> {
}