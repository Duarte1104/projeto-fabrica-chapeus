package com.teuprojeto.projeto.repository;

import com.teuprojeto.projeto.entity.Chapeu;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface ChapeuRepository extends JpaRepository<Chapeu, Long> {

    @Query("select coalesce(max(c.cod), 0) from Chapeu c")
    Long obterMaiorCodigo();
}