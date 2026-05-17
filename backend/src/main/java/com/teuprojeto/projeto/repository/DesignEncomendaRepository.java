package com.teuprojeto.projeto.repository;

import com.teuprojeto.projeto.entity.DesignEncomenda;
import org.springframework.data.jpa.repository.JpaRepository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface DesignEncomendaRepository extends JpaRepository<DesignEncomenda, Long> {

    List<DesignEncomenda> findByIdEncomenda(BigDecimal idEncomenda);

    List<DesignEncomenda> findByEstadoDesign(String estadoDesign);

    Optional<DesignEncomenda> findTopByIdEncomendaOrderByDataCriacaoDesc(
            BigDecimal idEncomenda
    );
}