package model;

// "extends Entity" significa: Il Sopravvissuto è un figlio di Entity!
public class Survivor extends Entity {

    // 1. La "tasca" del giocatore: all'inizio è vuota (false)
    private boolean hasKey = false;

    // Costruttore del Sopravvissuto
    public Survivor(int x, int y) {
        // Anche qui, passiamo le coordinate al Padre tramite "super"
        super(x, y);
    }

    // 2. Metodo per controllare se il giocatore ha preso la chiave
    public boolean hasKey() {
        return hasKey;
    }

    // 3. Metodo per raccogliere la chiave (mette la variabile a true)
    public void pickUpKey() {
        this.hasKey = true;
    }
}