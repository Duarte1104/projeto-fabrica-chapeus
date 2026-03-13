package com.teuprojeto.projeto.repository;

import com.teuprojeto.projeto.entity.Material;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MaterialRepository extends JpaRepository<Material, Long> {
}