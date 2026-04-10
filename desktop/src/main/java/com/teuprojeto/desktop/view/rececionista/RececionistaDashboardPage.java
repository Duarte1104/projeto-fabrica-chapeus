package com.teuprojeto.desktop.view.rececionista;

import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

public class RececionistaDashboardPage {

    private final RececionistaShellView shell;

    public RececionistaDashboardPage(RececionistaShellView shell) {
        this.shell = shell;
    }

    public Parent getView() {
        VBox root = RececionistaUiFactory.createPageContainer("Dashboard");

        HBox stats = new HBox(16);
        stats.getChildren().addAll(
                createStatCard("Clientes", "124"),
                createStatCard("Encomendas", "38"),
                createStatCard("Prontas", "9"),
                createStatCard("Pagas", "21")
        );

        VBox quickActions = RececionistaUiFactory.createCard();
        Label qaTitle = new Label("Ações rápidas");
        qaTitle.setStyle("-fx-font-size: 18; -fx-font-weight: bold;");

        HBox buttons = new HBox(12);
        var btn1 = RececionistaUiFactory.primaryButton("Listar Clientes");
        btn1.setOnAction(e -> shell.navigateTo(RececionistaPage.CLIENTES_LISTAR));

        var btn2 = RececionistaUiFactory.secondaryButton("Criar Cliente");
        btn2.setOnAction(e -> shell.navigateTo(RececionistaPage.CLIENTES_CRIAR));

        var btn3 = RececionistaUiFactory.secondaryButton("Criar Encomenda");
        btn3.setOnAction(e -> shell.navigateTo(RececionistaPage.ENCOMENDAS_CRIAR));

        buttons.getChildren().addAll(btn1, btn2, btn3);

        quickActions.getChildren().addAll(qaTitle, buttons);

        root.getChildren().addAll(stats, quickActions);
        return root;
    }

    private VBox createStatCard(String label, String value) {
        VBox card = RececionistaUiFactory.createCard();
        card.setPrefWidth(220);

        Label l1 = new Label(label);
        l1.setStyle("-fx-text-fill: #666; -fx-font-size: 14;");

        Label l2 = new Label(value);
        l2.setStyle("-fx-font-size: 28; -fx-font-weight: bold;");

        card.getChildren().addAll(l1, l2);
        HBox.setHgrow(card, Priority.ALWAYS);
        return card;
    }
}