package org.example.service;

import org.example.model.Commande;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

public class JsonStorageService {
    private static final File FILE = new File("commandes.json");

    /**
     * Sauvegarde en écrivant le JSON sous forme de texte brut (Sans Jackson)
     */
    public static void sauvegarder(List<Commande> liste) {
        StringBuilder sb = new StringBuilder();
        sb.append("[\n");
        for (int i = 0; i < liste.size(); i++) {
            Commande cmd = liste.get(i);
            sb.append("  {\n");
            sb.append("    \"id\": \"").append(cmd.getId()).append("\",\n");
            sb.append("    \"nom\": \"").append(cmd.getNom().replace("\"", "\\\"")).append("\",\n");
            sb.append("    \"description\": \"").append(cmd.getDescription().replace("\"", "\\\"")).append("\",\n");
            sb.append("    \"icon\": \"").append(cmd.getIcon().replace("\"", "\\\"")).append("\",\n");
            sb.append("    \"ligneDeCommande\": \"").append(cmd.getLigneDeCommande().replace("\"", "\\\"")).append("\"\n");
            sb.append("  }");
            if (i < liste.size() - 1) sb.append(",");
            sb.append("\n");
        }
        sb.append("]");

        try {
            Files.writeString(FILE.toPath(), sb.toString());
        } catch (IOException e) {
            System.err.println("Erreur d'écriture native du JSON");
            e.printStackTrace();
        }
    }

    /**
     * Charge le fichier texte et extrait les objets manuellement (Sans Jackson)
     */
    public static List<Commande> charger() {
        List<Commande> liste = new ArrayList<>();
        if (!FILE.exists()) return liste;

        try {
            String content = Files.readString(FILE.toPath()).trim();
            // Découpage rudimentaire mais efficace des blocs {...}
            String[] blocks = content.split("\\s*\\}\\s*,?\\s*\\{\\s*");

            for (String block : blocks) {
                if (!block.contains("\"id\"")) continue;

                String id = extraireValeur(block, "id");
                String nom = extraireValeur(block, "nom");
                String desc = extraireValeur(block, "description");
                String icon = extraireValeur(block, "icon");
                String cmdLine = extraireValeur(block, "ligneDeCommande");

                liste.add(new Commande(id, nom, desc, icon, cmdLine));
            }
        } catch (Exception e) {
            System.err.println("Erreur de lecture native du JSON, retour d'une liste vide.");
        }
        return liste;
    }

    /**
     * Petite méthode utilitaire pour récupérer le texte entre guillemets pour une clé donnée
     */
    private static String extraireValeur(String bloc, String cle) {
        String cible = "\"" + cle + "\":";
        int indexCle = bloc.indexOf(cible);
        if (indexCle == -1) return "";

        int debutValeur = bloc.indexOf("\"", indexCle + cible.length());
        int finValeur = bloc.indexOf("\"", debutValeur + 1);

        if (debutValeur == -1 || finValeur == -1) return "";
        return bloc.substring(debutValeur + 1, finValeur).replace("\\\"", "\"");
    }
}
