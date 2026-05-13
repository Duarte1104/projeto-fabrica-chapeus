package com.teuprojeto.projeto.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "design_encomenda_imagem", schema = "public")
public class DesignEncomendaImagem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "iddesignencomenda", nullable = false)
    private Long idDesignEncomenda;

    @Column(name = "urlimagem", nullable = false, length = 500)
    private String urlImagem;

    public Long getId() {
        return id;
    }

    public Long getIdDesignEncomenda() {
        return idDesignEncomenda;
    }

    public void setIdDesignEncomenda(Long idDesignEncomenda) {
        this.idDesignEncomenda = idDesignEncomenda;
    }

    public String getUrlImagem() {
        return urlImagem;
    }

    public void setUrlImagem(String urlImagem) {
        this.urlImagem = urlImagem;
    }
}