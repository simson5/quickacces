package org.example.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import org.example.model.Commande;

import java.util.UUID;

public class AddCommandController {
    @FXML private VBox popupBox;
    @FXML private Label popupTitleLabel;
    @FXML private TextField nameField;
    @FXML private TextField iconField;
    @FXML private TextField commandLineField;
    @FXML private TextArea descriptionField;
    @FXML private Button saveButton;

    private HomeController mainController;
    private Commande commandeEnCours;
    private boolean modeEdition = false;

    public void setMainController(HomeController mainController) {
        this.mainController = mainController;
    }

    // FONCTION CLÉ : Reçoit les données et pré-remplit les champs si c'est une édition
    public void chargerDonnees(Commande commande) {
        if (commande != null) {
            this.commandeEnCours = commande;
            this.modeEdition = true;

            // Pré-remplissage des inputs
            popupTitleLabel.setText("Modifier la Commande");
            nameField.setText(commande.getNom());
            iconField.setText(commande.getIcon());
            commandLineField.setText(commande.getLigneDeCommande());
            descriptionField.setText(commande.getDescription());
            saveButton.setText("Enregistrer");
        } else {
            this.modeEdition = false;
            popupTitleLabel.setText("Nouvelle Commande");
            saveButton.setText("Créer");
        }
    }

    @FXML
    private void handleCancel(ActionEvent event) {
        if (mainController != null) {
            mainController.closePopupAnimation(popupBox);
        }
    }

    @FXML
    private void handleSave(ActionEvent event) {
        // Extraction des valeurs saisies
        String nom = nameField.getText();
        String icon = iconField.getText();
        String cmdLine = commandLineField.getText();
        String desc = descriptionField.getText();

        if (nom == null || nom.trim().isEmpty() || cmdLine == null || cmdLine.trim().isEmpty()) {
            // Affiche un toaster rouge en bas à droite de l'application !
            org.example.service.ToastService.show(
                    mainController.getRootStackPane(),
                    "Erreur : Le nom et la ligne de commande sont obligatoires !",
                    org.example.service.ToastService.ToastType.ERROR
            );
            return;
        }

        if (modeEdition) {
            // On met à jour l'objet existant
            commandeEnCours.setNom(nom);
            commandeEnCours.setIcon(icon);
            commandeEnCours.setLigneDeCommande(cmdLine);
            commandeEnCours.setDescription(desc);
        } else {
            // On crée un nouvel objet complet avec un ID unique généré
            String uniqueID = UUID.randomUUID().toString();
            commandeEnCours = new Commande(uniqueID, nom, desc, icon, cmdLine);
        }

        // On renvoie l'objet configuré au contrôleur principal
        if (mainController != null) {
            mainController.ajouterOuMettreAJourFiche(commandeEnCours, modeEdition);
            mainController.closePopupAnimation(popupBox);
        }
    }
}
