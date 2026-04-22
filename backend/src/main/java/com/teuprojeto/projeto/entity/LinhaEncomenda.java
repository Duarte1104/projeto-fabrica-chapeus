package com.teuprojeto.projeto.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.Table;

@Entity
@Table(name = "\"LinhaEncomenda\"", schema = "public")
@IdClass(LinhaEncomendaId.class)
public class LinhaEncomenda {

    @Id
    @Column(name = "\"numencomenda\"")
    private Long numencomenda;

    @Id
    @Column(name = "\"codchapeu\"")
    private Long codchapeu;

    @Column(name = "\"quantidade\"")
    private Long quantidade;

    public LinhaEncomenda() {
    }

    public Long getNumencomenda() {
        return numencomenda;
    }

    public void setNumencomenda(Long numencomenda) {
        this.numencomenda = numencomenda;
    }

    public Long getCodchapeu() {
        return codchapeu;
    }

    public void setCodchapeu(Long codchapeu) {
        this.codchapeu = codchapeu;
    }

    public Long getQuantidade() {
        return quantidade;
    }

    public void setQuantidade(Long quantidade) {
        this.quantidade = quantidade;
    }
}