package view;

import javax.swing.JFrame;
import model.GameMap;
import model.MapLoader;
import model.GameManager;          // IMPORTIAMO L'ARBITRO
import controller.TurnController;  // IMPORTIAMO IL REGISTA

public class GameFrame extends JFrame {

    public GameFrame() {
        // Impostazioni base della finestra
        setTitle("Zombie Survivor - Map View");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // 1. Carichiamo la mappa
        MapLoader loader = new MapLoader();
        GameMap map = loader.loadMap("src/main/resources/mappa_livello1.json");

        // 2. Creiamo lo Schermo (MapPanel)
        MapPanel mapPanel = new MapPanel(map);

        // =================================================================
        // 3. COLLEGHIAMO IL CERVELLO AL GIOCO! (Il Cavo Mancante 🔌)
        // =================================================================
        if (map.getSurvivor() != null && map.getZombie() != null) {
            
            // A. Creiamo l'Arbitro e il Regista
            GameManager gameManager = new GameManager(map.getSurvivor(), map.getZombie());
            TurnController turnController = new TurnController(gameManager, map);

            // B. Presentiamo lo Schermo al Regista, e il Regista allo Schermo!
            mapPanel.setTurnController(turnController);
            turnController.setMapPanel(mapPanel);

        } else {
            System.err.println("Attenzione: Sopravvissuto o Zombie non trovati nella mappa!");
        }
        // =================================================================

        // 4. Aggiungiamo il pannello e mostriamo la finestra
        this.add(mapPanel);
        this.setResizable(false);
        this.pack();
        this.setLocationRelativeTo(null);
    }
}