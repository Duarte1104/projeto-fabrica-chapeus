package com.teuprojeto.projeto.dto.cliente;

public class AlterarPasswordClienteRequest {

    private Integer clienteId;
    private String passwordAtual;
    private String novaPassword;

    public AlterarPasswordClienteRequest() {
    }

    public Integer getClienteId() {
        return clienteId;
    }

    public String getPasswordAtual() {
        return passwordAtual;
    }

    public String getNovaPassword() {
        return novaPassword;
    }

    public void setClienteId(Integer clienteId) {
        this.clienteId = clienteId;
    }

    public void setPasswordAtual(String passwordAtual) {
        this.passwordAtual = passwordAtual;
    }

    public void setNovaPassword(String novaPassword) {
        this.novaPassword = novaPassword;
    }
}