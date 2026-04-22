package com.teuprojeto.desktop.view.rececionista;

import com.teuprojeto.desktop.dto.ClienteDto;
import com.teuprojeto.desktop.dto.CriarClienteRequestDto;
import com.teuprojeto.desktop.service.ClienteApiService;
import javafx.concurrent.Task;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class RececionistaCriarClientePage {

    private final RececionistaShellView shell;
    private final ClienteApiService clienteApiService = new ClienteApiService();

    public RececionistaCriarClientePage(RececionistaShellView shell) {
        this.shell = shell;
    }

    public Parent getView() {
        VBox root = RececionistaUiFactory.createPageContainer("Criar Cliente");

        GridPane form = new GridPane();
        form.setHgap(16);
        form.setVgap(14);
        form.setPadding(new Insets(10, 0, 0, 0));

        TextField nome = new TextField();
        TextField email = new TextField();
        TextField telefone = new TextField();
        TextField nif = new TextField();

        ComboBox<String> tipo = new ComboBox<>();
        tipo.getItems().addAll("Particular", "Empresa");
        tipo.setMaxWidth(Double.MAX_VALUE);

        TextField rua = new TextField();
        TextField porta = new TextField();
        TextField codPostal = new TextField();
        TextField cidade = new TextField();

        TextArea observacoes = new TextArea();
        observacoes.setPrefRowCount(4);

        form.add(new Label("Nome"), 0, 0);
        form.add(nome, 0, 1);
        form.add(new Label("Tipo"), 1, 0);
        form.add(tipo, 1, 1);

        form.add(new Label("Email"), 0, 2);
        form.add(email, 0, 3);
        form.add(new Label("Telefone"), 1, 2);
        form.add(telefone, 1, 3);

        form.add(new Label("NIF"), 0, 4);
        form.add(nif, 0, 5);
        form.add(new Label("Cidade"), 1, 4);
        form.add(cidade, 1, 5);

        form.add(new Label("Rua"), 0, 6);
        form.add(rua, 0, 7);
        form.add(new Label("Porta"), 1, 6);
        form.add(porta, 1, 7);

        form.add(new Label("Código Postal"), 0, 8);
        form.add(codPostal, 0, 9);

        form.add(new Label("Observações"), 0, 10);
        form.add(observacoes, 0, 11, 2, 1);

        Button guardar = RececionistaUiFactory.primaryButton("Guardar");
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

        HBox buttons = new HBox(10, guardar, cancelar);

        VBox card = RececionistaUiFactory.createCard();
        card.getChildren().addAll(form, buttons);

        root.getChildren().add(card);
        return root;
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