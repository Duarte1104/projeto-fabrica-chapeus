package com.teuprojeto.desktop.view.designer;

import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

public class DesignerUiFactory {

    public static VBox createPageContainer(String titleText) {
        VBox root = new VBox(18);
        root.setPadding(new Insets(22));

        Label title = new Label(titleText);
        title.setStyle("-fx-font-size: 24; -fx-font-weight: bold; -fx-text-fill: #111;");

        root.getChildren().add(title);
        return root;
    }

    public static VBox createCard() {
        VBox card = new VBox(14);
        card.setPadding(new Insets(18));
        card.setStyle("-fx-background-color: white; -fx-background-radius: 12; -fx-border-color: #dddddd; -fx-border-radius: 12;");
        return card;
    }

    public static Button primaryButton(String text) {
        Button btn = new Button(text);
        btn.setStyle("-fx-background-color: black; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 8;");
        btn.setPrefHeight(38);
        return btn;
    }

    public static Button secondaryButton(String text) {
        Button btn = new Button(text);
        btn.setStyle("-fx-background-color: white; -fx-text-fill: black; -fx-font-weight: bold; -fx-border-color: #d0d0d0; -fx-background-radius: 8; -fx-border-radius: 8;");
        btn.setPrefHeight(38);
        return btn;
    }
}