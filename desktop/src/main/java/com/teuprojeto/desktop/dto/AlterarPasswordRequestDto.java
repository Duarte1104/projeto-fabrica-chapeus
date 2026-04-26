package com.teuprojeto.desktop.dto;

public class AlterarPasswordRequestDto {

    private String email;
    private String passwordAtual;
    private String novaPassword;

    public AlterarPasswordRequestDto() {
    }

    public String getEmail() {
        return email;
    }

    public String getPasswordAtual() {
        return passwordAtual;
    }

    public String getNovaPassword() {
        return novaPassword;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPasswordAtual(String passwordAtual) {
        this.passwordAtual = passwordAtual;
    }

    public void setNovaPassword(String novaPassword) {
        this.novaPassword = novaPassword;
    }
}