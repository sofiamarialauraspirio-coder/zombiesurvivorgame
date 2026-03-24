package view;

import javax.swing.*;
import java.awt.*;
import model.GameSession;
import model.GameMap;
import model.MapLoader;

// ORA ESTENDE JPanel, NON PIÙ JFrame!
public class CharacterSelectionView extends JPanel {
    private GameSession session;
    private JFrame finestraPrincipale;

    public CharacterSelectionView(JFrame finestraPrincipale, GameSession session) {
        this.finestraPrincipale = finestraPrincipale;
        this.session = session;
        
        setLayout(new BorderLayout()); // Importante!

        BackgroundPanel backgroundPanel = new BackgroundPanel("/ZombieVSsopravvissuto.jpeg");
        backgroundPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 150, 30)); 

        JButton btnZombie = new JButton("Zombie");
        JButton btnSurvivor = new JButton("Sopravvissuto");

        Font fontBottoni = new Font("Segoe UI", Font.BOLD, 24);
        btnZombie.setFont(fontBottoni);
        btnSurvivor.setFont(fontBottoni);

        btnZombie.addActionListener(e -> {
            session.setPlayer1Choice("ZOMBIE");
            session.setPlayer2Choice("SOPRAVVISSUTO"); 
            completataSelezione(); 
        });

        btnSurvivor.addActionListener(e -> {
            session.setPlayer1Choice("SOPRAVVISSUTO");
            session.setPlayer2Choice("ZOMBIE"); 
            completataSelezione(); 
        });

        backgroundPanel.add(btnZombie);
        backgroundPanel.add(btnSurvivor);
        add(backgroundPanel, BorderLayout.CENTER);
    }

    private void completataSelezione() {
        // Messaggio opzionale (puoi anche toglierlo per rendere tutto fluido al 100%)
        JOptionPane.showMessageDialog(this, 
            "Scelte confermate!\nGiocatore 1: " + session.getPlayer1Choice() + 
            "\nGiocatore 2: " + session.getPlayer2Choice());

        // IL GRAND FINALE: CAMBIAMO LA TELA CON LA MAPPA!
        MapLoader loader = new MapLoader();
        // Assicurati che il percorso del file json sia giusto per il tuo progetto!
        GameMap map = loader.loadMap("src/main/resources/mappa_livello1.json"); 
        MapPanel mapPanel = new MapPanel(map);

        finestraPrincipale.setContentPane(mapPanel);
        finestraPrincipale.setResizable(false); // Blocchiamo le dimensioni come faceva il vecchio GameFrame
        finestraPrincipale.pack(); // Rimpiccioliamo la finestra per abbracciare la mappa
        finestraPrincipale.setLocationRelativeTo(null); // Ri-centriamo nello schermo
    }
}