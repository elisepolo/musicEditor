import javax.swing.JComponent;
import javax.swing.event.MouseInputListener;
import javax.swing.*; 
import java.awt.*;
import java.awt.Dimension;
import java.util.Hashtable;
import java.awt.image.ImageObserver;
import java.util.LinkedList;
import java.util.ArrayList;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import java.awt.event.*;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.datatransfer.DataFlavor;

/**
 * Mouse Listener for Staff + Notes
 */
public class staffMouseListener implements MouseListener, MouseInputListener, KeyListener {

    // Says whether or not a note is already selected.
    private Boolean alreadySelect = false;
    // Number of notes selected, so that it is just one note at a time
    private int numSelect = 0;
    // Note to be added
    private Note noteAdd = null;
    // Staff to be added to
    private Staff staffAdd = null;
    // Note Selected by the user
    private Note selectedNote = null;
    // Note Selected for Accidental
    private Note accNote = null;
    // Staff Selected for Accidental
    private Staff accStaff = null;
    // Accidental Selected
    private Boolean accSelected = false;
    // Return current staff
    private Staff sendStaff = null;

    public void mouseClicked(MouseEvent e) {
        if (musicEditor.getSelectModeOn()) {
            // highlight box around selected note -> Returns selected note, then rePaint() will draw border
            int x = e.getX();
            int y = e.getY();
            for (Staff s : musicView.getItems()) {
                if (x >= s.getxVal1() && x < s.getxVal2() && y >= s.getThreshold() && y <= s.getThreshold2()) {
                    // Finds the staff the user clicked inside
                    // Now to parse through and identify the correct note
                    for (Note n : s.getNotesList()) {
                        // If n was in the bounds of the user click
                        if (x >= n.getXCoord() - 20 && x < n.getX2Coord() && y >= n.getYCoord() && y <= n.getY2Coord() + 10) {
                            staffAdd = s;
                            // if no notes are selected already
                            if (x >= n.getAccX() && x < n.getAccX2() && y >= n.getAccY() && y< n.getAccY2()) {
                                    System.out.println("Clicked on the accidental.");
                                    n.setOnlyAccSelect(true);
                                    break;
                                }
                            if (numSelect == 0) {
                                n.setIsSelected(true);
                                selectedNote = n;
                                musicView.setDragNote(selectedNote);
                                numSelect++;
                                staffAdd = s;
                                n.setOnlyAccSelect(false);
                                musicEditor.setBottomMessage("Note selected: " + n);
                            }
                        }
                    }
                    
                }

        } 
    } else if (musicEditor.getAccidentalModeOn()) {  // ACCIDENTAL MODE ON!
            int x = e.getX();
            int y = e.getY();

            for (Staff s : musicView.getItems()) {
                if (x >= s.getxVal1() && x < s.getxVal2() && y >= s.getThreshold() && y <= s.getThreshold2()) {
                    // Finds the staff the user clicked inside
                    // Now to parse through and identify the correct note
                    for (Note n : s.getNotesList()) {
                        // If n was in the bounds of the user click
                        if (n.getNoteType() == Note.noteType.NOTE) {
                            if (x >= n.getXCoord() && x < n.getX2Coord() && y >= n.getYCoord() && y <= n.getY2Coord()) {
                                n.setIsAccidental(true);
                                n.setOnlyAccSelect(false);
                                if (musicEditor.getNoteTypeInt() == 6) {
                                    n.setIsFlat(true);
                                } else {
                                    n.setIsSharp(true);
                                }
                                n.repaint();
                            }
                        }
                    }
                    
                }
        } 
    } else {
            int x = e.getX();
            int y = e.getY();
            numSelect = 0;
            selectedNote = null;
            for (Staff s : musicView.getItems()) {
                    for (Note n : s.getNotesList()) {
                        // deselect all notes in the staff
                        n.setIsSelected(false);
                    }
                    

        }
        }
    }

