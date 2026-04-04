package model;

import java.util.ArrayList;
import java.util.List;

public class CrateManager {
    
    // Strict Capacity (Limite massimo di 2 casse)
    public static final int MAX_CRATES = 2;
    
    private List<Crate> activeCrates;

    public CrateManager() {
        this.activeCrates = new ArrayList<>();
    }

    // Pre-Generation Validation & Generation Bypass
    public boolean spawnCrate(int x, int y) {
        // Controllo: se abbiamo già 2 casse, blocca tutto e restituisci 'false'
        if (activeCrates.size() >= MAX_CRATES) {
            System.out.println("Limite casse raggiunto! Spawn annullato.");
            return false; 
        }
        
        // Se c'è spazio, crea la cassa e aggiungila alla lista
        Crate newCrate = new Crate(x, y);
        activeCrates.add(newCrate);
        System.out.println("Nuova cassa spawnata alle coordinate: " + x + ", " + y);
        return true; 
    }

    // Metodo per vedere quante casse ci sono (usato dal test)
    public List<Crate> getActiveCrates() {
        return activeCrates;
    }

    // Dynamic Reset (Quando un giocatore la raccoglie)
    public void removeCrate(Crate crate) {
        activeCrates.remove(crate);
        System.out.println("Cassa raccolta! Spazio liberato per un nuovo spawn.");
    }
}
