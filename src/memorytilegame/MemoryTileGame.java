/*
Developer: Parker Basham
Project: Java II Final - Memory Tile Game
Date: 5/14/26

AI NOTES:
AI was used to brainstorm project ideas based on criteria.
AI was used to help with the logic to randomly assign flash colors.
AI was used for a code review to help with typos and bug identification
at the end of the project.
 */
package memorytilegame;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Random;
import javax.swing.Timer;

public class MemoryTileGame {

    // These are the global variables for the class.
    static int rows = 6;
    static int cols = 6;
    static int clicks;
    static int plays;
    static int matches;
    static boolean gameStarted;
    static boolean preventInput;
    static boolean preventTimer;
    static int warnings;

    // Instantiate GUI Components
    static JMenuBar jmbGameMenuBar = new JMenuBar();
    static JMenu jmFile = new JMenu();
    static JMenuItem jmiQuit = new JMenuItem();
    static JFrame jfGame = new JFrame("Memory Tile Game - Parker Basham");
    static JFrame jfMessage = new JFrame();
    static JPanel jpGameBoard = new JPanel();
    static JLabel jlblPlays = new JLabel();
    static JLabel jlblMatches = new JLabel();
    static JButton jbttnStart = new JButton();
    static JButton jbttnCrazy = new JButton();
    static JButton jbttnHint = new JButton();

    // Random number generator.
    static Random rng;

    // Timer to flash the hidden colors.
    static Timer flashTimer;

    // This array stores our tiles.
    static GamePiece[][] tiles = new GamePiece[rows][cols];

    /*
     This array list is used to compare two
     tiles against each other each turn.
     */
    static ArrayList<GamePiece> playedTiles;

    // Array list to store colors.
    // These colors were generated using AI.
    static Color[] tileColors = {
            new Color(230, 25, 75),    // red
            new Color(60, 180, 75),    // green
            new Color(0, 130, 200),    // blue
            new Color(245, 130, 48),   // orange
            new Color(145, 30, 180),   // purple
            new Color(70, 240, 240),   // cyan
            new Color(240, 50, 230),   // magenta
            new Color(210, 245, 60),   // lime
            new Color(250, 190, 212),  // pink
            new Color(0, 128, 128),    // teal
            new Color(220, 190, 255),  // lavender
            new Color(170, 110, 40),   // brown
            new Color(255, 250, 200),  // cream
            new Color(128, 0, 0),      // maroon
            new Color(170, 255, 195),  // mint
            new Color(0, 0, 128),      // navy
            new Color(255, 215, 0),    // gold
            new Color(255, 80, 5)      // vermilion
    };

    /*
     This array list stores each color twice.
     We use this to randomly assign colors to tiles.
     */
    static ArrayList<Color> colorPairs;

    // This enum is used to trigger the proper logic for the buttons.
    enum ButtonAction {
        NEW_GAME, HINT, CRAZY
    }

    public MemoryTileGame() {
        // These function calls set up the various GUI components for the app.
        setupMenu(jfGame, jmbGameMenuBar, jmFile, jmiQuit);
        setupFrame(jfGame, 700, 750);
        setupLabels(jfGame, jlblPlays, 100, 25, 50, 10, " Plays:");
        setupLabels(jfGame, jlblMatches, 100, 25, 550, 10, " Matches:");
        setupPanel(jfGame, jpGameBoard, 600, 600, 50, 50);
        setupPieces();
        setupButtons(jfGame, jbttnHint, 100, 25, 50, 660, "Hint", ButtonAction.HINT, 2000);
        setupButtons(jfGame, jbttnCrazy, 100, 25, 300, 660, "Don't Press", ButtonAction.CRAZY, 1000);
        setupButtons(jfGame, jbttnStart, 100, 25, 550, 660, "New Game", ButtonAction.NEW_GAME, 2000);
    }

