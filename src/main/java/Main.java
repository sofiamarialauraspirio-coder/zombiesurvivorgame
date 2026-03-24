import javax.swing.JFrame;
import view.MainMenu;
import model.GameSession;

public class Main {
    public static void main(String[] args) {
        
        // 1. LA NOSTRA UNICA FINESTRA (La "Cornice" che non si chiuderà mai)
        JFrame finestraPrincipale = new JFrame("Zombie Survivor");
        finestraPrincipale.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        finestraPrincipale.setSize(800, 500); // Grandezza di partenza per i menu
        finestraPrincipale.setLocationRelativeTo(null);

        // 2. La memoria del gioco
        GameSession session = new GameSession();

        // 3. Creiamo la primissima "tela" (Il Menu) e la inseriamo nella cornice
        MainMenu menu = new MainMenu(finestraPrincipale, session);
        finestraPrincipale.setContentPane(menu);

        // 4. Ciak, si gira!
        finestraPrincipale.setVisible(true);
    }
}