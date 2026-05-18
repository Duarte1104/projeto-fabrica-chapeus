package com.teuprojeto.desktop.view.gestor;

import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

public class GestorUiFactory {

    public static VBox createPageContainer(String titleText) {
        VBox root = new VBox(24);
        root.setPadding(new Insets(32));
        root.setStyle("-fx-background-color: #f4f7fb;");

        Label title = new Label(titleText);
        title.setStyle(
                "-fx-font-size: 32;" +
                        "-fx-font-weight: bold;" +
                        "-fx-text-fill: #0f172a;"
        );

        root.getChildren().add(title);
        return root;
    }

    public static VBox createCard() {
        VBox card = new VBox(16);
        card.setPadding(new Insets(24));
        card.setStyle(
                "-fx-background-color: white;" +
                        "-fx-background-radius: 22;" +
                        "-fx-border-radius: 22;" +
                        "-fx-border-color: #e5e7eb;" +
                        "-fx-effect: dropshadow(gaussian, rgba(15,23,42,0.06), 18, 0, 0, 6);"
        );

        return card;
    }

    public static Button primaryButton(String text) {
        Button btn = new Button(text);
        btn.setPrefHeight(42);
        btn.setStyle(
                "-fx-background-color: linear-gradient(to right, #2563eb, #1d4ed8);" +
                        "-fx-text-fill: white;" +
                        "-fx-font-size: 14;" +
                        "-fx-font-weight: bold;" +
                        "-fx-background-radius: 14;" +
                        "-fx-cursor: hand;" +
                        "-fx-padding: 0 22 0 22;"
        );

        return btn;
    }

    public static Button secondaryButton(String text) {
        Button btn = new Button(text);
        btn.setPrefHeight(42);
        btn.setStyle(
                "-fx-background-color: white;" +
                        "-fx-text-fill: #0f172a;" +
                        "-fx-font-size: 14;" +
                        "-fx-font-weight: bold;" +
                        "-fx-border-color: #dbe2ea;" +
                        "-fx-border-width: 1.5;" +
                        "-fx-background-radius: 14;" +
                        "-fx-border-radius: 14;" +
                        "-fx-cursor: hand;" +
                        "-fx-padding: 0 20 0 20;"
        );

        return btn;
    }
}