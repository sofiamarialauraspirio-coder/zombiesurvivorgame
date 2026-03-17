package model;

public class GameSession {
    
    // Variabile di sessione per salvare la scelta del giocatore
    private String player1Choice;

    // Metodo per salvare (impostare) la scelta
    public void setPlayer1Choice(String choice) {
        this.player1Choice = choice;
    }

    // Metodo per leggere (recuperare) la scelta
    public String getPlayer1Choice() {
        return this.player1Choice;
    }
}