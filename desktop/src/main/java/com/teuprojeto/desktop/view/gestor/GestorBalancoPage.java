package com.teuprojeto.desktop.view.gestor;

import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class GestorBalancoPage {

    private final GestorShellView shell;

    public GestorBalancoPage(GestorShellView shell) {
        this.shell = shell;
    }

    public Parent getView() {
        VBox root = GestorUiFactory.createPageContainer("Balanço Financeiro");

        HBox stats = new HBox(18);
        stats.getChildren().addAll(
                statCard("Receitas Totais", "€328 000", "+15.3% vs período anterior", "#16a34a"),
                statCard("Despesas Totais", "€214 000", "+8.2% vs período anterior", "#dc2626"),
                statCard("Lucro Líquido", "€114 000", "+28.5% vs período anterior", "#2563eb"),
                statCard("Margem de Lucro", "34.8%", "Excelente performance", "#16a34a")
        );

        VBox chart1 = GestorUiFactory.createCard();
        chart1.getChildren().addAll(
                title("Evolução Receitas vs Despesas (6 meses)"),
                new Label("Jan | Receitas 45 000 | Despesas 32 000"),
                new Label("Fev | Receitas 52 000 | Despesas 35 000"),
                new Label("Mar | Receitas 48 000 | Despesas 33 000"),
                new Label("Abr | Receitas 61 000 | Despesas 38 000"),
                new Label("Mai | Receitas 55 000 | Despesas 36 000"),
                new Label("Jun | Receitas 68 000 | Despesas 40 000")
        );

        VBox chart2 = GestorUiFactory.createCard();
        chart2.getChildren().addAll(
                title("Análise de Lucro Mensal"),
                new Label("Jan | €13 000"),
                new Label("Fev | €17 000"),
                new Label("Mar | €15 000"),
                new Label("Abr | €23 000"),
                new Label("Mai | €19 000"),
                new Label("Jun | €28 000")
        );

        root.getChildren().addAll(stats, chart1, chart2);
        return root;
    }

    private VBox statCard(String titulo, String valor, String subtitulo, String corValor) {
        VBox card = GestorUiFactory.createCard();
        card.setPrefWidth(250);

        Label l1 = new Label(titulo);
        l1.setStyle("-fx-text-fill: #666; -fx-font-size: 14;");

        Label l2 = new Label(valor);
        l2.setStyle("-fx-font-size: 26; -fx-font-weight: bold; -fx-text-fill: " + corValor + ";");

        Label l3 = new Label(subtitulo);
        l3.setStyle("-fx-text-fill: #16a34a; -fx-font-size: 12;");

        card.getChildren().addAll(l1, l2, l3);
        return card;
    }

    private Label title(String text) {
        Label label = new Label(text);
        label.setStyle("-fx-font-size: 18; -fx-font-weight: bold;");
        return label;
    }
}