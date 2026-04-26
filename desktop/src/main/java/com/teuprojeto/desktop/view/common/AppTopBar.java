package com.teuprojeto.desktop.view.common;

import com.teuprojeto.desktop.model.AppUser;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.Side;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

public class AppTopBar {

    private AppTopBar() {
    }

    public static HBox create(
            AppUser user,
            String roleLabel,
            Runnable onAlterarPass,
            Runnable onLogout
    ) {
        HBox root = new HBox();
        root.setAlignment(Pos.CENTER_RIGHT);
        root.setPadding(new Insets(18, 24, 18, 24));
        root.setMinHeight(72);
        root.setPrefHeight(72);
        root.setStyle(
                "-fx-background-color: white;" +
                        "-fx-border-color: #d8d8d8;" +
                        "-fx-border-width: 0 0 1 0;"
        );

        Button userChip = buildUserChip(user, roleLabel);
        ContextMenu menu = buildProfileMenu(onAlterarPass, onLogout);

        userChip.setOnAction(e -> {
            if (menu.isShowing()) {
                menu.hide();
            } else {
                menu.show(userChip, Side.BOTTOM, 0, 8);
            }
        });

        root.getChildren().add(userChip);
        return root;
    }

    private static Button buildUserChip(AppUser user, String roleLabel) {
        String nome = obterNomeVisivel(user.getEmail());
        String iniciais = obterIniciais(nome);

        StackPane avatar = new StackPane();
        avatar.setMinSize(42, 42);
        avatar.setPrefSize(42, 42);
        avatar.setMaxSize(42, 42);

        Circle circle = new Circle(21);
        circle.setFill(Color.web("#2563EB"));

        Label initialsLabel = new Label(iniciais);
        initialsLabel.setStyle(
                "-fx-text-fill: white;" +
                        "-fx-font-size: 14;" +
                        "-fx-font-weight: bold;"
        );

        avatar.getChildren().addAll(circle, initialsLabel);

        Label nameLabel = new Label(nome);
        nameLabel.setStyle(
                "-fx-text-fill: #111111;" +
                        "-fx-font-size: 13;" +
                        "-fx-font-weight: bold;"
        );

        Label role = new Label(roleLabel);
        role.setStyle(
                "-fx-text-fill: #6B7280;" +
                        "-fx-font-size: 11;"
        );

        VBox textBox = new VBox(1, nameLabel, role);
        textBox.setAlignment(Pos.CENTER_LEFT);

        HBox content = new HBox(10, avatar, textBox);
        content.setAlignment(Pos.CENTER_LEFT);

        Button chip = new Button();
        chip.setGraphic(content);
        chip.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
        chip.setPrefHeight(48);
        chip.setMinHeight(48);
        chip.setStyle(
                "-fx-background-color: white;" +
                        "-fx-border-color: #cfcfcf;" +
                        "-fx-border-width: 1.5;" +
                        "-fx-border-radius: 14;" +
                        "-fx-background-radius: 14;" +
                        "-fx-padding: 4 12 4 10;" +
                        "-fx-cursor: hand;"
        );

        return chip;
    }

    private static ContextMenu buildProfileMenu(Runnable onAlterarPass, Runnable onLogout) {
        ContextMenu menu = new ContextMenu();
        menu.setAutoHide(true);
        menu.setStyle(
                "-fx-background-color: white;" +
                        "-fx-border-color: #E5E7EB;" +
                        "-fx-background-radius: 14;" +
                        "-fx-border-radius: 14;"
        );

        Label title = new Label("Minha Conta");
        title.setStyle(
                "-fx-font-size: 15;" +
                        "-fx-font-weight: bold;" +
                        "-fx-text-fill: #111111;"
        );

        VBox headerBox = new VBox(title);
        headerBox.setPadding(new Insets(10, 14, 10, 14));

        CustomMenuItem headerItem = new CustomMenuItem(headerBox, false);
        headerItem.setHideOnClick(false);

        CustomMenuItem alterarPassItem = createMenuItem("Alterar pass", onAlterarPass);
        CustomMenuItem sairItem = createMenuItem("Sair", onLogout);

        menu.getItems().addAll(
                headerItem,
                new SeparatorMenuItem(),
                alterarPassItem,
                new SeparatorMenuItem(),
                sairItem
        );

        return menu;
    }

    private static CustomMenuItem createMenuItem(String text, Runnable action) {
        Label label = new Label(text);
        label.setStyle(
                "-fx-font-size: 14;" +
                        "-fx-text-fill: #111111;"
        );

        HBox row = new HBox(label);
        row.setAlignment(Pos.CENTER_LEFT);
        row.setPadding(new Insets(10, 14, 10, 14));
        row.setMinWidth(180);

        CustomMenuItem item = new CustomMenuItem(row, true);
        item.setOnAction(e -> action.run());
        return item;
    }

    private static String obterNomeVisivel(String email) {
        if (email == null || email.isBlank()) {
            return "Utilizador";
        }

        String base = email.contains("@") ? email.substring(0, email.indexOf("@")) : email;
        base = base.replace(".", " ").replace("_", " ").replace("-", " ");

        String[] partes = base.trim().split("\\s+");
        StringBuilder nome = new StringBuilder();

        for (String parte : partes) {
            if (parte.isBlank()) {
                continue;
            }
            nome.append(parte.substring(0, 1).toUpperCase())
                    .append(parte.substring(1).toLowerCase())
                    .append(" ");
        }

        String resultado = nome.toString().trim();
        return resultado.isBlank() ? "Utilizador" : resultado;
    }

    private static String obterIniciais(String nome) {
        if (nome == null || nome.isBlank()) {
            return "U";
        }

        String[] partes = nome.trim().split("\\s+");
        if (partes.length == 1) {
            return partes[0].substring(0, 1).toUpperCase();
        }

        return (partes[0].substring(0, 1) + partes[1].substring(0, 1)).toUpperCase();
    }
}