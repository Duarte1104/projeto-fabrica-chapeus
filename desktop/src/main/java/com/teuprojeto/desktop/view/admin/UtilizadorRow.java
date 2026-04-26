package com.teuprojeto.desktop.view.admin;

public class UtilizadorRow {

    private final Long id;
    private final String email;
    private final String role;
    private final String estado;

    public UtilizadorRow(Long id, String email, String role, String estado) {
        this.id = id;
        this.email = email;
        this.role = role;
        this.estado = estado;
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

    public String getEstado() {
        return estado;
    }
}