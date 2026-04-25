package com.teuprojeto.projeto.repository;

import com.teuprojeto.projeto.entity.Utilizador;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UtilizadorRepository extends JpaRepository<Utilizador, Long> {
    Optional<Utilizador> findByEmailIgnoreCase(String email);
    boolean existsByEmailIgnoreCase(String email);
}