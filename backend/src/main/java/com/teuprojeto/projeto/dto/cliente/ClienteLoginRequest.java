package com.teuprojeto.projeto.dto.cliente;

public class ClienteLoginRequest {

    private String email;
    private String password;

    public ClienteLoginRequest() {
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}