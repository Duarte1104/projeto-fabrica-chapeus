package com.teuprojeto.desktop;

import com.teuprojeto.desktop.model.AppUser;
import com.teuprojeto.desktop.view.DashboardView;
import com.teuprojeto.desktop.view.LoginView;
import com.teuprojeto.desktop.view.RegisterView;
import com.teuprojeto.desktop.view.designer.DesignerShellView;
import com.teuprojeto.desktop.view.funcionario.FuncionarioShellView;
import com.teuprojeto.desktop.view.gestor.GestorShellView;
import com.teuprojeto.desktop.view.rececionista.RececionistaShellView;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class MainApp extends Application {

    private Stage stage;

    @Override
    public void start(Stage stage) {
        this.stage = stage;
        showLogin();
        stage.setTitle("Fábrica de Chapéus");
        stage.show();
    }

    public void showLogin() {
        Scene scene = new Scene(new LoginView(this).getView(), 1280, 820);
        stage.setScene(scene);
    }

    public void showRegister() {
        Scene scene = new Scene(new RegisterView(this).getView(), 1280, 820);
        stage.setScene(scene);
    }

    public void showDashboard(AppUser user) {
        if (user.getRole().name().equals("RECECIONISTA")) {
            Scene scene = new Scene(new RececionistaShellView(this, user).getView(), 1280, 820);
            stage.setScene(scene);
            return;
        }

        if (user.getRole().name().equals("GESTOR")) {
            Scene scene = new Scene(new GestorShellView(this, user).getView(), 1280, 820);
            stage.setScene(scene);
            return;
        }

        if (user.getRole().name().equals("DESIGNER")) {
            Scene scene = new Scene(new DesignerShellView(this, user).getView(), 1280, 820);
            stage.setScene(scene);
            return;
        }

        if (user.getRole().name().equals("FUNCIONARIO")) {
            Scene scene = new Scene(new FuncionarioShellView(this, user).getView(), 1280, 820);
            stage.setScene(scene);
            return;
        }

        Scene scene = new Scene(new DashboardView(this, user).getView(), 1280, 820);
        stage.setScene(scene);
    }

    public static void main(String[] args) {
        launch();
    }
}