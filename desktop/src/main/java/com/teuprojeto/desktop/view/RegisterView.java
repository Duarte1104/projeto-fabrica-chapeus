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

        VBox card = new VBox(12);
        card.setAlignment(Pos.CENTER);
        card.setPadding(new Insets(30));
        card.setMaxWidth(360);
        card.setStyle("-fx-background-color: white; -fx-background-radius: 12;");

        Label title = new Label("Registo");
        title.setStyle("-fx-font-size: 20; -fx-font-weight: bold;");

        TextField emailField = new TextField();
        emailField.setPromptText("Email");

        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Password");

        ComboBox<UserRole> roleBox = new ComboBox<>();
        roleBox.getItems().addAll(UserRole.values());
        roleBox.setPromptText("Seleciona a tua função");
        roleBox.setMaxWidth(Double.MAX_VALUE);

        Label message = new Label();
        message.setStyle("-fx-text-fill: red;");

        Button registerButton = new Button("Criar Conta");
        registerButton.setMaxWidth(Double.MAX_VALUE);
        registerButton.setStyle("-fx-background-color: black; -fx-text-fill: white;");

        Hyperlink backLink = new Hyperlink("Voltar ao login");

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
                app.showLogin();
            }
        });

        backLink.setOnAction(e -> app.showLogin());

        card.getChildren().addAll(title, emailField, passwordField, roleBox, registerButton, backLink, message);

        StackPane center = new StackPane(card);
        root.setCenter(center);

        return root;
    }
}