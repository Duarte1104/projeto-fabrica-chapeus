package com.teuprojeto.projeto.repository;

import com.teuprojeto.projeto.entity.LinhaEncomenda;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LinhaEncomendaRepository extends JpaRepository<LinhaEncomenda, Long> {

    List<LinhaEncomenda> findByNumencomenda(Long numencomenda);

    void deleteByNumencomenda(Long numencomenda);
}