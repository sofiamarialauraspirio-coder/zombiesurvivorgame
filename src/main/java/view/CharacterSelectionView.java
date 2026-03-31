package view;

import javax.swing.*;
import java.awt.*;
import model.GameSession;
import model.GameMap;
import model.MapLoader;
import model.GameManager;
import controller.TurnController;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.border.EmptyBorder;

public class CharacterSelectionView extends JPanel {
    private GameSession session;
    private JFrame finestraPrincipale;
    private JLabel lblTurno; // La scritta che dice di chi è il turno

    public CharacterSelectionView(JFrame finestraPrincipale, GameSession session) {
        this.finestraPrincipale = finestraPrincipale;
        this.session = session;
        
        setLayout(new BorderLayout()); 

        // Pannello di sfondo
        BackgroundPanel backgroundPanel = new BackgroundPanel("/ZombieVSsopravvissuto.jpeg");
        // Layout per mettere la scritta in alto e i bottoni al centro
        backgroundPanel.setLayout(new BoxLayout(backgroundPanel, BoxLayout.Y_AXIS));

        // ==========================================
        // STORY 3: LANCIO DELLA MONETA (COIN TOSS)
        // ==========================================
        int choosingPlayer = session.tossCoin();
        
        // Creazione dell'etichetta dinamica (Dynamic Label Feedback)
        lblTurno = new JLabel("Giocatore " + choosingPlayer + ": Scegli il tuo ruolo!", SwingConstants.CENTER);
        lblTurno.setFont(new Font("Segoe UI", Font.BOLD, 46));
        lblTurno.setForeground(Color.WHITE);
        lblTurno.setAlignmentX(Component.CENTER_ALIGNMENT);
        lblTurno.setBorder(new EmptyBorder(50, 0, 100, 0)); // Spazio sopra e sotto

        // Creazione bottoni
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 150, 30));
        btnPanel.setOpaque(false);
        IosGlassButton btnZombie = new IosGlassButton("Zombie");
        IosGlassButton btnSurvivor = new IosGlassButton("Sopravvissuto");

        // Azioni bottoni (One-Click Selection & Coupling)
        btnZombie.addActionListener(e -> {
            session.assignRole("ZOMBIE");
            completataSelezione(); 
        });

        btnSurvivor.addActionListener(e -> {
            session.assignRole("SOPRAVVISSUTO");
            completataSelezione(); 
        });

        btnPanel.add(btnZombie);
        btnPanel.add(btnSurvivor);

        backgroundPanel.add(lblTurno);
        backgroundPanel.add(btnPanel);
        
        add(backgroundPanel, BorderLayout.CENTER);
    }

    private void completataSelezione() {
        // --- POPUP DI CONFERMA ---
        Window parentWindow = SwingUtilities.getWindowAncestor(this);
        JDialog dialog = new JDialog((Frame) parentWindow, true); 
        dialog.setUndecorated(true); 
        dialog.setBackground(new Color(0, 0, 0, 0)); 

        JPanel glassPanel = new JPanel(new BorderLayout(0, 20)) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(20, 20, 20, 220)); 
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 30, 30);
                g2.setColor(new Color(255, 255, 255, 100)); 
                g2.setStroke(new BasicStroke(2.0f));
                g2.drawRoundRect(1, 1, getWidth() - 2, getHeight() - 2, 30, 30);
                g2.dispose();
                super.paintComponent(g);
            }
        }; 
        
        glassPanel.setOpaque(false);
        glassPanel.setBorder(new EmptyBorder(30, 50, 30, 50)); 

        // Usa i nuovi getter per prendere i ruoli
        String testoHTML = "<html><div style='text-align: center; color: white; font-family: Segoe UI;'>" +
                           "<h2 style='margin-bottom: 15px;'>Scelte Confermate!</h2>" +
                           "Giocatore 1: <b style='color: #ff5555;'>" + session.getPlayer1Role() + "</b><br><br>" +
                           "Giocatore 2: <b style='color: #55aaff;'>" + session.getPlayer2Role() + "</b>" +
                           "</div></html>";
        JLabel lblMessaggio = new JLabel(testoHTML, SwingConstants.CENTER);

        IosGlassButton btnOk = new IosGlassButton("INIZIA PARTITA");
        
        btnOk.addActionListener(e -> {
            dialog.dispose(); // Chiudi il popup

            MapLoader loader = new MapLoader();
            GameMap map = loader.loadMap("src/main/resources/mappa_livello1.json"); 
            MapPanel mapPanel = new MapPanel(map);

            if (map.getSurvivor() != null && map.getZombie() != null) {
                GameManager gameManager = new GameManager(map.getSurvivor(), map.getZombie());
                TurnController turnController = new TurnController(gameManager, map);

                mapPanel.setTurnController(turnController);
                turnController.setMapPanel(mapPanel);

                // Diciamo all'Arbitro chi è il Giocatore 1 usando i nuovi metodi
                boolean p1IsSurvivor = "SOPRAVVISSUTO".equals(session.getPlayer1Role());
                turnController.setSurvivorIsP1(p1IsSurvivor);

                turnController.startGame(); 
                
                if (p1IsSurvivor) {
                    mapPanel.evidenziaMossePersonaggio(map.getSurvivor().getX(), map.getSurvivor().getY());
                } else {
                    mapPanel.evidenziaMossePersonaggio(map.getZombie().getX(), map.getZombie().getY());
                }
                
            } else {
                System.err.println("Attenzione: Sopravvissuto o Zombie non trovati nella mappa!");
            }

            finestraPrincipale.setContentPane(mapPanel);
            finestraPrincipale.revalidate();
            finestraPrincipale.repaint();
            finestraPrincipale.pack();
            finestraPrincipale.setLocationRelativeTo(null);
            
            SwingUtilities.invokeLater(() -> mapPanel.requestFocusInWindow());
        });

        JPanel pnlBottone = new JPanel(new FlowLayout(FlowLayout.CENTER));
        pnlBottone.setOpaque(false);
        pnlBottone.add(btnOk);

        glassPanel.add(lblMessaggio, BorderLayout.CENTER);
        glassPanel.add(pnlBottone, BorderLayout.SOUTH);

        dialog.setContentPane(glassPanel);
        dialog.pack(); 
        dialog.setLocationRelativeTo(parentWindow); 
        dialog.setVisible(true); 
    }

    // --- CLASSE INTERNA PER I BOTTONI ---
    private class IosGlassButton extends JButton {
        private boolean hovered = false;

        public IosGlassButton(String text) {
            super(text);
            setFont(new Font("Segoe UI", Font.BOLD, 24)); 
            setForeground(Color.WHITE); 
            setContentAreaFilled(false); 
            setFocusPainted(false); 
            setBorderPainted(false); 
            setCursor(new Cursor(Cursor.HAND_CURSOR));
            setBorder(BorderFactory.createEmptyBorder(12, 35, 12, 35));

            addMouseListener(new MouseAdapter() {
                public void mouseEntered(MouseEvent e) { hovered = true; repaint(); }
                public void mouseExited(MouseEvent e) { hovered = false; repaint(); }
            });
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            int cornerRadius = 30; 
            int alpha = hovered ? 70 : 30; 
            Color topColor = new Color(255, 255, 255, alpha); 
            Color bottomColor = new Color(255, 255, 255, alpha / 2); 
            LinearGradientPaint glassGradient = new LinearGradientPaint(
                0, 0, getWidth(), getHeight(), new float[]{0f, 1f}, new Color[]{topColor, bottomColor}
            );
            g2.setPaint(glassGradient);
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), cornerRadius, cornerRadius);
            g2.setColor(new Color(255, 255, 255, 140)); 
            g2.setStroke(new BasicStroke(2.0f)); 
            g2.drawRoundRect(1, 1, getWidth() - 3, getHeight() - 3, cornerRadius, cornerRadius);
            g2.dispose(); 
            super.paintComponent(g);
        }
    }

    // --- CLASSE INTERNA PER LO SFONDO ---
    private class BackgroundPanel extends JPanel {
        private Image backgroundImage;
        public BackgroundPanel(String resourcePath) {
            try {
                backgroundImage = new ImageIcon(getClass().getResource(resourcePath)).getImage();
            } catch (Exception e) {
                System.err.println("Errore caricamento sfondo: " + e.getMessage());
            }
        }
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            if (backgroundImage != null) {
                g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
            }
        }
    }
}