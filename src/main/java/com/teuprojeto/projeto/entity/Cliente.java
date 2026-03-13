package com.teuprojeto.projeto.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "\"Cliente\"", schema = "public")
public class Cliente {

    @Id
    @Column(name = "\"cod\"")
    private Integer cod;

    @Column(name = "\"nome\"")
    private String nome;

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

    public Cliente() {
    }

    public Integer getCod() {
        return cod;
    }

    public void setCod(Integer cod) {
        this.cod = cod;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
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
}