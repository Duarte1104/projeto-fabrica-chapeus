package com.teuprojeto.desktop.view.gestor;

import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class GestorFaturacaoPage {

    private final GestorShellView shell;

    public GestorFaturacaoPage(GestorShellView shell) {
        this.shell = shell;
    }

    public Parent getView() {
        VBox root = GestorUiFactory.createPageContainer("Consultar Faturação");

        HBox stats = new HBox(18);
        stats.getChildren().addAll(
                statCard("Faturação Total", "€328 000", "+15.3% vs ano anterior"),
                statCard("IVA Total", "€75 440", "IVA a entregar ao Estado"),
                statCard("Faturação Média", "€54 667", "Por mês")
        );

        VBox chartCard = GestorUiFactory.createCard();
        chartCard.getChildren().addAll(
                title("Evolução da Faturação (6 meses)"),
                new Label("Jan  | Faturação 45 000 | IVA 10 350 | Líquido 34 650"),
                new Label("Fev  | Faturação 52 000 | IVA 11 960 | Líquido 40 040"),
                new Label("Mar  | Faturação 48 000 | IVA 11 040 | Líquido 36 960"),
                new Label("Abr  | Faturação 61 000 | IVA 14 030 | Líquido 46 970"),
                new Label("Mai  | Faturação 55 000 | IVA 12 650 | Líquido 42 350"),
                new Label("Jun  | Faturação 68 000 | IVA 15 640 | Líquido 52 360")
        );

        VBox tabelaCard = GestorUiFactory.createCard();
        tabelaCard.getChildren().addAll(
                title("Faturas Recentes"),
                new Label("FT-2026/234 | 15/03/2026 | João Santos | €330.58 | Paga"),
                new Label("FT-2026/233 | 14/03/2026 | Ana Costa | €55.35 | Pendente"),
                new Label("FT-2026/232 | 12/03/2026 | Pedro Silva | €153.75 | Paga")
        );

        root.getChildren().addAll(stats, chartCard, tabelaCard);
        return root;
    }

    private VBox statCard(String titulo, String valor, String subtitulo) {
        VBox card = GestorUiFactory.createCard();
        card.setPrefWidth(300);

        Label l1 = new Label(titulo);
        l1.setStyle("-fx-text-fill: #666; -fx-font-size: 14;");

        Label l2 = new Label(valor);
        l2.setStyle("-fx-font-size: 26; -fx-font-weight: bold;");

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