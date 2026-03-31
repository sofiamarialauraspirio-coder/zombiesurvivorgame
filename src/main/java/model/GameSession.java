package model;

import java.util.Random;

public class GameSession {
    private int choosingPlayer; // Chi ha vinto la moneta (1 o 2)
    private String player1Role;
    private String player2Role;

    public GameSession() {
        // Costruttore base
    }

    // ==========================================
    // STORY 3: COIN TOSS & RANDOM GENERATION
    // ==========================================
    public int tossCoin() {
        Random rand = new Random();
        // rand.nextInt(2) genera 0 o 1. Aggiungiamo 1 per ottenere il Giocatore 1 o 2.
        this.choosingPlayer = rand.nextInt(2) + 1;
        return this.choosingPlayer;
    }

    // Metodo per forzare il giocatore (molto utile per i test)
    public void setChoosingPlayer(int player) {
        this.choosingPlayer = player;
    }

    public int getChoosingPlayer() {
        return this.choosingPlayer;
    }

    // ==========================================
    // STORY 3: AUTOMATIC ROLE COUPLING & ONE-CLICK
    // ==========================================
    public void assignRole(String chosenRole) {
        // Capiamo qual è il ruolo rimasto ("Se scegli Sopravvissuto, l'altro è Zombie")
        String otherRole = chosenRole.equals("SURVIVOR") ? "ZOMBIE" : "SURVIVOR";

        // Assegniamo i ruoli in base a chi aveva il diritto di scelta
        if (this.choosingPlayer == 1) {
            this.player1Role = chosenRole;
            this.player2Role = otherRole;
        } else if (this.choosingPlayer == 2) {
            this.player2Role = chosenRole;
            this.player1Role = otherRole;
        }
        
        System.out.println("Ruoli assegnati! P1: " + player1Role + " | P2: " + player2Role);
    }

    // Getter per i ruoli
    public String getPlayer1Role() {
        return player1Role;
    }

    public String getPlayer2Role() {
        return player2Role;
    }
}