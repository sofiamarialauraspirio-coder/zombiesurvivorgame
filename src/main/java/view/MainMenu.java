package view;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import model.GameSession;

// ORA ESTENDE JPanel, NON PIÙ JFrame!
public class MainMenu extends JPanel { 

    private JButton btnGioca;
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

    // Riceviamo la finestra dal Main!
    public MainMenu(JFrame finestraPrincipale, GameSession session) {
        setLayout(new BorderLayout()); // Importante per i JPanel

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
        btnEsci = new IosGlassButton("Esci");

        btnEsci.addActionListener(e -> System.exit(0));

        // IL TRUCCO: Togliamo il menu e mettiamo la schermata personaggi!
        btnGioca.addActionListener(e -> {
            CharacterSelectionView selectionWindow = new CharacterSelectionView(finestraPrincipale, session);
            finestraPrincipale.setContentPane(selectionWindow);
            finestraPrincipale.revalidate(); // Diciamo a Java di "ricaricare"
            finestraPrincipale.repaint();
        });

        pnlBottoni.add(btnGioca);
        pnlBottoni.add(Box.createHorizontalStrut(25)); 
        pnlBottoni.add(btnEsci);
        pnlBottoni.setAlignmentX(Component.CENTER_ALIGNMENT);

        contentPane.add(lblTitoloInterno); 
        contentPane.add(Box.createVerticalStrut(120)); 
        contentPane.add(pnlBottoni); 
        add(contentPane, BorderLayout.CENTER);
    }
}