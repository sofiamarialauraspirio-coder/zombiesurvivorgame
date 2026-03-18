package view;

import javax.swing.*;
import java.awt.*;
import model.GameSession;

public class CharacterSelectionView extends JFrame {
    private GameSession session;

    public CharacterSelectionView(GameSession session) {
        this.session = session;
        
        setTitle("Scegli il tuo Personaggio");
        // L'ho ingrandita a 800x500 così l'immagine si vede in tutta la sua gloria!
        setSize(800, 500); 
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null); // Centra la finestra

        // --- 🪄 LA MAGIA DELLO SFONDO ---
        // 1. Creiamo il pannello caricando la tua immagine esatta
        BackgroundPanel backgroundPanel = new BackgroundPanel("/ZombieVSsopravvissuto.jpeg");
        
        // 2. Diciamo al pannello come posizionare i bottoni (centrati e un po' in basso)
        backgroundPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 150, 30)); 

        // Creiamo i bottoni
        JButton btnZombie = new JButton("Zombie");
        JButton btnSurvivor = new JButton("Sopravvissuto");

        // (Piccolo tocco di classe) Facciamo i bottoni un po' più grandi!
        Font fontBottoni = new Font("Segoe UI", Font.BOLD, 24);
        btnZombie.setFont(fontBottoni);
        btnSurvivor.setFont(fontBottoni);

        // Azione ZOMBIE
        btnZombie.addActionListener(e -> {
            session.setPlayer1Choice("ZOMBIE");
            session.setPlayer2Choice("SOPRAVVISSUTO"); // <-- TUA AGGIUNTA (Assegnazione automatica)
            
            btnZombie.setEnabled(false);
            btnSurvivor.setEnabled(false);
            
            completataSelezione(); // <-- TUA AGGIUNTA (Passaggio al gioco)
        });

        // Azione SOPRAVVISSUTO
        btnSurvivor.addActionListener(e -> {
            session.setPlayer1Choice("SOPRAVVISSUTO");
            session.setPlayer2Choice("ZOMBIE"); // <-- TUA AGGIUNTA (Assegnazione automatica)
            
            btnZombie.setEnabled(false);
            btnSurvivor.setEnabled(false);
            
            completataSelezione(); // <-- TUA AGGIUNTA (Passaggio al gioco)
        });

        // --- 🏗️ COSTRUZIONE FINALE ---
        // 3. Aggiungiamo i bottoni SULLO SFONDO (non più sulla finestra vuota)
        backgroundPanel.add(btnZombie);
        backgroundPanel.add(btnSurvivor);

        // 4. Incolliamo l'intero sfondo con i bottoni sulla finestra principale
        add(backgroundPanel, BorderLayout.CENTER);
    }

    private void completataSelezione() {
        // Log di verifica per te nel terminale
        System.out.println("--- SELEZIONE COMPLETATA ---");
        System.out.println("Giocatore 1: " + session.getPlayer1Choice());
        System.out.println("Giocatore 2: " + session.getPlayer2Choice());

        // Mostriamo un messaggio di conferma al giocatore
        JOptionPane.showMessageDialog(this, 
            "Scelte confermate!\nGiocatore 1: " + session.getPlayer1Choice() + 
            "\nGiocatore 2: " + session.getPlayer2Choice());

        // Soddisfiamo la User Story: il menu si chiude per passare alla mappa
        this.dispose(); 
    }
}