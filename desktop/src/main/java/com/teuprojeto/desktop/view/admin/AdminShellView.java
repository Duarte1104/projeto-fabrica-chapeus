package com.teuprojeto.desktop.view.admin;

import com.teuprojeto.desktop.MainApp;
import com.teuprojeto.desktop.model.AppUser;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.*;

public class AdminShellView {

    private final MainApp app;
    private final AppUser user;

    public AdminShellView(MainApp app, AppUser user) {
        this.app = app;
        this.user = user;
    }

    public Parent getView() {
        BorderPane root = new BorderPane();

        VBox sidebar = new VBox();
        sidebar.setPrefWidth(230);
        sidebar.setStyle("-fx-background-color: linear-gradient(to bottom, #14245c, #1d38b8);");

        VBox brandBox = new VBox(6);
        brandBox.setPadding(new Insets(28, 24, 28, 24));

        Label brand = new Label("Fábrica de chapéus");
        brand.setStyle("-fx-text-fill: white; -fx-font-size: 22; -fx-font-weight: bold;");

        Label subtitle = new Label("Admin");
        subtitle.setStyle("-fx-text-fill: #d7defa; -fx-font-size: 13;");

        brandBox.getChildren().addAll(brand, subtitle);

        Button logoutBtn = new Button("Logout");
        logoutBtn.setMaxWidth(Double.MAX_VALUE);
        logoutBtn.setPrefHeight(42);
        logoutBtn.setOnAction(e -> app.showLogin());
        logoutBtn.setStyle("-fx-background-color: rgba(255,255,255,0.12); -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 12;");

        Region spacer = new Region();
        VBox.setVgrow(spacer, Priority.ALWAYS);

        VBox bottomBox = new VBox(logoutBtn);
        bottomBox.setPadding(new Insets(12, 18, 22, 18));

        sidebar.getChildren().addAll(brandBox, spacer, bottomBox);

        HBox topbar = new HBox();
        topbar.setAlignment(Pos.CENTER_RIGHT);
        topbar.setPadding(new Insets(18, 24, 18, 24));
        topbar.setStyle("-fx-background-color: white; -fx-border-color: #d8d8d8; -fx-border-width: 0 0 1 0;");

        Label userLabel = new Label(user.getEmail() + "  |  Admin");
        userLabel.setStyle("-fx-font-size: 14; -fx-text-fill: #222;");
        topbar.getChildren().add(userLabel);

        root.setLeft(sidebar);
        root.setTop(topbar);
        root.setCenter(new AdminCriarUtilizadorPage().getView());

        return root;
    }
}