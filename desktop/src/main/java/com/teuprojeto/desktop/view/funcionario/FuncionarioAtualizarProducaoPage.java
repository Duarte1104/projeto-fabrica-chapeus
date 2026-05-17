package com.teuprojeto.desktop.view.funcionario;

import com.teuprojeto.desktop.dto.AtualizarProducaoEncomendaRequestDto;
import com.teuprojeto.desktop.dto.DesignEncomendaDto;
import com.teuprojeto.desktop.dto.DesignEncomendaImagemDto;
import com.teuprojeto.desktop.service.DesignApiService;
import com.teuprojeto.desktop.service.ProducaoApiService;
import javafx.concurrent.Task;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.util.Comparator;
import java.util.List;

public class FuncionarioAtualizarProducaoPage {

    private final FuncionarioShellView shell;
    private final ProducaoApiService producaoApiService = new ProducaoApiService();
    private final DesignApiService designApiService = new DesignApiService();

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

        VBox designCard = criarCardDesignAprovado(encomenda);

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

        if (designCard != null) {
            root.getChildren().addAll(dados, designCard, etapasCard, observacoesCard, estado, botoes);
        } else {
            root.getChildren().addAll(dados, etapasCard, observacoesCard, estado, botoes);
        }

        return FuncionarioUiFactory.wrapInScroll(root);
    }

    private VBox criarCardDesignAprovado(FuncionarioEncomendaRow encomenda) {
        if (!encomenda.isPrecisaPersonalizacao()) {
            return null;
        }

        VBox card = FuncionarioUiFactory.createCard();

        Label estado = new Label("A carregar design aprovado...");
        estado.setStyle("-fx-text-fill: #666666;");

        card.getChildren().addAll(
                sectionTitle("Design aprovado pelo cliente"),
                estado
        );

        Task<DesignInfo> task = new Task<>() {
            @Override
            protected DesignInfo call() {
                List<DesignEncomendaDto> designs =
                        designApiService.listarPorEncomenda(encomenda.getIdEncomenda());

                DesignEncomendaDto aprovado = designs.stream()
                        .filter(d -> "APROVADO_CLIENTE".equalsIgnoreCase(d.getEstadoDesign()))
                        .max(Comparator.comparing(d -> d.getDataCriacao() == null ? "" : d.getDataCriacao()))
                        .orElse(null);

                if (aprovado == null) {
                    return null;
                }

                List<DesignEncomendaImagemDto> imagens =
                        designApiService.listarImagens(aprovado.getId());

                return new DesignInfo(aprovado, imagens);
            }
        };

        task.setOnSucceeded(event -> {
            DesignInfo info = task.getValue();

            if (info == null) {
                estado.setText("Ainda não existe design aprovado para esta encomenda.");
                return;
            }

            estado.setText("Design carregado.");

            Label descricaoTitulo = new Label("Descrição do designer:");
            descricaoTitulo.setStyle("-fx-font-weight: bold;");

            TextArea descricao = new TextArea(info.design().getDescricaoDesigner());
            descricao.setEditable(false);
            descricao.setWrapText(true);
            descricao.setPrefRowCount(4);

            Label imagensTitulo = new Label("Imagens da proposta aprovada:");
            imagensTitulo.setStyle("-fx-font-weight: bold;");

            FlowPane galeria = new FlowPane();
            galeria.setHgap(12);
            galeria.setVgap(12);

            if (info.imagens().isEmpty()) {
                galeria.getChildren().add(new Label("Sem imagens associadas."));
            } else {
                for (DesignEncomendaImagemDto imagem : info.imagens()) {
                    ImageView imageView = criarImagemDesign(imagem.getUrlImagem());
                    galeria.getChildren().add(imageView);
                }
            }

            card.getChildren().addAll(
                    descricaoTitulo,
                    descricao,
                    imagensTitulo,
                    galeria
            );
        });

        task.setOnFailed(event -> {
            estado.setText("Erro ao carregar design aprovado.");
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setHeaderText("Erro");
            alert.setContentText(task.getException() == null ? "Erro desconhecido." : task.getException().getMessage());
            alert.showAndWait();
        });

        Thread thread = new Thread(task);
        thread.setDaemon(true);
        thread.start();

        return card;
    }

    private ImageView criarImagemDesign(String url) {
        Image image = new Image(url, true);

        ImageView imageView = new ImageView(image);
        imageView.setFitWidth(220);
        imageView.setFitHeight(160);
        imageView.setPreserveRatio(true);
        imageView.setSmooth(true);

        imageView.setStyle(
                "-fx-background-color: #f3f4f6;" +
                        "-fx-border-color: #d1d5db;" +
                        "-fx-border-radius: 8;" +
                        "-fx-background-radius: 8;"
        );

        return imageView;
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

    private record DesignInfo(
            DesignEncomendaDto design,
            List<DesignEncomendaImagemDto> imagens
    ) {
    }
}