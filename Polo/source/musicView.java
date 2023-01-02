import javax.swing.event.MouseInputListener;
import dollar.*;
import java.awt.event.*;
import javax.swing.*; 
import java.awt.*;
import java.awt.Dimension;
import java.util.LinkedList;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.StringBuilder;
import java.util.Scanner;  
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.awt.geom.Line2D;
import javax.imageio.ImageIO;
import javax.swing.Timer;


/**
 * Class to draw the Music View Area of the Music Editor Program
 */
public class musicView extends JComponent implements MouseInputListener, KeyListener {
    /**
     * Things needed:
     * paintComponent Method
     * Note class
     * Staff class
     * newStaff() function
     */
    public static LinkedList<Staff> items = new LinkedList<>();
    public static Note dragNote = null;
    public static Accidental dragAcc = null;
    public static boolean upperBounds = false;
    public static ArrayList<Point2D> stroke = new ArrayList<>();
    public static DollarRecognizer dollar = new DollarRecognizer();
    public static Staff sendStaff;
    public static Image star = null;
    public static int starX = -20;
    public static int starY = -20;
    public static Note karaoke = null;
    public static Timer timer;
    public static int numIterations = 0;
    public static int max = 0;
    public static ArrayList<Note> animation = new ArrayList<>();
    public static ArrayList<Staff> animationStaff = new ArrayList<>();
    public static ArrayList<Note> complete = new ArrayList<>();
    public static int noteIndex = 0;
    public static int numtimes = 0;
    public static int divisionX = 0;
    public static int divisionY = 0;
    public static int start = 0;
    public static int height = 0;
    public static int between = 40;
    

    public musicView() {

      this.setFocusable(true);
      this.requestFocusInWindow();
      staffMouseListener ml = new staffMouseListener();
      this.addKeyListener(ml);
      this.addKeyListener(this);
      for (Staff s : items) {
         s.addMouseMotionListener(ml);
         s.addMouseMotionListener(this);
         for (Note n : s.getNotesList()) {
            n.setTransferHandler(new TransferHandler("icon"));
            n.addMouseMotionListener(new noteMouseListener());
            n.addMouseListener(this);
         }
      }

      try {
               star = ImageIO.read(getClass().getResource("/images/star.png"));
            } catch (IOException e) {
                e.printStackTrace();
            }
    }

     /**
      * This will actually paint the music View on screen
      */
     public void paintComponent(Graphics g) {
        super.paintComponent(g);

        // Sets background for musicView
        g.setColor(Color.lightGray);
        g.fillRect(0, 0, getWidth(), getHeight());

        // Adds mouseListener and mouseMotionListener capabilities
        this.addMouseListener(this);
        this.addMouseMotionListener(this);

        // Iterates and draws each staff
        for (Staff j : items) {
         j.addMouseMotionListener(new staffMouseListener());
         j.addMouseListener(new staffMouseListener());

         this.add(j);
         j.paintComponent(g);

         // Draws each note on each staff
         for (Note n : j.getNotesList()) {
            if (!musicEditor.getSelectModeOn()) {
               // Initializing the select Function
               n.setIsSelected(false);
               n.addMouseListener(new noteMouseListener());
               n.addMouseMotionListener(new noteMouseListener());
            }
            // actually adding the note to the screen
            this.add(n);
            n.paint(g);
         }
        }
        // Drawing the dragged note
        if (dragNote != null) {
         this.add(dragNote);
         dragNote.paint(g);
        }

        if (musicEditor.getAccidentalModeOn()) {
            if (musicEditor.getNoteTypeInt() == 6) {
               //this.add(flatImage);
            } else {
               //this.add(sharpImage);
            }
        }

        if (musicEditor.getPenModeOn()) {
         if (stroke.size() != 0) {
            Point2D prev = stroke.get(0);
            for (Point2D p : stroke) {
               Graphics2D g2 = (Graphics2D) g;
               g2.setColor(Color.GREEN);
               g2.setStroke(new BasicStroke(10));
               g2.draw(new Line2D.Double(prev.getX(), prev.getY(), p.getX(), p.getY()));
               prev = p;
            }
         }
         
        }

        g.drawImage(star, starX, starY, null);
        // setting the preferred sized
        this.setPreferredSize(new Dimension(800, 400 + Staff.numStaves*(75)));
     }

