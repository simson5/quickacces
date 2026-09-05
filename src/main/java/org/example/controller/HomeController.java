package org.example.controller;

import javafx.animation.FadeTransition;
import javafx.animation.ParallelTransition;
import javafx.animation.ScaleTransition;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Duration;
import org.example.model.Commande;
import org.example.service.JsonStorageService;
import org.example.service.ToastService;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.ResourceBundle;

import static javafx.collections.FXCollections.observableArrayList;

public class HomeController implements Initializable {
    @FXML
    private StackPane rootStackPane;

    @FXML
    private FlowPane cardsFlowPane;

    @FXML
    private StackPane popupOverlay;

    @FXML
    private VBox popupBox;

    private ObservableList<Commande> masterCommandeList = observableArrayList();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        masterCommandeList.addListener((javafx.collections.ListChangeListener<Commande>) c -> {
            rafraichirGrilleGraphique();
        });

        List<Commande> donneesChargees = JsonStorageService.charger();

        if(!donneesChargees.isEmpty()) {
            masterCommandeList.addAll(donneesChargees);
        }
    }

    private void rafraichirGrilleGraphique() {
        // On vide complètement l'affichage actuel
        cardsFlowPane.getChildren().clear();

        // On recrée les cartes visuelles pour chaque commande présente dans la liste
        for (Commande cmd : masterCommandeList) {
            creerEtAjouterCardGraphique(cmd);
        }
    }

    /**
     * Crée le composant visuel d'une carte à partir de son modèle et l'injecte dans la grille.
     */
    private void creerEtAjouterCardGraphique(@NotNull Commande cmd) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/card-item.fxml"));
            Parent card = loader.load();

            Label iconLabel = (Label) card.lookup("#iconLabel");
            TextField titleLabel = (TextField) card.lookup("#titleLabel");
            TextArea descriptionLabel = (TextArea) card.lookup("#descriptionLabel");
            VBox cardRoot = (VBox) card.lookup("#cardRoot");
            Button deleteCardButton = (Button) cardRoot.lookup("#deleteCardButton");
            Button editCardButton = (Button) card.lookup("#editCardButton");
            Button showCardButton = (Button) card.lookup("#showCardButton");

            iconLabel.setText(cmd.getIcon());
            titleLabel.setText(cmd.getNom());
            descriptionLabel.setText(cmd.getDescription());

            // ajoute d'un action de visualisation
            showCardButton.setOnAction(event -> {
                event.consume();
                showCardPopup(cmd);
            });

            // ajoute d'un action de edition
            editCardButton.setOnAction(event -> {
                event.consume();
                handleEditPopup(cmd);
            });

            // ajoute d'un action de supression
            deleteCardButton.setOnAction(event -> {
                event.consume();
                supprimerFiche(cmd);
            });

            // Associe l'objet modèle à la structure graphique
            cardRoot.setUserData(cmd);

            // GESTION DU CLIC : Lancement de la ligne de commande système
            cardRoot.setOnMouseClicked(event -> {
                // Gestion du clic simple pour exécuter l'action
                VBox sourceCard = (VBox) event.getSource();
                Commande commandeCliquee = (Commande) sourceCard.getUserData();

                System.out.println("Exécution : " + commandeCliquee.getLigneDeCommande());

                // Utilisation de ProcessBuilder pour lancer le programme Windows
//                try {
//                    // Sépare la commande par les espaces pour gérer les arguments (ex: cmd.exe /c ...)
//                    String[] cmdArgs = commandeCliquee.getLigneDeCommande().split(" ");
//                    ProcessBuilder pb = new ProcessBuilder(cmdArgs);
//                    pb.start(); // Lance l'application en arrière-plan sans bloquer JavaFX
//
//                    ToastService.show(rootStackPane, "Action lancée : " + commandeCliquee.getNom(), ToastService.ToastType.SUCCESS);
//                } catch (IOException e) {
//                    ToastService.show(rootStackPane, "Erreur d'exécution de l'application.", ToastService.ToastType.ERROR);
//                }
            });



            cardsFlowPane.getChildren().add(card);

        } catch (IOException e) {
            ToastService.show(rootStackPane, "Erreur de chargement des Commandes", ToastService.ToastType.ERROR);
        }
    }

    private void showCardPopup(Commande cmd) {
        System.out.println(cmd.getNom());
    }

    @FXML
    private void handleCreatePopup(ActionEvent event) {
        // Cas d'un AJOUT : on passe "null" puisqu'il n'y a pas encore de commande
        openPopup(null);
    }

    public void handleEditPopup(Commande commandeAModifier) {
        // Cas d'une ÉDITION : on passe l'objet existant pour pré-remplir
        openPopup(commandeAModifier);
    }

    private void openPopup(Commande commandeExiste) {
        try {
            // CORRECTION DU CHEMIN AVEC UN "/" INITIAL SÉCURISÉ
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/add-command-view.fxml"));
            VBox popupBox = loader.load();

            AddCommandController popupController = loader.getController();
            popupController.setMainController(this);

            // Mode dynamique : Si une commande est passée, le popup passe en édition !
            popupController.chargerDonnees(commandeExiste);

            popupOverlay.getChildren().clear();
            popupOverlay.getChildren().add(popupBox);
            popupOverlay.setVisible(true);

            // Animations fluides
            FadeTransition fade = new FadeTransition(Duration.millis(200), popupOverlay);
            fade.setFromValue(0.0);
            fade.setToValue(1.0);

            ScaleTransition scale = new ScaleTransition(Duration.millis(250), popupBox);
            scale.setFromX(0.7); scale.setFromY(0.7);
            scale.setToX(1.0); scale.setToY(1.0);

            new ParallelTransition(fade, scale).play();

        } catch (IOException e) {
            ToastService.show(rootStackPane, "Erreur de chargement du FXML du popup", ToastService.ToastType.ERROR);
        }
    }

    public void closePopupAnimation(VBox popupBox) {
        FadeTransition fade = new FadeTransition(Duration.millis(150), popupOverlay);
        fade.setFromValue(1.0); fade.setToValue(0.0);

        ScaleTransition scale = new ScaleTransition(Duration.millis(150), popupBox);
        scale.setToX(0.8); scale.setToY(0.8);

        ParallelTransition pt = new ParallelTransition(fade, scale);
        pt.setOnFinished(e -> {
            popupOverlay.setVisible(false);
            popupOverlay.getChildren().clear();
        });
        pt.play();
    }

    public void ajouterOuMettreAJourFiche(Commande cmd, boolean estEdition) {
        if (estEdition) {
            // En mode édition, on cherche l'ancienne commande dans notre liste Java et on la remplace
            for (int i = 0; i < masterCommandeList.size(); i++) {
                if (masterCommandeList.get(i).getId().equals(cmd.getId())) {
                    masterCommandeList.set(i, cmd); // Remplace l'objet (déclenche le rafraîchissement)
                    break;
                }
            }
            ToastService.show(rootStackPane, "Commande modifiée avec succès !", ToastService.ToastType.SUCCESS);
        } else {
            // En mode création, on l'ajoute simplement à la liste (déclenche le rafraîchissement)
            masterCommandeList.add(cmd);
            ToastService.show(rootStackPane, "Nouvelle commande créée !", ToastService.ToastType.SUCCESS);
        }

        JsonStorageService.sauvegarder(masterCommandeList);
    }

    public void supprimerFiche(Commande cmd) {
        masterCommandeList.removeIf(c -> c.getId().equals(cmd.getId()));
        JsonStorageService.sauvegarder(masterCommandeList);
        ToastService.show(rootStackPane, "Commande supprimée !", ToastService.ToastType.SUCCESS);
    }


    public StackPane getRootStackPane() { return rootStackPane; }

}
