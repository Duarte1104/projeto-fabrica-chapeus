package com.teuprojeto.desktop.view.rececionista;

import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class RececionistaCriarEncomendaPage {

    private final RececionistaShellView shell;

    public RececionistaCriarEncomendaPage(RececionistaShellView shell) {
        this.shell = shell;
    }

    public Parent getView() {
        VBox root = RececionistaUiFactory.createPageContainer("Criar Encomenda");

        GridPane form = new GridPane();
        form.setHgap(16);
        form.setVgap(14);
        form.setPadding(new Insets(10, 0, 0, 0));

        ComboBox<String> cliente = new ComboBox<>();
        cliente.getItems().addAll("João Santos", "Ana Costa", "Pedro Lima");

        DatePicker entrega = new DatePicker();

        CheckBox temDesign = new CheckBox("Precisa de design");

        TextArea descricaoDesign = new TextArea();
        descricaoDesign.setPromptText("Descreve o pedido do design...");
        descricaoDesign.setPrefRowCount(4);

        TextArea observacoes = new TextArea();
        observacoes.setPromptText("Observações da encomenda...");
        observacoes.setPrefRowCount(4);

        form.add(new Label("Cliente"), 0, 0);
        form.add(cliente, 0, 1);

        form.add(new Label("Data de entrega"), 1, 0);
        form.add(entrega, 1, 1);

        form.add(temDesign, 0, 2);

        form.add(new Label("Descrição do design"), 0, 3);
        form.add(descricaoDesign, 0, 4, 2, 1);

        form.add(new Label("Observações"), 0, 5);
        form.add(observacoes, 0, 6, 2, 1);

        Button guardar = RececionistaUiFactory.primaryButton("Guardar");
        Button cancelar = RececionistaUiFactory.secondaryButton("Cancelar");
        cancelar.setOnAction(e -> shell.navigateTo(RececionistaPage.ENCOMENDAS_LISTAR));

        HBox buttons = new HBox(10, guardar, cancelar);

        VBox card = RececionistaUiFactory.createCard();
        card.getChildren().addAll(form, buttons);

        root.getChildren().add(card);
        return root;
    }
}