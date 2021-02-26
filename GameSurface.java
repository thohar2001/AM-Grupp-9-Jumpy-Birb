import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.Timer;

public class GameSurface extends JPanel implements ActionListener, KeyListener {
    private static final long serialVersionUID = 6260582674762246325L;
    private static final int LINE_HEIGHT_IN_PIXELS = 48;
    private boolean gameOver;
    private Timer timer;
    private List<WarpPipe> pipes; // used to be: private List<Rectangle> aliens;
    private Rectangle bird;
    private int jumpRemaining; // Related to Gravity
    private int frames;
    private int gravity;
    private int currentScore;
    private Dimension screenDimension;
    //private int screenWidth;
    //private int screenHeight;
    private int highscore = 0;
    private String difficulty;
    HighScoreList highscoreList;
    // to fix bug regarding tabbing down under gameOver screen
    private boolean preventDialogTabUp = false;
    private String recentName;


    public GameSurface(final int width, final int height, String difficulty) {
        this.difficulty = difficulty;
        screenDimension = new Dimension(width, height);

        this.gameOver = false;
        this.pipes = new ArrayList<>();

        pipes.add(new WarpPipe(screenDimension.width, screenDimension.height));

        this.bird = new Rectangle(20, screenDimension.width / 2 - 15, 30, 20);
        this.timer = new Timer(20, this);
        this.timer.start();

        // Create highscore list
        highscoreList = new HighScoreList();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        repaint(g);
    }

    /**
      * Draws a bird to GameSurface for each frame 
      */  
    private void repaintBird(Graphics g) {
        // draw the bird
        g.setColor(Color.yellow);
        g.fillRoundRect(bird.x, bird.y, bird.width, bird.height, 40, 300);

        // make the wings flap when bird jumps
        g.setColor(Color.black);

        if (jumpRemaining != 0) {
            g.fillRect(bird.x + 10, bird.y + 10, bird.width / 3, bird.height / 1);
        } else {
            g.fillRect(bird.x + 10, bird.y + 10, bird.width / 3, bird.height / -1);
        }

        g.setColor(Color.red);
        g.fillRect(bird.x + 25, bird.y + 7, bird.width / 7, bird.height / 5);
    }

    private void paintGameoverScreen(Graphics g) {
        g.setColor(Color.black);
        g.fillRect(0, 0, screenDimension.width, screenDimension.height);
        g.setColor(Color.red);
        g.setFont(new Font("Arial", Font.BOLD, LINE_HEIGHT_IN_PIXELS));
    }

    /**
     * Draws the pair of warp pipes.
     */
    private void drawPipe(Graphics g, WarpPipe pipe) {
        g.setColor(Color.green);
        Rectangle r1 = pipe.getRectangle1();
        Rectangle r2 = pipe.getRectangle2();
        g.fillRect(r1.x, r1.y, r1.width, r1.height);
        g.fillRect(r2.x, r2.y, r2.width, r2.height);
    }

    private void paintHighscores(Graphics g, boolean achievedHighscore) {
        String nameOfUser = null;


        if (achievedHighscore && !preventDialogTabUp) {
            nameOfUser = JOptionPane.showInputDialog(this, "What's your name?", "Game Over: you killed Jumpy Birb!",
                    JOptionPane.PLAIN_MESSAGE);
            if (nameOfUser == null) {
                nameOfUser = "Player";
            }
            highscoreList.addHighscore(new HighscoreItem(currentScore, nameOfUser));

            recentName = nameOfUser;
            preventDialogTabUp = true;
        }

        if(nameOfUser == null) {
            nameOfUser = recentName;
        }

        paintGameoverScreen(g);

        String displayName;
        if (achievedHighscore) {
            displayName = nameOfUser;
        } else {
            displayName = "player";
        }

        g.drawString("Game over " + displayName, 20, 48);
        g.drawString("Your score was: " + currentScore, 20, 96);
        // Skriv ut highscorelist:
        int position = 1;
        for (HighscoreItem item : HighScoreList.getList()) {
            g.drawString("#" + position + ": " + item.getName() + ": " + item.getScore(), 20,
                    +48 + (LINE_HEIGHT_IN_PIXELS * 2) + (LINE_HEIGHT_IN_PIXELS * position));
            position++;
        }
        try {
            HighScoreList.saveHighscoreFile();
        } catch (IOException e) {
            // custom title, error icon
            JOptionPane.showMessageDialog(this, "Unable to save highscore file.", "File error.",
                    JOptionPane.ERROR_MESSAGE);
            System.err.print(e.getStackTrace());
        }
    }

