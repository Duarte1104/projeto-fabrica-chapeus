package com.teuprojeto.desktop.view.rececionista;

import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class RececionistaVerClientePage {

    private final RececionistaShellView shell;

    public RececionistaVerClientePage(RececionistaShellView shell) {
        this.shell = shell;
    }

    public Parent getView() {
        VBox root = RececionistaUiFactory.createPageContainer("Ver Cliente");

        HBox content = new HBox(18);

        VBox info = RececionistaUiFactory.createCard();
        info.setPrefWidth(340);
        info.getChildren().addAll(
                title("Informações Pessoais"),
                item("Nome", "João Santos"),
                item("Email", "joao@email.com"),
                item("Telefone", "912345678"),
                item("Morada", "Rua Principal, 922"),
                item("NIF", "123456789"),
                item("Tipo", "Particular")
        );

        VBox historico = RececionistaUiFactory.createCard();
        historico.setPrefWidth(600);
        historico.getChildren().addAll(
                title("Histórico de Encomendas"),
                new Label("ENC-1121  |  20/03/2026  |  Chapéu Clássico  |  Concluído"),
                new Label("ENC-1110  |  20/03/2026  |  Chapéu Elegante  |  Enviado"),
                new Label("ENC-1232  |  20/03/2026  |  Chapéu Desportivo  |  Concluído")
        );

        content.getChildren().addAll(info, historico);
        root.getChildren().add(content);

        return root;
    }

    private Label title(String text) {
        Label label = new Label(text);
        label.setStyle("-fx-font-size: 18; -fx-font-weight: bold;");
        return label;
    }

    private VBox item(String label, String value) {
        VBox box = new VBox(4);
        Label l1 = new Label(label);
        l1.setStyle("-fx-text-fill: #666;");
        Label l2 = new Label(value);
        box.getChildren().addAll(l1, l2);
        return box;
    }
}