package com.teuprojeto.projeto.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "\"Codpostal\"", schema = "public")
public class Codpostal {

    @Id
    @Column(name = "\"codpostal\"")
    private String codpostal;

    @Column(name = "\"localidade\"")
    private String localidade;

    public Codpostal() {
    }

    public String getCodpostal() {
        return codpostal;
    }

    public void setCodpostal(String codpostal) {
        this.codpostal = codpostal;
    }

    public String getLocalidade() {
        return localidade;
    }

    public void setLocalidade(String localidade) {
        this.localidade = localidade;
    }
}