package model;

public class GameManager {
    private Survivor survivor;
    private Zombie zombie;

    public GameManager(Survivor survivor, Zombie zombie) {
        this.survivor = survivor;
        this.zombie = zombie;
    }

    public void resolveGlobalTurn() {
        // Le mosse segrete diventano realtà!
        survivor.executePlannedMove();
        zombie.executePlannedMove();
        
        System.out.println("Turno globale terminato: Personaggi mossi!");
    }

    public Survivor getSurvivor() { return survivor; }
    public Zombie getZombie() { return zombie; }
}