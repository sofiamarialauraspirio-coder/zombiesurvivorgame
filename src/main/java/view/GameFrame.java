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
    private GameSession session;

    // Elementi dell'HUD Laterale
    private JLabel lblNomeGiocatore;
    private JLabel lblAzione;
    private JPanel hudPanel;
    private JPanel pnlChecklist;
    private JLabel lblCheck1;
    private JLabel lblCheck2;
    
    // Variabile per non far spawnare infiniti popup a fine partita
    private boolean popupFinePartitaMostrato = false;

    public GameFrame(GameSession session) {
        this.session = session; 
        
        setTitle("Zombie Survivor - Map View");
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

        hudPanel.add(Box.createRigidArea(new Dimension(0, 30))); // Spazio sopra il box

        pnlChecklist = new JPanel();
        pnlChecklist.setLayout(new BoxLayout(pnlChecklist, BoxLayout.Y_AXIS));
        pnlChecklist.setBackground(new Color(40, 40, 45)); // Sfondo scuro elegante
        pnlChecklist.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(80, 80, 85), 1, true),
            new EmptyBorder(12, 15, 12, 15)
        ));
        pnlChecklist.setAlignmentX(Component.CENTER_ALIGNMENT);
        pnlChecklist.setMaximumSize(new Dimension(220, 90)); // Mantiene la forma fissa

        JLabel lblTitoloCheck = new JLabel("📋 FASI DEL TURNO");
        lblTitoloCheck.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblTitoloCheck.setForeground(new Color(200, 200, 200));
        lblTitoloCheck.setAlignmentX(Component.LEFT_ALIGNMENT);

        lblCheck1 = new JLabel("-");
        lblCheck1.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblCheck1.setForeground(Color.WHITE);
        lblCheck1.setAlignmentX(Component.LEFT_ALIGNMENT);

        lblCheck2 = new JLabel("-");
        lblCheck2.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblCheck2.setForeground(Color.WHITE);
        lblCheck2.setAlignmentX(Component.LEFT_ALIGNMENT);

        pnlChecklist.add(lblTitoloCheck);
        pnlChecklist.add(Box.createRigidArea(new Dimension(0, 10)));
        pnlChecklist.add(lblCheck1);
        pnlChecklist.add(Box.createRigidArea(new Dimension(0, 8)));
        pnlChecklist.add(lblCheck2);

        hudPanel.add(pnlChecklist);
        hudPanel.add(Box.createRigidArea(new Dimension(0, 20)));

        // Questo comando "colla" spinge tutto verso il FONDO del pannello
        hudPanel.add(Box.createVerticalGlue());

        JPanel pnlMenuBottom = new JPanel();
        pnlMenuBottom.setLayout(new GridLayout(3, 1, 0, 10)); // 3 righe, 1 colonna, spaziatura 10px tra i tasti
        pnlMenuBottom.setOpaque(false);
        pnlMenuBottom.setMaximumSize(new Dimension(220, 140)); // Impedisce al menu di sformarsi

        // Creiamo i bottoni usando il nostro nuovo metodo helper
        JButton btnPausa = creaBottonePiatto("⏸️ PAUSA", new Color(70, 130, 180));
        JButton btnRiavvia = creaBottonePiatto("🔄 RIAVVIA", new Color(210, 105, 30));
        JButton btnMenu = creaBottonePiatto("🏠 MENU PRINCIPALE", new Color(180, 50, 50));

        // Azione PAUSA (Un solo bottone per riprendere)
        btnPausa.addActionListener(e -> {
            mostraPopupCustom("⏸️ GIOCO IN PAUSA", "Riprendi fiato e pianifica la tua mossa.", 
                "RIPRENDI PARTITA", new Color(70, 130, 180), null, 
                null, null, null);
        });

       // Azione RIAVVIA potenziata
        btnRiavvia.addActionListener(e -> {
            mostraPopupCustom("🔄 CONFERMA RIAVVIO", "Vuoi davvero riavviare la partita?\nI progressi attuali andranno persi.", 
                "SÌ, RIAVVIA", new Color(210, 105, 30), () -> {
                    
                    this.dispose(); // Chiudi la vecchia finestra
                    
                    // 1. Creiamo un nuovo GameFrame passandogli la sessione attuale (QUESTO RISOLVE L'ERRORE ROSSO)
                    GameFrame nuovaPartita = new GameFrame(this.session); 
                    nuovaPartita.setVisible(true); 
                    
                    // 2. Facciamo ripartire il motore di gioco, recuperando i ruoli
                    boolean p1IsSurvivor = "SURVIVOR".equals(this.session.getPlayer1Role());
                    nuovaPartita.getTurnController().setSurvivorIsP1(p1IsSurvivor);
                    nuovaPartita.getTurnController().startGame();
                    
                    // 3. Mostriamo i primissimi quadrati gialli della nuova mappa!
                    nuovaPartita.aggiornaHud();
                    nuovaPartita.getMapPanel().evidenziaMossePersonaggio(
                        p1IsSurvivor ? nuovaPartita.getMap().getSurvivor().getX() : nuovaPartita.getMap().getZombie().getX(),
                        p1IsSurvivor ? nuovaPartita.getMap().getSurvivor().getY() : nuovaPartita.getMap().getZombie().getY()
                    );
                    
                }, 
                "ANNULLA", new Color(100, 100, 100), null);
        });

        // Azione MENU PRINCIPALE (Due bottoni: Sì / No)
        btnMenu.addActionListener(e -> {
            mostraPopupCustom("🏠 MENU PRINCIPALE", "Tornare al Menu Principale?\nLa partita attuale andrà persa.", 
                "SÌ, ESCI", new Color(180, 50, 50), () -> {
                    
                    // Invece di distruggere la finestra (this.dispose()), cambiamo solo il suo contenuto!
                    this.getContentPane().removeAll(); // Svuota la mappa e l'HUD
                    this.setSize(800, 500); // Ridimensiona la finestra per il menu
                    this.setContentPane(new MainMenu(this, new GameSession())); // Inserisce il Menu
                    this.setLocationRelativeTo(null); // Ricentra la finestra nello schermo
                    this.revalidate(); // Forza l'aggiornamento visivo
                    this.repaint();
                    
                }, 
                "ANNULLA", new Color(100, 100, 100), null);
        });

        // Aggiungiamo i bottoni al piccolo pannello del menu...
        pnlMenuBottom.add(btnPausa);
        pnlMenuBottom.add(btnRiavvia);
        pnlMenuBottom.add(btnMenu);
        // ... e aggiungiamo il pannello del menu all'HUD laterale!
        hudPanel.add(pnlMenuBottom);
    }

    public void aggiornaHud() {
        if (turnController == null) return;
        GameState state = turnController.getCurrentState();

        if (state == GameState.P1_CHOICE || state == GameState.P2_CHOICE) {
            boolean isSurvivorTurn = turnController.isSurvivorTurn();
            
            // Cambia nome e colore in base a chi gioca
            lblNomeGiocatore.setText(isSurvivorTurn ? "SOPRAVVISSUTO" : "ZOMBIE");
            lblNomeGiocatore.setForeground(isSurvivorTurn ? new Color(255, 200, 0) : new Color(180, 50, 255));

            // Indica se ci si deve muovere o piazzare il blocco con istruzioni precise sui tasti
            if (mapPanel.isChoosingBlock()) {
                lblAzione.setText("<html><center>Usa le <b>FRECCE</b> per scegliere<br>e premi <b>INVIO</b> per piazzare<br>la <font color='#FF5555'>TRAPPOLA (Rosso)</font></center></html>");
            } else {
                lblAzione.setText("<html><center>Usa le <b>FRECCE</b> per scegliere<br>e premi <b>INVIO</b> per confermare<br>il <font color='#FFFF55'>MOVIMENTO (Giallo)</font></center></html>");
            }
            
            if (!mapPanel.isChoosingBlock()) {
                // FASE 1: Movimento
                lblCheck1.setText("<html><font color='#FFFF55'><b>[►] Fase 1: Movimento</b></font></html>");
                lblCheck2.setText("<html><font color='#777777'>[ ] Fase 2: Piazzamento Blocco</font></html>"); 
            } else {
                // FASE 2: Trappola/Blocco
                lblCheck1.setText("<html><font color='#00FF00'>[✔]</font> <strike><font color='#AAAAAA'>Fase 1: Movimento</font></strike></html>");
                lblCheck2.setText("<html><font color='#FF5555'><b>[►] Fase 2: Piazzamento Blocco</b></font></html>");
            }
        } 
        else if (state == GameState.SURVIVOR_VICTORY || state == GameState.ZOMBIE_VICTORY) {
            if (state == GameState.SURVIVOR_VICTORY) {
                lblNomeGiocatore.setText("VITTORIA!");
                lblNomeGiocatore.setForeground(new Color(255, 215, 0));
                lblAzione.setText("<html><center>Il Sopravvissuto<br>è fuggito!</center></html>");
            } else {
                lblNomeGiocatore.setText("SCONFITTA!");
                lblNomeGiocatore.setForeground(new Color(255, 50, 50));
                lblAzione.setText("<html><center>Lo Zombie ha<br>vinto!</center></html>");
            }
            
            // Lancia il popup
            if (!popupFinePartitaMostrato) {
                popupFinePartitaMostrato = true;
                mostraSchermataFinePartita(state);
            }
        }
        
        hudPanel.revalidate();
        hudPanel.repaint();
    }

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

    private void mostraPopupCustom(String titolo, String messaggio, String testoBtn1, Color coloreBtn1, Runnable azione1, String testoBtn2, Color coloreBtn2, Runnable azione2) {
        JDialog dialog = new JDialog(this, true);
        dialog.setUndecorated(true);
        dialog.setBackground(new Color(0, 0, 0, 0)); // Sfondo trasparente

        JPanel glassPanel = new JPanel(new BorderLayout(0, 20)) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(30, 30, 35, 245)); // Grigio scuro quasi opaco
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 25, 25);
                g2.setColor(new Color(150, 150, 150)); // Bordo grigio chiaro
                g2.setStroke(new BasicStroke(2.0f));
                g2.drawRoundRect(1, 1, getWidth() - 3, getHeight() - 3, 25, 25);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        glassPanel.setOpaque(false);
        glassPanel.setBorder(new EmptyBorder(30, 40, 30, 40));

        // Formattazione del testo
        String htmlTesto = "<html><div style='text-align: center; font-family: Segoe UI;'>"
                + "<h2 style='color: #FFD700; margin-top: 0; margin-bottom: 10px; font-size: 22px;'>" + titolo + "</h2>"
                + "<p style='color: white; font-size: 14px; margin: 0;'>" + messaggio.replace("\n", "<br>") + "</p>"
                + "</div></html>";
        JLabel lblTesto = new JLabel(htmlTesto, SwingConstants.CENTER);

        // Pannello bottoni
        JPanel pnlBottoni = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 0));
        pnlBottoni.setOpaque(false);

        // Bottone 1 (Conferma o Azione principale)
        if (testoBtn1 != null) {
            JButton btn1 = creaBottonePiatto(testoBtn1, coloreBtn1);
            btn1.addActionListener(e -> {
                dialog.dispose();
                if (azione1 != null) azione1.run();
            });
            pnlBottoni.add(btn1);
        }

        // Bottone 2 (Annulla, opzionale)
        if (testoBtn2 != null) {
            JButton btn2 = creaBottonePiatto(testoBtn2, coloreBtn2);
            btn2.addActionListener(e -> {
                dialog.dispose();
                if (azione2 != null) azione2.run();
            });
            pnlBottoni.add(btn2);
        }

        glassPanel.add(lblTesto, BorderLayout.CENTER);
        glassPanel.add(pnlBottoni, BorderLayout.SOUTH);

        dialog.setContentPane(glassPanel);
        dialog.pack();
        dialog.setLocationRelativeTo(this); // Lo centra nello schermo
        dialog.setVisible(true);
    }    
}