     /**
      * Adds a new staff to MusicView
      * @param j the new Staff
      */
     public void addToMusicView(Staff j) {
      items.add(j);
     }

     /*
      * Removes staff from musicView + System
      * Also means all notes on the staff are lost
      */
     public void removeStaff() {
      this.remove(items.getLast());
      items.getLast().removeStaff();
      items.removeLast();
     }

     public static LinkedList<Staff> getItems() {
      return items;
     }

   @Override
   public void mouseClicked(MouseEvent e) {
      repaint();
      
   }

   @Override
   public void mousePressed(MouseEvent e) {
      if (musicEditor.getPenModeOn()) { // Drawing Mode!
         stroke.clear();
         Point2D curr = new Point2D.Double(e.getX(), e.getY());
         stroke.add(curr);
         dragNote = null;
         musicEditor.setBottomMessage("Determining user-drawn shape...");
      } else {
         stroke.clear();
         if (dragNote != null) {
            repaint();
            dragNote.setXCoord(e.getX() - dragNote.getPositionX());
            dragNote.setYCoord(e.getY() - dragNote.getPositionY());
            repaint();
         } else if (dragAcc != null) {
            repaint();
            dragAcc.setXCoord(e.getX());
            dragAcc.setYCoord(e.getY());
            repaint();
         }
      }
   }

