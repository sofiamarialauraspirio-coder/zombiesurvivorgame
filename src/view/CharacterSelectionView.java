package view;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import model.GameSession;

public class CharacterSelectionView extends JFrame {
    
    private GameSession session;

    public CharacterSelectionView(GameSession session) {
        this.session = session;
        
        // 1. Impostazioni della finestra (L'abbiamo fatta più grande per goderti l'immagine!)
        setTitle("Scegli il tuo Personaggio");
        setSize(800, 500); 
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        
        // 2. Il percorso esatto della tua nuova immagine
        String bgPath = "resources" + File.separator + "ZombieVSsopravvissuto.jpeg"; 

        // 3. Creiamo il pannello di sfondo
        BackgroundPanel backgroundPanel = new BackgroundPanel(bgPath);
        
        // Impostiamo il layout: bottoni al centro in alto, con un po' di spazio
        backgroundPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 200, 30));
        backgroundPanel.setOpaque(false); 
        
        // 4. Creiamo i due bottoni
        // (Opzionale: li facciamo un po' più grandini modificando il Font)
        JButton btnZombie = new JButton("ZOMBIE");
        btnZombie.setFont(new Font("Arial", Font.BOLD, 24));
        
        JButton btnSurvivor = new JButton("SOPRAVVISSUTO");
        btnSurvivor.setFont(new Font("Arial", Font.BOLD, 24));

        // Azione ZOMBIE
        btnZombie.addActionListener(e -> {
            session.setPlayer1Choice("ZOMBIE");
            btnZombie.setEnabled(false);
            btnSurvivor.setEnabled(false);
            System.out.println("Hai scelto: " + session.getPlayer1Choice());
        });

        // Azione SOPRAVVISSUTO
        btnSurvivor.addActionListener(e -> {
            session.setPlayer1Choice("SOPRAVVISSUTO");
            btnZombie.setEnabled(false);
            btnSurvivor.setEnabled(false);
            System.out.println("Hai scelto: " + session.getPlayer1Choice());
        });

        // Aggiungiamo i bottoni allo sfondo
        backgroundPanel.add(btnZombie);
        backgroundPanel.add(btnSurvivor);
        
        // Aggiungiamo lo sfondo alla finestra
        add(backgroundPanel, BorderLayout.CENTER);
    }
}