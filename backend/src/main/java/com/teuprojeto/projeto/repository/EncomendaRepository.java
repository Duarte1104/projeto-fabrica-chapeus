package com.teuprojeto.projeto.repository;

import com.teuprojeto.projeto.entity.Encomenda;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EncomendaRepository extends JpaRepository<Encomenda, Long> {

    List<Encomenda> findByIdcliente(Integer idcliente);

    List<Encomenda> findByDesignTrue();

    List<Encomenda> findByIdfuncionarioIsNullAndIdestado(Long idestado);

    List<Encomenda> findByIdfuncionario(Long idfuncionario);
}