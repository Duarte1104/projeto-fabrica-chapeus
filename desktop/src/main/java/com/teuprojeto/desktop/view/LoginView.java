package com.teuprojeto.desktop.view;

import com.teuprojeto.desktop.MainApp;
import com.teuprojeto.desktop.model.AppUser;
import com.teuprojeto.desktop.model.FakeAuthService;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.*;

public class LoginView {

    private final MainApp app;

    public LoginView(MainApp app) {
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

        Label title = new Label("Fábrica de chapéus");
        title.setStyle("-fx-font-size: 20; -fx-font-weight: bold;");

        Label subtitle = new Label("Login");
        subtitle.setStyle("-fx-text-fill: #666666;");

        TextField emailField = new TextField();
        emailField.setPromptText("Email");

        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Password");

        Label message = new Label();
        message.setStyle("-fx-text-fill: red;");

        Button loginButton = new Button("Entrar");
        loginButton.setMaxWidth(Double.MAX_VALUE);
        loginButton.setStyle("-fx-background-color: black; -fx-text-fill: white;");

        Hyperlink registerLink = new Hyperlink("Criar conta");

        loginButton.setOnAction(e -> {
            AppUser user = FakeAuthService.login(emailField.getText(), passwordField.getText());
            if (user == null) {
                message.setText("Credenciais inválidas.");
            } else {
                app.showDashboard(user);
            }
        });

        registerLink.setOnAction(e -> app.showRegister());

        card.getChildren().addAll(title, subtitle, emailField, passwordField, loginButton, registerLink, message);

        StackPane center = new StackPane(card);
        root.setCenter(center);

        return root;
    }
}