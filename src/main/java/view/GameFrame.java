package view;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import model.GameMap;
import model.MapLoader;
import model.GameManager;
import model.GameSession; 
import controller.TurnController;
import controller.TurnController.GameState;

public class GameFrame extends JFrame {
    private MapPanel mapPanel;
    private TurnController turnController;
    private GameMap map; 

    // Elementi dell'HUD Laterale
    private JLabel lblNomeGiocatore;
    private JLabel lblAzione;
    private JPanel hudPanel;
    
    // Variabile per non far spawnare infiniti popup a fine partita
    private boolean popupFinePartitaMostrato = false;

    public GameFrame() {
        setTitle("Zombie Survivor - Map View");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout()); 
        setBackground(new Color(20, 20, 20));

        MapLoader loader = new MapLoader();
        this.map = loader.loadMap("src/main/resources/mappa_livello1.json");
        mapPanel = new MapPanel(map);

        if (map.getSurvivor() != null && map.getZombie() != null) {
            GameManager gameManager = new GameManager(map.getSurvivor(), map.getZombie());
            turnController = new TurnController(gameManager, map);

            mapPanel.setTurnController(turnController);
            turnController.setMapPanel(mapPanel);
        } else {
            System.err.println("Attenzione: Sopravvissuto o Zombie non trovati nella mappa!");
        }

        this.add(mapPanel, BorderLayout.CENTER);

        creaHudLaterale();
        this.add(hudPanel, BorderLayout.EAST);

        mapPanel.setOnStateChanged(() -> aggiornaHud());

        this.setResizable(false);
        this.pack();
        this.setLocationRelativeTo(null);
        
