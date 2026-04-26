package com.teuprojeto.desktop.view.funcionario;

import com.teuprojeto.desktop.dto.AtualizarProducaoEncomendaRequestDto;
import com.teuprojeto.desktop.service.ProducaoApiService;
import javafx.concurrent.Task;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class FuncionarioAtualizarProducaoPage {

    private final FuncionarioShellView shell;
    private final ProducaoApiService producaoApiService = new ProducaoApiService();

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
                new Label("Encomenda: " + encomenda.getCodigoEncomenda()),
                new Label("Produto: " + encomenda.getProduto()),
                new Label("Cliente: " + encomenda.getCliente()),
                new Label("Quantidade total: " + encomenda.getQuantidadeTotal()),
                new Label("Estado: " + encomenda.getEstado())
        );

        GridPane etapas = new GridPane();
        etapas.setHgap(16);
        etapas.setVgap(16);
        etapas.setPadding(new Insets(8, 0, 0, 0));

        CheckBox montagem = new CheckBox("Concluído");
        montagem.setSelected(encomenda.isMontagemConcluida());
        TextArea comentarioMontagem = new TextArea();
        comentarioMontagem.setPromptText("Comentário da montagem...");
        comentarioMontagem.setText(encomenda.getMontagemComentario());
        comentarioMontagem.setPrefRowCount(2);

        CheckBox costuras = new CheckBox("Concluído");
        costuras.setSelected(encomenda.isCosturasConcluidas());
        TextArea comentarioCosturas = new TextArea();
        comentarioCosturas.setPromptText("Comentário das costuras...");
        comentarioCosturas.setText(encomenda.getCosturasComentario());
        comentarioCosturas.setPrefRowCount(2);

        CheckBox personalizacao = new CheckBox("Concluído");
        personalizacao.setSelected(encomenda.isPersonalizacaoConcluida());
        personalizacao.setDisable(!encomenda.isPrecisaPersonalizacao());

        TextArea comentarioPersonalizacao = new TextArea();
        comentarioPersonalizacao.setPromptText("Comentário da personalização...");
        comentarioPersonalizacao.setText(encomenda.getPersonalizacaoComentario());
        comentarioPersonalizacao.setPrefRowCount(2);
        comentarioPersonalizacao.setDisable(!encomenda.isPrecisaPersonalizacao());

        if (!encomenda.isPrecisaPersonalizacao()) {
            comentarioPersonalizacao.setText("Não aplicável para esta encomenda.");
        }

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

        Label estado = new Label("Pronto para atualizar.");
        estado.setStyle("-fx-text-fill: #666666;");

        Button guardar = FuncionarioUiFactory.primaryButton("Guardar Progresso");
        Button concluir = new Button("Marcar como Concluída");
        concluir.setPrefHeight(40);
        concluir.setStyle("-fx-background-color: #22c55e; -fx-text-fill: black; -fx-font-weight: bold; -fx-background-radius: 8;");

        Button cancelar = FuncionarioUiFactory.secondaryButton("Cancelar");
        cancelar.setOnAction(e -> shell.navigateTo(FuncionarioPage.MINHAS_ENCOMENDAS));

        guardar.setOnAction(e -> enviarAtualizacao(
                encomenda,
                montagem,
                comentarioMontagem,
                costuras,
                comentarioCosturas,
                personalizacao,
                comentarioPersonalizacao,
                observacoes,
                false,
                estado,
                guardar,
                concluir,
                cancelar
        ));

        concluir.setOnAction(e -> {
            if (!montagem.isSelected() || !costuras.isSelected() ||
                    (encomenda.isPrecisaPersonalizacao() && !personalizacao.isSelected())) {
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setHeaderText("Etapas incompletas");
                alert.setContentText("Para concluir a encomenda, todas as etapas aplicáveis têm de estar concluídas.");
                alert.showAndWait();
                return;
            }

            enviarAtualizacao(
                    encomenda,
                    montagem,
                    comentarioMontagem,
                    costuras,
                    comentarioCosturas,
                    personalizacao,
                    comentarioPersonalizacao,
                    observacoes,
                    true,
                    estado,
                    guardar,
                    concluir,
                    cancelar
            );
        });

        HBox botoes = new HBox(10, guardar, concluir, cancelar);

        root.getChildren().addAll(dados, etapasCard, observacoesCard, estado, botoes);
        return FuncionarioUiFactory.wrapInScroll(root);
    }

    private void enviarAtualizacao(FuncionarioEncomendaRow encomenda,
                                   CheckBox montagem,
                                   TextArea comentarioMontagem,
                                   CheckBox costuras,
                                   TextArea comentarioCosturas,
                                   CheckBox personalizacao,
                                   TextArea comentarioPersonalizacao,
                                   TextArea observacoes,
                                   boolean concluir,
                                   Label estado,
                                   Button guardar,
                                   Button concluirBtn,
                                   Button cancelar) {

        AtualizarProducaoEncomendaRequestDto dto = new AtualizarProducaoEncomendaRequestDto();
        dto.setIdEncomenda(encomenda.getIdEncomenda());
        dto.setMontagemConcluida(montagem.isSelected());
        dto.setMontagemComentario(textoOuNull(comentarioMontagem.getText()));
        dto.setCosturasConcluidas(costuras.isSelected());
        dto.setCosturasComentario(textoOuNull(comentarioCosturas.getText()));
        dto.setPersonalizacaoConcluida(encomenda.isPrecisaPersonalizacao() && personalizacao.isSelected());
        dto.setPersonalizacaoComentario(encomenda.isPrecisaPersonalizacao() ? textoOuNull(comentarioPersonalizacao.getText()) : null);
        dto.setObservacoes(textoOuNull(observacoes.getText()));
        dto.setConcluida(concluir);

        guardar.setDisable(true);
        concluirBtn.setDisable(true);
        cancelar.setDisable(true);
        estado.setText("A guardar produção...");

        Task<Void> task = new Task<>() {
            @Override
            protected Void call() {
                producaoApiService.atualizar(dto);
                return null;
            }
        };

        task.setOnSucceeded(event -> {
            guardar.setDisable(false);
            concluirBtn.setDisable(false);
            cancelar.setDisable(false);

            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setHeaderText(concluir ? "Encomenda concluída" : "Produção atualizada");
            alert.setContentText(concluir
                    ? "A encomenda foi marcada como concluída."
                    : "O progresso foi guardado com sucesso.");
            alert.showAndWait();

            shell.navigateTo(FuncionarioPage.MINHAS_ENCOMENDAS);
        });

        task.setOnFailed(event -> {
            guardar.setDisable(false);
            concluirBtn.setDisable(false);
            cancelar.setDisable(false);
            estado.setText("Erro ao atualizar produção.");

            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setHeaderText("Erro");
            alert.setContentText(task.getException() == null ? "Erro desconhecido." : task.getException().getMessage());
            alert.showAndWait();
        });

        Thread thread = new Thread(task);
        thread.setDaemon(true);
        thread.start();
    }

    private String textoOuNull(String texto) {
        return texto == null || texto.isBlank() ? null : texto.trim();
    }

    private Label sectionTitle(String text) {
        Label label = new Label(text);
        label.setStyle("-fx-font-size: 18; -fx-font-weight: bold;");
        return label;
    }
}