import javax.swing.JComponent;
import javax.swing.event.MouseInputListener;
import javax.swing.*; 
import java.awt.*;
import java.awt.Dimension;
import java.util.Hashtable;
import java.awt.image.ImageObserver;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.util.LinkedList;
import java.util.ArrayList;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import java.awt.event.MouseListener;
import java.util.Comparator;
import java.util.Collections;

/**
 * Staff class
 */
public class Staff extends JPanel {
    
    // Total number of staves
    static int numStaves;
    // number indicating whether or not it is the last staff
    private int staffNum;
    // list of notes on a staff
    private LinkedList<Note> notesList;
    // width of a staff
    static int width = 700;
    // height of a staff
    static int height = 55;
    // preferred width of total window
    static int preferX = 800;
    // preferred height of total window
    static int preferY = 550;
    // space between staves
    static int spaceBetween = 120;
    // vertical offset for drawing, so there is a border at the top
    static int offset = 30;

    // Bounding box for staff, to be used when 
    private int xVal1;
    private int xVal2;
    private int yVal1;
    private int yVal2;

    // Ledger Lines, determining how far the staff's reach is outside of what is drawn
    private int threshold = 0;
    private int threshold2 = 0;
    private int thresholdHeight = height;

    /**
     * Constructor
     * Assigns a Linkedlist for notes, a 'staff number' to determine if last
     * And updates the total number of staves shown on screen
     */
    public Staff() {
        notesList = new LinkedList<>();
        staffNum = numStaves;
        numStaves++;
        // Bounding box for Staff
        xVal1 = (preferX - width)/2;
        xVal2 = ((preferX - width)/2) + width;
        yVal1 = offset + this.getStaffNum()*spaceBetween;
        yVal2 = yVal1 + height;
    }

    /**
     * Paint Component
     * Draws the actual staff
     * Attempts to use variables to allow it to be more flexible and adjusted based on different
     * Screen Widths and Heights.
     */
    @Override
    public void paintComponent(Graphics g) {
        // Drawing a white background for the staff
        g.setColor(Color.WHITE);
        g.fillRect((preferX - width)/2, offset + this.getStaffNum()*spaceBetween, width, height);
        g.setColor(Color.BLACK);

        // Drawing the horizontal lines of the staff
        g.drawLine((preferX - width)/2, offset + this.getStaffNum()*spaceBetween, width + ((preferX - width)/2), offset + this.getStaffNum()*spaceBetween);
        g.drawLine((preferX - width)/2, (offset + (1 + (height/4))) + this.getStaffNum()*spaceBetween, width + ((preferX - width)/2), (offset + (1 + (height/4))) + this.getStaffNum()*spaceBetween);
        g.drawLine((preferX - width)/2, (offset + 2*(1 + (height/4))) + this.getStaffNum()*spaceBetween, width + ((preferX - width)/2), (offset + 2*(1 + (height/4))) + this.getStaffNum()*spaceBetween);
        g.drawLine((preferX - width)/2, (offset + 3*(1 + (height/4))) + this.getStaffNum()*spaceBetween, width + ((preferX - width)/2), (offset + 3*(1 + (height/4))) + this.getStaffNum()*spaceBetween);
        g.drawLine((preferX - width)/2, (offset + 4*(1 + (height/4))) + this.getStaffNum()*spaceBetween, width + ((preferX - width)/2), (offset + 4*(1 + (height/4))) + this.getStaffNum()*spaceBetween);
        
        // Drawing the vertical lines of the staff
        g.drawLine((preferX - width)/2, offset + this.getStaffNum()*spaceBetween, (preferX - width)/2, (offset + height) + this.getStaffNum()*spaceBetween);
        g.drawLine(width + ((preferX - width)/2), offset + this.getStaffNum()*spaceBetween, width + ((preferX - width)/2), (offset + height) + this.getStaffNum()*spaceBetween);

        // Drawing more vertical lines to break up the different sections of the staff
        int beginningX = (preferX - width)/2;
        int increment = width;
        int y1 = offset + this.getStaffNum()*spaceBetween;
        int y2 = offset + this.getStaffNum()*spaceBetween + height;

        g.drawLine(beginningX + (width / 4), y1, beginningX + (width / 4), y2);
        g.drawLine(beginningX + 2*(width / 4), y1, beginningX + 2*(width / 4), y2);
        g.drawLine(beginningX + 3*(width / 4), y1, beginningX + 3*(width / 4), y2);

        // Now drawing the end of the staff if is the last one
        if (this.staffNum == numStaves - 1) {
            g.fillRect(width + ((preferX - width) / 2) - (width/40), offset + this.getStaffNum()*spaceBetween, (width/40), height + 1);
            g.drawLine(beginningX + width - (width / 30), y1, beginningX + width - (width / 30), y2);
        }

        Image trebleClefImage = null;
        Image commonTimeImage = null;
        try {
            trebleClefImage = ImageIO.read(getClass().getResource("/images/trebleClef.png"));
            commonTimeImage = ImageIO.read(getClass().getResource("/images/commonTime.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        // Drawing the treble clef and common time image
        Image newCommon = commonTimeImage.getScaledInstance(20, 30, Image.SCALE_DEFAULT);
        g.drawImage(trebleClefImage, beginningX, (offset - 20) + this.getStaffNum()*spaceBetween, null);
        g.drawImage(newCommon, beginningX + 50, offset + (offset/2) + this.getStaffNum()*spaceBetween, null);

        this.addMouseListener(new staffMouseListener());
    }


    public int getStaffNum() {
        return staffNum;
    }

    public static void setPreferredX(int p) {
        preferX = p;
    }

    public static void setPreferredY(int p) {
        preferY = p;
    }

    public static void setPreferredWidth(int w) {
        width = w;
    }

    public static void setPreferredHeight(int h) {
        height = h;
    }

    public static void setSpaceBetween(int s) {
        spaceBetween = s;
    }

    public Boolean getIsLast() {
        return this.staffNum == numStaves;
    }

    public void removeStaff() {
        numStaves--;
    }

    public int getxVal1() {
        return xVal1;
    }

    public int getxVal2() {
        return xVal2;
    }

    public int getyVal1() {
        return yVal1;
    }

    public int getyVal2() {
        return yVal2;
    }

    public LinkedList<Note> getNotesList() {
        return notesList;
    }
    
    public static int getStaffHeight() {
        return height;
    }

    /**
     * Calculates the upper threshold of each staff
     * @return threshold1
     */
    public int getThreshold() {
        threshold =  yVal1 - (thresholdHeight / 2) + 7;
        return threshold;
    }

    /**
     * Calculates the lower threshold of each staff
     * @return threshold2
     */
    public int getThreshold2() {
        threshold2 =  yVal2 + (thresholdHeight / 2);
        return threshold2;
    }

    public void setThreshold(int i) {
        threshold = i;
    }

    public int getThresholdHeight() {
        return thresholdHeight;
    }


    public void sortStaff() {
        Collections.sort(notesList, new Comparator<Note>() {
            public int compare(Note n1, Note n2) {
                return n1.getXCoord() - n2.getXCoord();
            }
        });
    }
}