    // This function sets up the menu bar.
    private static void setupMenu(JFrame f1, JMenuBar mb, JMenu mnu, JMenuItem mi) {

        // Set up the file menu.
        mnu.setText("File");

        // Set up the menu item.
        mi.setText("Quit");

        // Add the action listener to quit.
        mi.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });

        // Set the ctrl-Q shortcut for quitting
        mi.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Q, InputEvent.CTRL_DOWN_MASK));

        // Add menu item to the menu
        mnu.add(mi);
        mnu.addSeparator();

        // Add the menu to the menu bar.
        mb.add(mnu);

        // Add the menu bar to the frame.
        f1.setJMenuBar(mb);
        f1.repaint();

    }

    // This function sets up the frame.
    private static void setupFrame(JFrame f1, int width, int height) {

        // Set the dimensions of the frame.
        f1.setSize(width, height);

        // Set the frame to open in the middle of the screen.
        f1.setLocationRelativeTo(null);

        // Set the visibility of the frame.
        f1.setVisible(true);

        // Set the app to quit when you close the frame.
        f1.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        // Set the layout manager.
        f1.setLayout(null);
    }

    // This function sets up the gameboard.
    private static void setupPanel(JFrame f1, JPanel p1, int width, int height, int xpos, int ypos) {

        // Create the border for the gameboard.
        Border brdr = BorderFactory.createLineBorder(Color.black, 2);

        // Set the dimensions and position of the gameboard.
        p1.setBounds(xpos, ypos, width, height);

        // Set the border of the gameboard.
        jpGameBoard.setBorder(brdr);

        // Set the visibility of the gameboard.
        p1.setVisible(true);

        // Set the layout manager of the gameboard.
        p1.setLayout(null);

        // Add the gameboard to the frame.
        f1.add(p1);
        f1.repaint();
    }

    // This function sets up the tiles for the game.
    private static void setupPieces() {

        // Initialize variables.
        int tileXpos = 0;
        int tileYpos = 0;
        clicks = 0;

        // Instantiate the arraylist.
        playedTiles = new ArrayList<>();

        // Create a game piece.
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++ ) {

                // Initialize a new tile.
                GamePiece gp = new GamePiece();

                // Set the attributes of the tile.
                gp.setLocation(tileXpos, tileYpos);
                gp.setSize(100, 100);
                gp.setVisible(true);
                gp.setPieceColor(Color.white);

                // Add the mouse listener.
                gp.addMouseListener(new MouseListener() {
                    @Override
                    public void mouseClicked(MouseEvent e) {

                        // Don't do anything until the game starts.
                        // The game starts when the user presses the new game button.
                        if (gameStarted && !preventInput) {

                            // We should only allow non-played tiles to be clicked.
                            if (gp.getState().isBlank()) {

                                /*
                                 Set the state of the tile to played to indicate it
                                 is currently being played in a turn.
                                 This ensures each tile can only be played
                                 once per turn.
                                 */
                                gp.setState("played");

                                // Display the flash color of the tile.
                                gp.setPieceColor(gp.getFlashColor());

                                // Increment clicks.
                                clicks += 1;

                                // Add tile to an array to check.
                                playedTiles.add(gp);

                                // Check the play after every two clicks.
                                if ((clicks % 2) == 0) {

                                    // Ensure more tiles cannot be played.
                                    preventInput = true;

                                    /*
                                    The timer delay will allow the user to see the flash color
                                    of both tiles they chose before checking for a match.
                                     */
                                    flashTimer = new Timer(250, new ActionListener() {
                                        @Override
                                        public void actionPerformed(ActionEvent e) {

                                            // Check for a match.
                                            checkPlay();
                                        }
                                    });

                                    // Start the timer to check for a match.
                                    flashTimer.start();
                                }
                            }
                        }
                    }

                    @Override
                    public void mousePressed(MouseEvent e) {

                    }

                    @Override
                    public void mouseReleased(MouseEvent e) {

                    }

                    @Override
                    public void mouseEntered(MouseEvent e) {

                    }

                    @Override
                    public void mouseExited(MouseEvent e) {
                    }
                });

                // Add the tile to our array and refresh the board.
                tiles[r][c] = gp;
                jpGameBoard.add(gp);

                // Add the pieces to the panel in a 6x6 grid.
                // Adjust tileXpos position.
                tileXpos = tileXpos + 100;
            }

            // Adjust tileYpos position and reset tileXpos.
            tileYpos = tileYpos + 100;
            tileXpos = 0;
        }

        // Update the panel.
        jpGameBoard.repaint();
    }

    // This function sets the flashColor of the pieces.
    private static void setFlashColor() {

        // Initialize colorPairs and the random number generator.
        colorPairs = new ArrayList<>();
        rng = new Random();

        // Add each color from tileColors twice to colorPairs.
        for (Color col : tileColors) {
            for (int i = 0; i < 2; i++) {
                colorPairs.add(col);
            }
        }

        // Set the flash color for each tile.
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {

                // Select a random color from colorPairs.
                Color randomColor = colorPairs.get(rng.nextInt(colorPairs.size()));

                // Set the flash color of the current tile.
                tiles[r][c].setFlashColor(randomColor);

                // Remove randomColor from Color pairs to ensure each color is only used twice.
                colorPairs.remove(randomColor);
            }
        }
    }

    // This function checks each play for a match.
    private static void checkPlay() {

        // Save the colors from the played tiles.
        Color c1 = playedTiles.get(0).getFlashColor();
        Color c2 = playedTiles.get(1).getFlashColor();

        // Increment plays.
        plays += 1;

        // Set the plays label.
        jlblPlays.setText(" Plays: " + plays);

        // Check to see if the colors match.
        if (c1.equals(c2)) {

            // Increment matches.
            matches += 1;

            // Display matches to the user.
            jlblMatches.setText(" Matches: " + matches);

            // Set the states of both tiles.
            playedTiles.get(0).setState("matched");
            playedTiles.get(1).setState("matched");

        } else {

            // If the tiles don't match, set color to white.
            playedTiles.get(0).setPieceColor(Color.white);
            playedTiles.get(1).setPieceColor(Color.white);

            // Reset the state of each tile.
            playedTiles.get(0).setState("");
            playedTiles.get(1).setState("");

        }

        // Clear the array list for next check.
        playedTiles.clear();

        // Stop the timer.
        flashTimer.stop();

        // Allow input to resume.
        preventInput = false;

        // Display a winner message if the user finds every match.
        if (matches == (rows * cols) / 2) {

            // Ensure the message frame pops up on top of the main frame.
            jfMessage.setAlwaysOnTop(true);

            // Display the winner message.
            JOptionPane.showMessageDialog(jfMessage, "You matched all tiles. Well done!", "Winner Winner", JOptionPane.DEFAULT_OPTION);

            // End the game.
            gameStarted = false;
        }
    }

    // This function flashes the flash colors of the tiles.
    private static void flashColors() {

        // Disable the ability to click tiles.
        preventInput = true;

        // Iterate through tiles to flash the hidden color.
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {

                // Display the flash colors to the user.
                tiles[r][c].setPieceColor(tiles[r][c].getFlashColor());

            }
        }
    }

    // This function sets all the tiles back to white.
    private static void hideColors(boolean resetBoard) {

        /*
         If resetBoard is true, we set everything to white and reset state.
         This is used for new games.
         */
        if (resetBoard) {
            for (int r = 0; r < rows; r++) {
                for (int c = 0; c < cols; c++) {

                    // Set the color of the tile to white.
                    tiles[r][c].setPieceColor(Color.white);

                    // Reset the state.
                    tiles[r][c].setState("");

                    // Allow input to resume.
                    preventInput = false;
                }
            }

        /*
         Otherwise, we only change un-matched tiles back to white.
         If a new game hasn't been started, or no tiles have been matched,
         everything will return to white.
         */
        } else {
            // Change tiles back to white.
            for (int r = 0; r < rows; r++) {
                for (int c = 0; c < cols; c++) {

                    // We only want to change un-matched tiles.
                    if (!tiles[r][c].getState().matches("matched")) {

                        // Set un-matched tiles to white.
                        tiles[r][c].setPieceColor(Color.white);
                    } else {
                        // Ensure matched tiles display flashColor.
                        tiles[r][c].setPieceColor(tiles[r][c].getFlashColor());
                    }

                    // Allow input to resume.
                    preventInput = false;
                }
            }
        }
    }

    // This function sets up the labels for the app.
    private static void setupLabels(JFrame f1, JLabel lbl, int width, int height, int xpos, int ypos, String txt) {

        Border brdr = BorderFactory.createLineBorder(Color.black, 1);

        // Set the dimensions and position of the label.
        lbl.setBounds(xpos, ypos, width, height);

        // Set the visibility of the label.
        lbl.setVisible(true);

        // Set the border of the label.
        lbl.setBorder(brdr);

        // Set the text of the label.
        lbl.setText(txt);

        // Add the label to the frame.
        f1.add(lbl);
        f1.repaint();
    }

    // This function sets up the buttons for the app.
    private static void setupButtons(JFrame f1, JButton btn, int width, int height, int xpos, int ypos, String txt, ButtonAction ba, int tick) {

        warnings = 0;
        rng = new Random();

        // Set the initial attributes of the button.
        btn.setSize(width, height);
        btn.setLocation(xpos, ypos);
        btn.setVisible(true);
        btn.setText(txt);

        /*
        Add the action listener.
        This action listener uses an enum that is passed into
        the function as an argument to determine which case to run
        in the switch statement.
         */
        btn.addActionListener(e -> {

            switch (ba) {

                // This is the logic to start a new game.
                case NEW_GAME:

                    // Ensure no other timers are running.
                    if (!preventTimer) {

                        // Set prevent timer to keep other buttons from running.
                        preventTimer = true;

                        // Start the game.
                        gameStarted = true;

                        // Reset plays and matches.
                        plays = 0;
                        matches = 0;

                        // Reset the labels displaying plays and matches.
                        jlblPlays.setText(" Plays: " + plays);
                        jlblMatches.setText(" Matches: " + matches);

                        // Set flash colors.
                        setFlashColor();

                        // Iterate through tiles to flash the hidden color.
                        flashColors();

                        // The timer is used to return the tiles back to white after the delay.
                        flashTimer = new Timer(tick, new ActionListener() {
                            @Override
                            public void actionPerformed(ActionEvent e) {

                                // Change the tiles back to white.
                                hideColors(true);

                                // Stop the timer.
                                flashTimer.stop();
                                preventTimer = false;
                            }
                        });

                        // Start the timer.
                        flashTimer.start();
                    }
                    break;

                // This is the logic to give the user a hint.
                case HINT:

                    // Only give a hint if we are in a game and no other timer is running.
                    if (gameStarted && !preventTimer) {

                        // Set prevent timer to keep other buttons from running.
                        preventTimer = true;

                        // Flash the hidden color.
                        flashColors();

                        // The timer will return un-matched tiles to white after the delay.
                        flashTimer = new Timer(tick, new ActionListener() {
                            @Override
                            public void actionPerformed(ActionEvent e) {

                                // Change un-matched tiles back to white.
                                hideColors(false);

                                // Stop the timer.
                                flashTimer.stop();
                                preventTimer = false;
                            }
                        });

                        // Start the timer to change un-matched tiles back to white.
                        flashTimer.start();
                    }
                    break;

                case CRAZY:

                    /*
                    WARNING:
                    This case is to be used exclusively to entertain
                    the toddlers and Coloradans in your life.
                     */
                    jfMessage.setAlwaysOnTop(true);

                    switch (warnings) {

                        case 0:
                            JOptionPane.showMessageDialog(jfMessage, "Do not press the button", "Bad Idea", JOptionPane.WARNING_MESSAGE);
                            warnings +=1;
                            break;

                        case 1:
                            JOptionPane.showMessageDialog(jfMessage, "Seriously, don't press the button", " Really Bad Idea", JOptionPane.WARNING_MESSAGE);
                            warnings +=1;
                            break;

                        case 2:
                            JOptionPane.showMessageDialog(jfMessage, "This is your last chance...", "The Worst Idea Ever", JOptionPane.WARNING_MESSAGE);
                            warnings +=1;
                            break;

                        case 3:

                            // Disable the ability for user input.
                            preventInput = true;

                            // Ensure no other timers are running.
                            if (!preventTimer) {

                                // Set prevent timer to keep other buttons from running.
                                preventTimer = true;

                                flashTimer = new Timer(100, new ActionListener() {
                                    @Override
                                    public void actionPerformed(ActionEvent e) {

                                        // Iterate through the array of tiles to flash random colors
                                        for (int r = 0; r < rows; r++) {
                                            for (int c = 0; c < cols; c++) {
                                                tiles[r][c].setPieceColor(tileColors[rng.nextInt(tileColors.length)]);
                                            }
                                        }

                                        /*
                                         Increment warnings.
                                         Used to stop the timer and reset the board.
                                         */
                                        warnings +=1;

                                        // Allow the timer to run through 50 iterations.
                                        if (warnings == 53) {

                                            // Stop the timer and reset warnings.
                                            flashTimer.stop();
                                            preventTimer = false;
                                            warnings = 0;

                                            // Ensure the gameboard returns to the proper state.
                                            if (gameStarted) {
                                                hideColors(false);
                                            } else if (matches == (rows * cols) / 2) {
                                                hideColors(false);
                                            } else {
                                                hideColors(true);
                                            }
                                        }
                                    }
                                });

                                // Start the timer.
                                flashTimer.start();
                            }
                            break;
                    }
            }
        });

        // Add the button to the frame.
        f1.add(btn);
        f1.repaint();
    }

    // The main function sets up the app.
    public static void main(String[] args) {
        MemoryTileGame newGame = new MemoryTileGame();
    }
}
