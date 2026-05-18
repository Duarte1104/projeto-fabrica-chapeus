package com.teuprojeto.desktop.view.designer;

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

public class DesignerShellView {

    private final MainApp app;
    private final AppUser user;

    private final BorderPane root = new BorderPane();
    private final StackPane contentArea = new StackPane();
    private final Map<DesignerPage, Button> menuButtons = new LinkedHashMap<>();

    private PedidoDesignRow pedidoSelecionado;

    public DesignerShellView(MainApp app, AppUser user) {
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

        setPage(DesignerPage.DASHBOARD);

        return root;
    }

    private VBox buildSidebar() {
        VBox sidebar = new VBox();
        sidebar.setPrefWidth(260);
        sidebar.setMinWidth(260);
        sidebar.setMaxWidth(260);
        sidebar.setStyle("-fx-background-color: linear-gradient(to bottom, #07152f, #0b2a66);");

        VBox brandBox = new VBox(8);
        brandBox.setPadding(new Insets(34, 26, 34, 26));

        Label brand = new Label("FÁBRICA\nDE CHAPÉUS");
        brand.setStyle("-fx-text-fill: white; -fx-font-size: 22; -fx-font-weight: bold;");

        Label subtitle = new Label("Sistema de Gestão");
        subtitle.setStyle("-fx-text-fill: #93c5fd; -fx-font-size: 13; -fx-font-weight: bold;");

        brandBox.getChildren().addAll(brand, subtitle);

        VBox menuBox = new VBox(12);
        menuBox.setPadding(new Insets(18));

        Label menuLabel = new Label("DESIGNER");
        menuLabel.setStyle("-fx-text-fill: #bfdbfe; -fx-font-size: 11; -fx-font-weight: bold; -fx-padding: 0 0 8 10;");

        menuBox.getChildren().add(menuLabel);

        addMenuButton(menuBox, "Dashboard", DesignerPage.DASHBOARD);
        addMenuButton(menuBox, "Pedidos de Design", DesignerPage.PEDIDOS_DESIGN);
        addMenuButton(menuBox, "Histórico", DesignerPage.HISTORICO);

        Region spacer = new Region();
        VBox.setVgrow(spacer, Priority.ALWAYS);

        VBox userBox = userBox("Designer", "Propostas e protótipos");

        Button logoutBtn = logoutButton();
        logoutBtn.setOnAction(e -> app.showLogin());

        VBox bottomBox = new VBox(14, userBox, logoutBtn);
        bottomBox.setPadding(new Insets(12, 18, 26, 18));

        sidebar.getChildren().addAll(brandBox, menuBox, spacer, bottomBox);
        return sidebar;
    }

    private HBox buildTopbar() {
        return AppTopBar.create(
                user,
                "Designer",
                () -> ProfileDialogUtil.abrirAlterarPassword(user),
                () -> app.showLogin()
        );
    }

    private void addMenuButton(VBox parent, String text, DesignerPage page) {
        Button btn = new Button(text);
        btn.setMaxWidth(Double.MAX_VALUE);
        btn.setPrefHeight(52);
        btn.setStyle(buttonStyle(false));
        btn.setOnAction(e -> setPage(page));

        menuButtons.put(page, btn);
        parent.getChildren().add(btn);
    }

    private void setPage(DesignerPage page) {
        for (Map.Entry<DesignerPage, Button> entry : menuButtons.entrySet()) {
            entry.getValue().setStyle(buttonStyle(entry.getKey() == page));
        }

        Parent pageView = switch (page) {
            case DASHBOARD -> new DesignerDashboardPage(this).getView();
            case PEDIDOS_DESIGN -> new DesignerPedidosDesignPage(this).getView();
            case CRIAR_PROPOSTA -> new DesignerCriarPropostaPage(this).getView();
            case HISTORICO -> new DesignerHistoricoPage(this).getView();
        };

        contentArea.getChildren().setAll(pageView);
    }

    public void navigateTo(DesignerPage page) {
        setPage(page);
    }

    public PedidoDesignRow getPedidoSelecionado() {
        return pedidoSelecionado;
    }

    public void setPedidoSelecionado(PedidoDesignRow pedidoSelecionado) {
        this.pedidoSelecionado = pedidoSelecionado;
    }

    private VBox userBox(String roleText, String areaText) {
        VBox box = new VBox(6);
        box.setPadding(new Insets(16));
        box.setStyle("-fx-background-color: rgba(255,255,255,0.10); -fx-background-radius: 18; -fx-border-color: rgba(255,255,255,0.18); -fx-border-radius: 18;");

        Label role = new Label(roleText);
        role.setStyle("-fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 14;");

        Label area = new Label(areaText);
        area.setStyle("-fx-text-fill: #bfdbfe; -fx-font-size: 12;");

        box.getChildren().addAll(role, area);
        return box;
    }

    private Button logoutButton() {
        Button btn = new Button("Logout");
        btn.setMaxWidth(Double.MAX_VALUE);
        btn.setPrefHeight(46);
        btn.setStyle("-fx-background-color: transparent; -fx-border-color: rgba(248,113,113,0.75); -fx-border-width: 1.5; -fx-text-fill: #fecaca; -fx-font-weight: bold; -fx-background-radius: 14; -fx-border-radius: 14; -fx-cursor: hand;");
        return btn;
    }

    private String buttonStyle(boolean active) {
        if (active) {
            return "-fx-background-color: linear-gradient(to right, #2563eb, #1d4ed8);" +
                    "-fx-text-fill: white;" +
                    "-fx-font-weight: bold;" +
                    "-fx-background-radius: 16;" +
                    "-fx-cursor: hand;" +
                    "-fx-alignment: center-left;" +
                    "-fx-padding: 0 0 0 18;";
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