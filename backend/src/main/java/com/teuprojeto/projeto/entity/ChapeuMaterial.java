package com.teuprojeto.projeto.entity;

import jakarta.persistence.*;

import java.math.BigDecimal;

@Entity
@Table(name = "chapeu_material", schema = "public")
public class ChapeuMaterial {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "id_chapeu", nullable = false)
    private Long idChapeu;

    @Column(name = "id_material", nullable = false)
    private Long idMaterial;

    @Column(name = "quantidade_por_unidade", nullable = false)
    private BigDecimal quantidadePorUnidade;

    public ChapeuMaterial() {
    }

    public Long getId() {
        return id;
    }

    public Long getIdChapeu() {
        return idChapeu;
    }

    public void setIdChapeu(Long idChapeu) {
        this.idChapeu = idChapeu;
    }

    public Long getIdMaterial() {
        return idMaterial;
    }

    public void setIdMaterial(Long idMaterial) {
        this.idMaterial = idMaterial;
    }

    public BigDecimal getQuantidadePorUnidade() {
        return quantidadePorUnidade;
    }

    public void setQuantidadePorUnidade(BigDecimal quantidadePorUnidade) {
        this.quantidadePorUnidade = quantidadePorUnidade;
    }
}