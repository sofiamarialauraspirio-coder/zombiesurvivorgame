package view;

import javax.swing.JFrame;
import model.GameMap;
import model.MapLoader;

public class GameFrame extends JFrame {

    public GameFrame() {
        // Impostazioni base della finestra
        setTitle("Zombie Survivor - Map View");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // 1. Carichiamo la mappa
        MapLoader loader = new MapLoader();
        GameMap map = loader.loadMap("src/main/resources/mappa_livello1.json");

        // 2. Creiamo il pannello della mappa
        MapPanel mapPanel = new MapPanel(map);

        // 3. Lo aggiungiamo e sistemiamo la finestra
        this.add(mapPanel);
        this.setResizable(false);
        this.pack();
        this.setLocationRelativeTo(null);
    }
}