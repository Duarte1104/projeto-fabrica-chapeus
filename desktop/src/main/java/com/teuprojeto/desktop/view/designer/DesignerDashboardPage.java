package com.teuprojeto.desktop.view.designer;

import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class DesignerDashboardPage {

    private final DesignerShellView shell;

    public DesignerDashboardPage(DesignerShellView shell) {
        this.shell = shell;
    }

    public Parent getView() {
        VBox root = DesignerUiFactory.createPageContainer("Dashboard");

        HBox stats = new HBox(18);
        stats.getChildren().addAll(
                statCard("Pendentes", String.valueOf(MockDesignerData.totalPendentes()), "Aguardam design"),
                statCard("Enviados", String.valueOf(MockDesignerData.totalEnviados()), "À espera do cliente"),
                statCard("Aprovados", String.valueOf(MockDesignerData.totalAprovados()), "Prontos para seguir"),
                statCard("Rejeitados", String.valueOf(MockDesignerData.totalRejeitados()), "Rever propostas")
        );

        HBox blocos = new HBox(18);

        VBox pendentes = DesignerUiFactory.createCard();
        pendentes.setPrefWidth(520);
        pendentes.getChildren().addAll(
                sectionTitle("Pedidos Pendentes"),
                new Label("ENC-101 | João Santos | Chapéu Clássico"),
                new Label("ENC-102 | Ana Costa | Chapéu Elegante")
        );

        VBox recentes = DesignerUiFactory.createCard();
        recentes.setPrefWidth(520);
        recentes.getChildren().addAll(
                sectionTitle("Últimos Estados"),
                new Label("ENC-103 | ENVIADO_CLIENTE"),
                new Label("ENC-104 | APROVADO_CLIENTE"),
                new Label("ENC-105 | REJEITADO_CLIENTE")
        );

        blocos.getChildren().addAll(pendentes, recentes);

        VBox resumo = DesignerUiFactory.createCard();
        resumo.getChildren().addAll(
                sectionTitle("Resumo"),
                new Label("Aqui vais acompanhar os pedidos que precisam de proposta de design."),
                new Label("O dashboard é mock por agora e ficará dinâmico quando ligarmos ao backend.")
        );

        root.getChildren().addAll(stats, blocos, resumo);
        return root;
    }

    private VBox statCard(String titulo, String valor, String subtitulo) {
        VBox card = DesignerUiFactory.createCard();
        card.setPrefWidth(240);

        Label l1 = new Label(titulo);
        l1.setStyle("-fx-text-fill: #666; -fx-font-size: 14;");

        Label l2 = new Label(valor);
        l2.setStyle("-fx-font-size: 26; -fx-font-weight: bold;");

        Label l3 = new Label(subtitulo);
        l3.setStyle("-fx-text-fill: #16a34a; -fx-font-size: 12;");

        card.getChildren().addAll(l1, l2, l3);
        return card;
    }

    private Label sectionTitle(String text) {
        Label label = new Label(text);
        label.setStyle("-fx-font-size: 18; -fx-font-weight: bold;");
        return label;
    }
}