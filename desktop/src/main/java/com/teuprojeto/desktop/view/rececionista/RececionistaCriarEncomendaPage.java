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
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.*;
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
        VBox root = new VBox(24);
        root.setPadding(new Insets(28));
        root.setStyle("-fx-background-color: #f4f7fb;");

        VBox header = new VBox(6);

        Label title = new Label("Criar Encomenda");
        title.setStyle("-fx-font-size: 30; -fx-font-weight: bold; -fx-text-fill: #0f172a;");

        Label subtitle = new Label("Registe uma nova encomenda com uma ou várias linhas de chapéus.");
        subtitle.setStyle("-fx-font-size: 14; -fx-text-fill: #64748b;");

        header.getChildren().addAll(title, subtitle);

        Label estado = new Label("A carregar dados...");
        estado.setStyle("-fx-text-fill: #64748b; -fx-font-weight: bold;");

        ComboBox<ClienteDto> clienteBox = new ComboBox<>();
        clienteBox.setMaxWidth(Double.MAX_VALUE);
        clienteBox.setStyle(inputStyle());
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
        entregaPicker.setMaxWidth(Double.MAX_VALUE);
        entregaPicker.setStyle(inputStyle());

        VBox dadosCard = card();

        HBox dadosHeader = sectionHeader("📦", "Dados da Encomenda", "Cliente, data de entrega e estado inicial da encomenda.");

        HBox linhaClienteEntrega = new HBox(18);
        linhaClienteEntrega.getChildren().addAll(
                criarCampoBox("Cliente", clienteBox),
                criarCampoBox("Data de entrega", entregaPicker)
        );

        dadosCard.getChildren().addAll(
                dadosHeader,
                separator(),
                linhaClienteEntrega
        );

        VBox linhasBox = new VBox(14);
        adicionarLinha(linhasBox);

        Button adicionarLinhaBtn = secondaryButton("+ Adicionar Linha");
        adicionarLinhaBtn.setOnAction(e -> adicionarLinha(linhasBox));

        VBox linhasCard = card();
        linhasCard.getChildren().addAll(
                sectionHeader("🎩", "Linhas da Encomenda", "Adicione os chapéus, tamanhos, cores e quantidades pretendidas."),
                separator(),
                linhasBox,
                adicionarLinhaBtn
        );

        CheckBox temDesignBox = new CheckBox("Esta encomenda precisa de design personalizado");
        temDesignBox.setStyle("-fx-font-weight: bold; -fx-text-fill: #0f172a;");

        TextArea descricaoDesign = new TextArea();
        descricaoDesign.setPromptText("Descreve o pedido do design...");
        descricaoDesign.setPrefRowCount(4);
        descricaoDesign.setWrapText(true);
        descricaoDesign.setDisable(true);
        descricaoDesign.setStyle(inputStyle());

        temDesignBox.selectedProperty().addListener((obs, oldValue, selected) -> {
            descricaoDesign.setDisable(!selected);
            if (!selected) {
                descricaoDesign.clear();
            }
        });

        TextArea observacoes = new TextArea();
        observacoes.setPromptText("Observações da encomenda...");
        observacoes.setPrefRowCount(4);
        observacoes.setWrapText(true);
        observacoes.setStyle(inputStyle());

        VBox designCard = card();
        designCard.getChildren().addAll(
                sectionHeader("🎨", "Design e Observações", "Indique se a encomenda precisa de intervenção do designer."),
                separator(),
                temDesignBox,
                criarCampoBox("Descrição do design", descricaoDesign),
                criarCampoBox("Observações", observacoes)
        );

        Button guardar = primaryButton("Guardar Encomenda");
        Button cancelar = secondaryButton("Cancelar");

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

        HBox botoes = new HBox(12, guardar, cancelar);
        botoes.setAlignment(Pos.CENTER_LEFT);

        root.getChildren().addAll(
                header,
                estado,
                dadosCard,
                linhasCard,
                designCard,
                botoes
        );

        carregarDados(clienteBox, linhasBox, estado);

        return wrap(root);
    }

    private VBox criarCampoBox(String labelText, Control control) {
        VBox box = new VBox(8);

        Label label = new Label(labelText);
        label.setStyle("-fx-font-size: 13; -fx-font-weight: bold; -fx-text-fill: #334155;");

        control.setMaxWidth(Double.MAX_VALUE);

        box.getChildren().addAll(label, control);
        HBox.setHgrow(box, Priority.ALWAYS);

        return box;
    }

    private void adicionarLinha(VBox linhasBox) {
        LinhaItem linha = new LinhaItem();

        Button removerBtn = dangerButton("Remover");
        removerBtn.setOnAction(e -> {
            if (linhasBox.getChildren().size() <= 1) {
                mostrarErro("A encomenda tem de ter pelo menos uma linha.");
                return;
            }

            linhasBox.getChildren().remove(linha.root);
        });

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
            String tamanho = linhaItem.tamanhoBox.getValue();
            String cores = linhaItem.coresField.getText();

            if (chapeu == null) {
                mostrarErro("Seleciona um chapéu em todas as linhas.");
                return;
            }

            if (isBlank(quantidadeTexto) || isBlank(precoTexto) || isBlank(tamanho) || isBlank(cores)) {
                mostrarErro("Preenche quantidade, preço unitário, tamanho e cores em todas as linhas.");
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
                linha.setTamanho(tamanho.trim());
                linha.setCores(cores.trim());

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

    private HBox sectionHeader(String iconText, String title, String subtitle) {
        HBox box = new HBox(14);
        box.setAlignment(Pos.CENTER_LEFT);

        StackPane icon = new StackPane();
        icon.setMinSize(58, 58);
        icon.setPrefSize(58, 58);
        icon.setStyle("-fx-background-color: #eff6ff; -fx-background-radius: 18;");

        Label iconLabel = new Label(iconText);
        iconLabel.setStyle("-fx-font-size: 24;");
        icon.getChildren().add(iconLabel);

        VBox text = new VBox(4);

        Label titleLabel = new Label(title);
        titleLabel.setStyle("-fx-font-size: 22; -fx-font-weight: bold; -fx-text-fill: #0f172a;");

        Label subtitleLabel = new Label(subtitle);
        subtitleLabel.setStyle("-fx-font-size: 13; -fx-text-fill: #64748b;");

        text.getChildren().addAll(titleLabel, subtitleLabel);
        box.getChildren().addAll(icon, text);

        return box;
    }

    private VBox card() {
        VBox card = new VBox(18);
        card.setPadding(new Insets(22));
        card.setStyle(
                "-fx-background-color: white;" +
                        "-fx-background-radius: 22;" +
                        "-fx-border-radius: 22;" +
                        "-fx-border-color: #e5e7eb;" +
                        "-fx-effect: dropshadow(gaussian, rgba(15,23,42,0.06), 18, 0, 0, 6);"
        );
        return card;
    }

    private String inputStyle() {
        return "-fx-background-color: white;" +
                "-fx-border-color: #dbe2ea;" +
                "-fx-border-radius: 14;" +
                "-fx-background-radius: 14;" +
                "-fx-padding: 11;" +
                "-fx-font-size: 14;";
    }

    private Button primaryButton(String text) {
        Button button = RececionistaUiFactory.primaryButton(text);
        button.setPrefHeight(42);
        return button;
    }

    private Button secondaryButton(String text) {
        Button button = RececionistaUiFactory.secondaryButton(text);
        button.setPrefHeight(42);
        return button;
    }

    private Button dangerButton(String text) {
        Button button = new Button(text);
        button.setPrefHeight(42);
        button.setStyle(
                "-fx-background-color: white;" +
                        "-fx-border-color: #fecaca;" +
                        "-fx-border-width: 1.5;" +
                        "-fx-background-radius: 14;" +
                        "-fx-border-radius: 14;" +
                        "-fx-font-weight: bold;" +
                        "-fx-text-fill: #dc2626;" +
                        "-fx-padding: 0 18 0 18;" +
                        "-fx-cursor: hand;"
        );
        return button;
    }

    private Region separator() {
        Region region = new Region();
        region.setPrefHeight(1);
        region.setStyle("-fx-background-color: #e5e7eb;");
        return region;
    }

    private Parent wrap(VBox root) {
        ScrollPane scrollPane = new ScrollPane(root);
        scrollPane.setFitToWidth(true);
        scrollPane.setPannable(true);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setStyle("-fx-background: #f4f7fb; -fx-background-color: #f4f7fb;");
        return scrollPane;
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
        private final ComboBox<String> tamanhoBox = new ComboBox<>();
        private final TextField coresField = new TextField();

        private LinhaItem() {
            root.setUserData(this);
            root.setAlignment(Pos.BOTTOM_LEFT);
            root.setPadding(new Insets(14));
            root.setStyle(
                    "-fx-background-color: #f8fafc;" +
                            "-fx-background-radius: 18;" +
                            "-fx-border-color: #e5e7eb;" +
                            "-fx-border-radius: 18;"
            );

            chapeuBox.setPromptText("Seleciona um chapéu");
            chapeuBox.setMaxWidth(Double.MAX_VALUE);
            chapeuBox.setStyle(inputStyle());
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

            quantidadeField.setPromptText("Qtd.");
            quantidadeField.setStyle(inputStyle());

            precoField.setPromptText("Preço");
            precoField.setStyle(inputStyle());

            tamanhoBox.setPromptText("Tamanho");
            tamanhoBox.setStyle(inputStyle());
            tamanhoBox.getItems().setAll("S", "M", "L", "XL");

            coresField.setPromptText("Ex: Preto, Branco");
            coresField.setStyle(inputStyle());

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
            VBox tamanhoCol = criarCampoBox("Tamanho", tamanhoBox);
            VBox coresCol = criarCampoBox("Cores", coresField);

            chapeuCol.setPrefWidth(280);
            quantidadeCol.setPrefWidth(130);
            precoCol.setPrefWidth(150);
            tamanhoCol.setPrefWidth(130);
            coresCol.setPrefWidth(220);

            HBox.setHgrow(chapeuCol, Priority.ALWAYS);

            root.getChildren().addAll(chapeuCol, quantidadeCol, precoCol, tamanhoCol, coresCol);
        }

        private String formatarValor(BigDecimal valor) {
            if (valor == null) {
                return "-";
            }
            return String.format("%.2f €", valor.doubleValue());
        }
    }
}