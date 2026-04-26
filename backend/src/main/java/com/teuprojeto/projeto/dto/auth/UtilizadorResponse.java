package com.teuprojeto.projeto.dto.auth;

public class UtilizadorResponse {

    private Long id;
    private String email;
    private String role;
    private Boolean ativo;

    public UtilizadorResponse() {
    }

    public UtilizadorResponse(Long id, String email, String role, Boolean ativo) {
        this.id = id;
        this.email = email;
        this.role = role;
        this.ativo = ativo;
    }

    public Long getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }

    public String getRole() {
        return role;
    }

    public Boolean getAtivo() {
        return ativo;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public void setAtivo(Boolean ativo) {
        this.ativo = ativo;
    }
}