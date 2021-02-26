import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class HighScoreList {
    static final int MAX_SIZE = 10;
    private static List<HighscoreItem> highscoreList;
    private static final String HIGHSCORE_FILE_STRING = "highscore.txt";
    private static final Path highscoreFile = FileSystems.getDefault().getPath(HIGHSCORE_FILE_STRING);

    public HighScoreList() {
        highscoreList = new ArrayList<>();
        if(Files.exists(highscoreFile)) {
            loadHighscore();
        }
    }

    public int getLowestHighscore() {
        if (highscoreList.size() < MAX_SIZE) {
            return 0;
        }

        if (highscoreList.size() <= MAX_SIZE) {
            return highscoreList.get(highscoreList.size() - 1).getScore();
        } else {
            return highscoreList.get(MAX_SIZE - 1).getScore();
        }
    }

    public int getHighestHighscore() {
        if (highscoreList.size() == 0)
            return 0;
        else
            return highscoreList.get(0).getScore();
    }

    /**
     * 
     * Adds a new highscore to the list.
     * 
     * @param newHighscore Highscore that you want to add to the highscore list.
     * @throws IllegalArgumentException If you try to add a new score which is not
     *                                  good enough for the highscore leaderboard. A score of zero is not permitted.
     */
    public void addHighscore(HighscoreItem newHighscore) throws IllegalArgumentException {
        // Check to see if this score is enough to make the leaderboard.
        if (newHighscore.getScore() < getLowestHighscore() || newHighscore.getScore() == 0) {
            throw new IllegalArgumentException("Score of" + newHighscore.getScore() + "(" + newHighscore.getName()
                    + ") is not enough to make it to the highscore list");
        }

        // Is this person already on the leaderboard?
        boolean scoreSaved = false;
        for (int i = 0; i < highscoreList.size(); i++) {
            // This person already has a previous score on the leaderboard.
            if (highscoreList.get(i).getName() == newHighscore.getName()) {
                if (highscoreList.get(i).getScore() > newHighscore.getScore()) {
                    // The previous score is better than this new one. Keep the old score.
                    scoreSaved = true;
                    break;
                } else {
                    // New score is better than old score. Replace old score with new.
                    highscoreList.set(i, newHighscore);
                    scoreSaved = true;
                    break;
                }
            }
        }

        // Save score to the leaderboard (if this has not yet been done in previous
        // for-loop).
        if (scoreSaved == false) {
            highscoreList.add(newHighscore);
        }

        Collections.sort(highscoreList);

        // If list has overflowed in size: remove last element.
        if (highscoreList.size() > MAX_SIZE) {
            highscoreList.remove(highscoreList.size() - 1);
        }
    }

    public static List<HighscoreItem> getList() {
        return highscoreList;
    }

    private void loadHighscore() {      
        try {
            loadHighscoreFile();
        } catch (IOException e) {
            System.out.println("Unable to load highscore from file.");
            e.printStackTrace();
        }
    }

    public static void saveHighscoreFile() throws IOException {

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(HIGHSCORE_FILE_STRING, false))) {

            for (HighscoreItem i : highscoreList) {
                writer.write(i.getName());
                writer.newLine();
                writer.write(Integer.toString(i.getScore()));
                writer.newLine();
            }
        }
    }

    private void loadHighscoreFile() throws FileNotFoundException, IOException {

        try (BufferedReader reader = new BufferedReader(new FileReader(HIGHSCORE_FILE_STRING))) {
            while (reader.ready()) {
                String name=reader.readLine();
                int score = Integer.parseInt(reader.readLine());
                addHighscore(new HighscoreItem(score, name));
            }
        }
    }
}