package com.teuprojeto.projeto.repository;

import com.teuprojeto.projeto.entity.GastoMaterial;
import org.springframework.data.jpa.repository.JpaRepository;

import java.math.BigDecimal;
import java.util.List;

public interface GastoMaterialRepository extends JpaRepository<GastoMaterial, Long> {
    List<GastoMaterial> findByIdEncomenda(BigDecimal idEncomenda);
}