   @Override
   public void mouseReleased(MouseEvent e) {
      // AND NOW WE CALL THE $1 RECOGNIZER!
      if (musicEditor.getPenModeOn() && stroke.size() != 0) {
         Result dollar_result = dollar.recognize(stroke);
         if (dollar_result.getName().equals("No match")) {
            musicEditor.setBottomMessage("No Shape Recognized. Try Again");
         } else {
            int shapeX = (int)dollar_result.getCandidate().getOriginalPoints().get(0).getX();
            int shapeY = (int)dollar_result.getCandidate().getOriginalPoints().get(0).getY();
            String name = dollar_result.getName();

            Staff currStaff = sendStaff;

            if (sendStaff == null) {
               System.out.println("An error occurred.");
               return;
            }
            System.out.println(sendStaff.getStaffNum());

            if (name.equals("circle")) {
               String out = "WHOLE NOTE";
               musicEditor.setBottomMessage("Shape Recognized: " + out);
               Note newAdd = new Note(Note.pitchEnum.B4, Note.noteType.NOTE, 1);
               newAdd.setXCoord(shapeX - newAdd.getPositionX());
               newAdd.setYCoord(shapeY - newAdd.getPositionY());
               currStaff.getNotesList().add(newAdd);
               calcPitch(currStaff, newAdd);
               musicEditor.setBottomMessage("Pen Mode: New " + newAdd.pitchString() + " " + out + " Added At (" + (shapeX - newAdd.getPositionX()) + "," + (shapeY - newAdd.getPositionY()) + ")");
               repaint();

            } else if (name.equals("half note")) {
               String out = "HALF NOTE";
               musicEditor.setBottomMessage("Shape Recognized: " + out);
               Note newAdd = new Note(Note.pitchEnum.B4, Note.noteType.NOTE, 2);
               newAdd.setXCoord(shapeX - newAdd.getPositionX());
               newAdd.setYCoord(shapeY - newAdd.getPositionY());
               currStaff.getNotesList().add(newAdd);
               calcPitch(currStaff, newAdd);
               musicEditor.setBottomMessage("Pen Mode: New " + newAdd.pitchString() + " " + out + " Added At (" + (shapeX - newAdd.getPositionX()) + "," + (shapeY - newAdd.getPositionY()) + ")");
               repaint();

            } else if (name.equals("quarter note")) {
               String out = "QUARTER NOTE";
               musicEditor.setBottomMessage("Shape Recognized: " + out);
               Note newAdd = new Note(Note.pitchEnum.B4, Note.noteType.NOTE, 3);
               newAdd.setXCoord(shapeX - newAdd.getPositionX());
               newAdd.setYCoord(shapeY - newAdd.getPositionY());
               currStaff.getNotesList().add(newAdd);
               calcPitch(currStaff, newAdd);
               musicEditor.setBottomMessage("Pen Mode: New " + newAdd.pitchString() + " " + out + " Added At (" + (shapeX - newAdd.getPositionX()) + "," + (shapeY - newAdd.getPositionY()) + ")");
               repaint();

            } else if (name.equals("eighth note")) {
               String out = "EIGHTH NOTE";
               musicEditor.setBottomMessage("Shape Recognized: " + out);
               Note newAdd = new Note(Note.pitchEnum.B4, Note.noteType.NOTE, 4);
               newAdd.setXCoord(shapeX - newAdd.getPositionX());
               newAdd.setYCoord(shapeY - newAdd.getPositionY());
               currStaff.getNotesList().add(newAdd);
               calcPitch(currStaff, newAdd);
               musicEditor.setBottomMessage("Pen Mode: New " + newAdd.pitchString() + " " + out + " Added At (" + (shapeX - newAdd.getPositionX()) + "," + (shapeY - newAdd.getPositionY()) + ")");
               repaint();

            } else if (name.equals("sixteenth note")) {
               String out = "SIXTEENTH NOTE";
               musicEditor.setBottomMessage("Shape Recognized: " + out);
               Note newAdd = new Note(Note.pitchEnum.B4, Note.noteType.NOTE, 5);
               newAdd.setXCoord(shapeX - newAdd.getPositionX());
               newAdd.setYCoord(shapeY - newAdd.getPositionY());
               currStaff.getNotesList().add(newAdd);
               calcPitch(currStaff, newAdd);
               musicEditor.setBottomMessage("Pen Mode: New " + newAdd.pitchString() + " " + out + " Added At (" + (shapeX - newAdd.getPositionX()) + "," + (shapeY - newAdd.getPositionY()) + ")");
               repaint();

            } else if (name.equals("flat")) {
               String out = "FLAT";
               musicEditor.setBottomMessage("Shape Recognized: " + out);
               Boolean found = false;
               Note note = null;
               for (Staff s : items) {
                  for (Note n : s.getNotesList()) {
                     if (n.getNoteType() == Note.noteType.NOTE && shapeX >= n.getXCoord() - 10 && shapeX < n.getX2Coord() + 10 && shapeY >= n.getYCoord() - 10 && shapeY <= n.getY2Coord() + 10) {
                        found = true;
                        n.setIsAccidental(true);
                        n.setIsFlat(true);
                        n.setIsSharp(false);
                        note = n;
                     }
                  }
               }

               if (!found) {
                  musicEditor.setBottomMessage(out + " not within bounds of a note");
               } else {
                  musicEditor.setBottomMessage("Added " + out + " to " + note);
               }
               repaint();

            } else if (name.equals("star")) {
               String out = "SHARP";
               musicEditor.setBottomMessage("Shape Recognized: " + out + " Determining if within bounds of a note.");
               Boolean found = false;
               Note note = null;
               for (Staff s : items) {
                  for (Note n : s.getNotesList()) {
                     if (n.getNoteType() == Note.noteType.NOTE && shapeX >= n.getXCoord() - 10 && shapeX < n.getX2Coord() + 10 && shapeY >= n.getYCoord() - 10 && shapeY <= n.getY2Coord() + 10) {
                        found = true;
                        n.setIsAccidental(true);
                        n.setIsSharp(true);
                        n.setIsFlat(false);
                        note = n;
                     }
                  }
               }

               if (!found) {
                  musicEditor.setBottomMessage(out + " not within bounds of a note");
               } else {
                  musicEditor.setBottomMessage("Added " + out + " to " + note);
               }
               repaint();

            } else if (name.equals("rectangle")) {
               String out = "WHOLE REST";
               musicEditor.setBottomMessage("Shape Recognized: " + out);
               Note newAdd = new Note(Note.pitchEnum.B4, Note.noteType.REST, 1);
               newAdd.setXCoord(shapeX);
               newAdd.setYCoord(shapeY);
               currStaff.getNotesList().add(newAdd);
               calcPitch(currStaff, newAdd);
               musicEditor.setBottomMessage("Pen Mode: New " + out + " Added At (" + (shapeX - newAdd.getPositionX()) + "," + (shapeY - newAdd.getPositionY()) + ")");
               repaint();

            } else if (name.equals("half rest")) {
               String out = "HALF REST";
               musicEditor.setBottomMessage("Shape Recognized: " + out);
               Note newAdd = new Note(Note.pitchEnum.B4, Note.noteType.REST, 2);
               newAdd.setXCoord(shapeX - newAdd.getPositionX());
               newAdd.setYCoord(shapeY - newAdd.getPositionY());
               currStaff.getNotesList().add(newAdd);
               calcPitch(currStaff, newAdd);
               musicEditor.setBottomMessage("Pen Mode: New " + out + " Added At (" + (shapeX - newAdd.getPositionX()) + "," + (shapeY - newAdd.getPositionY()) + ")");
               repaint();

            } else if (name.equals("right curly brace")) {
               String out = "QUARTER REST";
               musicEditor.setBottomMessage("Shape Recognized: " + out);
               Note newAdd = new Note(Note.pitchEnum.B4, Note.noteType.REST, 3);
               newAdd.setXCoord(shapeX - newAdd.getPositionX());
               newAdd.setYCoord(shapeY - newAdd.getPositionY());
               currStaff.getNotesList().add(newAdd);
               calcPitch(currStaff, newAdd);
               musicEditor.setBottomMessage("Pen Mode: New " + out + " Added At (" + (shapeX - newAdd.getPositionX()) + "," + (shapeY - newAdd.getPositionY()) + ")");
               repaint();

            } else if (name.equals("eighth rest")) {
               String out = "EIGHTH REST";
               musicEditor.setBottomMessage("Shape Recognized: " + out);
               Note newAdd = new Note(Note.pitchEnum.B4, Note.noteType.REST, 4);
               newAdd.setXCoord(shapeX - newAdd.getPositionX());
               newAdd.setYCoord(shapeY - newAdd.getPositionY());
               currStaff.getNotesList().add(newAdd);
               calcPitch(currStaff, newAdd);
               musicEditor.setBottomMessage("Pen Mode: New " + out + " Added At (" + (shapeX - newAdd.getPositionX()) + "," + (shapeY - newAdd.getPositionY()) + ")");
               repaint();
            } else if (name.equals("sixteenth rest")) { // name.equals("sixteenth rest")
               String out = "SIXTEENTH REST";
               musicEditor.setBottomMessage("Shape Recognized: " + out);
               Note newAdd = new Note(Note.pitchEnum.B4, Note.noteType.REST, 5);
               newAdd.setXCoord(shapeX - newAdd.getPositionX());
               newAdd.setYCoord(shapeY - newAdd.getPositionY());
               currStaff.getNotesList().add(newAdd);
               calcPitch(currStaff, newAdd);
               musicEditor.setBottomMessage("Pen Mode: New " + out + " Added At (" + (shapeX - newAdd.getPositionX()) + "," + (shapeY - newAdd.getPositionY()) + ")");
               repaint();

            } else {
               musicEditor.setBottomMessage("No Shape Recognized. Try Again");
            }
         }
      }
      repaint();
      stroke.clear();
      dragNote = null;
      dragAcc = null;
   }

