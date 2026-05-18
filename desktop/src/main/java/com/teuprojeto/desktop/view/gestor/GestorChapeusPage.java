package com.teuprojeto.desktop.view.gestor;

import com.teuprojeto.desktop.dto.ChapeuDto;
import com.teuprojeto.desktop.dto.ChapeuMaterialDto;
import com.teuprojeto.desktop.dto.MaterialDto;
import com.teuprojeto.desktop.service.ChapeuApiService;
import com.teuprojeto.desktop.service.ChapeuMaterialApiService;
import com.teuprojeto.desktop.service.MaterialApiService;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.*;
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
    private final VBox listaChapeus = new VBox(16);

    public GestorChapeusPage(GestorShellView shell) {
        this.shell = shell;
    }

    public Parent getView() {
        VBox root = new VBox(24);
        root.setPadding(new Insets(28));
        root.setStyle("-fx-background-color: #f4f7fb;");

        VBox header = new VBox(6);

        Label title = new Label("Gestão de Chapéus");
        title.setStyle("-fx-font-size: 30; -fx-font-weight: bold; -fx-text-fill: #0f172a;");

        Label subtitle = new Label("Crie chapéus para o catálogo e associe materiais de produção.");
        subtitle.setStyle("-fx-font-size: 14; -fx-text-fill: #64748b;");

        header.getChildren().addAll(title, subtitle);

        Label estado = new Label("A carregar dados...");
        estado.setStyle("-fx-text-fill: #64748b; -fx-font-weight: bold;");

        TextField nomeField = input("Nome do chapéu");
        TextField precoField = input("Preço de venda");

        TextField imagemField = input("Nenhuma imagem selecionada");
        imagemField.setEditable(false);

        final Path[] imagemSelecionada = {null};

        Button escolherImagem = secondaryButton("Escolher Imagem");
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
        materialInicialBox.setStyle(inputStyle());

        TextField quantidadeInicialField = input("Quantidade por unidade");

        Button criarChapeu = primaryButton("Criar Chapéu");
        criarChapeu.setOnAction(e -> criarChapeuComMaterial(
                nomeField,
                precoField,
                imagemSelecionada,
                materialInicialBox,
                quantidadeInicialField,
                estado,
                imagemField
        ));

        VBox formChapeu = card();
        formChapeu.getChildren().addAll(
                sectionHeader("🎩", "Novo Chapéu", "Crie um chapéu com imagem e material inicial obrigatório."),
                separator(),
                criarCampoBox("Nome", nomeField),
                criarCampoBox("Preço de venda", precoField),
                criarCampoBox("Imagem", new HBox(10, imagemField, escolherImagem)),
                criarCampoBox("Material inicial obrigatório", materialInicialBox),
                criarCampoBox("Quantidade necessária para produzir 1 unidade", quantidadeInicialField),
                criarChapeu
        );

        ComboBox<ChapeuDto> chapeuBox = new ComboBox<>();
        chapeuBox.setMaxWidth(Double.MAX_VALUE);
        chapeuBox.setConverter(chapeuConverter());
        chapeuBox.setStyle(inputStyle());

        ComboBox<MaterialDto> materialBox = new ComboBox<>();
        materialBox.setMaxWidth(Double.MAX_VALUE);
        materialBox.setConverter(materialConverter());
        materialBox.setStyle(inputStyle());

        TextField quantidadePorUnidadeField = input("Quantidade por unidade");

        Button associar = primaryButton("Associar Material");

        VBox materiaisAssociadosBox = new VBox(12);

        associar.setOnAction(e ->
                associarMaterial(
                        chapeuBox,
                        materialBox,
                        quantidadePorUnidadeField,
                        materiaisAssociadosBox,
                        estado
                )
        );

        chapeuBox.valueProperty().addListener((obs, oldValue, selected) ->
                carregarMateriaisDoChapeu(selected, materiaisAssociadosBox, estado)
        );

        VBox associacaoCard = card();
        associacaoCard.getChildren().addAll(
                sectionHeader("🧵", "Materiais do Chapéu", "Associe materiais adicionais ao chapéu selecionado."),
                separator(),
                criarCampoBox("Chapéu", chapeuBox),
                criarCampoBox("Material", materialBox),
                criarCampoBox("Quantidade necessária para produzir 1 unidade", quantidadePorUnidadeField),
                associar,
                smallTitle("Materiais associados"),
                materiaisAssociadosBox
        );

        VBox listaCard = card();
        listaCard.getChildren().addAll(
                sectionHeader("📋", "Chapéus Registados", "Chapéus disponíveis para o catálogo da parte web."),
                separator(),
                listaChapeus
        );

        root.getChildren().addAll(
                header,
                estado,
                formChapeu,
                associacaoCard,
                listaCard
        );

        carregarTudo(
                chapeuBox,
                materialBox,
                materialInicialBox,
                estado
        );

        return wrap(root);
    }

    private void carregarTudo(
            ComboBox<ChapeuDto> chapeuBox,
            ComboBox<MaterialDto> materialBox,
            ComboBox<MaterialDto> materialInicialBox,
            Label estado
    ) {
        try {
            List<ChapeuDto> chapeus = chapeuApiService.listar();

            materiaisDisponiveis.clear();
            materiaisDisponiveis.addAll(materialApiService.listarTodos());

            chapeuBox.getItems().setAll(chapeus);
            materialBox.getItems().setAll(materiaisDisponiveis);
            materialInicialBox.getItems().setAll(materiaisDisponiveis);

            atualizarListaChapeus(chapeus);

            estado.setText("Dados carregados.");

        } catch (RuntimeException e) {
            estado.setText("Erro ao carregar dados.");
            mostrarErro(e.getMessage());
        }
    }

    private void atualizarListaChapeus(List<ChapeuDto> chapeus) {
        listaChapeus.getChildren().clear();

        if (chapeus == null || chapeus.isEmpty()) {
            listaChapeus.getChildren().add(emptyBox("Ainda não existem chapéus registados."));
            return;
        }

        for (ChapeuDto chapeu : chapeus) {
            listaChapeus.getChildren().add(chapeuCard(chapeu));
        }
    }

    private VBox chapeuCard(ChapeuDto chapeu) {
        VBox card = new VBox(16);
        card.setPadding(new Insets(18));
        card.setStyle(
                "-fx-background-color: #f8fafc;" +
                        "-fx-background-radius: 18;" +
                        "-fx-border-color: #e5e7eb;" +
                        "-fx-border-radius: 18;"
        );

        HBox top = new HBox(14);
        top.setAlignment(Pos.CENTER_LEFT);

        StackPane icon = new StackPane();
        icon.setMinSize(58, 58);
        icon.setPrefSize(58, 58);
        icon.setStyle("-fx-background-color: #eff6ff; -fx-background-radius: 18;");

        Label iconText = new Label("🎩");
        iconText.setStyle("-fx-font-size: 24;");
        icon.getChildren().add(iconText);

        VBox text = new VBox(4);

        Label nome = new Label(valor(chapeu.getNome()));
        nome.setStyle("-fx-font-size: 18; -fx-font-weight: bold; -fx-text-fill: #0f172a;");

        Label codigo = new Label(chapeu.getCod() == null ? "Código: -" : "Código: " + chapeu.getCod());
        codigo.setStyle("-fx-text-fill: #2563eb; -fx-font-weight: bold;");

        text.getChildren().addAll(nome, codigo);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Label preco = badge(
                formatarValor(chapeu.getPrecoactvenda()),
                "#dcfce7",
                "#15803d"
        );

        Label imagem = badge(
                chapeu.getImagemUrl() == null || chapeu.getImagemUrl().isBlank() ? "Sem imagem" : "Com imagem",
                chapeu.getImagemUrl() == null || chapeu.getImagemUrl().isBlank() ? "#fee2e2" : "#dbeafe",
                chapeu.getImagemUrl() == null || chapeu.getImagemUrl().isBlank() ? "#dc2626" : "#2563eb"
        );

        top.getChildren().addAll(icon, text, spacer, preco, imagem);

        card.getChildren().add(top);

        return card;
    }

    private void criarChapeuComMaterial(
            TextField nomeField,
            TextField precoField,
            Path[] imagemSelecionada,
            ComboBox<MaterialDto> materialInicialBox,
            TextField quantidadeInicialField,
            Label estado,
            TextField imagemField
    ) {
        try {
            if (isBlank(nomeField.getText()) || isBlank(precoField.getText())) {
                mostrarErro("Preenche nome e preço.");
                return;
            }

            if (imagemSelecionada[0] == null) {
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
                    imagemSelecionada[0]
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
            imagemSelecionada[0] = null;

            atualizarListaChapeus(chapeuApiService.listar());

            estado.setText("Chapéu criado com sucesso.");

        } catch (RuntimeException e) {
            mostrarErro(e.getMessage());
        }
    }

    private void associarMaterial(
            ComboBox<ChapeuDto> chapeuBox,
            ComboBox<MaterialDto> materialBox,
            TextField quantidadePorUnidadeField,
            VBox materiaisAssociadosBox,
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

            carregarMateriaisDoChapeu(chapeu, materiaisAssociadosBox, estado);

            estado.setText("Material associado ao chapéu com sucesso.");

        } catch (RuntimeException e) {
            mostrarErro(e.getMessage());
        }
    }

    private void carregarMateriaisDoChapeu(
            ChapeuDto chapeu,
            VBox materiaisAssociadosBox,
            Label estado
    ) {
        materiaisAssociadosBox.getChildren().clear();

        if (chapeu == null || chapeu.getCod() == null) {
            materiaisAssociadosBox.getChildren().add(emptyBox("Seleciona um chapéu para ver os materiais."));
            return;
        }

        try {
            List<ChapeuMaterialDto> associacoes =
                    chapeuMaterialApiService.listarPorChapeu(chapeu.getCod());

            Map<Long, MaterialDto> materiaisPorId =
                    materiaisDisponiveis.stream()
                            .filter(m -> m.getId() != null)
                            .collect(Collectors.toMap(MaterialDto::getId, m -> m, (a, b) -> a));

            if (associacoes.isEmpty()) {
                materiaisAssociadosBox.getChildren().add(emptyBox("Este chapéu ainda não tem materiais associados."));
            } else {
                for (ChapeuMaterialDto associacao : associacoes) {
                    MaterialDto material = materiaisPorId.get(associacao.getIdMaterial());

                    String nomeMaterial =
                            material == null
                                    ? "Material #" + associacao.getIdMaterial()
                                    : material.getNome() + " (" + material.getUnidade() + ")";

                    String quantidade =
                            associacao.getQuantidadePorUnidade() == null
                                    ? "-"
                                    : associacao.getQuantidadePorUnidade().toPlainString();

                    materiaisAssociadosBox.getChildren().add(materialRow(nomeMaterial, quantidade));
                }
            }

            estado.setText("Materiais do chapéu carregados.");

        } catch (RuntimeException e) {
            estado.setText("Erro ao carregar materiais do chapéu.");
            mostrarErro(e.getMessage());
        }
    }

    private HBox materialRow(String material, String quantidade) {
        HBox row = new HBox(14);
        row.setAlignment(Pos.CENTER_LEFT);
        row.setPadding(new Insets(14));
        row.setStyle(
                "-fx-background-color: #f8fafc;" +
                        "-fx-background-radius: 18;" +
                        "-fx-border-color: #e5e7eb;" +
                        "-fx-border-radius: 18;"
        );

        StackPane icon = new StackPane();
        icon.setMinSize(46, 46);
        icon.setPrefSize(46, 46);
        icon.setStyle("-fx-background-color: #eff6ff; -fx-background-radius: 15;");

        Label iconText = new Label("🧵");
        iconText.setStyle("-fx-font-size: 20;");
        icon.getChildren().add(iconText);

        VBox text = new VBox(4);

        Label nome = new Label(material);
        nome.setStyle("-fx-font-size: 15; -fx-font-weight: bold; -fx-text-fill: #0f172a;");

        Label qtd = new Label("Quantidade por unidade: " + quantidade);
        qtd.setStyle("-fx-font-size: 12; -fx-text-fill: #64748b;");

        text.getChildren().addAll(nome, qtd);

        row.getChildren().addAll(icon, text);

        return row;
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

    private VBox criarCampoBox(String labelText, Control control) {
        VBox box = new VBox(8);

        Label label = new Label(labelText);
        label.setStyle("-fx-font-size: 13; -fx-font-weight: bold; -fx-text-fill: #334155;");

        control.setMaxWidth(Double.MAX_VALUE);

        box.getChildren().addAll(label, control);
        HBox.setHgrow(box, Priority.ALWAYS);

        return box;
    }

    private VBox criarCampoBox(String labelText, HBox controlBox) {
        VBox box = new VBox(8);

        Label label = new Label(labelText);
        label.setStyle("-fx-font-size: 13; -fx-font-weight: bold; -fx-text-fill: #334155;");

        for (var node : controlBox.getChildren()) {
            HBox.setHgrow(node, Priority.ALWAYS);
        }

        box.getChildren().addAll(label, controlBox);
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

    private VBox emptyBox(String text) {
        VBox box = new VBox();
        box.setPadding(new Insets(18));
        box.setStyle("-fx-background-color: #f8fafc; -fx-background-radius: 16;");

        Label label = new Label(text);
        label.setStyle("-fx-text-fill: #64748b; -fx-font-weight: bold;");

        box.getChildren().add(label);

        return box;
    }

    private Label smallTitle(String text) {
        Label label = new Label(text);
        label.setStyle("-fx-font-size: 17; -fx-font-weight: bold; -fx-text-fill: #0f172a;");
        return label;
    }

    private Label badge(String text, String bg, String fg) {
        Label label = new Label(text);

        label.setStyle(
                "-fx-background-color: " + bg + ";" +
                        "-fx-text-fill: " + fg + ";" +
                        "-fx-padding: 7 12 7 12;" +
                        "-fx-background-radius: 14;" +
                        "-fx-font-size: 12;" +
                        "-fx-font-weight: bold;"
        );

        return label;
    }

    private Button primaryButton(String text) {
        Button button = GestorUiFactory.primaryButton(text);
        button.setPrefHeight(42);
        return button;
    }

    private Button secondaryButton(String text) {
        Button button = GestorUiFactory.secondaryButton(text);
        button.setPrefHeight(42);
        return button;
    }

    private TextField input(String prompt) {
        TextField field = new TextField();
        field.setPromptText(prompt);
        field.setStyle(inputStyle());
        return field;
    }

    private String inputStyle() {
        return "-fx-background-color: white;" +
                "-fx-border-color: #dbe2ea;" +
                "-fx-border-radius: 14;" +
                "-fx-background-radius: 14;" +
                "-fx-padding: 11;" +
                "-fx-font-size: 14;";
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

    private String formatarValor(BigDecimal valor) {
        if (valor == null) {
            return "-";
        }

        return String.format("%.2f €", valor.doubleValue()).replace(".", ",");
    }

    private String valor(String value) {
        return value == null || value.isBlank() ? "-" : value;
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
}