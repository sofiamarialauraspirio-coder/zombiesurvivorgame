import javax.swing.JFrame;
import view.MainMenu;
import model.GameSession;

public class Main {
    public static void main(String[] args) {
        
        JFrame finestraPrincipale = new JFrame("Zombie Survivor");
        finestraPrincipale.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        finestraPrincipale.setSize(800, 500); // Grandezza di partenza per i menu
        finestraPrincipale.setLocationRelativeTo(null);

        GameSession session = new GameSession();

        MainMenu menu = new MainMenu(finestraPrincipale, session);
        finestraPrincipale.setContentPane(menu);

        finestraPrincipale.setVisible(true);
    }
}