   @Override
   public void mouseEntered(MouseEvent e) {
      repaint();
      
      
   }

   @Override
   public void mouseExited(MouseEvent e) {
      repaint();
      
   }

   @Override
   public void mouseDragged(MouseEvent e) {
      int x = e.getX();
      int y = e.getY();
      boolean inThreshold = false;
      if (musicEditor.getPenModeOn()) {
         Point2D curr = new Point2D.Double(e.getX(), e.getY());
         stroke.add(curr);
         repaint();
         dragNote = null;
      } else {
         stroke.clear();
         if (dragNote != null) {
            repaint();
            dragNote.setXCoord(e.getX() - dragNote.getPositionX());
            dragNote.setYCoord(e.getY() - dragNote.getPositionY());
            repaint();
         } else if (dragAcc != null) {
            repaint();
            dragAcc.setXCoord(e.getX());
            dragAcc.setYCoord(e.getY());
            repaint();
         }
   
         // Handling Chords
         if (dragNote != null) {
            for (Staff j : musicView.getItems()) {
               if (x >= j.getxVal1() && x < j.getxVal2() && y >= j.getThreshold() && y <= j.getThreshold2()) {
                   // marks the staff the user has clicked on
                   if (x >= j.getxVal1() && x < j.getxVal2() && y >= j.getyVal1() && y <= j.getyVal2()) {
                     // No need to handle the ledger.
                     musicView.setDragNoteLedger(0);
                 } else {
                     // Now we must handle the ledger.
                     if (dragNote.getNoteType() == Note.noteType.NOTE) {
                        musicView.setDragNoteLedger(1); // now we must come up with the two different ledgers.
                        calcPitch(j, dragNote);
                     } else {
                        musicView.setDragNoteLedger(0);
                     }
                 }
      
                   for (Note k : j.getNotesList()) { // iterates through notes
                     if (k.getNoteType() == Note.noteType.NOTE && !k.getAccPic()) { // makes sure we are only talking about notes
                        if (x >= k.getThreshold() && x < k.getThreshold2()) {
                           dragNote.setXCoord(k.getXCoord()); // sets the chord x
                           dragNote.setChordX(k.getXCoord());
                           inThreshold = true; // marks that it has entered threshold of a note
                       } else {
                           inThreshold = false;
                           dragNote.setChordX(0);
                       }
                     } else {
                        dragNote.setChordX(0);
                     }
                       
                   }
                   
               }
       }
         }
      }
      
   }

