package com.teuprojeto.projeto.repository;

import com.teuprojeto.projeto.entity.Encomenda;
import org.springframework.data.jpa.repository.JpaRepository;

import java.math.BigDecimal;
import java.util.List;

public interface EncomendaRepository extends JpaRepository<Encomenda, BigDecimal> {

    List<Encomenda> findByIdcliente(Integer idcliente);

    List<Encomenda> findByDesignTrue();

    List<Encomenda> findByIdfuncionarioIsNullAndIdestado(Long idestado);

    List<Encomenda> findByIdfuncionario(Long idfuncionario);
}