        aggiornaHud();
    }

    private void creaHudLaterale() {
        hudPanel = new JPanel();
        hudPanel.setLayout(new BoxLayout(hudPanel, BoxLayout.Y_AXIS));
        hudPanel.setPreferredSize(new Dimension(260, mapPanel.getPreferredSize().height));
        hudPanel.setBackground(new Color(30, 30, 35));
        
        hudPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 4, 0, 0, new Color(50, 50, 55)),
                new EmptyBorder(20, 15, 20, 15)
        ));

        JLabel lblHeader = new JLabel("PANNELLO DI COMANDO");
        lblHeader.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblHeader.setForeground(new Color(150, 150, 150));
        lblHeader.setAlignmentX(Component.CENTER_ALIGNMENT);

        hudPanel.add(lblHeader);
        hudPanel.add(Box.createRigidArea(new Dimension(0, 40))); 

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
        
        hudPanel.add(Box.createVerticalGlue());
    }

    public void aggiornaHud() {
        if (turnController == null) return;
        GameState state = turnController.getCurrentState();

        if (state == GameState.P1_CHOICE || state == GameState.P2_CHOICE) {
            boolean isSurvivorTurn = turnController.isSurvivorTurn();
            
            lblNomeGiocatore.setText(isSurvivorTurn ? "SOPRAVVISSUTO" : "ZOMBIE");
            lblNomeGiocatore.setForeground(isSurvivorTurn ? new Color(255, 200, 0) : new Color(180, 50, 255));

            if (mapPanel.isChoosingBlock()) {
                lblAzione.setText("<html><center>Piazza la<br><font color='#FF5555'>TRAPPOLA (Rosso)</font></center></html>");
            } else {
                lblAzione.setText("<html><center>Scegli il<br><font color='#FFFF55'>MOVIMENTO (Giallo)</font></center></html>");
            }
        } 
        else if (state == GameState.SURVIVOR_VICTORY || state == GameState.ZOMBIE_VICTORY) {
            // Aggiorniamo l'HUD come prima
            if (state == GameState.SURVIVOR_VICTORY) {
                lblNomeGiocatore.setText("VITTORIA!");
                lblNomeGiocatore.setForeground(new Color(255, 215, 0));
                lblAzione.setText("<html><center>Il Sopravvissuto<br>è fuggito!</center></html>");
            } else {
                lblNomeGiocatore.setText("SCONFITTA!");
                lblNomeGiocatore.setForeground(new Color(255, 50, 50));
                lblAzione.setText("<html><center>Lo Zombie ha<br>vinto!</center></html>");
            }
            
            // 🚀 E QUI LANCIAMO IL POPUP! 🚀
            if (!popupFinePartitaMostrato) {
                popupFinePartitaMostrato = true;
                mostraSchermataFinePartita(state);
            }
        }
        
        hudPanel.revalidate();
        hudPanel.repaint();
    }

    // =========================================================
    // 🏆 NUOVO METODO: IL POPUP DI FINE PARTITA
    // =========================================================
    private void mostraSchermataFinePartita(GameState state) {
        // Usiamo un Timer per aspettare mezzo secondo prima di mostrare il popup, 
        // così il giocatore fa in tempo a vedere l'ultima mossa!
        Timer timer = new Timer(500, e -> {
            JDialog dialog = new JDialog(this, true);
            dialog.setUndecorated(true);
            dialog.setBackground(new Color(0, 0, 0, 0)); // Sfondo trasparente per il vetro arrotondato

            boolean survivorVince = (state == GameState.SURVIVOR_VICTORY);
            Color coloreTema = survivorVince ? new Color(255, 215, 0) : new Color(255, 50, 50);

            // Creiamo il pannello "vetro"
            JPanel glassPanel = new JPanel(new BorderLayout(0, 25)) {
                @Override
                protected void paintComponent(Graphics g) {
                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    g2.setColor(new Color(15, 15, 15, 230)); // Grigio scuro quasi nero
                    g2.fillRoundRect(0, 0, getWidth(), getHeight(), 30, 30);
                    g2.setColor(coloreTema); // Bordo dorato o rosso
                    g2.setStroke(new BasicStroke(3.0f));
                    g2.drawRoundRect(1, 1, getWidth() - 3, getHeight() - 3, 30, 30);
                    g2.dispose();
                    super.paintComponent(g);
                }
            };
            glassPanel.setOpaque(false);
            glassPanel.setBorder(new EmptyBorder(40, 60, 40, 60));

            // Testo di vittoria
            String titolo = survivorVince ? "VITTORIA!" : "SCONFITTA!";
            String coloreHex = survivorVince ? "#FFD700" : "#FF3333";
            String messaggio = survivorVince ? "Il Sopravvissuto ha raggiunto la porta sano e salvo." 
                                             : "Lo Zombie ha catturato la sua preda!";

            String htmlTesto = "<html><div style='text-align: center; font-family: Segoe UI;'>"
                    + "<h1 style='color: " + coloreHex + "; font-size: 42px; margin-top: 0; margin-bottom: 5px;'>" + titolo + "</h1>"
                    + "<p style='color: white; font-size: 16px; margin: 0;'>" + messaggio + "</p>"
                    + "</div></html>";
                    
            JLabel lblTesto = new JLabel(htmlTesto, SwingConstants.CENTER);

            // Bottoni
            JPanel pnlBottoni = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 0));
            pnlBottoni.setOpaque(false);

            JButton btnMenu = creaBottonePiatto("MENU PRINCIPALE", new Color(60, 60, 65));
            JButton btnEsci = creaBottonePiatto("ESCI DAL GIOCO", new Color(180, 50, 50));

            // Azione Menu: Chiude tutto e ricrea la prima finestra da zero
            btnMenu.addActionListener(event -> {
                dialog.dispose();
                this.dispose(); // Chiude la partita corrente
                
                // Ricrea il Main Menu pulito
                JFrame nuovaFinestra = new JFrame("Zombie Survivor");
                nuovaFinestra.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                nuovaFinestra.setSize(800, 500);
                nuovaFinestra.setContentPane(new MainMenu(nuovaFinestra, new GameSession())); 
                nuovaFinestra.setLocationRelativeTo(null);
                nuovaFinestra.setVisible(true);
            });

            // Azione Esci
            btnEsci.addActionListener(event -> System.exit(0));

            pnlBottoni.add(btnMenu);
            pnlBottoni.add(btnEsci);

            glassPanel.add(lblTesto, BorderLayout.CENTER);
            glassPanel.add(pnlBottoni, BorderLayout.SOUTH);

            dialog.setContentPane(glassPanel);
            dialog.pack();
            dialog.setLocationRelativeTo(this); // Lo centra esattamente in mezzo al gioco!
            dialog.setVisible(true);
        });
        
        timer.setRepeats(false);
        timer.start();
    }

    // Metodo helper per creare dei bottoni belli e moderni per il popup
    private JButton creaBottonePiatto(String text, Color baseColor) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btn.setForeground(Color.WHITE);
        btn.setBackground(baseColor);
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createEmptyBorder(12, 20, 12, 20));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) { btn.setBackground(baseColor.brighter()); }
            public void mouseExited(java.awt.event.MouseEvent evt) { btn.setBackground(baseColor); }
        });
        return btn;
    }

    public TurnController getTurnController() { return turnController; }
    public MapPanel getMapPanel() { return mapPanel; }
    public GameMap getMap() { return map; }
}