   @Override
   public void mouseMoved(MouseEvent e) {
      repaint();
   }

   @Override
   public void keyTyped(KeyEvent e) {
      repaint();
      
   }

   @Override
   public void keyPressed(KeyEvent e) {
      repaint();
      
   }

   @Override
   public void keyReleased(KeyEvent e) {
      repaint();
      
      
   }

   public static void setDragNote(Note n) {
      dragNote = n;
   }

   public static void setDragAcc(Accidental a) {
      dragAcc = a;
   }

   public static void setDragNoteLedger(int i) {
      dragNote.setLedger(i); 
   }

   public int calcPitch(Staff s, Note n) {
      // Only working with Y values here
      // 8 Boundaries needed, in reverse order (?)
      

      int increment = (Staff.getStaffHeight() + s.getThresholdHeight()) / 15;
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
          n.setPitch(14);  // Unused
          System.out.println("Bound1");
          n.boundInfo(s.getThreshold() - 7, bound1);
          upperBounds = true;
      }
      
      if ((n.getYCoord() + n.getPositionY()) >= bound1 && (n.getYCoord() + n.getPositionY()) <= bound2) {
          n.setPitch(13); // B5
          System.out.println("Bound2");
          n.boundInfo(bound1, bound3);
          upperBounds = true;
      }
     
