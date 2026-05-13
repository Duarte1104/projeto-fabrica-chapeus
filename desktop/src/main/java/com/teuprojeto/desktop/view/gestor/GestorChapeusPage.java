package com.teuprojeto.desktop.view.gestor;

import com.teuprojeto.desktop.dto.ChapeuDto;
import com.teuprojeto.desktop.dto.ChapeuMaterialDto;
import com.teuprojeto.desktop.dto.MaterialDto;
import com.teuprojeto.desktop.service.ChapeuApiService;
import com.teuprojeto.desktop.service.ChapeuMaterialApiService;
import com.teuprojeto.desktop.service.MaterialApiService;
import javafx.collections.FXCollections;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.util.StringConverter;

import java.io.File;
import java.math.BigDecimal;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class GestorChapeusPage {

    private final GestorShellView shell;
    private final ChapeuApiService chapeuApiService = new ChapeuApiService();
    private final MaterialApiService materialApiService = new MaterialApiService();
    private final ChapeuMaterialApiService chapeuMaterialApiService = new ChapeuMaterialApiService();

    private final List<MaterialDto> materiaisDisponiveis = new ArrayList<>();

    public GestorChapeusPage(GestorShellView shell) {
        this.shell = shell;
    }

    public Parent getView() {

        VBox root = GestorUiFactory.createPageContainer("Gestão de Chapéus");

        Label estado = new Label("A carregar dados...");
        estado.setStyle("-fx-text-fill: #666666;");

        TableView<ChapeuDto> chapeusTable = new TableView<>();
        chapeusTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_ALL_COLUMNS);
        chapeusTable.setPrefHeight(260);

        TableColumn<ChapeuDto, String> codCol = new TableColumn<>("Código");
        codCol.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(
                c.getValue().getCod() == null ? "-" : c.getValue().getCod().toString()
        ));

        TableColumn<ChapeuDto, String> nomeCol = new TableColumn<>("Nome");
        nomeCol.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(
                c.getValue().getNome()
        ));

        TableColumn<ChapeuDto, String> precoCol = new TableColumn<>("Preço");
        precoCol.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(
                c.getValue().getPrecoactvenda() == null ? "-" : c.getValue().getPrecoactvenda() + " €"
        ));

        TableColumn<ChapeuDto, String> imagemCol = new TableColumn<>("Imagem");
        imagemCol.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(
                c.getValue().getImagemUrl() == null ? "-" : "Com imagem"
        ));

        chapeusTable.getColumns().addAll(codCol, nomeCol, precoCol, imagemCol);

        TextField nomeField = new TextField();
        nomeField.setPromptText("Nome do chapéu");

        TextField precoField = new TextField();
        precoField.setPromptText("Preço de venda");

        TextField imagemField = new TextField();
        imagemField.setPromptText("Nenhuma imagem selecionada");
        imagemField.setEditable(false);

        final Path[] imagemSelecionada = {null};

        Button escolherImagem = GestorUiFactory.secondaryButton("Escolher Imagem");

        escolherImagem.setOnAction(e -> {

            FileChooser chooser = new FileChooser();

            chooser.setTitle("Selecionar imagem do chapéu");

            chooser.getExtensionFilters().add(
                    new FileChooser.ExtensionFilter(
                            "Imagens",
                            "*.png",
                            "*.jpg",
                            "*.jpeg",
                            "*.webp"
                    )
            );

            File file = chooser.showOpenDialog(null);

            if (file != null) {
                imagemSelecionada[0] = file.toPath();
                imagemField.setText(file.getName());
            }
        });

        ComboBox<MaterialDto> materialInicialBox = new ComboBox<>();
        materialInicialBox.setMaxWidth(Double.MAX_VALUE);
        materialInicialBox.setPromptText("Material obrigatório");
        materialInicialBox.setConverter(materialConverter());

        TextField quantidadeInicialField = new TextField();
        quantidadeInicialField.setPromptText("Quantidade por unidade");

        Button criarChapeu = GestorUiFactory.primaryButton("Criar Chapéu com Material");

        criarChapeu.setOnAction(e -> criarChapeuComMaterial(
                nomeField,
                precoField,
                imagemSelecionada[0],
                materialInicialBox,
                quantidadeInicialField,
                chapeusTable,
                estado,
                imagemField
        ));

        VBox formChapeu = GestorUiFactory.createCard();

        formChapeu.getChildren().addAll(
                new Label("Novo Chapéu"),
                new Label("O código é gerado automaticamente pelo sistema."),
                nomeField,
                precoField,
                new Label("Imagem"),
                new HBox(10, imagemField, escolherImagem),
                new Label("Material inicial obrigatório"),
                materialInicialBox,
                quantidadeInicialField,
                criarChapeu
        );

        HBox.setHgrow(imagemField, Priority.ALWAYS);

        ComboBox<ChapeuDto> chapeuBox = new ComboBox<>();
        chapeuBox.setMaxWidth(Double.MAX_VALUE);
        chapeuBox.setConverter(chapeuConverter());

        ComboBox<MaterialDto> materialBox = new ComboBox<>();
        materialBox.setMaxWidth(Double.MAX_VALUE);
        materialBox.setConverter(materialConverter());

        TextField quantidadePorUnidadeField = new TextField();
        quantidadePorUnidadeField.setPromptText("Quantidade por unidade");

        Button associar = GestorUiFactory.primaryButton("Associar Material");

        associar.setOnAction(e ->
                associarMaterial(
                        chapeuBox,
                        materialBox,
                        quantidadePorUnidadeField,
                        estado
                )
        );

        TableView<ChapeuMaterialLinha> materiaisTable = new TableView<>();
        materiaisTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_ALL_COLUMNS);
        materiaisTable.setPrefHeight(220);

        TableColumn<ChapeuMaterialLinha, String> materialCol = new TableColumn<>("Material");
        materialCol.setCellValueFactory(c ->
                new javafx.beans.property.SimpleStringProperty(c.getValue().material())
        );

        TableColumn<ChapeuMaterialLinha, String> quantidadeCol = new TableColumn<>("Quantidade por unidade");
        quantidadeCol.setCellValueFactory(c ->
                new javafx.beans.property.SimpleStringProperty(c.getValue().quantidade())
        );

        materiaisTable.getColumns().addAll(materialCol, quantidadeCol);

        chapeuBox.valueProperty().addListener((obs, oldValue, selected) ->
                carregarMateriaisDoChapeu(selected, materiaisTable, estado)
        );

        VBox associacaoCard = GestorUiFactory.createCard();

        associacaoCard.getChildren().addAll(
                new Label("Associar mais materiais a um chapéu"),
                new Label("Chapéu"),
                chapeuBox,
                new Label("Material"),
                materialBox,
                new Label("Quantidade necessária para produzir 1 unidade"),
                quantidadePorUnidadeField,
                associar,
                new Label("Materiais associados"),
                materiaisTable
        );

        VBox listaCard = GestorUiFactory.createCard();
        listaCard.getChildren().addAll(new Label("Chapéus registados"), chapeusTable);

        root.getChildren().addAll(
                estado,
                formChapeu,
                associacaoCard,
                listaCard
        );

        carregarTudo(
                chapeusTable,
                chapeuBox,
                materialBox,
                materialInicialBox,
                estado
        );

        ScrollPane scrollPane = new ScrollPane(root);
        scrollPane.setFitToWidth(true);
        scrollPane.setPannable(true);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        scrollPane.setStyle("-fx-background: #efefef; -fx-background-color: #efefef;");

        return scrollPane;
    }

    private void carregarTudo(
            TableView<ChapeuDto> chapeusTable,
            ComboBox<ChapeuDto> chapeuBox,
            ComboBox<MaterialDto> materialBox,
            ComboBox<MaterialDto> materialInicialBox,
            Label estado
    ) {

        try {

            List<ChapeuDto> chapeus = chapeuApiService.listar();

            materiaisDisponiveis.clear();
            materiaisDisponiveis.addAll(materialApiService.listarTodos());

            chapeusTable.setItems(FXCollections.observableArrayList(chapeus));

            chapeuBox.getItems().setAll(chapeus);

            materialBox.getItems().setAll(materiaisDisponiveis);

            materialInicialBox.getItems().setAll(materiaisDisponiveis);

            estado.setText("Dados carregados.");

        } catch (RuntimeException e) {

            estado.setText("Erro ao carregar dados.");
            mostrarErro(e.getMessage());
        }
    }

    private void criarChapeuComMaterial(
            TextField nomeField,
            TextField precoField,
            Path imagemPath,
            ComboBox<MaterialDto> materialInicialBox,
            TextField quantidadeInicialField,
            TableView<ChapeuDto> chapeusTable,
            Label estado,
            TextField imagemField
    ) {

        try {

            if (isBlank(nomeField.getText()) || isBlank(precoField.getText())) {
                mostrarErro("Preenche nome e preço.");
                return;
            }

            if (imagemPath == null) {
                mostrarErro("Seleciona uma imagem do chapéu.");
                return;
            }

            MaterialDto material = materialInicialBox.getValue();

            if (material == null) {
                mostrarErro("Seleciona pelo menos um material.");
                return;
            }

            if (isBlank(quantidadeInicialField.getText())) {
                mostrarErro("Indica a quantidade por unidade.");
                return;
            }

            ChapeuDto chapeuCriado = chapeuApiService.criar(
                    nomeField.getText().trim(),
                    precoField.getText().trim(),
                    imagemPath
            );

            ChapeuMaterialDto associacao = new ChapeuMaterialDto();

            associacao.setIdChapeu(chapeuCriado.getCod());
            associacao.setIdMaterial(material.getId());

            associacao.setQuantidadePorUnidade(
                    new BigDecimal(quantidadeInicialField.getText().trim())
            );

            chapeuMaterialApiService.criar(associacao);

            nomeField.clear();
            precoField.clear();
            imagemField.clear();
            quantidadeInicialField.clear();

            materialInicialBox.setValue(null);

            chapeusTable.setItems(
                    FXCollections.observableArrayList(chapeuApiService.listar())
            );

            estado.setText("Chapéu criado com sucesso.");

        } catch (RuntimeException e) {

            mostrarErro(e.getMessage());
        }
    }

    private void associarMaterial(
            ComboBox<ChapeuDto> chapeuBox,
            ComboBox<MaterialDto> materialBox,
            TextField quantidadePorUnidadeField,
            Label estado
    ) {

        try {

            ChapeuDto chapeu = chapeuBox.getValue();
            MaterialDto material = materialBox.getValue();

            if (chapeu == null) {
                mostrarErro("Seleciona um chapéu.");
                return;
            }

            if (material == null) {
                mostrarErro("Seleciona um material.");
                return;
            }

            if (isBlank(quantidadePorUnidadeField.getText())) {
                mostrarErro("Indica a quantidade por unidade.");
                return;
            }

            ChapeuMaterialDto dto = new ChapeuMaterialDto();

            dto.setIdChapeu(chapeu.getCod());
            dto.setIdMaterial(material.getId());

            dto.setQuantidadePorUnidade(
                    new BigDecimal(quantidadePorUnidadeField.getText().trim())
            );

            chapeuMaterialApiService.criar(dto);

            quantidadePorUnidadeField.clear();

            estado.setText("Material associado ao chapéu com sucesso.");

        } catch (RuntimeException e) {

            mostrarErro(e.getMessage());
        }
    }

    private void carregarMateriaisDoChapeu(
            ChapeuDto chapeu,
            TableView<ChapeuMaterialLinha> table,
            Label estado
    ) {

        if (chapeu == null || chapeu.getCod() == null) {
            table.getItems().clear();
            return;
        }

        try {

            List<ChapeuMaterialDto> associacoes =
                    chapeuMaterialApiService.listarPorChapeu(chapeu.getCod());

            Map<Long, MaterialDto> materiaisPorId =
                    materiaisDisponiveis.stream()
                            .filter(m -> m.getId() != null)
                            .collect(Collectors.toMap(MaterialDto::getId, m -> m, (a, b) -> a));

            List<ChapeuMaterialLinha> linhas =
                    associacoes.stream()
                            .map(a -> {

                                MaterialDto material = materiaisPorId.get(a.getIdMaterial());

                                String nomeMaterial =
                                        material == null
                                                ? "Material #" + a.getIdMaterial()
                                                : material.getNome() + " (" + material.getUnidade() + ")";

                                return new ChapeuMaterialLinha(
                                        nomeMaterial,
                                        a.getQuantidadePorUnidade() == null
                                                ? "-"
                                                : a.getQuantidadePorUnidade().toPlainString()
                                );
                            })
                            .toList();

            table.setItems(FXCollections.observableArrayList(linhas));

            estado.setText("Materiais do chapéu carregados.");

        } catch (RuntimeException e) {

            estado.setText("Erro ao carregar materiais do chapéu.");
            mostrarErro(e.getMessage());
        }
    }

    private StringConverter<MaterialDto> materialConverter() {

        return new StringConverter<>() {

            @Override
            public String toString(MaterialDto value) {

                if (value == null) {
                    return "";
                }

                return value.getNome() + " (" + value.getUnidade() + ")";
            }

            @Override
            public MaterialDto fromString(String string) {
                return null;
            }
        };
    }

    private StringConverter<ChapeuDto> chapeuConverter() {

        return new StringConverter<>() {

            @Override
            public String toString(ChapeuDto value) {

                if (value == null) {
                    return "";
                }

                return "#" + value.getCod() + " - " + value.getNome();
            }

            @Override
            public ChapeuDto fromString(String string) {
                return null;
            }
        };
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

    private record ChapeuMaterialLinha(String material, String quantidade) {
    }
}