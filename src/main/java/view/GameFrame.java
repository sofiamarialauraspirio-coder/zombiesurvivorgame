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

        // =================================================================
        // TEST VISIVO: Accendiamo i quadrati gialli attorno al Sopravvissuto!
        // (ORA E' DENTRO LA PARENTESI GIUSTA!)
        // =================================================================
        if (map.getSurvivor() != null) {
            int sx = map.getSurvivor().getX();
            int sy = map.getSurvivor().getY();
            
            // STAMPIAMO NEL TERMINALE LE COORDINATE PER CONTROLLO
            System.out.println("➡️ Posizione Sopravvissuto: X=" + sx + ", Y=" + sy);
            
            java.util.List<java.awt.Point> mosseValide = map.getValidMoves(sx, sy, 1);
            
            // STAMPIAMO QUANTE MOSSE HA TROVATO
            System.out.println("➡️ Mosse valide calcolate: " + mosseValide.size());
            
            mapPanel.setValidMoves(mosseValide);
        }
        // =================================================================

        // 3. Lo aggiungiamo e sistemiamo la finestra
        this.add(mapPanel);
        this.setResizable(false);
        this.pack();
        this.setLocationRelativeTo(null);
    }
}