    @Override
    public void mousePressed(MouseEvent e) {
        Note selected = null;

        // checking to see if user clicked within bounds of a staff
        if (!musicEditor.getSelectModeOn()) {
            int x = e.getX();
            int y = e.getY();
        for (Staff s : musicView.getItems()) {
            if (x >= s.getxVal1() && x < s.getxVal2() && y >= s.getThreshold() && y <= s.getThreshold2()) {
                System.out.println("Clicked inside Staff BOUNDS #" + s.getStaffNum());
                staffAdd = s;
                musicView.setSendStaff(s);
                Note newNote = null;
                int n = musicEditor.getNoteTypeInt();
                if (n == 0) {
                    return;
                }
                // creating a note based on length, pitch not decided yet
                switch (n) {
                    case 1:
                        newNote = new Note(Note.pitchEnum.B5, Note.noteType.NOTE, musicEditor.getSliderVal());
                        break;
                    case 2:
                        newNote = new Note(Note.pitchEnum.B5, Note.noteType.REST, musicEditor.getSliderVal());
                        break;
                    case 3:
                        newNote = new Note(Note.pitchEnum.B5, Note.noteType.FLAT, musicEditor.getSliderVal());
                        newNote.setAccPic(true);
                        break;
                    case 4:
                        newNote = new Note(Note.pitchEnum.B5, Note.noteType.SHARP, musicEditor.getSliderVal());
                        newNote.setAccPic(true);
                        break;
                    default:
                        newNote = new Note(Note.pitchEnum.B5, Note.noteType.NOTE, musicEditor.getSliderVal());
                        break;
                }

                noteAdd = newNote;
                musicView.setDragNote(newNote);
                selected = newNote;

                if (x >= s.getxVal1() && x < s.getxVal2() && y >= s.getyVal1() && y <= s.getyVal2()) {
                    System.out.println("Clicked INSIDE");
                    // No need to handle the ledger.
                    
                } else {
                    System.out.println("Clicked OUTSIDE");
                    // Now we must handle the ledger.
                    musicView.setDragNoteLedger(1); // now we must come up with the two different ledgers.
                    
                }
                

                
        }
        }
        } else if (musicEditor.getAccidentalModeOn()) { // Accidental Mode is ON!
            int x = e.getX();
            int y = e.getY();

            boolean clickedOn = false;
            for (Staff s : musicView.getItems()) {
                if (x >= s.getxVal1() && x < s.getxVal2() && y >= s.getThreshold() && y <= s.getThreshold()) {
                    staffAdd = s;
                    // marks the staff the user has clicked on
                    for (Note n : s.getNotesList()) {
                        if (x >= n.getXCoord() && x < n.getX2Coord() && y >= n.getYCoord() && y <= n.getY2Coord()) {
                            System.out.println("Accidental Note Clicked");
                        }
                    }
                    
                }
        }
        Accidental acc = new Accidental(false);
        musicView.setDragAcc(acc);
        } else { // select mode is on
            int x = e.getX();
            int y = e.getY();
            // checks to see if a note was clicked on during select mode
            boolean clickedOn = false;
            for (Staff s : musicView.getItems()) {
                if (x >= s.getxVal1() && x < s.getxVal2() && y >= s.getThreshold() && y <= s.getThreshold2()) {
                    staffAdd = s;
                    // marks the staff the user has clicked on
                    for (Note n : s.getNotesList()) {
                        if (x >= n.getXCoord() && x < n.getX2Coord() && y >= n.getYCoord() && y <= n.getY2Coord()) {
                            clickedOn = true;
                            staffAdd = s;
                        }
                    }
                    
                }
        }
        if (clickedOn == false) {
            for (Staff s : musicView.getItems()) {
                if (x >= s.getxVal1() && x < s.getxVal2() && y >= s.getThreshold() && y <= s.getThreshold2()) {
                    staffAdd = s;
                    // Finds the staff the user clicked inside
                    // Now to parse through and identify the correct note
                    for (Note n : s.getNotesList()) {
                        n.setIsSelected(false);
                        n.setOnlyAccSelect(false);
                    }
                    numSelect = 0;
                    selectedNote = null;
                }
        }
        }
    }

    
    }


