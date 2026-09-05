package org.example.service;

import javafx.animation.FadeTransition;
import javafx.animation.SequentialTransition;
import javafx.animation.TranslateTransition;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.util.Duration;
import org.example.Main;

import java.io.IOException;

public class ToastService {
    // Énumération pour typer proprement les notifications
    public enum ToastType {
        INFO("ℹ️", "toast-info"),
        ERROR("❌", "toast-error"),
        WARNING("⚠️", "toast-warning"),
        SUCCESS("✅", "toast-success");

        private final String icon;
        private final String cssClass;

        ToastType(String icon, String cssClass) {
            this.icon = icon;
            this.cssClass = cssClass;
        }
    }

    public static void show(StackPane rootPane, String message, ToastType type) {
        try {
            // 1. Charger la vue FXML du toast
            FXMLLoader loader = new FXMLLoader(Main.class.getResource("/view/toast-view.fxml"));
            HBox toastNode = loader.load();

            // 2. Récupérer les éléments internes
            Label iconLabel = (Label) toastNode.lookup("#toastIcon");
            Label messageLabel = (Label) toastNode.lookup("#toastMessage");

            // 3. Injecter les données selon le type choisi
            iconLabel.setText(type.icon);
            messageLabel.setText(message);
            toastNode.getStyleClass().add(type.cssClass);

            // 4. Positionner le toast en bas à droite dans le StackPane principal
            StackPane.setAlignment(toastNode, Pos.BOTTOM_RIGHT);
            StackPane.setMargin(toastNode, new javafx.geometry.Insets(0, 20, 20, 0));

            // Rendre le toast transparent au départ pour l'animation
            toastNode.setOpacity(0.0);
            rootPane.getChildren().add(toastNode);

            // 5. ANIMATIONS (Apparition -> Attente -> Disparition)
            // Entrée : Glisse vers le haut et fondu en entrée
            FadeTransition fadeIn = new FadeTransition(Duration.millis(300), toastNode);
            fadeIn.setToValue(1.0);

            TranslateTransition slideIn = new TranslateTransition(Duration.millis(300), toastNode);
            slideIn.setFromY(50);
            slideIn.setToY(0);

            // Attente de 3 secondes
            FadeTransition stay = new FadeTransition(Duration.seconds(3), toastNode);
            stay.setFromValue(1.0);
            stay.setToValue(1.0);

            // Sortie : Fondu en fermeture
            FadeTransition fadeOut = new FadeTransition(Duration.millis(300), toastNode);
            fadeOut.setToValue(0.0);

            // Enchaînement séquentiel
            SequentialTransition sequence = new SequentialTransition(fadeIn, stay, fadeOut);

            // Nettoyage : retirer le composant graphique du StackPane une fois l'animation finie
            sequence.setOnFinished(e -> rootPane.getChildren().remove(toastNode));

            // Lancement simultané du slide et de la séquence
            slideIn.play();
            sequence.play();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
