package com.teuprojeto.desktop.view.gestor;

import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class GestorDashboardPage {

    private final GestorShellView shell;

    public GestorDashboardPage(GestorShellView shell) {
        this.shell = shell;
    }

    public Parent getView() {
        VBox root = GestorUiFactory.createPageContainer("Dashboard");

        HBox stats = new HBox(18);

        VBox card1 = statCard("Saldo Atual", "€114 000", "+12.5%");
        VBox card2 = statCard("Faturação", "€328 000", "+15.3%");
        VBox card3 = statCard("Despesas", "€214 000", "+8.2%");
        VBox card4 = statCard("Stock Baixo", "2 materiais", "Atenção");

        stats.getChildren().addAll(card1, card2, card3, card4);

        HBox blocosMeio = new HBox(18);

        VBox despesasRecentes = GestorUiFactory.createCard();
        despesasRecentes.setPrefWidth(520);
        despesasRecentes.getChildren().addAll(
                sectionTitle("Últimas Despesas"),
                new Label("DESP-123  |  Fita Decorativa  |  DecorTex  |  €110.00"),
                new Label("DESP-122  |  Couro  |  Couros SA  |  €426.00"),
                new Label("DESP-121  |  Linho  |  TextiPro  |  €1300.00")
        );

        VBox stockBaixo = GestorUiFactory.createCard();
        stockBaixo.setPrefWidth(520);
        stockBaixo.getChildren().addAll(
                sectionTitle("Materiais com Stock Baixo"),
                new Label("Couro  |  Atual: 48  |  Mínimo: 60"),
                new Label("Caixa Embalagem  |  Atual: 35  |  Mínimo: 30")
        );

        blocosMeio.getChildren().addAll(despesasRecentes, stockBaixo);

        VBox resumo = GestorUiFactory.createCard();
        resumo.getChildren().addAll(
                sectionTitle("Resumo Financeiro"),
                new Label("Receitas Totais: €328 000"),
                new Label("Despesas Totais: €214 000"),
                new Label("Balanço Atual: €114 000"),
                new Label("Este dashboard é mock e só ficará dinâmico quando ligarmos ao backend.")
        );

        root.getChildren().addAll(stats, blocosMeio, resumo);
        return root;
    }

    private VBox statCard(String titulo, String valor, String variacao) {
        VBox card = GestorUiFactory.createCard();
        card.setPrefWidth(240);

        Label l1 = new Label(titulo);
        l1.setStyle("-fx-text-fill: #666; -fx-font-size: 14;");

        Label l2 = new Label(valor);
        l2.setStyle("-fx-font-size: 26; -fx-font-weight: bold;");

        Label l3 = new Label(variacao);
        l3.setStyle("-fx-text-fill: #16a34a; -fx-font-size: 12; -fx-font-weight: bold;");

        card.getChildren().addAll(l1, l2, l3);
        return card;
    }

    private Label sectionTitle(String text) {
        Label label = new Label(text);
        label.setStyle("-fx-font-size: 18; -fx-font-weight: bold;");
        return label;
    }
}