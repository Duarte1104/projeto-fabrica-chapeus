package com.teuprojeto.desktop.view.admin;

import com.teuprojeto.desktop.dto.CriarUtilizadorRequestDto;
import com.teuprojeto.desktop.service.AuthApiService;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;

public class AdminCriarUtilizadorPage {

    private final AuthApiService authApiService = new AuthApiService();

    public Parent getView() {
        VBox root = new VBox(18);
        root.setPadding(new Insets(22));
        root.setStyle("-fx-background-color: #efefef;");

        Label title = new Label("Criar Utilizador");
        title.setStyle("-fx-font-size: 24; -fx-font-weight: bold; -fx-text-fill: #111;");

        GridPane form = new GridPane();
        form.setHgap(16);
        form.setVgap(14);

        TextField email = new TextField();
        PasswordField password = new PasswordField();

        ComboBox<String> role = new ComboBox<>();
        role.getItems().addAll("RECECIONISTA", "DESIGNER", "FUNCIONARIO", "GESTOR");

        Button guardar = new Button("Criar");
        guardar.setStyle("-fx-background-color: black; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 8;");
        guardar.setPrefHeight(38);

        form.add(new Label("Email"), 0, 0);
        form.add(email, 0, 1);
        form.add(new Label("Password"), 1, 0);
        form.add(password, 1, 1);
        form.add(new Label("Role"), 0, 2);
        form.add(role, 0, 3);

        guardar.setOnAction(e -> {
            try {
                CriarUtilizadorRequestDto dto = new CriarUtilizadorRequestDto();
                dto.setEmail(email.getText().trim());
                dto.setPassword(password.getText());
                dto.setRole(role.getValue());

                authApiService.criarUtilizador(dto);

                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setHeaderText("Utilizador criado");
                alert.setContentText("Conta criada com sucesso.");
                alert.showAndWait();

                email.clear();
                password.clear();
                role.setValue(null);
            } catch (Exception ex) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setHeaderText("Erro");
                alert.setContentText(ex.getMessage());
                alert.showAndWait();
            }
        });

        VBox card = new VBox(14, form, guardar);
        card.setPadding(new Insets(18));
        card.setStyle("-fx-background-color: white; -fx-background-radius: 12; -fx-border-color: #dddddd; -fx-border-radius: 12;");

        root.getChildren().addAll(title, card);
        return root;
    }
}