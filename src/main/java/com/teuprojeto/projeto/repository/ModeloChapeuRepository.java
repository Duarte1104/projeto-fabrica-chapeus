package com.teuprojeto.projeto.repository;

import com.teuprojeto.projeto.entity.ModeloChapeu;
import com.teuprojeto.projeto.entity.ModeloChapeuId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ModeloChapeuRepository extends JpaRepository<ModeloChapeu, ModeloChapeuId> {
}