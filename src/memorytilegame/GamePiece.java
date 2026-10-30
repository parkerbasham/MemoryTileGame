package memorytilegame;
/*
This class is used to create the tiles for the game.
 */

import javax.swing.*;
import java.awt.*;

public class GamePiece extends JComponent {

    // These are the variables for the class.
    String state;
    Color pieceColor = Color.white;
    Color flashColor;

    // This is the constructor for the class.
    public GamePiece() {
        this.setForeground(pieceColor);
        this.setVisible(true);
    }

    // Call super to paint the component.
    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        g.setColor(Color.black);
        g.fillRect(0, 0, 100, 100);
        g.setColor(pieceColor);
        g.fillRect(5, 5, 90, 90);
        g.setColor(Color.black);
    }

    // These are the getters and setters.
    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public Color getPieceColor() {
        return pieceColor;
    }

    public void setPieceColor(Color pc) {
        this.pieceColor = pc;
        this.repaint();
    }

    public Color getFlashColor() {
        return flashColor;
    }

    public void setFlashColor(Color hc) {
        this.flashColor = hc;
    }

}
