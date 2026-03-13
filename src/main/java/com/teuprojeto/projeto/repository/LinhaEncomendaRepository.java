package com.teuprojeto.projeto.repository;

import com.teuprojeto.projeto.entity.LinhaEncomenda;
import com.teuprojeto.projeto.entity.LinhaEncomendaId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LinhaEncomendaRepository extends JpaRepository<LinhaEncomenda, LinhaEncomendaId> {
}