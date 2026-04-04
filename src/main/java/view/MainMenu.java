package view;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import model.GameSession;

public class MainMenu extends JPanel { 

    private JButton btnGioca;
    private JButton btnRegole; 
    private JButton btnEsci;

    private class BackgroundPanel extends JPanel {
        private Image backgroundImage;
        public BackgroundPanel(String resourcePath) {
            try { backgroundImage = ImageIO.read(getClass().getResource(resourcePath)); } 
            catch (Exception e) { e.printStackTrace(); }
        }
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g); 
            if (backgroundImage != null) g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
        }
    }

    private class IosGlassButton extends JButton {
        private boolean hovered = false;
        public IosGlassButton(String text) {
            super(text);
            setFont(new Font("Segoe UI", Font.BOLD, 18)); 
            setForeground(Color.WHITE); 
            setFocusPainted(false); setBorderPainted(false); setContentAreaFilled(false); 
            setCursor(new Cursor(Cursor.HAND_CURSOR));
            setBorder(BorderFactory.createEmptyBorder(12, 35, 12, 35));
            addMouseListener(new MouseAdapter() {
                public void mouseEntered(MouseEvent evt) { hovered = true; repaint(); }
                public void mouseExited(MouseEvent evt) { hovered = false; repaint(); }
            });
        }
        
        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            int cornerRadius = 30; 
            Color topColor = new Color(255, 255, 255, hovered ? 70 : 30);
            Color bottomColor = new Color(255, 255, 255, hovered ? 40 : 10);
            g2.setPaint(new LinearGradientPaint(0, 0, getWidth(), getHeight(), new float[]{0f, 1f}, new Color[]{topColor, bottomColor}));
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), cornerRadius, cornerRadius);
            g2.setColor(new Color(255, 255, 255, 160)); 
            g2.setStroke(new BasicStroke(3.0f)); 
            g2.drawRoundRect(1, 1, getWidth() - 3, getHeight() - 3, cornerRadius, cornerRadius);
            g2.dispose(); 
            super.paintComponent(g);
        }
    }

    public MainMenu(JFrame finestraPrincipale, GameSession session) {
        setLayout(new BorderLayout()); 

        BackgroundPanel contentPane = new BackgroundPanel("/zombie_chase_background.png");
        contentPane.setLayout(new BoxLayout(contentPane, BoxLayout.Y_AXIS)); 
        contentPane.setBorder(new EmptyBorder(30, 0, 30, 0)); 
        contentPane.setOpaque(false); 

        JLabel lblTitoloInterno = new JLabel("Zombie Chase", SwingConstants.CENTER);
        lblTitoloInterno.setFont(new Font("Segoe UI", Font.BOLD, 40)); 
        lblTitoloInterno.setForeground(Color.WHITE); 
        lblTitoloInterno.setAlignmentX(Component.CENTER_ALIGNMENT); 

        JPanel pnlBottoni = new JPanel(); 
        pnlBottoni.setOpaque(false); 
        
        btnGioca = new IosGlassButton("Gioca"); 
        btnRegole = new IosGlassButton("Regole");
        btnEsci = new IosGlassButton("Esci");

        // Azioni dei Pulsanti
        btnEsci.addActionListener(e -> System.exit(0));

        btnGioca.addActionListener(e -> {
            CharacterSelectionView selectionWindow = new CharacterSelectionView(finestraPrincipale, session);
            finestraPrincipale.setContentPane(selectionWindow);
            finestraPrincipale.revalidate(); 
            finestraPrincipale.repaint();
        });

        // Azione per il popup Regole
        btnRegole.addActionListener(e -> mostraPopupRegole(finestraPrincipale));

        // Layout dei Pulsanti 
        pnlBottoni.add(btnGioca);
        pnlBottoni.add(Box.createHorizontalStrut(15)); 
        pnlBottoni.add(btnRegole); 
        pnlBottoni.add(Box.createHorizontalStrut(15)); 
        pnlBottoni.add(btnEsci);
        pnlBottoni.setAlignmentX(Component.CENTER_ALIGNMENT);

        contentPane.add(lblTitoloInterno); 
        contentPane.add(Box.createVerticalStrut(120)); 
        contentPane.add(pnlBottoni); 
        add(contentPane, BorderLayout.CENTER);
    }

    private void mostraPopupRegole(JFrame parent) {
        JDialog dialog = new JDialog(parent, true); 
        dialog.setUndecorated(true);
        dialog.setBackground(new Color(0, 0, 0, 0)); 

        JPanel glassPanel = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(30, 30, 30, 240)); 
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 25, 25);
                g2.setColor(new Color(255, 255, 255, 120));
                g2.setStroke(new BasicStroke(2.0f));
                g2.drawRoundRect(1, 1, getWidth() - 3, getHeight() - 3, 25, 25);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        glassPanel.setOpaque(false);
        glassPanel.setBorder(new EmptyBorder(20, 30, 20, 30));

        String htmlTesto = "<html><div style='font-family: Segoe UI; color: #E0E0E0; width: 450px;'>"
                + "<h1 style='text-align: center; color: #FFD700; margin-top: 0;'>REGOLE DEL GIOCO</h1>"
                
                + "<h2 style='color: #4CAF50; margin-bottom: 5px;'>🏃‍♂️ Sopravvissuto</h2>"
                + "<p style='margin-top: 0; font-size: 13px;'><b>Obiettivo:</b> Trova la chiave e fuggi dalla porta.<br>"
                + "<b>Turno:</b> Spostati di 1 casella, poi piazza 1 <i>Trappola Invisibile</i> per fermare lo Zombie.</p>"
                
                + "<h2 style='color: #FF5252; margin-bottom: 5px;'>🧟‍♂️ Zombie</h2>"
                + "<p style='margin-top: 0; font-size: 13px;'><b>Obiettivo:</b> Raggiungi la stessa casella del Sopravvissuto per mangiarlo.<br>"
                + "<b>Turno:</b> Spostati di 1 casella, poi piazza 1 <i>Trappola Invisibile</i> per bloccargli la fuga.</p>"
                
                + "<h2 style='color: #FF9800; margin-bottom: 5px;'>🎁 Casse Misteriose</h2>"
                + "<p style='margin-top: 0; font-size: 13px;'>Cammina sulle casse per scoprire cosa nascondono (25% di probabilità):</p>"
                + "<ul style='margin-top: 0; padding-left: 20px; font-size: 12px;'>"
                + "<li>⚡ <b>Doppio Movimento:</b> Corri fino a 2 caselle.</li>"
                + "<li>❄️ <b>Ghiaccio:</b> Congela e blocca l'avversario per un intero turno.</li>"
                + "<li>🧱 <b>Doppio Blocco:</b> Piazza 2 fantastici <i>Muri Grigi visibili</i> sulla mappa!</li>"
                + "<li>⚠️ <b style='color: #FF5252;'>Cassa Maledetta:</b> Fai attenzione! È una tagliola che ti auto-congelerà per un turno.</li>"
                + "</ul>"
                + "</div></html>";

        JLabel lblTesto = new JLabel(htmlTesto);
        glassPanel.add(lblTesto, BorderLayout.CENTER);

        JButton btnChiudi = new IosGlassButton("Ho Capito!");
        btnChiudi.addActionListener(e -> dialog.dispose());
        JPanel pnlBottone = new JPanel();
        pnlBottone.setOpaque(false);
        pnlBottone.add(btnChiudi);
        
        glassPanel.add(pnlBottone, BorderLayout.SOUTH);

        dialog.setContentPane(glassPanel);
        dialog.pack();
        dialog.setLocationRelativeTo(parent);
        dialog.setVisible(true);
    }

    public JButton getBtnGioca() { return btnGioca; }
    public JButton getBtnRegole() { return btnRegole; }
}