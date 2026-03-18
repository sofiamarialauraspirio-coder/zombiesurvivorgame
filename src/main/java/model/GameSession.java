package model;

public class GameSession {
    private String player1Choice;
    private String player2Choice; // <-- AGGIUNTA: Il posto per il secondo giocatore

    // Metodi per il Giocatore 1 (già esistenti)
    public void setPlayer1Choice(String choice) {
        this.player1Choice = choice;
    }

    public String getPlayer1Choice() {
        return player1Choice;
    }

    // --- TUA PARTE: Metodi per il Giocatore 2 ---
    
    public void setPlayer2Choice(String choice) {
        this.player2Choice = choice;
    }

    public String getPlayer2Choice() {
        return player2Choice;
    }
}