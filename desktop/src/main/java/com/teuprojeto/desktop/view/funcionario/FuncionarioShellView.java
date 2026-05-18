package com.teuprojeto.desktop.view.funcionario;

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

public class FuncionarioShellView {

    private final MainApp app;
    private final AppUser user;

    private final BorderPane root = new BorderPane();
    private final StackPane contentArea = new StackPane();
    private final Map<FuncionarioPage, Button> menuButtons = new LinkedHashMap<>();

    private FuncionarioEncomendaRow encomendaSelecionada;

    public FuncionarioShellView(MainApp app, AppUser user) {
        this.app = app;
        this.user = user;
    }

    public Parent getView() {
        root.setLeft(buildSidebar());

        VBox mainArea = new VBox();
        mainArea.setStyle("-fx-background-color: #f4f7fb;");

        contentArea.setStyle("-fx-background-color: #f4f7fb;");
        VBox.setVgrow(contentArea, Priority.ALWAYS);

        mainArea.getChildren().addAll(buildTopbar(), contentArea);

        root.setCenter(mainArea);

        setPage(FuncionarioPage.DASHBOARD);

        return root;
    }

    private VBox buildSidebar() {
        VBox sidebar = new VBox();
        sidebar.setPrefWidth(260);
        sidebar.setMinWidth(260);
        sidebar.setMaxWidth(260);
        sidebar.setStyle(
                "-fx-background-color: linear-gradient(to bottom, #07152f, #0b2a66);" +
                        "-fx-effect: dropshadow(gaussian, rgba(15,23,42,0.25), 22, 0, 8, 0);"
        );

        VBox brandBox = new VBox(8);
        brandBox.setPadding(new Insets(34, 26, 34, 26));

        Label brand = new Label("FÁBRICA\nDE CHAPÉUS");
        brand.setStyle(
                "-fx-text-fill: white;" +
                        "-fx-font-size: 22;" +
                        "-fx-font-weight: bold;" +
                        "-fx-line-spacing: 2;"
        );

        Label subtitle = new Label("Sistema de Gestão");
        subtitle.setStyle(
                "-fx-text-fill: #93c5fd;" +
                        "-fx-font-size: 13;" +
                        "-fx-font-weight: bold;"
        );

        brandBox.getChildren().addAll(brand, subtitle);

        VBox menuBox = new VBox(12);
        menuBox.setPadding(new Insets(18, 18, 18, 18));

        Label menuLabel = new Label("FUNCIONÁRIO");
        menuLabel.setStyle(
                "-fx-text-fill: #bfdbfe;" +
                        "-fx-font-size: 11;" +
                        "-fx-font-weight: bold;" +
                        "-fx-padding: 0 0 8 10;"
        );

        menuBox.getChildren().add(menuLabel);

        addMenuButton(menuBox, "Dashboard", FuncionarioPage.DASHBOARD);
        addMenuButton(menuBox, "Minhas Encomendas", FuncionarioPage.MINHAS_ENCOMENDAS);
        addMenuButton(menuBox, "Histórico", FuncionarioPage.HISTORICO);

        Region spacer = new Region();
        VBox.setVgrow(spacer, Priority.ALWAYS);

        VBox userBox = new VBox(6);
        userBox.setPadding(new Insets(16));
        userBox.setStyle(
                "-fx-background-color: rgba(255,255,255,0.10);" +
                        "-fx-background-radius: 18;" +
                        "-fx-border-color: rgba(255,255,255,0.18);" +
                        "-fx-border-radius: 18;"
        );

        Label role = new Label("Funcionário");
        role.setStyle("-fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 14;");

        Label area = new Label("Produção");
        area.setStyle("-fx-text-fill: #bfdbfe; -fx-font-size: 12;");

        userBox.getChildren().addAll(role, area);

        Button logoutBtn = new Button("Logout");
        logoutBtn.setMaxWidth(Double.MAX_VALUE);
        logoutBtn.setPrefHeight(46);
        logoutBtn.setStyle(
                "-fx-background-color: transparent;" +
                        "-fx-border-color: rgba(248,113,113,0.75);" +
                        "-fx-border-width: 1.5;" +
                        "-fx-text-fill: #fecaca;" +
                        "-fx-font-weight: bold;" +
                        "-fx-background-radius: 14;" +
                        "-fx-border-radius: 14;" +
                        "-fx-cursor: hand;"
        );
        logoutBtn.setOnAction(e -> app.showLogin());

        VBox bottomBox = new VBox(14, userBox, logoutBtn);
        bottomBox.setPadding(new Insets(12, 18, 26, 18));

        sidebar.getChildren().addAll(brandBox, menuBox, spacer, bottomBox);
        return sidebar;
    }

    private HBox buildTopbar() {
        return AppTopBar.create(
                user,
                "Funcionário",
                () -> ProfileDialogUtil.abrirAlterarPassword(user),
                () -> app.showLogin()
        );
    }

    private void addMenuButton(VBox parent, String text, FuncionarioPage page) {
        Button btn = new Button(text);
        btn.setMaxWidth(Double.MAX_VALUE);
        btn.setPrefHeight(52);
        btn.setStyle(buttonStyle(false));
        btn.setOnAction(e -> setPage(page));

        menuButtons.put(page, btn);
        parent.getChildren().add(btn);
    }

    private void setPage(FuncionarioPage page) {
        for (Map.Entry<FuncionarioPage, Button> entry : menuButtons.entrySet()) {
            entry.getValue().setStyle(buttonStyle(entry.getKey() == page));
        }

        Parent pageView = switch (page) {
            case DASHBOARD -> new FuncionarioDashboardPage(this).getView();
            case MINHAS_ENCOMENDAS -> new FuncionarioMinhasEncomendasPage(this).getView();
            case HISTORICO -> new FuncionarioHistoricoPage(this).getView();
            case ATUALIZAR_PRODUCAO -> new FuncionarioAtualizarProducaoPage(this).getView();
        };

        contentArea.getChildren().setAll(pageView);
    }

    public void navigateTo(FuncionarioPage page) {
        setPage(page);
    }

    public FuncionarioEncomendaRow getEncomendaSelecionada() {
        return encomendaSelecionada;
    }

    public void setEncomendaSelecionada(FuncionarioEncomendaRow encomendaSelecionada) {
        this.encomendaSelecionada = encomendaSelecionada;
    }

    public Long getFuncionarioId() {
        return 1L;
    }

    private String buttonStyle(boolean active) {
        if (active) {
            return "-fx-background-color: linear-gradient(to right, #2563eb, #1d4ed8);" +
                    "-fx-text-fill: white;" +
                    "-fx-font-weight: bold;" +
                    "-fx-background-radius: 16;" +
                    "-fx-cursor: hand;" +
                    "-fx-alignment: center-left;" +
                    "-fx-padding: 0 0 0 18;" +
                    "-fx-effect: dropshadow(gaussian, rgba(37,99,235,0.35), 14, 0, 0, 5);";
        }

        return "-fx-background-color: transparent;" +
                "-fx-text-fill: #e5e7eb;" +
                "-fx-font-weight: bold;" +
                "-fx-background-radius: 16;" +
                "-fx-cursor: hand;" +
                "-fx-alignment: center-left;" +
                "-fx-padding: 0 0 0 18;";
    }
}