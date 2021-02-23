import javax.swing.JFrame;
import static javax.swing.JFrame.EXIT_ON_CLOSE;

/**
 * This is a very small "game" just to show the absolute basics of how to draw
 * on a surface in a frame using Swing/AWT.
 * 
 */
public class App {
    public static void startGame(String difficulty) {
        JFrame main = new JFrame("Jumpy Birb");
        GameSurface gs = new GameSurface(800, 800, difficulty);
        main.setSize(800, 800);
        main.setResizable(false);
        main.add(gs);
        main.addKeyListener(gs);
        main.setDefaultCloseOperation(EXIT_ON_CLOSE);
        main.setVisible(true);
    }
    public static void main(String[] args) {
        String difficulty = "";
        if (args.length == 1) {
            difficulty = args[0].trim().toLowerCase();
            if (difficulty.equals("easy") || difficulty.equals("hard")) {
                startGame(difficulty);
            }
            else {
                System.err.println("Please enter 'hard' or 'easy' as parameter to start game!");
            }
        }
        else {
            System.err.println("Please enter 'hard' or 'easy' as parameter to start game!");
        }
    }

}