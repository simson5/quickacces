package org.example.model;

public class Commande {
    private String id;
    private String nom;
    private String description;
    private String icon;
    private String ligneDeCommande;

    // Constructeur complet
    public Commande(String id, String nom, String description, String icon, String ligneDeCommande) {
        this.id = id;
        this.nom = nom;
        this.description = (description == null || description.trim().isEmpty()) ? "" : description;
        this.icon = (icon == null || icon.trim().isEmpty()) ? "⚙️" : icon;
        this.ligneDeCommande = ligneDeCommande;
    }

    public Commande() {}

    // Getters et Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getIcon() { return icon; }
    public void setIcon(String icon) { this.icon = icon; }

    public String getLigneDeCommande() { return ligneDeCommande; }
    public void setLigneDeCommande(String ligneDeCommande) { this.ligneDeCommande = ligneDeCommande; }
}