    @Override
    public void mouseReleased(MouseEvent e) {
        // noteAdd. add to staff
        if (musicEditor.getPenModeOn()) {
            // Do nothing! 
        } else if (!musicEditor.getSelectModeOn() && musicEditor.getNoteTypeInt() != 0 && !musicEditor.getAccidentalModeOn()) {
            if (noteAdd.getNoteType() == Note.noteType.NOTE && noteAdd.getChordX() != 0) { // If note is a part of a chord
                noteAdd.setXCoord(noteAdd.getChordX()); // adds it to the correct location post-drag
            } else {
                noteAdd.setXCoord(e.getX() - noteAdd.getPositionX()); // regular non-chord X
            }
            noteAdd.setYCoord(e.getY() - noteAdd.getPositionY()); // yCoord will always be what the user inputs

            calcPitch(staffAdd, noteAdd); // calculates the pitch based on final location

            staffAdd.getNotesList().add(noteAdd); // adds it to the staff
            musicEditor.setBottomMessage("Note Added: " + noteAdd);
        } else if (selectedNote != null && numSelect != 0) {
            // re-calculates the note's pitch, and moves its location
            staffAdd.getNotesList().remove(selectedNote);

            Note tempSelect = selectedNote;

            if (tempSelect.getChordX() != 0) {
                tempSelect.setXCoord(tempSelect.getChordX());
            } else {
                tempSelect.setXCoord(e.getX() - tempSelect.getPositionX());
            }
            tempSelect.setYCoord(e.getY() - tempSelect.getPositionY());


            calcPitch(staffAdd, tempSelect);

            tempSelect.setIsSelected(false);
            tempSelect.setOnlyAccSelect(false);
            staffAdd.getNotesList().add(tempSelect);

            selectedNote = tempSelect;
            selectedNote.setIsSelected(false);
            selectedNote.setOnlyAccSelect(false);
            numSelect = 0;

            musicEditor.setBottomMessage("Note Moved: " + tempSelect);
        } else if (musicEditor.getAccidentalModeOn()) { // accidental mode on, accidental being added 
            int x = e.getX();
            int y = e.getY();

            for (Staff s : musicView.getItems()) {
                if (x >= s.getxVal1() && x < s.getxVal2() && y >= s.getThreshold() && y <= s.getThreshold2()) {
                    staffAdd = s;
                    
                    for (Note n : s.getNotesList()) {

                        if (n.getNoteType() == Note.noteType.NOTE) { // only add to notes, not rests

                            if (x >= n.getXCoord() - 10 && x < n.getX2Coord() + 10 && y >= n.getYCoord() - 10 && y <= n.getY2Coord() + 10) {

                                n.setIsAccidental(true); // sets the note as an accidental
                                n.setOnlyAccSelect(false); // turns off 'accidental' selected

                                if (musicEditor.getNoteTypeInt() == 3) { // if flat is picked

                                    String prevString = n.accString(); // previous accidental
                                    n.setIsFlat(true);
                                    n.setIsSharp(false);
                                    musicEditor.setBottomMessage("Note changed from " + n.pitchString() + " " + prevString + " to " + n.pitchString() + " " + n.accString());

                                } else {

                                    String prevString = n.accString(); // previous accidental to be used to write bottom message.
                                    n.setIsSharp(true);
                                    n.setIsFlat(false);
                                    musicEditor.setBottomMessage("Note changed from " + n.pitchString() + " " + prevString + " to " + n.pitchString() + " " + n.accString());
                                
                                }
                                    n.repaint();
                                
                            }
                        }
                        
                    }
                }
            }
        }
    }
    