      if ((n.getYCoord() + n.getPositionY()) >= bound2 && (n.getYCoord() + n.getPositionY()) <= bound3) {
          n.setPitch(12); // A5
          System.out.println("Bound3");
          n.boundInfo(bound1, bound3);
          upperBounds = true;
      }
      
      if ((n.getYCoord() + n.getPositionY()) >= bound3 && (n.getYCoord() + n.getPositionY()) <= bound4) {
          n.setPitch(11); // G5
          System.out.println("Bound4");
          n.boundInfo(bound3, bound5);
          upperBounds = true;
      }
      
      if ((n.getYCoord() + n.getPositionY()) >= bound4 && (n.getYCoord() + n.getPositionY()) <= bound5) {
          n.setPitch(10); // F5
          System.out.println("Bound5");
          n.boundInfo(bound3, bound5);
          upperBounds = true;
      }
      
      if ((n.getYCoord() + n.getPositionY()) >= bound5 && (n.getYCoord() + n.getPositionY()) <= bound6) {
          n.setPitch(9); // E5
          System.out.println("Bound6");
          n.boundInfo(bound5, bound7);
          upperBounds = false;
      }
      
      if ((n.getYCoord() + n.getPositionY()) >= bound6 && (n.getYCoord() + n.getPositionY()) <= bound7) {
          n.setPitch(8); // D5
          System.out.println("Bound7");
          n.boundInfo(bound5, bound7);
          upperBounds = false;
      }
      
      if ((n.getYCoord() + n.getPositionY()) >= bound7 && (n.getYCoord() + n.getPositionY()) <= bound8) {
          n.setPitch(7); // C5
          System.out.println("Bound8");
          n.boundInfo(bound7, bound9);
          upperBounds = false;
      }
      
      if ((n.getYCoord() + n.getPositionY()) >= bound8 && (n.getYCoord() + n.getPositionY()) <= bound9) {
          n.setPitch(6); // B4
          System.out.println("Bound9");
          n.boundInfo(bound7, bound9);
      }
      
      if ((n.getYCoord() + n.getPositionY()) >= bound9 && (n.getYCoord() + n.getPositionY()) <= bound10) {
          n.setPitch(5); // A4
          System.out.println("Bound10");
          n.boundInfo(bound9, bound11);
          upperBounds = false;
      }
      
      if ((n.getYCoord() + n.getPositionY()) >= bound10 && (n.getYCoord() + n.getPositionY()) <= bound11) {
          n.setPitch(4); // G4
          System.out.println("Bound11");
          n.boundInfo(bound9, bound11);
          upperBounds = false;
      }
     
      if ((n.getYCoord() + n.getPositionY()) >= bound11 && (n.getYCoord() + n.getPositionY()) <= bound12) {
          n.setPitch(3); // F4
          System.out.println("Bound12");
          n.boundInfo(bound11, bound13);
          upperBounds = false;
      }
      
      if ((n.getYCoord() + n.getPositionY()) >= bound12 && (n.getYCoord() + n.getPositionY()) <= bound13) {
          n.setPitch(2); // E4
          System.out.println("Bound13");
          n.boundInfo(bound11, bound13);
          upperBounds = false;
      }
     
      if ((n.getYCoord() + n.getPositionY()) >= bound13 && (n.getYCoord() + n.getPositionY()) <= bound14) {
          n.setPitch(1); // D4
          System.out.println("Bound14");
          n.boundInfo(bound13, bound15);
          upperBounds = false;
      }
      
      if ((n.getYCoord() + n.getPositionY()) >= bound15 && (n.getYCoord() + n.getPositionY()) <= bound15) {
          n.setPitch(0); // C4
          System.out.println("Bound15");
          n.boundInfo(bound13, bound15);
          upperBounds = false;
      }
      
