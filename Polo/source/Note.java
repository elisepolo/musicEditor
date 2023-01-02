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



public class Note extends JComponent {

    /*
     * Variables for each note
     * X,Y coordinates to determine pitch
     */
    private int xCoord = 0;
    private int yCoord = 0;
    /* Coordinates to be used to draw select Bounding Box */
    private int x2Coord;
    private int y2Coord;

    /* image of note */
    private Image noteImage = null;

    /* to keep track of what kind of note */
    public static enum noteType {
        NOTE, REST, SHARP, FLAT
    }

    /* to keep track of note length */
    public static enum noteLength {
        WHOLE, HALF, QUARTER, EIGTH, SIXTEENTH
    }

    /* Keep track of pitch */
    public static enum pitchEnum {
       B3, C4, D4, E4, F4, G4, A4, B4, C5, D5, E5, F5, G5, A5, B5
    }

    /* enum */
    private pitchEnum pitch;
    private noteType type;
    private noteLength length;
    /* to determine whether to draw bounding box for selectMode */
    private Boolean isSelected = false;

    /* Position Point */
    int positionx = 0;
    int positiony = 0;

    /* Accidental info */
    private Boolean isAccidental = false;
    private Boolean isSharp = false;
    private Boolean isFlat = false;
    private Image accidentalImage = null;
    private int accX = 0;
    private int accX2 = 0;
    private int accY = 0;
    private int accY2 = 0;
    private boolean onlyAccSelect = false;


    /* Chord info */
    private int threshold = 0;
    private int thresholdWidth = 0;
    private int chordX = 0;
    private boolean accPic = false;

    /* Ledger Line info */
    private int ledger = 0;
    private int boundTop = 0;
    private int boundBottom = 0;

    /* Animation */
    private int ani_length = 0;
    private int full_length = 0;
    private int skip = 0;
    private Image highlight = null;
    private Image constant = null;
    
