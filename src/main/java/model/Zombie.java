package model;

public class Zombie extends Entity {

    public Zombie(int startX, int startY) {
        // Chiama il costruttore della classe padre (Entity)
        super(startX, startY);
    }
    
    // In futuro, se lo Zombie avrà abilità speciali (es. sfondare porte),
    // potrai aggiungerle qui!
}