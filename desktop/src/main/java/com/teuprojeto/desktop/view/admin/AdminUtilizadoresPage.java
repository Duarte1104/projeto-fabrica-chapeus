package com.teuprojeto.desktop.view.admin;

import com.teuprojeto.desktop.dto.UtilizadorDto;
import com.teuprojeto.desktop.service.AuthApiService;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

import java.util.ArrayList;
import java.util.List;

public class AdminUtilizadoresPage {

    private final AdminShellView shell;
    private final AuthApiService authApiService = new AuthApiService();

    private final List<UtilizadorDto> utilizadoresCache = new ArrayList<>();
    private final ObservableList<UtilizadorRow> tableData = FXCollections.observableArrayList();

    public AdminUtilizadoresPage(AdminShellView shell) {
        this.shell = shell;
    }

    public Parent getView() {
        VBox root = new VBox(18);
        root.setStyle("-fx-background-color: #efefef;");
        root.setPadding(new javafx.geometry.Insets(22));

        Label title = new Label("Utilizadores");
        title.setStyle("-fx-font-size: 24; -fx-font-weight: bold; -fx-text-fill: #111;");

        TextField search = new TextField();
        search.setPromptText("Pesquisar utilizador...");
        HBox.setHgrow(search, Priority.ALWAYS);

        Button novo = new Button("Novo Utilizador");
        novo.setStyle("-fx-background-color: black; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 8;");
        novo.setPrefHeight(38);
        novo.setOnAction(e -> shell.navigateTo(AdminPage.CRIAR_UTILIZADOR));

        Button atualizar = new Button("Atualizar");
        atualizar.setStyle("-fx-background-color: white; -fx-text-fill: black; -fx-font-weight: bold; -fx-border-color: #d0d0d0; -fx-background-radius: 8; -fx-border-radius: 8;");
        atualizar.setPrefHeight(38);

        HBox actions = new HBox(12, search, novo, atualizar);

        Label estado = new Label("A carregar utilizadores...");
        estado.setStyle("-fx-text-fill: #666666;");

        TableView<UtilizadorRow> table = new TableView<>();
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_ALL_COLUMNS);
        table.setItems(tableData);

