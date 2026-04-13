package com.teuprojeto.desktop.view;

import com.teuprojeto.desktop.MainApp;
import com.teuprojeto.desktop.model.FakeAuthService;
import com.teuprojeto.desktop.model.UserRole;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.*;

public class RegisterView {

    private final MainApp app;

    public RegisterView(MainApp app) {
        this.app = app;
    }

    public Parent getView() {
        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: linear-gradient(to right, #16235c, #1f3ccf);");

        VBox card = new VBox(18);
        card.setAlignment(Pos.CENTER);
        card.setPadding(new Insets(44, 42, 44, 42));
        card.setPrefWidth(460);
        card.setMaxWidth(460);
        card.setPrefHeight(Region.USE_COMPUTED_SIZE);
        card.setMaxHeight(Region.USE_PREF_SIZE);
        card.setStyle(
                "-fx-background-color: white; " +
                        "-fx-background-radius: 16;"
        );

        StackPane logoCircle = new StackPane();
        logoCircle.setPrefSize(88, 88);
        logoCircle.setMaxSize(88, 88);
        logoCircle.setStyle("-fx-background-color: linear-gradient(to bottom, #16235c, #1f3ccf); -fx-background-radius: 999;");

        Label title = new Label("Fábrica de chapéus");
        title.setStyle("-fx-font-size: 21; -fx-font-weight: bold; -fx-text-fill: #111;");

        Label subtitle = new Label("Sistema de Gestão - Registe-se para continuar");
        subtitle.setStyle("-fx-text-fill: #8a8a8a; -fx-font-size: 13;");

        VBox header = new VBox(10, logoCircle, title, subtitle);
        header.setAlignment(Pos.CENTER);

        Label emailLabel = new Label("Email");
        emailLabel.setStyle("-fx-font-size: 13; -fx-font-weight: bold; -fx-text-fill: #222;");

        TextField emailField = new TextField();
        emailField.setPromptText("Seu@email.com");
        emailField.setPrefHeight(42);
        emailField.setMaxWidth(Double.MAX_VALUE);
        emailField.setStyle(
                "-fx-background-color: #f2f2f2; " +
                        "-fx-background-radius: 10; " +
                        "-fx-border-radius: 10; " +
                        "-fx-border-color: transparent;"
        );

        Label passwordLabel = new Label("Password");
        passwordLabel.setStyle("-fx-font-size: 13; -fx-font-weight: bold; -fx-text-fill: #222;");

        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("********");
        passwordField.setPrefHeight(42);
        passwordField.setMaxWidth(Double.MAX_VALUE);
        passwordField.setStyle(
                "-fx-background-color: #f2f2f2; " +
                        "-fx-background-radius: 10; " +
                        "-fx-border-radius: 10; " +
                        "-fx-border-color: transparent;"
        );

        Label roleLabel = new Label("Função");
        roleLabel.setStyle("-fx-font-size: 13; -fx-font-weight: bold; -fx-text-fill: #222;");

        ComboBox<UserRole> roleBox = new ComboBox<>();
        roleBox.getItems().addAll(UserRole.values());
        roleBox.setPromptText("Seleciona a tua função");
        roleBox.setPrefHeight(42);
        roleBox.setMaxWidth(Double.MAX_VALUE);
        roleBox.setStyle(
                "-fx-background-color: #f2f2f2; " +
                        "-fx-background-radius: 10; " +
                        "-fx-border-radius: 10; " +
                        "-fx-border-color: transparent;"
        );

        VBox form = new VBox(10,
                emailLabel, emailField,
                passwordLabel, passwordField,
                roleLabel, roleBox
        );
        form.setAlignment(Pos.CENTER_LEFT);

        Label message = new Label();
        message.setStyle("-fx-text-fill: red; -fx-font-size: 12;");
        message.setWrapText(true);
        message.setMaxWidth(Double.MAX_VALUE);

        Button registerButton = new Button("Criar Conta");
        registerButton.setPrefHeight(46);
        registerButton.setMaxWidth(Double.MAX_VALUE);
        registerButton.setStyle(
                "-fx-background-color: black; " +
                        "-fx-text-fill: white; " +
                        "-fx-font-size: 16; " +
                        "-fx-font-weight: bold; " +
                        "-fx-background-radius: 10; " +
                        "-fx-cursor: hand;"
        );

        Label backText = new Label("Já tem conta?");
        backText.setStyle("-fx-text-fill: #8a8a8a; -fx-font-size: 13;");

        Hyperlink backLink = new Hyperlink("Entrar");
        backLink.setStyle(
                "-fx-text-fill: #1f3ccf; " +
                        "-fx-font-size: 13; " +
                        "-fx-font-weight: bold;"
        );
        backLink.setBorder(Border.EMPTY);
        backLink.setPadding(Insets.EMPTY);

        HBox bottom = new HBox(4, backText, backLink);
        bottom.setAlignment(Pos.CENTER);

        registerButton.setOnAction(e -> {
            if (roleBox.getValue() == null) {
                message.setText("Seleciona uma função.");
                return;
            }

            boolean ok = FakeAuthService.register(
                    emailField.getText(),
                    passwordField.getText(),
                    roleBox.getValue()
            );

            if (!ok) {
                message.setText("Esse email já existe.");
            } else {
                message.setText("");
                app.showLogin();
            }
        });

        backLink.setOnAction(e -> app.showLogin());

        VBox.setMargin(registerButton, new Insets(8, 0, 0, 0));

        card.getChildren().addAll(header, form, registerButton, bottom, message);

        StackPane center = new StackPane(card);
        center.setPadding(new Insets(40));
        center.setAlignment(Pos.CENTER);

        root.setCenter(center);
        return root;
    }
}