import view.MainMenu;
import view.GameFrame;

public class Main {
    public static void main(String[] args) {
        // 1. Creiamo la finestra del menu
       //MainMenu menu = new MainMenu();
        
        // 2. Diciamo a Java di renderla visibile sullo schermo!
        //menu.setVisible(true);

        GameFrame gameFrame = new GameFrame();
        gameFrame.setVisible(true);
    }
}