        TableColumn<UtilizadorRow, String> emailCol = new TableColumn<>("Email");
        emailCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getEmail()));

        TableColumn<UtilizadorRow, String> roleCol = new TableColumn<>("Role");
        roleCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getRole()));

        TableColumn<UtilizadorRow, String> estadoCol = new TableColumn<>("Estado");
        estadoCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getEstado()));

        TableColumn<UtilizadorRow, Void> acoesCol = new TableColumn<>("Ações");
        acoesCol.setCellFactory(col -> new TableCell<>() {
            private final Button editarBtn = new Button("Editar");
            private final Button apagarBtn = new Button("Apagar");
            private final HBox box = new HBox(10, editarBtn, apagarBtn);

            {
                box.setAlignment(Pos.CENTER);

                editarBtn.setStyle("-fx-background-color: white; -fx-border-color: #cfcfcf; -fx-background-radius: 8; -fx-border-radius: 8;");
                apagarBtn.setStyle("-fx-background-color: white; -fx-border-color: #ef4444; -fx-text-fill: #ef4444; -fx-background-radius: 8; -fx-border-radius: 8;");

                editarBtn.setOnAction(e -> {
                    UtilizadorRow row = getTableView().getItems().get(getIndex());
                    abrirDialogEditar(row, () -> carregarUtilizadores(search.getText(), estado));
                });

                apagarBtn.setOnAction(e -> {
                    UtilizadorRow row = getTableView().getItems().get(getIndex());
                    confirmarApagar(row, () -> carregarUtilizadores(search.getText(), estado));
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : box);
            }
        });

        table.getColumns().addAll(emailCol, roleCol, estadoCol, acoesCol);

        VBox card = new VBox(16, actions, estado, table);
        card.setPadding(new javafx.geometry.Insets(18));
        card.setStyle("-fx-background-color: white; -fx-background-radius: 12; -fx-border-color: #dddddd; -fx-border-radius: 12;");

        search.textProperty().addListener((obs, oldValue, newValue) -> filtrarTabela(newValue));
        atualizar.setOnAction(e -> carregarUtilizadores(search.getText(), estado));

        root.getChildren().addAll(title, card);

        carregarUtilizadores("", estado);

        return root;
    }

    private void carregarUtilizadores(String filtroAtual, Label estado) {
        Task<List<UtilizadorDto>> task = new Task<>() {
            @Override
            protected List<UtilizadorDto> call() {
                return authApiService.listarUtilizadores();
            }
        };

        task.setOnSucceeded(event -> {
            utilizadoresCache.clear();
            utilizadoresCache.addAll(task.getValue());

            filtrarTabela(filtroAtual);
            estado.setText("Utilizadores carregados: " + utilizadoresCache.size());
        });

        task.setOnFailed(event -> {
            estado.setText("Erro ao carregar utilizadores.");

            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setHeaderText("Erro");
            alert.setContentText(task.getException() == null ? "Erro desconhecido." : task.getException().getMessage());
            alert.showAndWait();
        });

        Thread thread = new Thread(task);
        thread.setDaemon(true);
        thread.start();
    }

    private void filtrarTabela(String termo) {
        String t = termo == null ? "" : termo.trim().toLowerCase();

        List<UtilizadorRow> rows = utilizadoresCache.stream()
                .filter(u ->
                        t.isBlank()
                                || (u.getEmail() != null && u.getEmail().toLowerCase().contains(t))
                                || (u.getRole() != null && u.getRole().toLowerCase().contains(t))
                )
                .map(u -> new UtilizadorRow(
                        u.getId(),
                        u.getEmail(),
                        u.getRole(),
                        Boolean.TRUE.equals(u.getAtivo()) ? "Ativo" : "Inativo"
                ))
                .toList();

        tableData.setAll(rows);
    }

    private void abrirDialogEditar(UtilizadorRow row, Runnable onSuccess) {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Editar Utilizador");
        dialog.setHeaderText("Editar " + row.getEmail());

        ButtonType guardarType = new ButtonType("Guardar", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(guardarType, ButtonType.CANCEL);

        TextField emailField = new TextField(row.getEmail());
        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Nova password");

        VBox box = new VBox(12,
                new Label("Email"),
                emailField,
                new Label("Nova password"),
                passwordField
        );
        box.setPadding(new javafx.geometry.Insets(12));

        dialog.getDialogPane().setContent(box);

        dialog.showAndWait().ifPresent(result -> {
            if (result == guardarType) {
                try {
                    authApiService.atualizarUtilizador(
                            row.getId(),
                            emailField.getText().trim(),
                            passwordField.getText()
                    );

                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setHeaderText("Sucesso");
                    alert.setContentText("Utilizador atualizado com sucesso.");
                    alert.showAndWait();

                    onSuccess.run();
                } catch (Exception ex) {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setHeaderText("Erro");
                    alert.setContentText(ex.getMessage());
                    alert.showAndWait();
                }
            }
        });
    }

    private void confirmarApagar(UtilizadorRow row, Runnable onSuccess) {
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setHeaderText("Confirmar apagamento");
        confirm.setContentText("Tem a certeza que deseja apagar o utilizador " + row.getEmail() + "?");

        confirm.showAndWait().ifPresent(result -> {
            if (result == ButtonType.OK) {
                try {
                    authApiService.apagarUtilizador(row.getId());

                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setHeaderText("Sucesso");
                    alert.setContentText("Utilizador apagado com sucesso.");
                    alert.showAndWait();

                    onSuccess.run();
                } catch (Exception ex) {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setHeaderText("Erro");
                    alert.setContentText(ex.getMessage());
                    alert.showAndWait();
                }
            }
        });
    }
}