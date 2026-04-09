package com.teuprojeto.projeto.repository;

import com.teuprojeto.projeto.entity.CompraMaterial;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CompraMaterialRepository extends JpaRepository<CompraMaterial, Long> {
    List<CompraMaterial> findByIdMaterial(Long idMaterial);
}