package com.teuprojeto.desktop.view.rececionista;

import com.teuprojeto.desktop.dto.ChapeuDto;
import com.teuprojeto.desktop.dto.ClienteDto;
import com.teuprojeto.desktop.dto.CriarEncomendaRequestDto;
import com.teuprojeto.desktop.dto.EncomendaDto;
import com.teuprojeto.desktop.dto.LinhaEncomendaRequestDto;
import com.teuprojeto.desktop.service.ClienteApiService;
import com.teuprojeto.desktop.service.EncomendaApiService;
import javafx.concurrent.Task;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.util.StringConverter;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class RececionistaCriarEncomendaPage {

    private final RececionistaShellView shell;
    private final ClienteApiService clienteApiService = new ClienteApiService();
    private final EncomendaApiService encomendaApiService = new EncomendaApiService();

    private final List<ChapeuDto> chapeusDisponiveis = new ArrayList<>();

    public RececionistaCriarEncomendaPage(RececionistaShellView shell) {
        this.shell = shell;
    }

    public Parent getView() {
        VBox page = RececionistaUiFactory.createPageContainer("Criar Encomenda");

        Label estado = new Label("A carregar dados...");
        estado.setStyle("-fx-text-fill: #666666;");

        ComboBox<ClienteDto> clienteBox = new ComboBox<>();
        clienteBox.setMaxWidth(Double.MAX_VALUE);
        clienteBox.setConverter(new StringConverter<>() {
            @Override
            public String toString(ClienteDto value) {
                if (value == null) {
                    return "";
                }
                return value.getNome() + " (" + value.getEmail() + ")";
            }

            @Override
            public ClienteDto fromString(String string) {
                return null;
            }
        });

        DatePicker entregaPicker = new DatePicker();

        CheckBox temDesignBox = new CheckBox("Precisa de design");

        TextArea descricaoDesign = new TextArea();
        descricaoDesign.setPromptText("Descreve o pedido do design...");
        descricaoDesign.setPrefRowCount(4);
        descricaoDesign.setDisable(true);

        temDesignBox.selectedProperty().addListener((obs, oldValue, selected) -> {
            descricaoDesign.setDisable(!selected);
            if (!selected) {
                descricaoDesign.clear();
            }
        });

        TextArea observacoes = new TextArea();
        observacoes.setPromptText("Observações da encomenda...");
        observacoes.setPrefRowCount(4);

        VBox linhasBox = new VBox(10);
        adicionarLinha(linhasBox);

        Button adicionarLinhaBtn = RececionistaUiFactory.secondaryButton("+ Adicionar Linha");
        adicionarLinhaBtn.setOnAction(e -> adicionarLinha(linhasBox));

        VBox clienteCard = RececionistaUiFactory.createCard();
        Label clienteTitulo = new Label("Dados da Encomenda");
        clienteTitulo.setStyle("-fx-font-size: 18; -fx-font-weight: bold;");

        HBox linhaClienteEntrega = new HBox(16,
                criarCampoBox("Cliente", clienteBox),
                criarCampoBox("Data de entrega", entregaPicker)
        );
        HBox.setHgrow(linhaClienteEntrega.getChildren().get(0), Priority.ALWAYS);
        HBox.setHgrow(linhaClienteEntrega.getChildren().get(1), Priority.ALWAYS);

        clienteCard.getChildren().addAll(
                estado,
                clienteTitulo,
                linhaClienteEntrega
        );

        VBox linhasCard = RececionistaUiFactory.createCard();
        Label linhasTitulo = new Label("Linhas da Encomenda");
        linhasTitulo.setStyle("-fx-font-size: 18; -fx-font-weight: bold;");
        linhasCard.getChildren().addAll(linhasTitulo, linhasBox, adicionarLinhaBtn);

        VBox designCard = RececionistaUiFactory.createCard();
        Label designTitulo = new Label("Design e Observações");
        designTitulo.setStyle("-fx-font-size: 18; -fx-font-weight: bold;");
        designCard.getChildren().addAll(
                designTitulo,
                temDesignBox,
                criarCampoBox("Descrição do design", descricaoDesign),
                criarCampoBox("Observações", observacoes)
        );

        Button guardar = RececionistaUiFactory.primaryButton("Guardar");
        Button cancelar = RececionistaUiFactory.secondaryButton("Cancelar");

        cancelar.setOnAction(e -> shell.navigateTo(RececionistaPage.ENCOMENDAS_LISTAR));
        guardar.setOnAction(e -> criarEncomenda(
                clienteBox.getValue(),
                entregaPicker.getValue() == null ? null : entregaPicker.getValue().toString(),
                temDesignBox.isSelected(),
                descricaoDesign.getText(),
                observacoes.getText(),
                linhasBox,
                guardar,
                cancelar,
                estado
        ));

        HBox botoes = new HBox(10, guardar, cancelar);

        VBox content = new VBox(18, clienteCard, linhasCard, designCard, botoes);

        ScrollPane scrollPane = new ScrollPane(content);
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background: transparent; -fx-background-color: transparent;");

        page.getChildren().add(scrollPane);

        carregarDados(clienteBox, linhasBox, estado);

        return page;
    }

    private VBox criarCampoBox(String labelText, Control control) {
        VBox box = new VBox(8);

        Label label = new Label(labelText);
        label.setStyle("-fx-font-size: 14; -fx-text-fill: #333333;");

        control.setMaxWidth(Double.MAX_VALUE);
        VBox.setVgrow(control, Priority.NEVER);

        box.getChildren().addAll(label, control);
        VBox.setVgrow(box, Priority.NEVER);
        HBox.setHgrow(box, Priority.ALWAYS);
        return box;
    }

    private void adicionarLinha(VBox linhasBox) {
        LinhaItem linha = new LinhaItem();

        Button removerBtn = RececionistaUiFactory.secondaryButton("Remover");
        removerBtn.setOnAction(e -> linhasBox.getChildren().remove(linha.root));

        linha.root.getChildren().add(removerBtn);

        linhasBox.getChildren().add(linha.root);
    }

    private void carregarDados(ComboBox<ClienteDto> clienteBox, VBox linhasBox, Label estado) {
        estado.setText("A carregar clientes e chapéus...");

        Task<DadosCriarEncomenda> task = new Task<>() {
            @Override
            protected DadosCriarEncomenda call() {
                List<ClienteDto> clientes = clienteApiService.listarTodos();
                List<ChapeuDto> chapeus = encomendaApiService.listarChapeus();
                return new DadosCriarEncomenda(clientes, chapeus);
            }
        };

        task.setOnSucceeded(event -> {
            DadosCriarEncomenda dados = task.getValue();

            clienteBox.getItems().setAll(dados.clientes());

            chapeusDisponiveis.clear();
            chapeusDisponiveis.addAll(dados.chapeus());

            for (var node : linhasBox.getChildren()) {
                if (node.getUserData() instanceof LinhaItem linhaItem) {
                    linhaItem.chapeuBox.getItems().setAll(chapeusDisponiveis);
                }
            }

            if (shell.getClienteSelecionado() != null && shell.getClienteSelecionado().getCod() != null) {
                Integer idSelecionado = shell.getClienteSelecionado().getCod();

                for (ClienteDto dto : dados.clientes()) {
                    if (dto.getCod() != null && dto.getCod().equals(idSelecionado)) {
                        clienteBox.setValue(dto);
                        break;
                    }
                }
            }

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

    private void criarEncomenda(ClienteDto cliente,
                                String dataEntrega,
                                boolean temDesign,
                                String descricaoDesign,
                                String observacoes,
                                VBox linhasBox,
                                Button guardar,
                                Button cancelar,
                                Label estado) {

        if (cliente == null) {
            mostrarErro("Seleciona um cliente.");
            return;
        }

        if (isBlank(dataEntrega)) {
            mostrarErro("Seleciona a data de entrega.");
            return;
        }

        if (temDesign && isBlank(descricaoDesign)) {
            mostrarErro("Preenche a descrição do design.");
            return;
        }

        List<LinhaEncomendaRequestDto> linhas = new ArrayList<>();

        for (var node : linhasBox.getChildren()) {
            if (!(node.getUserData() instanceof LinhaItem linhaItem)) {
                continue;
            }

            ChapeuDto chapeu = linhaItem.chapeuBox.getValue();
            String quantidadeTexto = linhaItem.quantidadeField.getText();
            String precoTexto = linhaItem.precoField.getText();

            if (chapeu == null) {
                mostrarErro("Seleciona um chapéu em todas as linhas.");
                return;
            }

            if (isBlank(quantidadeTexto) || isBlank(precoTexto)) {
                mostrarErro("Preenche quantidade e preço unitário em todas as linhas.");
                return;
            }

            try {
                Long quantidade = Long.parseLong(quantidadeTexto.trim());
                BigDecimal precoUnitario = new BigDecimal(precoTexto.trim());

                if (quantidade <= 0) {
                    mostrarErro("A quantidade tem de ser maior que zero.");
                    return;
                }

                if (precoUnitario.compareTo(BigDecimal.ZERO) <= 0) {
                    mostrarErro("O preço unitário tem de ser maior que zero.");
                    return;
                }

                LinhaEncomendaRequestDto linha = new LinhaEncomendaRequestDto();
                linha.setCodChapeu(chapeu.getCod());
                linha.setQuantidade(quantidade);
                linha.setPrecoUnitario(precoUnitario);

                linhas.add(linha);

            } catch (NumberFormatException e) {
                mostrarErro("Quantidade ou preço unitário inválidos.");
                return;
            }
        }

        if (linhas.isEmpty()) {
            mostrarErro("A encomenda tem de ter pelo menos uma linha.");
            return;
        }

        CriarEncomendaRequestDto dto = new CriarEncomendaRequestDto();
        dto.setIdCliente(cliente.getCod());
        dto.setDataEntrega(dataEntrega);
        dto.setObservacoes(isBlank(observacoes) ? null : observacoes.trim());
        dto.setDesign(temDesign);
        dto.setDescricaoDesign(temDesign ? descricaoDesign.trim() : null);
        dto.setLinhas(linhas);

        guardar.setDisable(true);
        cancelar.setDisable(true);
        estado.setText("A criar encomenda...");

        Task<EncomendaDto> task = new Task<>() {
            @Override
            protected EncomendaDto call() {
                return encomendaApiService.criar(dto);
            }
        };

        task.setOnSucceeded(event -> {
            guardar.setDisable(false);
            cancelar.setDisable(false);
            estado.setText("Encomenda criada com sucesso.");

            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setHeaderText("Encomenda criada com sucesso");
            alert.setContentText("A encomenda foi criada no backend.");
            alert.showAndWait();

            shell.navigateTo(RececionistaPage.ENCOMENDAS_LISTAR);
        });

        task.setOnFailed(event -> {
            guardar.setDisable(false);
            cancelar.setDisable(false);
            estado.setText("Erro ao criar encomenda.");
            mostrarErro(task.getException() == null ? "Erro desconhecido." : task.getException().getMessage());
        });

        Thread thread = new Thread(task);
        thread.setDaemon(true);
        thread.start();
    }

    private boolean isBlank(String value) {
        return value == null || value.isBlank();
    }

    private void mostrarErro(String mensagem) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setHeaderText("Erro");
        alert.setContentText(mensagem);
        alert.showAndWait();
    }

    private record DadosCriarEncomenda(List<ClienteDto> clientes, List<ChapeuDto> chapeus) {
    }

    private class LinhaItem {
        private final HBox root = new HBox(10);
        private final ComboBox<ChapeuDto> chapeuBox = new ComboBox<>();
        private final TextField quantidadeField = new TextField();
        private final TextField precoField = new TextField();

        private LinhaItem() {
            root.setUserData(this);

            chapeuBox.setPromptText("Seleciona um chapéu");
            chapeuBox.setMaxWidth(Double.MAX_VALUE);
            chapeuBox.getItems().setAll(chapeusDisponiveis);
            chapeuBox.setConverter(new StringConverter<>() {
                @Override
                public String toString(ChapeuDto value) {
                    if (value == null) {
                        return "";
                    }
                    return value.getNome() + " - " + formatarValor(value.getPrecoactvenda());
                }

                @Override
                public ChapeuDto fromString(String string) {
                    return null;
                }
            });

            quantidadeField.setPromptText("Quantidade");
            precoField.setPromptText("Preço unitário");

            chapeuBox.valueProperty().addListener((obs, oldValue, selected) -> {
                if (selected != null && selected.getPrecoactvenda() != null) {
                    precoField.setText(selected.getPrecoactvenda().toPlainString());
                } else {
                    precoField.clear();
                }
            });

            VBox chapeuCol = criarCampoBox("Chapéu", chapeuBox);
            VBox quantidadeCol = criarCampoBox("Quantidade", quantidadeField);
            VBox precoCol = criarCampoBox("Preço unitário", precoField);

            HBox.setHgrow(chapeuCol, Priority.ALWAYS);
            HBox.setHgrow(quantidadeCol, Priority.ALWAYS);
            HBox.setHgrow(precoCol, Priority.ALWAYS);

            Region spacer = new Region();
            spacer.setMinWidth(0);

            root.getChildren().addAll(chapeuCol, quantidadeCol, precoCol, spacer);
            HBox.setHgrow(spacer, Priority.NEVER);
        }

        private String formatarValor(BigDecimal valor) {
            if (valor == null) {
                return "-";
            }
            return String.format("%.2f €", valor.doubleValue());
        }
    }
}