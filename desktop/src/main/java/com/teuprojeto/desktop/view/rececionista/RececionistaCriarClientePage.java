package com.teuprojeto.desktop.view.rececionista;

import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class RececionistaCriarClientePage {

    private final RececionistaShellView shell;

    public RececionistaCriarClientePage(RececionistaShellView shell) {
        this.shell = shell;
    }

    public Parent getView() {
        VBox root = RececionistaUiFactory.createPageContainer("Criar Cliente");

        GridPane form = new GridPane();
        form.setHgap(16);
        form.setVgap(14);
        form.setPadding(new Insets(10, 0, 0, 0));

        TextField nome = new TextField();
        TextField email = new TextField();
        TextField telefone = new TextField();
        TextField nif = new TextField();
        ComboBox<String> tipo = new ComboBox<>();
        tipo.getItems().addAll("Particular", "Empresa");
        TextField rua = new TextField();
        TextField porta = new TextField();
        TextField codPostal = new TextField();
        TextField cidade = new TextField();
        TextArea observacoes = new TextArea();
        observacoes.setPrefRowCount(4);

        form.add(new Label("Nome"), 0, 0);
        form.add(nome, 0, 1);
        form.add(new Label("Tipo"), 1, 0);
        form.add(tipo, 1, 1);

        form.add(new Label("Email"), 0, 2);
        form.add(email, 0, 3);
        form.add(new Label("Telefone"), 1, 2);
        form.add(telefone, 1, 3);

        form.add(new Label("NIF"), 0, 4);
        form.add(nif, 0, 5);
        form.add(new Label("Cidade"), 1, 4);
        form.add(cidade, 1, 5);

        form.add(new Label("Rua"), 0, 6);
        form.add(rua, 0, 7);
        form.add(new Label("Porta"), 1, 6);
        form.add(porta, 1, 7);

        form.add(new Label("Código Postal"), 0, 8);
        form.add(codPostal, 0, 9);

        form.add(new Label("Observações"), 0, 10);
        form.add(observacoes, 0, 11, 2, 1);

        Button guardar = RececionistaUiFactory.primaryButton("Guardar");
        Button cancelar = RececionistaUiFactory.secondaryButton("Cancelar");
        cancelar.setOnAction(e -> shell.navigateTo(RececionistaPage.CLIENTES_LISTAR));

        HBox buttons = new HBox(10, guardar, cancelar);

        VBox card = RececionistaUiFactory.createCard();
        card.getChildren().addAll(form, buttons);

        root.getChildren().add(card);
        return root;
    }
}