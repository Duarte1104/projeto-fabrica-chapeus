package com.teuprojeto.desktop.view.admin;

import com.teuprojeto.desktop.dto.CriarUtilizadorRequestDto;
import com.teuprojeto.desktop.service.AuthApiService;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class AdminCriarUtilizadorPage {

    private final AdminShellView shell;
    private final AuthApiService authApiService = new AuthApiService();

    public AdminCriarUtilizadorPage(AdminShellView shell) {
        this.shell = shell;
    }

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
        email.setPromptText("Email");

        PasswordField password = new PasswordField();
        password.setPromptText("Password");

        ComboBox<String> role = new ComboBox<>();
        role.getItems().addAll("RECECIONISTA", "DESIGNER", "FUNCIONARIO", "GESTOR");
        role.setPromptText("Role");

        form.add(new Label("Email"), 0, 0);
        form.add(email, 0, 1);
        form.add(new Label("Password"), 1, 0);
        form.add(password, 1, 1);
        form.add(new Label("Role"), 0, 2);
        form.add(role, 0, 3);

        Button guardar = new Button("Criar");
        guardar.setStyle("-fx-background-color: black; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 8;");
        guardar.setPrefHeight(38);

        Button verUtilizadores = new Button("Ver Utilizadores");
        verUtilizadores.setStyle("-fx-background-color: white; -fx-text-fill: black; -fx-font-weight: bold; -fx-border-color: #d0d0d0; -fx-background-radius: 8; -fx-border-radius: 8;");
        verUtilizadores.setPrefHeight(38);
        verUtilizadores.setOnAction(e -> shell.navigateTo(AdminPage.UTILIZADORES));

        guardar.setOnAction(e -> {
            try {
                if (email.getText().isBlank() || password.getText().isBlank() || role.getValue() == null) {
                    Alert alert = new Alert(Alert.AlertType.WARNING);
                    alert.setHeaderText("Dados em falta");
                    alert.setContentText("Preenche email, password e role.");
                    alert.showAndWait();
                    return;
                }

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

                shell.navigateTo(AdminPage.UTILIZADORES);

            } catch (Exception ex) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setHeaderText("Erro");
                alert.setContentText(ex.getMessage());
                alert.showAndWait();
            }
        });

        HBox actions = new HBox(10, guardar, verUtilizadores);

        VBox card = new VBox(14, form, actions);
        card.setPadding(new Insets(18));
        card.setStyle("-fx-background-color: white; -fx-background-radius: 12; -fx-border-color: #dddddd; -fx-border-radius: 12;");

        root.getChildren().addAll(title, card);
        return root;
    }
}