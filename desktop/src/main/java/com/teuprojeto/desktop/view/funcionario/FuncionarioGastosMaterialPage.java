package com.teuprojeto.desktop.view.funcionario;

import com.teuprojeto.desktop.dto.CriarGastoMaterialRequestDto;
import com.teuprojeto.desktop.dto.MaterialDto;
import com.teuprojeto.desktop.service.FuncionarioDataService;
import com.teuprojeto.desktop.service.GastoMaterialApiService;
import com.teuprojeto.desktop.service.MaterialApiService;
import javafx.concurrent.Task;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.util.StringConverter;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class FuncionarioGastosMaterialPage {

    private final FuncionarioShellView shell;
    private final VBox linhasMateriais = new VBox(10);
    private final FuncionarioDataService funcionarioDataService = new FuncionarioDataService();
    private final MaterialApiService materialApiService = new MaterialApiService();
    private final GastoMaterialApiService gastoMaterialApiService = new GastoMaterialApiService();

    private final List<MaterialDto> materiaisDisponiveis = new ArrayList<>();
    private final List<FuncionarioEncomendaRow> encomendasFuncionario = new ArrayList<>();

    public FuncionarioGastosMaterialPage(FuncionarioShellView shell) {
        this.shell = shell;
    }

    public Parent getView() {
        VBox root = FuncionarioUiFactory.createPageContainer("Gastos de Material");

        VBox card = FuncionarioUiFactory.createCard();

        Label estado = new Label("A carregar dados...");
        estado.setStyle("-fx-text-fill: #666666;");

        ComboBox<FuncionarioEncomendaRow> encomendaCombo = new ComboBox<>();
        encomendaCombo.setMaxWidth(Double.MAX_VALUE);
        encomendaCombo.setConverter(new StringConverter<>() {
            @Override
            public String toString(FuncionarioEncomendaRow value) {
                if (value == null) {
                    return "";
                }
                return value.getCodigoEncomenda() + " - " + value.getProduto();
            }

            @Override
            public FuncionarioEncomendaRow fromString(String string) {
                return null;
            }
        });

        card.getChildren().addAll(
                estado,
                sectionTitle("Registo de Gastos"),
                new Label("Encomenda"),
                encomendaCombo
        );

        linhasMateriais.getChildren().clear();
        linhasMateriais.getChildren().add(criarLinhaMaterial());

        Button adicionarLinha = FuncionarioUiFactory.secondaryButton("+ Adicionar Material");
        adicionarLinha.setOnAction(e -> linhasMateriais.getChildren().add(criarLinhaMaterial()));

        Button enviar = FuncionarioUiFactory.primaryButton("Enviar Gestor");
        Button cancelar = FuncionarioUiFactory.secondaryButton("Cancelar");
        cancelar.setOnAction(e -> shell.navigateTo(FuncionarioPage.DASHBOARD));

        enviar.setOnAction(e -> enviarGastos(encomendaCombo, estado, enviar, cancelar));

        HBox botoes = new HBox(10, enviar, cancelar);

        card.getChildren().addAll(linhasMateriais, adicionarLinha, botoes);

        root.getChildren().add(card);

        carregarDados(encomendaCombo, estado);

        return FuncionarioUiFactory.wrapInScroll(root);
    }

    private void carregarDados(ComboBox<FuncionarioEncomendaRow> encomendaCombo, Label estado) {
        estado.setText("A carregar encomendas e materiais...");

        Task<Void> task = new Task<>() {
            @Override
            protected Void call() {
                encomendasFuncionario.clear();
                encomendasFuncionario.addAll(funcionarioDataService.carregarMinhasEncomendas(shell.getFuncionarioId()));

                materiaisDisponiveis.clear();
                materiaisDisponiveis.addAll(materialApiService.listarTodos());

                return null;
            }
        };

        task.setOnSucceeded(event -> {
            encomendaCombo.getItems().setAll(encomendasFuncionario);

            if (shell.getEncomendaSelecionada() != null) {
                for (FuncionarioEncomendaRow row : encomendasFuncionario) {
                    if (row.getIdEncomenda().equals(shell.getEncomendaSelecionada().getIdEncomenda())) {
                        encomendaCombo.setValue(row);
                        break;
                    }
                }
            }

            atualizarMateriaisNasLinhas();

            estado.setText("Dados carregados.");
        });

        task.setOnFailed(event -> {
            estado.setText("Erro ao carregar dados.");

            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setHeaderText("Erro");
            alert.setContentText(task.getException() == null ? "Erro desconhecido." : task.getException().getMessage());
            alert.showAndWait();
        });

        Thread thread = new Thread(task);
        thread.setDaemon(true);
        thread.start();
    }

    private void atualizarMateriaisNasLinhas() {
        for (var node : linhasMateriais.getChildren()) {
            if (node.getUserData() instanceof LinhaMaterialItem item) {
                item.materialBox.getItems().setAll(materiaisDisponiveis);
            }
        }
    }

    private VBox criarLinhaMaterial() {
        VBox bloco = new VBox(8);
        bloco.setPadding(new Insets(12));
        bloco.setStyle("-fx-background-color: #fafafa; -fx-border-color: #e5e5e5; -fx-border-radius: 8; -fx-background-radius: 8;");

        LinhaMaterialItem item = new LinhaMaterialItem();
        bloco.setUserData(item);

        HBox row1 = new HBox(12);

        item.materialBox.setPromptText("Selecionar material");
        item.materialBox.setMaxWidth(Double.MAX_VALUE);
        item.materialBox.getItems().setAll(materiaisDisponiveis);
        item.materialBox.setConverter(new StringConverter<>() {
            @Override
            public String toString(MaterialDto value) {
                if (value == null) {
                    return "";
                }
                return value.getNome() + " (" + value.getStockAtual() + " " + value.getUnidade() + ")";
            }

            @Override
            public MaterialDto fromString(String string) {
                return null;
            }
        });

        item.quantidadeField.setPromptText("Quantidade");
        HBox.setHgrow(item.materialBox, Priority.ALWAYS);

        row1.getChildren().addAll(item.materialBox, item.quantidadeField);

        item.observacoesArea.setPromptText("Observações...");
        item.observacoesArea.setPrefRowCount(2);

        Button remover = FuncionarioUiFactory.secondaryButton("Remover");
        remover.setOnAction(e -> linhasMateriais.getChildren().remove(bloco));

        bloco.getChildren().addAll(row1, item.observacoesArea, remover);
        return bloco;
    }

    private void enviarGastos(ComboBox<FuncionarioEncomendaRow> encomendaCombo,
                              Label estado,
                              Button enviar,
                              Button cancelar) {

        FuncionarioEncomendaRow encomenda = encomendaCombo.getValue();

        if (encomenda == null) {
            mostrarErro("Seleciona uma encomenda.");
            return;
        }

        List<CriarGastoMaterialRequestDto> pedidos = new ArrayList<>();

        for (var node : linhasMateriais.getChildren()) {
            if (!(node.getUserData() instanceof LinhaMaterialItem item)) {
                continue;
            }

            MaterialDto material = item.materialBox.getValue();
            String quantidadeTexto = item.quantidadeField.getText();

            if (material == null || quantidadeTexto == null || quantidadeTexto.isBlank()) {
                mostrarErro("Preenche material e quantidade em todas as linhas.");
                return;
            }

            try {
                BigDecimal quantidade = new BigDecimal(quantidadeTexto.trim());

                if (quantidade.compareTo(BigDecimal.ZERO) <= 0) {
                    mostrarErro("A quantidade tem de ser maior que zero.");
                    return;
                }

                CriarGastoMaterialRequestDto dto = new CriarGastoMaterialRequestDto();
                dto.setIdEncomenda(encomenda.getIdEncomenda());
                dto.setIdMaterial(material.getId());
                dto.setQuantidade(quantidade);
                dto.setObservacoes(item.observacoesArea.getText() == null || item.observacoesArea.getText().isBlank()
                        ? null
                        : item.observacoesArea.getText().trim());

                pedidos.add(dto);
            } catch (NumberFormatException e) {
                mostrarErro("Quantidade inválida.");
                return;
            }
        }

        if (pedidos.isEmpty()) {
            mostrarErro("Adiciona pelo menos um gasto de material.");
            return;
        }

        enviar.setDisable(true);
        cancelar.setDisable(true);
        estado.setText("A enviar gastos de material...");

        Task<Void> task = new Task<>() {
            @Override
            protected Void call() {
                for (CriarGastoMaterialRequestDto pedido : pedidos) {
                    gastoMaterialApiService.criar(pedido);
                }
                return null;
            }
        };

        task.setOnSucceeded(event -> {
            enviar.setDisable(false);
            cancelar.setDisable(false);
            estado.setText("Gastos enviados com sucesso.");

            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setHeaderText("Gastos registados");
            alert.setContentText("Os gastos de material foram enviados com sucesso.");
            alert.showAndWait();

            shell.navigateTo(FuncionarioPage.MINHAS_ENCOMENDAS);
        });

        task.setOnFailed(event -> {
            enviar.setDisable(false);
            cancelar.setDisable(false);
            estado.setText("Erro ao enviar gastos.");

            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setHeaderText("Erro");
            alert.setContentText(task.getException() == null ? "Erro desconhecido." : task.getException().getMessage());
            alert.showAndWait();
        });

        Thread thread = new Thread(task);
        thread.setDaemon(true);
        thread.start();
    }

    private Label sectionTitle(String text) {
        Label label = new Label(text);
        label.setStyle("-fx-font-size: 18; -fx-font-weight: bold;");
        return label;
    }

    private void mostrarErro(String mensagem) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setHeaderText("Erro");
        alert.setContentText(mensagem);
        alert.showAndWait();
    }

    private static class LinhaMaterialItem {
        private final ComboBox<MaterialDto> materialBox = new ComboBox<>();
        private final TextField quantidadeField = new TextField();
        private final TextArea observacoesArea = new TextArea();
    }
}