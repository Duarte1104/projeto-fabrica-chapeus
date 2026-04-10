package com.teuprojeto.desktop.model;

import java.util.ArrayList;
import java.util.List;

public class FakeAuthService {

    private static final List<AppUser> users = new ArrayList<>();


    static {
        users.add(new AppUser("rececionista@email.com", "1234", UserRole.RECECIONISTA));
        users.add(new AppUser("designer@email.com", "1234", UserRole.DESIGNER));
        users.add(new AppUser("funcionario@email.com", "1234", UserRole.FUNCIONARIO));
        users.add(new AppUser("gestor@email.com", "1234", UserRole.GESTOR));
    }

    public static AppUser login(String email, String password) {
        return users.stream()
                .filter(u -> u.getEmail().equalsIgnoreCase(email) && u.getPassword().equals(password))
                .findFirst()
                .orElse(null);
    }

    public static boolean register(String email, String password, UserRole role) {
        boolean exists = users.stream().anyMatch(u -> u.getEmail().equalsIgnoreCase(email));
        if (exists) return false;

        users.add(new AppUser(email, password, role));
        return true;
    }
}