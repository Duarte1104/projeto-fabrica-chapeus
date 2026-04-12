package com.teuprojeto.desktop.view.funcionario;

import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

public class FuncionarioGastosMaterialPage {

    private final FuncionarioShellView shell;
    private final VBox linhasMateriais = new VBox(10);

    public FuncionarioGastosMaterialPage(FuncionarioShellView shell) {
        this.shell = shell;
    }

    public Parent getView() {
        VBox root = FuncionarioUiFactory.createPageContainer("Gastos de Material");

        VBox card = FuncionarioUiFactory.createCard();

        ComboBox<String> encomendaCombo = new ComboBox<>();
        for (FuncionarioEncomendaRow encomenda : MockFuncionarioData.getEncomendas()) {
            encomendaCombo.getItems().add(encomenda.getCodigoEncomenda() + " - " + encomenda.getProduto());
        }

        if (shell.getEncomendaSelecionada() != null) {
            encomendaCombo.setValue(shell.getEncomendaSelecionada().getCodigoEncomenda() + " - " + shell.getEncomendaSelecionada().getProduto());
        }

        encomendaCombo.setMaxWidth(Double.MAX_VALUE);

        card.getChildren().addAll(
                sectionTitle("Registo de Gastos"),
                new Label("Encomenda"),
                encomendaCombo
        );

        linhasMateriais.getChildren().clear();
        linhasMateriais.getChildren().add(criarLinhaMaterial());

        Button adicionarLinha = FuncionarioUiFactory.secondaryButton("+ Adicionar Material");
        adicionarLinha.setOnAction(e -> linhasMateriais.getChildren().add(criarLinhaMaterial()));

        Button enviar = FuncionarioUiFactory.primaryButton("Enviar Gestor");
        enviar.setOnAction(e -> mostrarIndisponivel());

        Button cancelar = FuncionarioUiFactory.secondaryButton("Cancelar");
        cancelar.setOnAction(e -> shell.navigateTo(FuncionarioPage.DASHBOARD));

        HBox botoes = new HBox(10, enviar, cancelar);

        card.getChildren().addAll(linhasMateriais, adicionarLinha, botoes);

        root.getChildren().add(card);
        return FuncionarioUiFactory.wrapInScroll(root);
    }

    private VBox criarLinhaMaterial() {
        VBox bloco = new VBox(8);
        bloco.setPadding(new Insets(12));
        bloco.setStyle("-fx-background-color: #fafafa; -fx-border-color: #e5e5e5; -fx-border-radius: 8; -fx-background-radius: 8;");

        HBox row1 = new HBox(12);

        ComboBox<String> material = new ComboBox<>();
        material.getItems().addAll(MockFuncionarioData.getMateriais());
        material.setPromptText("Selecionar material");
        material.setMaxWidth(Double.MAX_VALUE);

        TextField quantidade = new TextField();
        quantidade.setPromptText("Quantidade");

        HBox.setHgrow(material, Priority.ALWAYS);

        row1.getChildren().addAll(material, quantidade);

        TextArea observacoes = new TextArea();
        observacoes.setPromptText("Observações...");
        observacoes.setPrefRowCount(2);

        bloco.getChildren().addAll(row1, observacoes);
        return bloco;
    }

    private Label sectionTitle(String text) {
        Label label = new Label(text);
        label.setStyle("-fx-font-size: 18; -fx-font-weight: bold;");
        return label;
    }

    private void mostrarIndisponivel() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setHeaderText("Função ainda não disponível");
        alert.setContentText("O envio de gastos de material só vai funcionar quando ligarmos ao backend.");
        alert.showAndWait();
    }
}