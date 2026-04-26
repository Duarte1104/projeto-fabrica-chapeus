package com.teuprojeto.desktop.view;

import com.teuprojeto.desktop.model.AppUser;
import com.teuprojeto.desktop.service.AuthApiService;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;

public class ProfileDialogUtil {

    public static void abrirAlterarPassword(AppUser user) {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Perfil");
        dialog.setHeaderText("Alterar palavra-passe");

        ButtonType guardarType = new ButtonType("Guardar", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(guardarType, ButtonType.CANCEL);

        PasswordField passwordAtual = new PasswordField();
        passwordAtual.setPromptText("Palavra-passe atual");

        PasswordField novaPassword = new PasswordField();
        novaPassword.setPromptText("Nova palavra-passe");

        PasswordField confirmarPassword = new PasswordField();
        confirmarPassword.setPromptText("Confirmar nova palavra-passe");

        GridPane grid = new GridPane();
        grid.setHgap(12);
        grid.setVgap(12);
        grid.setPadding(new Insets(20));

        grid.add(new Label("Email"), 0, 0);
        grid.add(new Label(user.getEmail()), 1, 0);

        grid.add(new Label("Atual"), 0, 1);
        grid.add(passwordAtual, 1, 1);

        grid.add(new Label("Nova"), 0, 2);
        grid.add(novaPassword, 1, 2);

        grid.add(new Label("Confirmar"), 0, 3);
        grid.add(confirmarPassword, 1, 3);

        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(buttonType -> {
            if (buttonType == guardarType) {
                return guardarType;
            }
            return null;
        });

        dialog.showAndWait().ifPresent(result -> {
            if (result == guardarType) {
                if (passwordAtual.getText().isBlank() || novaPassword.getText().isBlank() || confirmarPassword.getText().isBlank()) {
                    mostrarErro("Preenche todos os campos.");
                    return;
                }

                if (!novaPassword.getText().equals(confirmarPassword.getText())) {
                    mostrarErro("A confirmação da nova palavra-passe não coincide.");
                    return;
                }

                try {
                    AuthApiService authApiService = new AuthApiService();
                    authApiService.alterarPassword(
                            user.getEmail(),
                            passwordAtual.getText(),
                            novaPassword.getText()
                    );

                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setHeaderText("Sucesso");
                    alert.setContentText("A palavra-passe foi alterada com sucesso.");
                    alert.showAndWait();

                } catch (Exception ex) {
                    mostrarErro(ex.getMessage());
                }
            }
        });
    }

    private static void mostrarErro(String mensagem) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setHeaderText("Erro");
        alert.setContentText(mensagem);
        alert.showAndWait();
    }
}