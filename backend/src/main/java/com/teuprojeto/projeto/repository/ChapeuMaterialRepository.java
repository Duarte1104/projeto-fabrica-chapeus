package com.teuprojeto.projeto.repository;

import com.teuprojeto.projeto.entity.ChapeuMaterial;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ChapeuMaterialRepository extends JpaRepository<ChapeuMaterial, Long> {

    List<ChapeuMaterial> findByIdChapeu(Long idChapeu);
}