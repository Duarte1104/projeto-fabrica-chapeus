package com.teuprojeto.desktop.view;

import com.teuprojeto.desktop.MainApp;
import com.teuprojeto.desktop.model.AppUser;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.*;

public class DashboardView {

    private final MainApp app;
    private final AppUser user;

    public DashboardView(MainApp app, AppUser user) {
        this.app = app;
        this.user = user;
    }

    public Parent getView() {
        BorderPane root = new BorderPane();

        VBox sidebar = new VBox(20);
        sidebar.setPadding(new Insets(20));
        sidebar.setPrefWidth(220);
        sidebar.setStyle("-fx-background-color: linear-gradient(to bottom, #16235c, #1f3ccf);");

        Label brand = new Label("Fábrica de chapéus\nSistema de Gestão");
        brand.setStyle("-fx-text-fill: white; -fx-font-size: 18; -fx-font-weight: bold;");

        Button dashboardBtn = menuButton("Dashboard");
        Button clientesBtn = menuButton("Clientes");
        Button encomendasBtn = menuButton("Encomendas");
        Button faturasBtn = menuButton("Faturas");
        Button logoutBtn = menuButton("Logout");

        logoutBtn.setOnAction(e -> app.showLogin());

        sidebar.getChildren().addAll(brand, dashboardBtn, clientesBtn, encomendasBtn, faturasBtn, logoutBtn);

        HBox topbar = new HBox();
        topbar.setPadding(new Insets(20));
        topbar.setAlignment(Pos.CENTER_RIGHT);
        topbar.setStyle("-fx-background-color: white; -fx-border-color: #dddddd;");
        Label userLabel = new Label(user.getEmail() + " - " + user.getRole());
        topbar.getChildren().add(userLabel);

        VBox content = new VBox(20);
        content.setPadding(new Insets(30));
        content.setStyle("-fx-background-color: #f3f3f3;");

        Label title = new Label("Dashboard");
        title.setStyle("-fx-font-size: 28; -fx-font-weight: bold;");

        VBox card = new VBox(10);
        card.setPadding(new Insets(20));
        card.setStyle("-fx-background-color: white; -fx-background-radius: 10;");
        card.getChildren().addAll(
                new Label("Bem-vindo"),
                new Label("Perfil: " + user.getRole()),
                new Label("Aqui vamos depois ligar os dashboards ao backend.")
        );

        content.getChildren().addAll(title, card);

        root.setLeft(sidebar);
        root.setTop(topbar);
        root.setCenter(content);

        return root;
    }

    private Button menuButton(String text) {
        Button button = new Button(text);
        button.setMaxWidth(Double.MAX_VALUE);
        button.setStyle("-fx-background-color: white; -fx-text-fill: #16235c; -fx-font-weight: bold;");
        return button;
    }
}