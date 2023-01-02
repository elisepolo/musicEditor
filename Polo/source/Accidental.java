import javax.swing.JComponent;
import javax.swing.*; 
import java.awt.*;
import java.awt.Dimension;
import java.util.Hashtable;
import java.awt.image.ImageObserver;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import java.awt.event.MouseListener;
import java.awt.event.MouseEvent;
import java.awt.event.*;  
import javax.swing.TransferHandler;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.datatransfer.DataFlavor;
import java.util.LinkedList;

public class Accidental extends JComponent {

    /* Image of accidental */
    private Image image = null;
    /* whether the accidental is flat or sharp */
    private boolean isFlat = false;
    /* appropriate coordinates */
    private int xCoord = 0;
    private int yCoord = 0;

    public Accidental(boolean flat) {
        this.isFlat = flat;
        if (isFlat) { // if true, it is flat, if false it is sharp
             try {
                this.image = ImageIO.read(getClass().getResource("/images/flat.png"));
             } catch (IOException e) {
                e.printStackTrace();
             }
        } else {
            try {
                this.image = ImageIO.read(getClass().getResource("/images/sharp.png"));
             } catch (IOException e) {
                e.printStackTrace();
             }
        }
    }

    public void paintComponent(Graphics g) {
        g.drawImage(this.image, xCoord, yCoord, null);
    }

    public void setXCoord(int i) {
        xCoord = i;
    }

    public void setYCoord(int i) {
        yCoord = i;
    }

    public void setIsFlat(Boolean b) {
        isFlat = b;
    }

    public int getXCoord() {
        return xCoord;
    }

    public int getYCoord() {
        return yCoord;
    }

    public boolean getIsFlat() {
        return isFlat;
    }
    
}
