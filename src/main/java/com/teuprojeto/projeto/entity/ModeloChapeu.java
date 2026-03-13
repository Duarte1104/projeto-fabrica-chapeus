package com.teuprojeto.projeto.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.Table;

@Entity
@Table(name = "\"ModeloChapeu\"", schema = "public")
@IdClass(ModeloChapeuId.class)
public class ModeloChapeu {

    @Id
    @Column(name = "\"codchapeu\"")
    private Long codchapeu;

    @Id
    @Column(name = "\"idmaterial\"")
    private Long idmaterial;

    public ModeloChapeu() {
    }

    public Long getCodchapeu() {
        return codchapeu;
    }

    public void setCodchapeu(Long codchapeu) {
        this.codchapeu = codchapeu;
    }

    public Long getIdmaterial() {
        return idmaterial;
    }

    public void setIdmaterial(Long idmaterial) {
        this.idmaterial = idmaterial;
    }
}