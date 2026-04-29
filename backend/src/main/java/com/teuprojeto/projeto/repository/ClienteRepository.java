package com.teuprojeto.projeto.repository;

import com.teuprojeto.projeto.entity.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ClienteRepository extends JpaRepository<Cliente, Integer> {

    boolean existsByEmailIgnoreCase(String email);

    boolean existsByNif(String nif);

    boolean existsByEmailIgnoreCaseAndCodNot(String email, Integer cod);

    boolean existsByNifAndCodNot(String nif, Integer cod);

    Optional<Cliente> findByEmailIgnoreCase(String email);
}