      return 0;
  }

  public static boolean getUpperBounds() {
      return upperBounds;
  }

  public static void setUpperBounds(boolean b) {
      upperBounds = b;
  }

  /**
   * Saves the current musicView as either a text file to read
   * or a csv file to save and reupload.
   * @param newFile
   * @param filetype
   */
  public static void saveFile(String newFile, int filetype) {
      String type;
      switch (filetype) {
         case 0: // .txt file
            type = ".txt";
            break;
         case 1: // .csv file
            type = ".csv";
            break;
         default:
            type = ".csv";
            break;
      }
      try {
         File musicFile = new File(newFile + type);
         if (musicFile.createNewFile()) {
            System.out.println("New file created: "+ newFile);
         } else {
            System.out.println("File already exists");
         }
      } catch (IOException e) {
         System.out.println("An error occured.");
      }

      // Now time to write to file
      switch (filetype) {
         case 0: // .txt file, just going to write information
            try {
               FileWriter writer = new FileWriter(newFile + type);
               for (Staff s : musicView.getItems()) {
                  writer.write("Staff "+ s.getStaffNum() + " Contents:\n");
                  int noteNum = 0;
                  for (Note n : s.getNotesList()) {
                     writer.write(noteNum + ". " + n +"\n");
                     noteNum++;
                  }
                  writer.write("\nTotal number of Notes: " + (noteNum + 1) + "\n");
                  
               }
               writer.close();

            } catch (IOException e) {
               System.out.println("Writing to .txt file failed.");
            }

            break;
         case 1:
         try {
            FileWriter writer = new FileWriter(newFile + type);
            StringBuilder line = new StringBuilder();
            for (Staff s : musicView.getItems()) {
               // Save staff info to CSV, so we can recognize it
               StringBuilder line2 = new StringBuilder();
               for (Note n : s.getNotesList()) {
                  line2.append(s.getStaffNum() +","+ n.typeString() + "," + n.lengthString() + "," + n.pitchString() + "," + n.getXCoord() + "," + n.getY2Coord() +"\n");
                  writer.write(line2.toString());
               }
            }
            writer.close();

         } catch (IOException e) {
            System.out.println("Writing to .txt file failed.");
         }
            break;
         default:
            break;

      }
  }

  /**
   * Read File Function
   * Allows the user to read the file that they wrote in the 
   * 'save file' operation.
   * Currently only supports .csv files
   * @param inputFile
   */
  public static void readFile(String inputFile) {
   File input = new File(inputFile + ".csv");
   try {
      Scanner scan = new Scanner(input);
      while (scan.hasNext()) {
         System.out.println(scan.next());
         // Staff, Type, Length, Pitch, XCoord, YCoord
         String[] noteInfo = scan.next().split(",");
         // int staffNum = Integer.parseInt(noteInfo[0]);
         try {
            int StaffNum = Integer.valueOf(noteInfo[0]); // Staff the note was on
            Note noteAdd = new Note(Note.pitchEnum.valueOf(noteInfo[3]), Note.noteType.valueOf(noteInfo[1]), Note.noteLengthCalc(noteInfo[2]));
            items.get(StaffNum).getNotesList().add(noteAdd);
            try {
               noteAdd.setXCoord(Integer.valueOf(noteInfo[4]) - noteAdd.getPositionX());
            } catch (NumberFormatException e) {
               System.out.println("Error XCoord");
            }

            try {
               noteAdd.setYCoord(Integer.valueOf(noteInfo[5]) - noteAdd.getPositionY());
            } catch (NumberFormatException e) {
               System.out.println("Error YCoord");
            }
            
         } catch (NumberFormatException e) {
            System.out.println("Error.");

         }
         
      }
      scan.close();
   } catch (IOException e) {
      System.out.println("File does not exist.");
   }
  }

  /*
   * Allows the user to clear the window.
   * Removes all notes from visible staves.
   */
  public void clear() {
   for (Staff s : items) {
      for (Note n : s.getNotesList()) {
         s.getNotesList().remove(n);
         n.setIsSelected(false); // de-select it
         n.setOnlyAccSelect(false); // remove that the acc is selected, just in case
         this.setDragNote(null); // remove it from drag
      }
   }
  }

  public static void setSendStaff(Staff s) {
      sendStaff = s;
  }

  public static Note getKaraoke() {
      return karaoke;
  }

  public static void setKaraoke(Note n) {
      karaoke = n;
  }

  public void karaokeMode() {
      /* Preparing for the animation */
      complete.clear();
      animation.clear();
      animationStaff.clear();

      /* Assembling the needed notes and staff lists */
      for (Staff s : musicView.getItems()) {
         s.sortStaff();
         for (Note n : s.getNotesList()) {
            animation.add(n);
            animationStaff.add(s);
            n.setAni_Length(n.getFullLength());
         }
      }

      noteIndex = 0;
      between = 10;
      starX = 10;
      starY = 10;

      /* Initializing the Timer */
      timer = new Timer(100, new ActionListener() {
         public void actionPerformed(ActionEvent ae) {
            /* Time to assemble the Animation */

            if (complete.size() != animation.size()) {
               musicEditor.setBottomMessage("Animation currently playing...");
               if (animation.get(noteIndex).getAni_Length() >= 0) { 
                  /* duration of note */
                  // n.getYCoord() - n.getPositionY();
                  starX = animation.get(noteIndex).getXCoord();
                  animation.get(noteIndex).highlight();
                  //divisionY = (animationStaff.get(noteIndex).getyVal1() - 20) - (animation.get(noteIndex).getYCoord() - animation.get(noteIndex).getPositionY());

                  if (animation.get(noteIndex).getAni_Length() >= (animation.get(noteIndex).getFullLength())) {
                     starY = animationStaff.get(noteIndex).getThreshold() - 15;
                  } else if (animation.get(noteIndex).getAni_Length() >= (animation.get(noteIndex).getFullLength() / 2)) {
                     starY += 1 * animation.get(noteIndex).getSkip();
                  } else {
                     starY -= 1 * animation.get(noteIndex).getSkip();
                  }

                  animation.get(noteIndex).deduct();
                  System.out.println("Playing " + animation.get(noteIndex));
                  System.out.println(animation.get(noteIndex).getAni_Length() + " " + animation.get(noteIndex).getFullLength());
                  repaint();

               } else if (noteIndex + 1 < animation.size() && between >= 0) {
                  /* moving between two notes, 40 steps */
                  if (animationStaff.get(noteIndex) == animationStaff.get(noteIndex + 1)) {
                     /* moving x of star between notes */
                     starX = animation.get(noteIndex).getXCoord() + (10 - between) * (((animation.get(noteIndex + 1).getXCoord()) - (animation.get(noteIndex).getXCoord())) / 10);
                     animation.get(noteIndex).unhighlight();
                     //starY = animationStaff.get(noteIndex).getThreshold() - 15;
                     if (between <= 5) {
                        starY += 2;
                        repaint();
                     } else {
                        starY -= 2;
                        repaint();
                     }
                     repaint();
                     
                  } else {
                     /* If the next note is on another staff, shorten between time */
                     between -= 4;
                     repaint();

                  }
                  between--;
                  System.out.println("Moving between notes");
                  repaint();

               } else {
                  /* move to next note, 0 steps */

                  between = 10;
                  animation.get(noteIndex).unhighlight();
                  System.out.println(noteIndex);
                  complete.add(animation.get(noteIndex));
                  System.out.println("Increment");
                  noteIndex++;
                  repaint();

               }
               
            } else {
               starX = -20;
               starY = -20;
               for (Note n : animation) {
                  n.unhighlight();
               }
               timer.stop();
               musicEditor.toolPlay.setEnabled(true);
               repaint();
               musicEditor.setBottomMessage("Animation ended.");
            }
         }
      });
      timer.start();
  }

  public void stopKaraokeMode() {
      timer.stop();
      starX = -20;
      starY = -20;
      for (Note n : animation) {
         n.unhighlight();
      }
      musicEditor.setBottomMessage("Animation stopped.");
      repaint();
  }




}
