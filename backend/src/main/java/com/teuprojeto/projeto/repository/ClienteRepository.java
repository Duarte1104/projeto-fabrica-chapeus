package com.teuprojeto.projeto.repository;

import com.teuprojeto.projeto.entity.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ClienteRepository extends JpaRepository<Cliente, Integer> {
    boolean existsByEmail(String email);
    boolean existsByNif(String nif);
}