    @Override
    public void mouseEntered(MouseEvent e) {
        
    }

    @Override
    public void mouseExited(MouseEvent e) {
        
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        // TODO Auto-generated method stub
        
    }
    
    /**
     * Calculates and determines pitch of note
     * based on bounds of the staff
     * and changes pitch of note accordingly
     * @param s staff note is on
     * @param n not being added to staff
     * @return
     */
    public int calcPitch(Staff s, Note n) {
        // Only working with Y values here
        // 8 Boundaries needed, in reverse order (?)
        
  
        int increment = (s.getStaffHeight() + s.getThresholdHeight()) / 15;
        // Calculating each bound, so that we can use them after they are declared.
        int bound1 = s.getThreshold() + increment;
        int bound2 = bound1 + increment;
        int bound3 = bound2 + increment;
        int bound4 = bound3 + increment;
        int bound5 = bound4 + increment;
        int bound6 = bound5 + increment;
        int bound7 = bound6 + increment;
        int bound8 = bound7 + increment;
        int bound9 = bound8 + increment;
        int bound10 = bound9 + increment;
        int bound11 = bound10 + increment;
        int bound12 = bound11 + increment;
        int bound13 = bound12 + increment;
        int bound14 = bound13 + increment;
        int bound15 = bound14 + increment;
        
  
        if ((n.getYCoord() + n.getPositionY()) >= (s.getThreshold() - 7) && (n.getYCoord() + n.getPositionY()) <= bound1) {
            n.setPitch(14);  // 
            System.out.println("Bound1");
            n.boundInfo(s.getThreshold() - 7, bound1); // the actual width of the drawn bound, not determining what note it is
        }
        
        if ((n.getYCoord() + n.getPositionY()) >= bound1 && (n.getYCoord() + n.getPositionY()) <= bound2) {
            n.setPitch(13); // B5
            System.out.println("Bound2");
            n.boundInfo(bound1, bound3); // the actual height of the drawn bound, not determining what note it is
        }
       
        if ((n.getYCoord() + n.getPositionY()) >= bound2 && (n.getYCoord() + n.getPositionY()) <= bound3) {
            n.setPitch(12); // A5
            System.out.println("Bound3");
            n.boundInfo(bound1, bound3); // the actual height of the drawn bound, not determining what note it is
        }
        
        if ((n.getYCoord() + n.getPositionY()) >= bound3 && (n.getYCoord() + n.getPositionY()) <= bound4) {
            n.setPitch(11); // G5
            System.out.println("Bound4");
            n.boundInfo(bound3, bound5); // the actual height of the drawn bound, not determining what note it is
        }
        
        if ((n.getYCoord() + n.getPositionY()) >= bound4 && (n.getYCoord() + n.getPositionY()) <= bound5) {
            n.setPitch(10); // F5
            System.out.println("Bound5");
            n.boundInfo(bound3, bound5); // the actual height of the drawn bound, not determining what note it is
        }
        
        if ((n.getYCoord() + n.getPositionY()) >= bound5 && (n.getYCoord() + n.getPositionY()) <= bound6) {
            n.setPitch(9); // E5
            System.out.println("Bound6");
            n.boundInfo(bound5, bound7); // the actual height of the drawn bound, not determining what note it is
        }
        
        if ((n.getYCoord() + n.getPositionY()) >= bound6 && (n.getYCoord() + n.getPositionY()) <= bound7) {
            n.setPitch(8); // D5
            System.out.println("Bound7");
            n.boundInfo(bound5, bound7); // the actual height of the drawn bound, not determining what note it is
        }
        
        if ((n.getYCoord() + n.getPositionY()) >= bound7 && (n.getYCoord() + n.getPositionY()) <= bound8) {
            n.setPitch(7); // C5
            System.out.println("Bound8");
            n.boundInfo(bound7, bound9); // the actual height of the drawn bound, not determining what note it is
        }
        
        if ((n.getYCoord() + n.getPositionY()) >= bound8 && (n.getYCoord() + n.getPositionY()) <= bound9) {
            n.setPitch(6); // B4
            System.out.println("Bound9");
            n.boundInfo(bound7, bound9); // the actual height of the drawn bound, not determining what note it is
        }
        
        if ((n.getYCoord() + n.getPositionY()) >= bound9 && (n.getYCoord() + n.getPositionY()) <= bound10) {
            n.setPitch(5); // A4
            System.out.println("Bound10");
            n.boundInfo(bound9, bound11); // the actual height of the drawn bound, not determining what note it is
        }
        
        if ((n.getYCoord() + n.getPositionY()) >= bound10 && (n.getYCoord() + n.getPositionY()) <= bound11) {
            n.setPitch(4); // G4
            System.out.println("Bound11");
            n.boundInfo(bound9, bound11); // the actual height of the drawn bound, not determining what note it is
        }
       
        if ((n.getYCoord() + n.getPositionY()) >= bound11 && (n.getYCoord() + n.getPositionY()) <= bound12) {
            n.setPitch(3); // F4
            System.out.println("Bound12");
            n.boundInfo(bound11, bound13); // the actual height of the drawn bound, not determining what note it is
        }
        
        if ((n.getYCoord() + n.getPositionY()) >= bound12 && (n.getYCoord() + n.getPositionY()) <= bound13) {
            n.setPitch(2); // E4
            System.out.println("Bound13");
            n.boundInfo(bound11, bound13); // the actual height of the drawn bound, not determining what note it is
        }
       
        if ((n.getYCoord() + n.getPositionY()) >= bound13 && (n.getYCoord() + n.getPositionY()) <= bound14) {
            n.setPitch(1); // D4
            System.out.println("Bound14");
            n.boundInfo(bound13, bound15); // the actual height of the drawn bound, not determining what note it is
        }
        
        if ((n.getYCoord() + n.getPositionY()) >= bound15 && (n.getYCoord() + n.getPositionY()) <= bound15) {
            n.setPitch(0); // C4
            System.out.println("Bound15");
            n.boundInfo(bound13, bound15); // the actual height of the drawn bound, not determining what note it is
        }
        
        return 0;
    }