    /**
     * Call this method when the graphics needs to be repainted on the graphics
     * surface.
     * 
     * @param g the graphics to paint on
     */
    private void repaint(Graphics g) {
        
        boolean achievedHighscore = currentScore > highscoreList.getLowestHighscore();
        
        if (gameOver) {
            paintHighscores(g, achievedHighscore);

        } else {
            // fill the background
            g.setColor(Color.blue);
            g.fillRect(0, 0, screenDimension.width, screenDimension.height);

            // draw the Warp Pipes.
            for (WarpPipe warpPipe : pipes) {
                drawPipe(g, warpPipe);
            }
            repaintBird(g);
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        // We count the frames because we want to trigger
        // certain events after a specific amount of frames
        frames++;

        // This will make the game end when you hit a
        // warp pipe.

        if (gameOver) {
            timer.stop();
            return;
        }
        final List<WarpPipe> toRemove = new ArrayList<>();
        for (WarpPipe pipe : pipes) {
            pipe.translate(-5, 0);

            if (pipe.noLongerOnScreen()) {
                // we add to another list and remove later
                // to avoid concurrent modification in a for-each loop
                toRemove.add(pipe);
            }

            // Bird has crashed into pipe
            if (pipe.intersects(bird)) {
                gameOver = true;
            }
        }

        pipes.removeAll(toRemove);
        // add a new pair of Warp Pipes for every one that was removed.
        for (int i = 0; i < toRemove.size(); ++i) {
            pipes.add(new WarpPipe(screenDimension.width, screenDimension.height));
            // For every pipe that passes the screen, one currentScore is added.
            // If your current currentScore is higher than your highscore,
            // the highscore is updated.

            currentScore++;
            if (highscore < currentScore) {
                highscore = currentScore;
            }
            // addWarpPipe(d.width, d.height);
        }

        WarpPipe newPipeToAdd = null;

        // Is there only one pipe in the game? In that case add one more (only in hard
        // mode).
        if (difficulty.equals("hard") && pipes.size() == 1) {
            for (WarpPipe pipe : pipes) {
                // Only add new pipe if current pipe has moved more than halfway across the
                // screen.
                if (pipe.halfwayAcrossScreen(screenDimension.width)) {
                    newPipeToAdd = new WarpPipe(screenDimension.width, screenDimension.height);
                }
            }
        }

        // Add an additional pipe to the ongoing game.
        if (newPipeToAdd != null) {
            pipes.add(newPipeToAdd);
        }

        // jumpRemaining is an instance variable, everytime
        // the bird jumps its set to 30. Every frame/actionPerformed
        // the bird jumps 5px in Y-direction until jumpRemaining
        // becomes 0. This is because we want the bird to jump
        // in a smooth motion.

        if (jumpRemaining > 0) {
            bird.translate(0, -5);
            jumpRemaining = jumpRemaining - 5;
            gravity = 0;
        }

        // After every jump, the gravity starts affecting
        // the bird again. Variable gravity is also set to 0 after
        // every jump.
        // Currently the instance variable gravity increases by 1
        // every 7th frame. To have less or more gravity,
        // change the 7 to any other number. Higher number means
        // lower gravity and lower number means higher gravity.

        else {
            bird.translate(0, gravity);
            if (frames % 3 == 0) {
                frames = 0;
                gravity++;
            }
        }
        // if bird falls below gamesurface area = game over
        if (bird.y > 800) {
            gameOver = true;
        }

        this.repaint();
    }

    @Override
    public void keyReleased(KeyEvent e) {
        //
    }

    @Override
    public void keyTyped(KeyEvent e) {
        // do nothing
    }

    @Override
    public void keyPressed(KeyEvent e) {
        // this event triggers when we press a key
        // in this case Space bar is for jumping
        final int minHeight = 10;
        final int maxHeight = this.getSize().height - bird.height - 10;
        final int kc = e.getKeyCode();

        if (gameOver && kc == KeyEvent.VK_SPACE) {
            // When game is over, gameover screen will show and timer will stop
            // To restart game, click space button.

            // New bird is drawn and will start at the same position
            // as when the game started the first time.
            this.bird = new Rectangle(20, screenDimension.width / 2 - 15, 30, 20);

            // The List of pipes have to be cleared, otherwise
            // the old pipes from last round will still appear on the screen.
            // Then add a new pipe again.
            pipes.clear();
            pipes.add(new WarpPipe(screenDimension.width, screenDimension.height));

            // Clear the current current score
            currentScore = 0;

            // Set gravity to zero again after death.
            gravity = 0;
            // Set gameOver to false again so that the repaint method will
            // draw the game and then start the timer again.
            gameOver = false;
            preventDialogTabUp = false;
            timer.start();
        }

        else if (kc == KeyEvent.VK_SPACE && bird.y > minHeight && bird.y < maxHeight) {
            jumpRemaining = 30;
        }
    }
}