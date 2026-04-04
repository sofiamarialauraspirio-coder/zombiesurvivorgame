package view;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;

public class BackgroundPanel extends JPanel {
    private Image backgroundImage;

    // Costruttore che accetta il percorso della risorsa Maven
    public BackgroundPanel(String resourcePath) {
        try {
            backgroundImage = ImageIO.read(getClass().getResource(resourcePath));
        } catch (Exception e) {
            System.err.println("Errore nel caricamento dello sfondo: " + resourcePath);
            e.printStackTrace();
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g); 
        if (backgroundImage != null) {
            // Disegna l'immagine a pieno pannello
            g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
        }
    }
}