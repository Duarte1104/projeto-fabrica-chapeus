package com.teuprojeto.projeto.repository;

import com.teuprojeto.projeto.entity.DesignEncomendaImagem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DesignEncomendaImagemRepository extends JpaRepository<DesignEncomendaImagem, Long> {

    List<DesignEncomendaImagem> findByIdDesignEncomenda(Long idDesignEncomenda);
}