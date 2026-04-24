package com.teuprojeto.desktop.view.rececionista;

import com.teuprojeto.desktop.MainApp;
import com.teuprojeto.desktop.model.AppUser;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.*;

import java.util.LinkedHashMap;
import java.util.Map;

public class RececionistaShellView {

    private final MainApp app;
    private final AppUser user;

    private final BorderPane root = new BorderPane();
    private final StackPane contentArea = new StackPane();
    private final Map<RececionistaPage, Button> menuButtons = new LinkedHashMap<>();

    private ClienteRow clienteSelecionado;
    private Long encomendaSelecionadaId;

    public RececionistaShellView(MainApp app, AppUser user) {
        this.app = app;
        this.user = user;
    }

    public Parent getView() {
        root.setLeft(buildSidebar());
        root.setTop(buildTopbar());
        root.setCenter(contentArea);

        contentArea.setStyle("-fx-background-color: #efefef;");
        setPage(RececionistaPage.DASHBOARD);

        return root;
    }

    private VBox buildSidebar() {
        VBox sidebar = new VBox();
        sidebar.setPrefWidth(230);
        sidebar.setStyle("-fx-background-color: linear-gradient(to bottom, #14245c, #1d38b8);");

        VBox brandBox = new VBox(6);
        brandBox.setPadding(new Insets(28, 24, 28, 24));
        brandBox.setStyle("-fx-border-color: rgba(255,255,255,0.10); -fx-border-width: 0 0 1 0;");

        Label brand = new Label("Fábrica de chapéus");
        brand.setStyle("-fx-text-fill: white; -fx-font-size: 22; -fx-font-weight: bold;");

        Label subtitle = new Label("Sistema de Gestão");
        subtitle.setStyle("-fx-text-fill: #d7defa; -fx-font-size: 13;");

        brandBox.getChildren().addAll(brand, subtitle);

        VBox menuBox = new VBox(14);
        menuBox.setPadding(new Insets(28, 18, 18, 18));

        addMenuButton(menuBox, "Dashboard", RececionistaPage.DASHBOARD);
        addMenuButton(menuBox, "Clientes", RececionistaPage.CLIENTES_LISTAR);
        addMenuButton(menuBox, "Encomendas", RececionistaPage.ENCOMENDAS_LISTAR);
        addMenuButton(menuBox, "Faturas", RececionistaPage.FATURAS);

        Region spacer = new Region();
        VBox.setVgrow(spacer, Priority.ALWAYS);

        Button logoutBtn = new Button("Logout");
        logoutBtn.setMaxWidth(Double.MAX_VALUE);
        logoutBtn.setPrefHeight(42);
        logoutBtn.setStyle(buttonStyle(false));
        logoutBtn.setOnAction(e -> app.showLogin());

        VBox bottomBox = new VBox(logoutBtn);
        bottomBox.setPadding(new Insets(12, 18, 22, 18));

        sidebar.getChildren().addAll(brandBox, menuBox, spacer, bottomBox);
        return sidebar;
    }

    private HBox buildTopbar() {
        HBox topbar = new HBox();
        topbar.setAlignment(Pos.CENTER_RIGHT);
        topbar.setPadding(new Insets(18, 24, 18, 24));
        topbar.setStyle("-fx-background-color: white; -fx-border-color: #d8d8d8; -fx-border-width: 0 0 1 0;");

        Label userLabel = new Label(user.getEmail() + "  |  Rececionista");
        userLabel.setStyle("-fx-font-size: 14; -fx-text-fill: #222;");
        topbar.getChildren().add(userLabel);

        return topbar;
    }

    private void addMenuButton(VBox parent, String text, RececionistaPage page) {
        Button btn = new Button(text);
        btn.setMaxWidth(Double.MAX_VALUE);
        btn.setPrefHeight(44);
        btn.setStyle(buttonStyle(false));
        btn.setOnAction(e -> setPage(page));

        menuButtons.put(page, btn);
        parent.getChildren().add(btn);
    }

    private void setPage(RececionistaPage page) {
        for (Map.Entry<RececionistaPage, Button> entry : menuButtons.entrySet()) {
            entry.getValue().setStyle(buttonStyle(entry.getKey() == page));
        }

        Parent pageView = switch (page) {
            case DASHBOARD -> new RececionistaDashboardPage(this).getView();
            case CLIENTES_LISTAR -> new RececionistaClientesListPage(this).getView();
            case CLIENTES_CRIAR -> new RececionistaCriarClientePage(this).getView();
            case CLIENTES_VER -> new RececionistaVerClientePage(this).getView();
            case ENCOMENDAS_LISTAR -> new RececionistaEncomendasListPage(this).getView();
            case ENCOMENDAS_CRIAR -> new RececionistaCriarEncomendaPage(this).getView();
            case ENCOMENDAS_VER -> new RececionistaVerEncomendaPage(this).getView();
            case FATURAS -> new RececionistaFaturasPage(this).getView();
        };

        contentArea.getChildren().setAll(pageView);
    }

    public void navigateTo(RececionistaPage page) {
        setPage(page);
    }

    public ClienteRow getClienteSelecionado() {
        return clienteSelecionado;
    }

    public void setClienteSelecionado(ClienteRow clienteSelecionado) {
        this.clienteSelecionado = clienteSelecionado;
    }

    public Long getEncomendaSelecionadaId() {
        return encomendaSelecionadaId;
    }

    public void setEncomendaSelecionadaId(Long encomendaSelecionadaId) {
        this.encomendaSelecionadaId = encomendaSelecionadaId;
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