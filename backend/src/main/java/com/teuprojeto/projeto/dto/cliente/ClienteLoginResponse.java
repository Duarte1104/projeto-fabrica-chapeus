package com.teuprojeto.projeto.dto.cliente;

public class ClienteLoginResponse {

    private Integer cod;
    private String nome;
    private String email;

    public ClienteLoginResponse() {
    }

    public ClienteLoginResponse(Integer cod, String nome, String email) {
        this.cod = cod;
        this.nome = nome;
        this.email = email;
    }

    public Integer getCod() {
        return cod;
    }

    public String getNome() {
        return nome;
    }

    public String getEmail() {
        return email;
    }

    public void setCod(Integer cod) {
        this.cod = cod;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}