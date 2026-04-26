package com.teuprojeto.desktop.dto;

public class AtualizarUtilizadorRequestDto {

    private String email;
    private String novaPassword;

    public AtualizarUtilizadorRequestDto() {
    }

    public String getEmail() {
        return email;
    }

    public String getNovaPassword() {
        return novaPassword;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setNovaPassword(String novaPassword) {
        this.novaPassword = novaPassword;
    }
}