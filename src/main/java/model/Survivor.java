package model;

public class Survivor extends Entity {
    
    private boolean hasKey = false;

    public Survivor(int startX, int startY) {
        // Chiama il costruttore della classe padre (Entity)
        super(startX, startY);
    }

    // Metodi specifici del Sopravvissuto
    public boolean hasKey() { 
        return hasKey; 
    }
    
    public void collectKey() { 
        this.hasKey = true; 
    }
    
    public void dropKey() { 
        this.hasKey = false; 
    }
}