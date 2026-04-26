package com.teuprojeto.desktop.dto;

public class UtilizadorDto {

    private Long id;
    private String email;
    private String role;
    private Boolean ativo;

    public UtilizadorDto() {
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