    /**
     * Constructor for the note class
     * @param x X coordinate
     * @param y Y coordinate
     * @param note type of note
     */
    public Note(pitchEnum p, noteType t, int l) {
        pitch = p;
        type = t; 

        int tempLength = l;
        if (type == noteType.NOTE) { // if NOTE is selected
            switch (tempLength) { // determining pic based on note length
                case 0:
                    try {
                        noteImage = ImageIO.read(getClass().getResource("/images/wholeNote.png"));
                        constant = noteImage;
                        this.length = noteLength.WHOLE;
                        positionx = 10;
                        positiony = 6;
                        ani_length = 1600;
                        full_length = 160;
                        skip = 1;
                        highlight = ImageIO.read(getClass().getResource("/images/wholeNoteH.png"));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    break;
                case 1:
                    try {
                        noteImage = ImageIO.read(getClass().getResource("/images/wholeNote.png"));
                        this.length = noteLength.WHOLE;
                        constant = noteImage;
                        positionx = 10;
                        positiony = 6;
                        ani_length = 1600;
                        full_length = 160;
                        skip = 1;
                        highlight = ImageIO.read(getClass().getResource("/images/wholeNoteH.png"));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    break;
                case 2:
                    try {
                        noteImage = ImageIO.read(getClass().getResource("/images/halfNote.png"));
                        this.length = noteLength.HALF;
                        constant = noteImage;
                        positionx = 15;
                        positiony = 34;
                        ani_length = 800;
                        full_length = 80;
                        skip = 2;
                        highlight = ImageIO.read(getClass().getResource("/images/halfNoteH.png"));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    break;
                case 3:
                    try {
                        noteImage = ImageIO.read(getClass().getResource("/images/quarterNote.png"));
                        this.length = noteLength.QUARTER;
                        constant = noteImage;
                        positionx = 7;
                        positiony = 35;
                        ani_length = 400;
                        full_length = 40;
                        skip = 2;
                        highlight = ImageIO.read(getClass().getResource("/images/quarterNoteH.png"));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    break;
                case 4:
                    try {
                        noteImage = ImageIO.read(getClass().getResource("/images/eighthNote.png"));
                        this.length = noteLength.EIGTH;
                        constant = noteImage;
                        positionx = 15;
                        positiony = 36;
                        ani_length = 200;
                        full_length = 20;
                        skip = 4;
                        highlight = ImageIO.read(getClass().getResource("/images/eighthNoteH.png"));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    break;
                case 5:
                    try {
                        noteImage = ImageIO.read(getClass().getResource("/images/sixteenthNote.png"));
                        this.length = noteLength.SIXTEENTH;
                        constant = noteImage;
                        positionx = 6;
                        positiony = 35;
                        ani_length = 100;
                        full_length = 10;
                        skip = 4;
                        highlight = ImageIO.read(getClass().getResource("/images/sixteenthNoteH.png"));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    break;
                default:
                    break;

            }

        } else if (type == noteType.REST) { // if REST is selected
            switch (tempLength) { // determining pic based on note length
                case 0:
                    try {
                        noteImage = ImageIO.read(getClass().getResource("/images/wholeRest.png"));
                        this.length = noteLength.WHOLE;
                        constant = noteImage;
                        ani_length = 1600;
                        full_length = 160;
                        skip = 1;
                        highlight = ImageIO.read(getClass().getResource("/images/wholeRestH.png"));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    break;
                case 1:
                    try {
                        noteImage = ImageIO.read(getClass().getResource("/images/wholeRest.png"));
                        this.length = noteLength.WHOLE;
                        constant = noteImage;
                        ani_length = 1600;
                        full_length = 160;
                        skip = 1;
                        highlight = ImageIO.read(getClass().getResource("/images/wholeRestH.png"));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    break;
                case 2:
                    try {
                        noteImage = ImageIO.read(getClass().getResource("/images/halfRest.png"));
                        this.length = noteLength.HALF;
                        constant = noteImage;
                        ani_length = 800;
                        full_length = 80;
                        skip = 2;
                        highlight = ImageIO.read(getClass().getResource("/images/halfRestH.png"));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    break;
                case 3:
                    try {
                        noteImage = ImageIO.read(getClass().getResource("/images/quarterRest.png"));
                        this.length = noteLength.QUARTER;
                        constant = noteImage;
                        ani_length = 400;
                        full_length = 40;
                        skip = 2;
                        highlight = ImageIO.read(getClass().getResource("/images/quarterRestH.png"));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    break;
                case 4:
                    try {
                        noteImage = ImageIO.read(getClass().getResource("/images/eighthRest.png"));
                        this.length = noteLength.EIGTH;
                        constant = noteImage;
                        ani_length = 200;
                        full_length = 20;
                        skip = 4;
                        highlight = ImageIO.read(getClass().getResource("/images/eighthRestH.png"));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    break;
                case 5:
                    try {
                        noteImage = ImageIO.read(getClass().getResource("/images/sixteenthRest.png"));
                        this.length = noteLength.SIXTEENTH;
                        ani_length = 100;
                        constant = noteImage;
                        full_length = 10;
                        skip = 4;
                        highlight = ImageIO.read(getClass().getResource("/images/sixteenthRestH.png"));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    break;
                default:
                    break;

            }
        } else if (type == noteType.FLAT) { // only one kind of photo
            try {
                noteImage = ImageIO.read(getClass().getResource("/images/flat.png"));
                highlight = ImageIO.read(getClass().getResource("/images/flatH.png"));
                constant = noteImage;
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else { // only one kind of photo
            try {
                noteImage = ImageIO.read(getClass().getResource("/images/sharp.png"));
                highlight = ImageIO.read(getClass().getResource("/images/sharpH.png"));
                constant = noteImage;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Paint function, draws the note.
     */
    public void paint(Graphics g) {
        g.drawImage(this.noteImage, xCoord, yCoord, null);
        
        // If Selected in select mode, draw rectangle
        if (isSelected) {
            g.setColor(Color.RED);
            g.drawRect(this.getXCoord(), this.getYCoord(), this.noteImage.getWidth(null), this.noteImage.getHeight(null));
        }
        // if accidental is picked, highlight notes
        if (musicEditor.getAccidentalModeOn() && (this.type == noteType.NOTE)) {
            g.setColor(Color.RED);
            g.drawRect(this.getXCoord(), this.getYCoord(), this.noteImage.getWidth(null), this.noteImage.getHeight(null));

        }

        switch (ledger) { // Determining whether to draw ledger lines or not
            case 0:
                // No ledger. Regular note
                break;
            case 1:
                // one ledger line in the middle
                int yLocation = this.yCoord + this.positiony;
                g.setColor(Color.BLACK);
                // decide whether or not to draw both lines
                // calculate boundTop and boundBottom
                // If point between two bound lines, then draw two

                // Upper bounds behavior
                if (musicView.getUpperBounds()) {
                    g.drawLine(this.xCoord - positionx, this.boundBottom, this.x2Coord + positionx, this.boundBottom);
                    if (yLocation < boundBottom) {
                        g.drawLine(this.xCoord - positionx, this.boundTop, this.x2Coord + positionx, this.boundTop);
                    }
                }
                // Lower bounds behavior
                if (!musicView.getUpperBounds()) {
                    g.drawLine(this.xCoord - positionx, this.boundTop, this.x2Coord + positionx, this.boundTop);
                     // Adds a little buffer so that a note can be on the line without
                     // two lines being drawn at a time.
                    if (yLocation > boundBottom) {
                        g.drawLine(this.xCoord - positionx, this.boundBottom, this.x2Coord + positionx, this.boundBottom);
                    }
                }

                break;
            default:
                // No ledger. Regular note
                break;
        }

        if (this.isAccidental) { // if note is accidental
            if (this.isFlat) {
                Image temp = null; // create a temp image
                try {
                    temp = ImageIO.read(getClass().getResource("/images/flat.png")); // fill image with image of flat
                } catch (IOException e) {
                    e.printStackTrace();
                }

                g.drawImage(temp, this.getXCoord() - (temp.getWidth(null)), (this.getYCoord() + positiony - (temp.getHeight(null)/2)), null);

                if ((isSelected || musicEditor.getAccidentalModeOn()) || onlyAccSelect) { // if note is selected or accidental mode on, draw highlighted box
                    g.setColor(Color.RED);
                    g.drawRect(this.getXCoord() - (temp.getWidth(null)), (this.getYCoord() + positiony - (temp.getHeight(null)/2)), temp.getWidth(null), temp.getHeight(null));
                }

                // getting the image bound information, to be used to
                // determine if a user clicks on only the accidental.
                accX = this.getXCoord() - (temp.getWidth(null));
                accX2 = accX + temp.getWidth(null);
                accY = (this.getYCoord() + positiony - (temp.getHeight(null)/2));
                accY2 = accY + temp.getHeight(null);

            } else {
                Image temp = null; // create a temp image
                try {
                    temp = ImageIO.read(getClass().getResource("/images/sharp.png"));
                } catch (IOException e) {
                    e.printStackTrace();
                }

                g.drawImage(temp, this.getXCoord() - (temp.getWidth(null)), (this.getYCoord() + positiony - (temp.getHeight(null)/2)), null);
                if ((isSelected || musicEditor.getAccidentalModeOn()) || onlyAccSelect) { // if note is selected, draw appropriate bounding box
                    g.setColor(Color.RED);
                    g.drawRect(this.getXCoord() - (temp.getWidth(null)), (this.getYCoord() + positiony - (temp.getHeight(null)/2)), temp.getWidth(null), temp.getHeight(null));
                }

                // getting the image bound information, to be used to
                // determine if a user clicks on only the accidental.
                accX = this.getXCoord() - temp.getWidth(null);
                accX2 = accX + temp.getWidth(null);
                accY = (this.getYCoord() + positiony - (temp.getHeight(null)/2));
                accY2 = accY + temp.getHeight(null);
            }
        }
        return;
    }

    /**
     * Set XCoordinates of the note
     * @param x xCoord
     */
    public void setXCoord(int x) { 
        xCoord = x;
        x2Coord = x + this.noteImage.getWidth(null);
    }

    /**
     * Set YCoordinates of the note
     * @param y yCoord
     */
    public void setYCoord(int y) { 
        yCoord = y;
        y2Coord = y + this.noteImage.getHeight(null);
    }

 
    public int getXCoord() {
        return xCoord;
    }

    public int getYCoord() {
        return yCoord;
    }

    public int getX2Coord() {
        return x2Coord;
    }

    public int getY2Coord() {
        return y2Coord;
    }

    public Note.pitchEnum getPitch() {
        return pitch;
    }

    public Note.noteType getNoteType() {
        return type;
    }

    public Note.noteLength getNoteLength() {
        return length;
    }

    public static int noteLengthCalc(String s) {
        if (s.equals("WHOLE")) {
            return 1;
        }

        if (s.equals("HALF")) {
            return 2;
        }

        if (s.equals("QUARTER")) {
            return 3;
        }

        if (s.equals("EIGTH")) {
            return 4;
        }
        
        if (s.equals("SIXTEENTH")) {
            return 5;
        }

        return 1;
    }

    public void setPitch(pitchEnum p) {
        pitch = p;
        // pitch is calculated in musicView and in staffMouseListener
    }

    public void setIsSelected(Boolean b) {
        isSelected = b;
    }

    public Boolean getIsSelected() {
        return isSelected;
    }

    public void setNoteType(noteType n) {
        type = n;
    }

    /**
     * Sets note length
     * @param tempLength length to be switched to
     */
    public void setNoteLength(int tempLength) {
        if (this.getNoteType() == noteType.NOTE) {
            switch (tempLength) {
                case 0:
                    try {
                        noteImage = ImageIO.read(getClass().getResource("/images/wholeNote.png"));
                        this.length = noteLength.WHOLE;
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    break;
                case 1:
                    try {
                        noteImage = ImageIO.read(getClass().getResource("/images/wholeNote.png"));
                        this.length = noteLength.WHOLE;
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    break;
                case 2:
                    try {
                        noteImage = ImageIO.read(getClass().getResource("/images/halfNote.png"));
                        this.length = noteLength.HALF;
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    break;
                case 3:
                    try {
                        noteImage = ImageIO.read(getClass().getResource("/images/quarterNote.png"));
                        this.length = noteLength.QUARTER;
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    break;
                case 4:
                    try {
                        noteImage = ImageIO.read(getClass().getResource("/images/eighthNote.png"));
                        this.length = noteLength.EIGTH;
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    break;
                case 5:
                    try {
                        noteImage = ImageIO.read(getClass().getResource("/images/sixteenthNote.png"));
                        this.length = noteLength.SIXTEENTH;
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    break;
                default:
                    break;

            }

        } else if (type == noteType.REST) {
            switch (tempLength) {
                case 0:
                    try {
                        noteImage = ImageIO.read(getClass().getResource("/images/wholeRest.png"));
                        this.length = noteLength.WHOLE;
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    break;
                case 1:
                    try {
                        noteImage = ImageIO.read(getClass().getResource("/images/wholeRest.png"));
                        this.length = noteLength.WHOLE;
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    break;
                case 2:
                    try {
                        noteImage = ImageIO.read(getClass().getResource("/images/halfRest.png"));
                        this.length = noteLength.HALF;
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    break;
                case 3:
                    try {
                        noteImage = ImageIO.read(getClass().getResource("/images/quarterRest.png"));
                        this.length = noteLength.QUARTER;
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    break;
                case 4:
                    try {
                        noteImage = ImageIO.read(getClass().getResource("/images/eighthRest.png"));
                        this.length = noteLength.EIGTH;
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    break;
                case 5:
                    try {
                        noteImage = ImageIO.read(getClass().getResource("/images/sixteenthRest.png"));
                        this.length = noteLength.SIXTEENTH;
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    break;
                default:
                    break;

            }
        }
        // change icon
    }

    /**
     * Set pitch, to be used with staffMouseListener to determine bounds
     * @param i bound area
     */
    public void setPitch(int i) {
        // B3, C4, D4, E4, F4, G4, A4, B4, C5, D5, E5, F5, G5, A5, B5
        switch(i) {
            case 0:
                this.pitch = pitchEnum.B3;
                break;
            case 1:
                this.pitch = pitchEnum.C4;
                break;
            case 2:
                this.pitch = pitchEnum.D4;
                break;
            case 3:
                this.pitch = pitchEnum.E4;
                break;
            case 4:
                this.pitch = pitchEnum.F4;
                break;
            case 5:
                this.pitch = pitchEnum.G4;
                break;
            case 6:
                this.pitch = pitchEnum.A4;
                break;
            case 7:
                this.pitch = pitchEnum.B4;
                break;
            case 8:
                this.pitch = pitchEnum.C5;
                break;
            case 9:
                this.pitch = pitchEnum.D5;
                break;
            case 10:
                this.pitch = pitchEnum.E5;
                break;
            case 11:
                this.pitch = pitchEnum.F5;
                break;
            case 12:
                this.pitch = pitchEnum.G5;
                break;
            case 13:
                this.pitch = pitchEnum.A5;
                break;
            case 14:
                this.pitch = pitchEnum.B5;
                break;
            default:
                this.pitch = pitchEnum.E4;
                break;
        }
    }

    
    public String toString() {
        String accident = "";
        if (this.isAccidental) {
            if (this.isFlat) {
                accident = "Flat";
            } else {
                accident = "Sharp";
            }
        }
        return length + " " + pitch + " " + accident;
    }

    /**
     * Returns the pitch as a string
     * @return pitch
     */
    public String pitchString() {
        return "" + pitch;
    }

    public String lengthString() {
        return "" + length;
    }

    public String typeString() {
        return "" + type;
    }

    /**
     * Returns the accidental as a string
     * @return accidental
     */
    public String accString() {
        String returnstring = "";
        if (this.isFlat) {
            returnstring = "Flat";
        }

        if (this.isSharp) {
            returnstring = "Sharp";
        }
        return returnstring;
    }

    public int getPositionX() {
        return positionx;
    }

    public int getPositionY() {
        return positiony;
    }

    public boolean getIsFlat() {
        return this.isFlat;
    }

    public boolean getIsSharp() {
        return this.isSharp;
    }

    public boolean getIsAccidental() {
        return this.isAccidental;
    }

    public void setIsFlat(boolean b) {
        isFlat = b;
        if (isFlat) {
            try {
                accidentalImage = ImageIO.read(getClass().getResource("/images/flat.png"));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void setIsSharp(boolean b) {
        isSharp = b;
        if (isSharp) {
            try {
                accidentalImage = ImageIO.read(getClass().getResource("/images/sharp.png"));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void setIsAccidental(boolean b) {
        isAccidental = b;
    }

    public void setAccPic(boolean b) {
        accPic = b;
    }

    public boolean getAccPic() {
        return accPic;
    }
   
    public int getLedger() {
        return this.ledger;
    }

    public void setLedger(int b) {
        ledger = b;
    }

    public int getAccX() {
        return this.accX;
    }

    public int getAccX2() {
        return this.accX2;
    }

    public int getAccY() {
        return this.accY;
    }

    public int getAccY2() {
        return this.accY2;
    }

    public void setOnlyAccSelect(boolean b) {
        onlyAccSelect = b;
    }

    public boolean getOnlyAccSelect() {
        return onlyAccSelect;
    }

    /**
     * Calculates the threshold of the staff
     * to determine where the outside bounds begin
     * @return
     */
    public int getThreshold() {
        threshold = xCoord - (thresholdWidth / 2);
        return threshold;
    }

    /**
     * Calculates the bottom threshold of the staff
     * @return
     */
    public int getThreshold2() {
        this.thresholdWidth = 40 + noteImage.getWidth(null);
        return threshold + thresholdWidth;
    }

    public int getChordX() {
        return chordX;
    }

    public void setChordX(int i) {
        chordX = i;
    }

    /**
     * Sets the appropriate bound info
     * @param top top of the bound
     * @param bottom bottom of the bound
     */
    public void boundInfo(int top, int bottom) {
        boundTop = top;
        boundBottom = bottom;
    }

    public int getAni_Length() {
        return this.ani_length;
    }

    public void deduct() {
        this.ani_length = this.ani_length - 5;
    }

    public void setAni_Length(int i) {
        this.ani_length = i;
    }

    public int getFullLength() {
        return full_length;
    }

    public int getSkip() {
        return this.skip;
    }

    public void highlight() {
        this.noteImage = this.highlight;
    }
    
    public void unhighlight() {
        this.noteImage = constant;
    }

}
