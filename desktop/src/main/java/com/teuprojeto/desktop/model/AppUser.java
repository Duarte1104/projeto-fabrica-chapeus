package com.teuprojeto.desktop.model;

public class AppUser {

    private String email;
    private String password;
    private UserRole role;

    public AppUser(String email, String password, UserRole role) {
        this.email = email;
        this.password = password;
        this.role = role;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public UserRole getRole() {
        return role;
    }
}