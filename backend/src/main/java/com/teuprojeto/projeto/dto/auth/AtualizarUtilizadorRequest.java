package com.teuprojeto.projeto.dto.auth;

public class AtualizarUtilizadorRequest {

    private String email;
    private String novaPassword;

    public AtualizarUtilizadorRequest() {
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