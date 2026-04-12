package com.teuprojeto.desktop.view.funcionario;

import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class FuncionarioAtualizarProducaoPage {

    private final FuncionarioShellView shell;

    public FuncionarioAtualizarProducaoPage(FuncionarioShellView shell) {
        this.shell = shell;
    }

    public Parent getView() {
        FuncionarioEncomendaRow encomenda = shell.getEncomendaSelecionada();

        VBox root = FuncionarioUiFactory.createPageContainer("Atualizar Produção");

        if (encomenda == null) {
            VBox card = FuncionarioUiFactory.createCard();

            Label aviso = new Label("Nenhuma encomenda foi selecionada.");
            Button voltar = FuncionarioUiFactory.secondaryButton("Voltar");
            voltar.setOnAction(e -> shell.navigateTo(FuncionarioPage.MINHAS_ENCOMENDAS));

            card.getChildren().addAll(aviso, voltar);
            root.getChildren().add(card);
            return root;
        }

        VBox dados = FuncionarioUiFactory.createCard();
        dados.getChildren().addAll(
                sectionTitle("Encomenda"),
                new Label("Ordem: " + encomenda.getCodigoOrdem()),
                new Label("Encomenda: " + encomenda.getCodigoEncomenda()),
                new Label("Produto: " + encomenda.getProduto()),
                new Label("Cliente: " + encomenda.getCliente())
        );

        GridPane etapas = new GridPane();
        etapas.setHgap(16);
        etapas.setVgap(16);
        etapas.setPadding(new Insets(8, 0, 0, 0));

        CheckBox montagem = new CheckBox("Concluído");
        montagem.setSelected(encomenda.isMontagemConcluida());
        TextArea comentarioMontagem = new TextArea();
        comentarioMontagem.setPromptText("Comentário da montagem...");
        comentarioMontagem.setPrefRowCount(2);

        CheckBox costuras = new CheckBox("Concluído");
        costuras.setSelected(encomenda.isCosturasConcluidas());
        TextArea comentarioCosturas = new TextArea();
        comentarioCosturas.setPromptText("Comentário das costuras...");
        comentarioCosturas.setPrefRowCount(2);

        CheckBox personalizacao = new CheckBox("Concluído");
        personalizacao.setSelected(encomenda.isPersonalizacaoConcluida());
        TextArea comentarioPersonalizacao = new TextArea();
        comentarioPersonalizacao.setPromptText("Comentário da personalização...");
        comentarioPersonalizacao.setPrefRowCount(2);

        etapas.add(new Label("Montagem"), 0, 0);
        etapas.add(montagem, 1, 0);
        etapas.add(comentarioMontagem, 0, 1, 2, 1);

        etapas.add(new Label("Costuras"), 0, 2);
        etapas.add(costuras, 1, 2);
        etapas.add(comentarioCosturas, 0, 3, 2, 1);

        etapas.add(new Label("Personalização"), 0, 4);
        etapas.add(personalizacao, 1, 4);
        etapas.add(comentarioPersonalizacao, 0, 5, 2, 1);

        VBox etapasCard = FuncionarioUiFactory.createCard();
        etapasCard.getChildren().addAll(sectionTitle("Etapas de Produção"), etapas);

        VBox observacoesCard = FuncionarioUiFactory.createCard();
        TextArea observacoes = new TextArea();
        observacoes.setPromptText("Informações adicionais sobre a encomenda...");
        observacoes.setText(encomenda.getObservacoes());
        observacoes.setPrefRowCount(5);

        observacoesCard.getChildren().addAll(
                sectionTitle("Observações"),
                observacoes
        );

        Button guardar = FuncionarioUiFactory.primaryButton("Guardar Progresso");
        guardar.setOnAction(e -> mostrarIndisponivel("guardar progresso"));

        Button concluir = new Button("Marcar como Concluída");
        concluir.setPrefHeight(40);
        concluir.setStyle("-fx-background-color: #22c55e; -fx-text-fill: black; -fx-font-weight: bold; -fx-background-radius: 8;");
        concluir.setOnAction(e -> mostrarIndisponivel("marcar a encomenda como concluída"));

        Button cancelar = FuncionarioUiFactory.secondaryButton("Cancelar");
        cancelar.setOnAction(e -> shell.navigateTo(FuncionarioPage.MINHAS_ENCOMENDAS));

        HBox botoes = new HBox(10, guardar, concluir, cancelar);

        root.getChildren().addAll(dados, etapasCard, observacoesCard, botoes);
        return FuncionarioUiFactory.wrapInScroll(root);
    }

    private Label sectionTitle(String text) {
        Label label = new Label(text);
        label.setStyle("-fx-font-size: 18; -fx-font-weight: bold;");
        return label;
    }

    private void mostrarIndisponivel(String acao) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setHeaderText("Função ainda não disponível");
        alert.setContentText("A função de " + acao + " só vai funcionar quando ligarmos ao backend.");
        alert.showAndWait();
    }
}