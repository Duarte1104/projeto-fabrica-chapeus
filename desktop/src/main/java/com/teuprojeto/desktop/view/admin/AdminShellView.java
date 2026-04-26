package com.teuprojeto.desktop.view.admin;

import com.teuprojeto.desktop.MainApp;
import com.teuprojeto.desktop.model.AppUser;
import com.teuprojeto.desktop.view.ProfileDialogUtil;
import com.teuprojeto.desktop.view.common.AppTopBar;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.*;

import java.util.LinkedHashMap;
import java.util.Map;

public class AdminShellView {

    private final MainApp app;
    private final AppUser user;

    private final BorderPane root = new BorderPane();
    private final StackPane contentArea = new StackPane();
    private final Map<AdminPage, Button> menuButtons = new LinkedHashMap<>();

    public AdminShellView(MainApp app, AppUser user) {
        this.app = app;
        this.user = user;
    }

    public Parent getView() {
        root.setLeft(buildSidebar());
        root.setTop(
                AppTopBar.create(
                        user,
                        "Admin",
                        () -> ProfileDialogUtil.abrirAlterarPassword(user),
                        () -> app.showLogin()
                )
        );
        root.setCenter(contentArea);

        contentArea.setStyle("-fx-background-color: #efefef;");
        setPage(AdminPage.CRIAR_UTILIZADOR);

        return root;
    }

    private VBox buildSidebar() {
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

        VBox menuBox = new VBox(14);
        menuBox.setPadding(new Insets(28, 18, 18, 18));

        addMenuButton(menuBox, "Criar Utilizador", AdminPage.CRIAR_UTILIZADOR);
        addMenuButton(menuBox, "Utilizadores", AdminPage.UTILIZADORES);

        Region spacer = new Region();
        VBox.setVgrow(spacer, Priority.ALWAYS);

        Button logoutBtn = new Button("Logout");
        logoutBtn.setMaxWidth(Double.MAX_VALUE);
        logoutBtn.setPrefHeight(42);
        logoutBtn.setOnAction(e -> app.showLogin());
        logoutBtn.setStyle("-fx-background-color: rgba(255,255,255,0.12); -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 12;");

        VBox bottomBox = new VBox(logoutBtn);
        bottomBox.setPadding(new Insets(12, 18, 22, 18));

        sidebar.getChildren().addAll(brandBox, menuBox, spacer, bottomBox);
        return sidebar;
    }

    private void addMenuButton(VBox parent, String text, AdminPage page) {
        Button btn = new Button(text);
        btn.setMaxWidth(Double.MAX_VALUE);
        btn.setPrefHeight(44);
        btn.setStyle(buttonStyle(false));
        btn.setOnAction(e -> setPage(page));

        menuButtons.put(page, btn);
        parent.getChildren().add(btn);
    }

    private void setPage(AdminPage page) {
        for (Map.Entry<AdminPage, Button> entry : menuButtons.entrySet()) {
            entry.getValue().setStyle(buttonStyle(entry.getKey() == page));
        }

        Parent pageView = switch (page) {
            case CRIAR_UTILIZADOR -> new AdminCriarUtilizadorPage(this).getView();
            case UTILIZADORES -> new AdminUtilizadoresPage(this).getView();
        };

        contentArea.getChildren().setAll(pageView);
    }

    public void navigateTo(AdminPage page) {
        setPage(page);
    }

    private String buttonStyle(boolean active) {
        if (active) {
            return "-fx-background-color: white; " +
                    "-fx-text-fill: #16235c; " +
                    "-fx-font-weight: bold; " +
                    "-fx-background-radius: 12; " +
                    "-fx-cursor: hand;";
        }

        return "-fx-background-color: rgba(255,255,255,0.12); " +
                "-fx-text-fill: white; " +
                "-fx-font-weight: bold; " +
                "-fx-background-radius: 12; " +
                "-fx-cursor: hand;";
    }
}