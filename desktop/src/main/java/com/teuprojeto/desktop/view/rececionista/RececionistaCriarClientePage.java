package com.teuprojeto.desktop.view.rececionista;

import com.teuprojeto.desktop.dto.ClienteDto;
import com.teuprojeto.desktop.dto.CriarClienteRequestDto;
import com.teuprojeto.desktop.service.ClienteApiService;
import javafx.concurrent.Task;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.*;

public class RececionistaCriarClientePage {

    private final RececionistaShellView shell;
    private final ClienteApiService clienteApiService = new ClienteApiService();

    public RececionistaCriarClientePage(RececionistaShellView shell) {
        this.shell = shell;
    }

    public Parent getView() {
        VBox root = new VBox(24);
        root.setPadding(new Insets(28));
        root.setStyle("-fx-background-color: #f4f7fb;");

        VBox header = new VBox(6);

        Label title = new Label("Criar Cliente");
        title.setStyle("-fx-font-size: 30; -fx-font-weight: bold; -fx-text-fill: #0f172a;");

        Label subtitle = new Label("Registe um novo cliente na base de dados da fábrica.");
        subtitle.setStyle("-fx-font-size: 14; -fx-text-fill: #64748b;");

        header.getChildren().addAll(title, subtitle);

        VBox card = card();

        HBox cardHeader = new HBox(14);
        cardHeader.setAlignment(Pos.CENTER_LEFT);

        StackPane icon = new StackPane();
        icon.setMinSize(58, 58);
        icon.setPrefSize(58, 58);
        icon.setStyle("-fx-background-color: #eff6ff; -fx-background-radius: 18;");

        Label iconText = new Label("👤");
        iconText.setStyle("-fx-font-size: 24;");
        icon.getChildren().add(iconText);

        VBox cardTitleBox = new VBox(4);

        Label cardTitle = new Label("Dados do Cliente");
        cardTitle.setStyle("-fx-font-size: 22; -fx-font-weight: bold; -fx-text-fill: #0f172a;");

        Label cardSub = new Label("Preencha os dados principais e a morada do cliente.");
        cardSub.setStyle("-fx-font-size: 13; -fx-text-fill: #64748b;");

        cardTitleBox.getChildren().addAll(cardTitle, cardSub);
        cardHeader.getChildren().addAll(icon, cardTitleBox);

        GridPane form = new GridPane();
        form.setHgap(18);
        form.setVgap(14);

        TextField nome = input("Nome do cliente");
        TextField email = input("Email");
        TextField telefone = input("Telefone");
        TextField nif = input("NIF");

        ComboBox<String> tipo = new ComboBox<>();
        tipo.getItems().addAll("Particular", "Empresa");
        tipo.setPromptText("Selecionar tipo");
        tipo.setMaxWidth(Double.MAX_VALUE);
        tipo.setStyle(inputStyle());

        TextField rua = input("Rua");
        TextField porta = input("Porta");
        TextField codPostal = input("Código postal");
        TextField cidade = input("Cidade");

        TextArea observacoes = new TextArea();
        observacoes.setPromptText("Observações adicionais...");
        observacoes.setPrefRowCount(4);
        observacoes.setWrapText(true);
        observacoes.setStyle(inputStyle());

        addField(form, "Nome", nome, 0, 0);
        addField(form, "Tipo", tipo, 1, 0);

        addField(form, "Email", email, 0, 2);
        addField(form, "Telefone", telefone, 1, 2);

        addField(form, "NIF", nif, 0, 4);
        addField(form, "Cidade", cidade, 1, 4);

        addField(form, "Rua", rua, 0, 6);
        addField(form, "Porta", porta, 1, 6);

        addField(form, "Código Postal", codPostal, 0, 8);

        Label obsLabel = fieldLabel("Observações");
        form.add(obsLabel, 0, 10);
        form.add(observacoes, 0, 11, 2, 1);

        Button guardar = RececionistaUiFactory.primaryButton("Guardar Cliente");
        Button cancelar = RececionistaUiFactory.secondaryButton("Cancelar");

        cancelar.setOnAction(e -> shell.navigateTo(RececionistaPage.CLIENTES_LISTAR));

        guardar.setOnAction(e -> criarCliente(
                nome.getText(),
                email.getText(),
                telefone.getText(),
                nif.getText(),
                tipo.getValue(),
                rua.getText(),
                porta.getText(),
                codPostal.getText(),
                cidade.getText(),
                observacoes.getText(),
                guardar,
                cancelar
        ));

        HBox buttons = new HBox(12, guardar, cancelar);
        buttons.setAlignment(Pos.CENTER_LEFT);

        card.getChildren().addAll(
                cardHeader,
                separator(),
                form,
                buttons
        );

        root.getChildren().addAll(header, card);

        return wrap(root);
    }

    private void criarCliente(String nome,
                              String email,
                              String telefone,
                              String nif,
                              String tipo,
                              String rua,
                              String porta,
                              String codPostal,
                              String cidade,
                              String observacoes,
                              Button guardar,
                              Button cancelar) {

        if (isBlank(nome) || isBlank(email) || isBlank(telefone) || isBlank(nif)
                || isBlank(tipo) || isBlank(rua) || isBlank(codPostal) || isBlank(cidade)) {
            mostrarErro("Preenche todos os campos obrigatórios.");
            return;
        }

        CriarClienteRequestDto dto = new CriarClienteRequestDto();
        dto.setNome(nome.trim());
        dto.setEmail(email.trim());
        dto.setTelefone(telefone.trim());
        dto.setNif(nif.trim());
        dto.setTipo(tipo);
        dto.setRua(rua.trim());
        dto.setNporta(isBlank(porta) ? null : porta.trim());
        dto.setCodpostal(codPostal.trim());
        dto.setCidade(cidade.trim());
        dto.setObservacoes(isBlank(observacoes) ? null : observacoes.trim());

        guardar.setDisable(true);
        cancelar.setDisable(true);

        Task<ClienteDto> task = new Task<>() {
            @Override
            protected ClienteDto call() {
                return clienteApiService.criar(dto);
            }
        };

        task.setOnSucceeded(event -> {
            guardar.setDisable(false);
            cancelar.setDisable(false);

            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setHeaderText("Cliente criado com sucesso");
            alert.setContentText("O cliente foi criado no backend.");
            alert.showAndWait();

            shell.navigateTo(RececionistaPage.CLIENTES_LISTAR);
        });

        task.setOnFailed(event -> {
            guardar.setDisable(false);
            cancelar.setDisable(false);
            mostrarErro(task.getException() == null ? "Erro ao criar cliente." : task.getException().getMessage());
        });

        Thread thread = new Thread(task);
        thread.setDaemon(true);
        thread.start();
    }

    private void addField(GridPane form, String label, Control input, int col, int row) {
        form.add(fieldLabel(label), col, row);
        form.add(input, col, row + 1);

        GridPane.setHgrow(input, Priority.ALWAYS);
        input.setMaxWidth(Double.MAX_VALUE);
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

    private Label fieldLabel(String text) {
        Label label = new Label(text);
        label.setStyle("-fx-font-size: 13; -fx-font-weight: bold; -fx-text-fill: #334155;");
        return label;
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
}