    /**
     * Deletes the accidental or note
     * based on which is selected, and if the
     * backspace key is hit.
     */
    @Override
    public void keyTyped(KeyEvent e) {
        if (e.getKeyChar() == KeyEvent.VK_BACK_SPACE && musicEditor.getSelectModeOn()) {
            for (Staff s : musicView.getItems()) {
                for (Note n : s.getNotesList()) {
                    if (n.getIsSelected() || n.getOnlyAccSelect()) {
                        if (n.getOnlyAccSelect()) { // if only the accidental is selected
                            n.setIsAccidental(false); // set the note as non-accidental
                            n.setIsFlat(false); // not flat
                            n.setIsSharp(false); // not sharp
                            n.setIsSelected(false); // de-select the note
                            n.setOnlyAccSelect(false); // and remove that the acc is selected
                            musicEditor.setBottomMessage("Accidental removed from: " + n);
                            musicView.setDragNote(null); // remove it from drag
                            n.repaint();
                            break;
                        }
                        s.getNotesList().remove(n); // else we must remove the entire note
                        n.setIsSelected(false); // de-select it
                        n.setOnlyAccSelect(false); // remove that the acc is selected, just in case
                        musicView.setDragNote(null); // remove it from drag
                        musicEditor.setBottomMessage("Note removed: " + n);
                        break;
                    }
                }
            }
        }
    }

    @Override
    public void keyPressed(KeyEvent e) {
        
        
    }

    @Override
    public void keyReleased(KeyEvent e) {
        
    }

    public Staff getStaff() {
        return sendStaff;
    }




    
}
