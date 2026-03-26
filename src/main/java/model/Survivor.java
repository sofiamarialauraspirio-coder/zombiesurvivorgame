package model;

// "extends Entity" significa: Il Sopravvissuto è un figlio di Entity!
public class Survivor extends Entity {

    // Costruttore del Sopravvissuto
    public Survivor(int x, int y) {
        // Anche qui, passiamo le coordinate al Padre tramite "super"
        super(x, y);
    }
}
