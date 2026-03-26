package model;

// "extends Entity" significa: Lo Zombie è un figlio di Entity!
public class Zombie extends Entity {

    // Costruttore dello Zombie
    public Zombie(int x, int y) {
        // La parola "super" chiama il costruttore del Padre (Entity)
        // e gli passa le coordinate X e Y per salvarle
        super(x, y);
    }
}