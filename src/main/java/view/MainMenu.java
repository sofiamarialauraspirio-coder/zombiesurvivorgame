package view;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import java.awt.BorderLayout;
import java.awt.BasicStroke; // <-- Nuova importazione per lo spessore del bordo
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.LinearGradientPaint;
import java.awt.RenderingHints;
import java.io.File;
import java.io.IOException;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class MainMenu extends JFrame {

    // --- Pannello Speciale per lo Sfondo ---
    private class BackgroundPanel extends JPanel {
        private Image backgroundImage;

        public BackgroundPanel(String fileName) {
            try {
                backgroundImage = ImageIO.read(new File(fileName));
            } catch (IOException e) {
                System.err.println("Errore nel caricamento dell'immagine: " + fileName);
                e.printStackTrace();
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

    // --- Pulsante in stile "Vetro iOS" (Glassmorphism) ---
    private class IosGlassButton extends JButton {
        private boolean hovered = false;

        public IosGlassButton(String text) {
            super(text);
            
            setFont(new Font("Segoe UI", Font.BOLD, 18)); 
            setForeground(Color.WHITE); 
            setFocusPainted(false);
            setBorderPainted(false); 
            setContentAreaFilled(false); 
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

            int width = getWidth();
            int height = getHeight();
            int cornerRadius = 30; 

            // 1. Il corpo di vetro
            int topAlpha = hovered ? 70 : 30; 
            int bottomAlpha = hovered ? 40 : 10;
            
            Color topColor = new Color(255, 255, 255, topAlpha);
            Color bottomColor = new Color(255, 255, 255, bottomAlpha);

            LinearGradientPaint glassGradient = new LinearGradientPaint(
                    0, 0, width, height,
                    new float[]{0f, 1f},
                    new Color[]{topColor, bottomColor}
            );
            
            g2.setPaint(glassGradient);
            g2.fillRoundRect(0, 0, width, height, cornerRadius, cornerRadius);

            // 2. Il bordo luminoso PIÙ SPESSO
            g2.setColor(new Color(255, 255, 255, 160)); // Bianco un po' più visibile
            g2.setStroke(new BasicStroke(3.0f)); // <-- ECCO LO SPESSORE (3 pixel)
            
            // Rimpiccioliamo leggermente le coordinate del disegno del bordo
            // altrimenti uno spessore troppo grande verrebbe tagliato fuori dai limiti del bottone
            g2.drawRoundRect(1, 1, width - 3, height - 3, cornerRadius, cornerRadius);

            g2.dispose(); 

            // 3. Disegniamo la scritta al centro
            super.paintComponent(g);
        }
    }

    public MainMenu() {
        setTitle("Zombie Chase - Menu"); 
        setSize(400, 300); 
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); 
        setLocationRelativeTo(null); 

        String backgroundPath = "resources" + File.separator + "zombie_chase_background.png";
        BackgroundPanel contentPane = new BackgroundPanel(backgroundPath);
        
        contentPane.setLayout(new BoxLayout(contentPane, BoxLayout.Y_AXIS)); 
        contentPane.setBorder(new EmptyBorder(30, 0, 30, 0)); 
        contentPane.setOpaque(false); 

        JLabel lblTitoloInterno = new JLabel("Zombie Chase", SwingConstants.CENTER);
        lblTitoloInterno.setFont(new Font("Segoe UI", Font.BOLD, 40)); 
        lblTitoloInterno.setForeground(Color.WHITE); 
        lblTitoloInterno.setAlignmentX(Component.CENTER_ALIGNMENT); 

        JPanel pnlBottoni = new JPanel(); 
        pnlBottoni.setOpaque(false); 

        JButton btnGioca = new IosGlassButton("Gioca"); 
        JButton btnEsci = new IosGlassButton("Esci");

        btnEsci.addActionListener(e -> System.exit(0));

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