package com.teuprojeto.projeto.repository;

import com.teuprojeto.projeto.entity.Chapeu;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChapeuRepository extends JpaRepository<Chapeu, Long> {
}