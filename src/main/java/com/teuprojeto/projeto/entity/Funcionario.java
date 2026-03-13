package com.teuprojeto.projeto.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "\"Funcionario\"", schema = "public")
public class Funcionario {

    @Id
    @Column(name = "\"num\"")
    private Long num;

    @Column(name = "\"nif\"")
    private String nif;

    @Column(name = "\"telefone\"")
    private String telefone;

    @Column(name = "\"email\"")
    private String email;

    @Column(name = "\"rua\"")
    private String rua;

    @Column(name = "\"nporta\"")
    private String nporta;

    @Column(name = "\"codpostal\"")
    private String codpostal;

    @Column(name = "\"idtipofuncionario\"")
    private Long idtipofuncionario;

    @Column(name = "\"idordemproducao\"")
    private Long idordemproducao;

    public Funcionario() {
    }

    public Long getNum() {
        return num;
    }

    public void setNum(Long num) {
        this.num = num;
    }

    public String getNif() {
        return nif;
    }

    public void setNif(String nif) {
        this.nif = nif;
    }

    public String getTelefone() {
        return telefone;
    }

    public void setTelefone(String telefone) {
        this.telefone = telefone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getRua() {
        return rua;
    }

    public void setRua(String rua) {
        this.rua = rua;
    }

    public String getNporta() {
        return nporta;
    }

    public void setNporta(String nporta) {
        this.nporta = nporta;
    }

    public String getCodpostal() {
        return codpostal;
    }

    public void setCodpostal(String codpostal) {
        this.codpostal = codpostal;
    }

    public Long getIdtipofuncionario() {
        return idtipofuncionario;
    }

    public void setIdtipofuncionario(Long idtipofuncionario) {
        this.idtipofuncionario = idtipofuncionario;
    }

    public Long getIdordemproducao() {
        return idordemproducao;
    }

    public void setIdordemproducao(Long idordemproducao) {
        this.idordemproducao = idordemproducao;
    }
}