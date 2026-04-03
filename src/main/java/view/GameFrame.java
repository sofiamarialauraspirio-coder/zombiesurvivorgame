package view;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import model.GameMap;
import model.MapLoader;
import model.GameManager;
import controller.TurnController;
import controller.TurnController.GameState;

public class GameFrame extends JFrame {
    private MapPanel mapPanel;
    private TurnController turnController;
    private GameMap map; // <-- Aggiunto per permettere agli altri file di leggere la mappa

    // Elementi dell'HUD Laterale
    private JLabel lblNomeGiocatore;
    private JLabel lblAzione;
    private JPanel hudPanel;

    public GameFrame() {
        // Impostazioni base della finestra
        setTitle("Zombie Survivor - Map View");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout()); // Divisore dello schermo (Sinistra/Destra)
        setBackground(new Color(20, 20, 20));

        // 1. Carichiamo la mappa
        MapLoader loader = new MapLoader();
        this.map = loader.loadMap("src/main/resources/mappa_livello1.json");

        // 2. Creiamo lo Schermo (MapPanel)
        mapPanel = new MapPanel(map);

        // 3. COLLEGHIAMO IL CERVELLO AL GIOCO! 
        if (map.getSurvivor() != null && map.getZombie() != null) {
            GameManager gameManager = new GameManager(map.getSurvivor(), map.getZombie());
            turnController = new TurnController(gameManager, map);

            mapPanel.setTurnController(turnController);
            turnController.setMapPanel(mapPanel);
        } else {
            System.err.println("Attenzione: Sopravvissuto o Zombie non trovati nella mappa!");
        }

        // 4. Aggiungiamo la Mappa a SINISTRA (Centro)
        this.add(mapPanel, BorderLayout.CENTER);

        // 5. Creiamo e aggiungiamo l'HUD a DESTRA (East)
        creaHudLaterale();
        this.add(hudPanel, BorderLayout.EAST);

        // 6. Colleghiamo l'HUD alla mappa in modo che si aggiorni quando premi le frecce!
        mapPanel.setOnStateChanged(() -> aggiornaHud());

        this.setResizable(false);
        this.pack();
        this.setLocationRelativeTo(null);
        
        // Primo aggiornamento dei testi per farli apparire subito
        aggiornaHud();
    }

    private void creaHudLaterale() {
        hudPanel = new JPanel();
        hudPanel.setLayout(new BoxLayout(hudPanel, BoxLayout.Y_AXIS));
        // L'HUD sarà largo 260 pixel e alto quanto la mappa
        hudPanel.setPreferredSize(new Dimension(260, mapPanel.getPreferredSize().height));
        hudPanel.setBackground(new Color(30, 30, 35));
        
        // Un bel bordo scuro per separare la mappa dall'HUD
        hudPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 4, 0, 0, new Color(50, 50, 55)),
                new EmptyBorder(20, 15, 20, 15)
        ));

        // Titolo fisso "PANNELLO DI COMANDO"
        JLabel lblHeader = new JLabel("PANNELLO DI COMANDO");
        lblHeader.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblHeader.setForeground(new Color(150, 150, 150));
        lblHeader.setAlignmentX(Component.CENTER_ALIGNMENT);

        hudPanel.add(lblHeader);
        hudPanel.add(Box.createRigidArea(new Dimension(0, 40))); // Spazio vuoto

        // Sezione: TURNO ATTUALE
        JLabel lblTurnoTitolo = new JLabel("TURNO ATTUALE:");
        lblTurnoTitolo.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblTurnoTitolo.setForeground(Color.LIGHT_GRAY);
        lblTurnoTitolo.setAlignmentX(Component.CENTER_ALIGNMENT);

        lblNomeGiocatore = new JLabel("-");
        lblNomeGiocatore.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblNomeGiocatore.setAlignmentX(Component.CENTER_ALIGNMENT);

        hudPanel.add(lblTurnoTitolo);
        hudPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        hudPanel.add(lblNomeGiocatore);
        hudPanel.add(Box.createRigidArea(new Dimension(0, 50)));

        // Sezione: AZIONE RICHIESTA
        JLabel lblAzioneTitolo = new JLabel("AZIONE RICHIESTA:");
        lblAzioneTitolo.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblAzioneTitolo.setForeground(Color.LIGHT_GRAY);
        lblAzioneTitolo.setAlignmentX(Component.CENTER_ALIGNMENT);

        lblAzione = new JLabel("<html><center>-</center></html>");
        lblAzione.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblAzione.setForeground(Color.WHITE);
        lblAzione.setAlignmentX(Component.CENTER_ALIGNMENT);

        hudPanel.add(lblAzioneTitolo);
        hudPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        hudPanel.add(lblAzione);
        
        // Spingiamo tutto verso l'alto
        hudPanel.add(Box.createVerticalGlue());
    }

    // Questo metodo è la magia: viene richiamato ogni volta che giochi
    public void aggiornaHud() {
        if (turnController == null) return;
        GameState state = turnController.getCurrentState();

        if (state == GameState.P1_CHOICE || state == GameState.P2_CHOICE) {
            boolean isSurvivorTurn = turnController.isSurvivorTurn();
            
            // Cambia nome e colore in base a chi gioca
            lblNomeGiocatore.setText(isSurvivorTurn ? "SOPRAVVISSUTO" : "ZOMBIE");
            lblNomeGiocatore.setForeground(isSurvivorTurn ? new Color(255, 200, 0) : new Color(180, 50, 255));

            // Indica se devi muoverti o piazzare il blocco
            if (mapPanel.isChoosingBlock()) {
                lblAzione.setText("<html><center>Piazza la<br><font color='#FF5555'>TRAPPOLA (Rosso)</font></center></html>");
            } else {
                lblAzione.setText("<html><center>Scegli il<br><font color='#FFFF55'>MOVIMENTO (Giallo)</font></center></html>");
            }
        } 
        else if (state == GameState.SURVIVOR_VICTORY) {
            lblNomeGiocatore.setText("VITTORIA!");
            lblNomeGiocatore.setForeground(new Color(255, 215, 0));
            lblAzione.setText("<html><center>Il Sopravvissuto<br>è fuggito!</center></html>");
        }
        else if (state == GameState.ZOMBIE_VICTORY) {
            lblNomeGiocatore.setText("SCONFITTA!");
            lblNomeGiocatore.setForeground(new Color(255, 50, 50));
            lblAzione.setText("<html><center>Lo Zombie ha<br>vinto!</center></html>");
        }
        
        hudPanel.revalidate();
        hudPanel.repaint();
    }

    // I GETTER necessari alla selezione dei personaggi
    public TurnController getTurnController() { return turnController; }
    public MapPanel getMapPanel() { return mapPanel; }
    public GameMap getMap() { return map; }
}