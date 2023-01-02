import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.util.Hashtable;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.Timer;


/**
 * Music Editor Application
 * CS 4470
 * By Julia Elise Polo
 */
public class musicEditor implements ChangeListener {

    JFrame mainFrame;
    /**
     * Menu Bar Items
     */
    JMenuBar menuBar;
    JMenu file;
    JMenu edit;
    JMenuItem fileExit;
    JMenuItem fileSave;
    JMenuItem fileOpen;
    JMenuItem editNewStaff;
    JMenuItem editDeleteStaff;

    /**
     * Bottom Label
     */
    static JLabel bottomLabel;

    /**
     * Tool Panel Items pt. 1: Buttons
     */
    JPanel toolPanel;
    JPanel overallPanel;
    JPanel selectAndPen;
    JPanel newStaffAndDeleteStaff;
    JPanel playAndStop;
    JButton toolSelect;
    static JButton toolPen;
    JButton toolNewStaff;
    JButton toolDeleteStaff;
    static JButton toolPlay;
    JButton toolStop;

    /**
     * Tool Panel Icons
     */
    ImageIcon selectIcon;
    ImageIcon penIcon;
    ImageIcon newIcon;
    ImageIcon deleteIcon;
    ImageIcon playIcon;
    ImageIcon stopIcon;

    /**
     * Tool Panel Items pt. 2: Slider + Radio Buttons
     */
    JPanel toolPanel2;
    JPanel radioButtons;
    ButtonGroup noteTypes;
    JPanel notesPanel;
    JRadioButton NoteButton;
    JRadioButton Rest;
    JRadioButton Flat;
    JRadioButton Sharp;
    JSlider noteLength;
    Hashtable labelTable;

    /**
     * Content Area Items
     */
    JScrollPane mainContent;
    JLabel contentLabel;

    /**
     * GLobal Variables 
     */
    int numStaves = 4;
    static int sliderVal = 0;
    Note cursor;
    static int noteTypeInt = 0;
    static Boolean selectModeOn = false;
    static Boolean penModeOn = false;
    static Boolean playing = false;
    static String bottomMessage = "";
    static Boolean accidentalModeOn = false;
    static Note karaoke = null;
    static Timer timer;


    /**
     * Constructor for the Application.
     */
    public musicEditor() {
        musicView window = new musicView();
        window.addMouseListener(new staffMouseListener());
        window.addMouseListener(window);
        // initialize the frame
        mainFrame = new JFrame("My Music Editor");
        mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        // initialize the borderLayout
        mainFrame.getContentPane().setLayout(new BorderLayout());

        // initialize the menu bar
        menuBar = new JMenuBar();
        file = new JMenu("File");
        edit = new JMenu("Edit");
        menuBar.add(file);
        menuBar.add(edit);
        // adding to the file submenu
        fileExit = new JMenuItem("Exit");
        fileSave = new JMenu("Save File");
        JMenuItem saveCSV = new JMenuItem("Save as CSV");
        JMenuItem saveTXT = new JMenuItem("Save as TXT");
        JMenuItem fileClear = new JMenuItem("Clear");
        fileSave.add(saveCSV);
        fileSave.add(saveTXT);
        fileOpen = new JMenuItem("Open File");
        file.add(fileExit);
        file.add(fileSave);
        file.add(fileOpen);
        file.add(fileClear);
        fileExit.addActionListener(e -> System.exit(0));
        fileClear.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                window.clear();
            }
        });
        saveCSV.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String fileName = JOptionPane.showInputDialog(mainFrame,
                        "What would you like to name the file?", null);
                musicView.saveFile(fileName, 1);
            }
        });
        saveTXT.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String fileName = JOptionPane.showInputDialog(mainFrame,
                        "What would you like to name the file?", null);
                musicView.saveFile(fileName, 0);
            }
        });
        fileOpen.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String fileName = JOptionPane.showInputDialog(mainFrame,
                        "What file would you like to read?\nWe only support .CSV files.", null);
                musicView.readFile(fileName);
            }
        });
        // adding to the edit submenu
        editNewStaff = new JMenuItem("New Staff");
        editNewStaff.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                bottomLabel.setText("New Staff Button (Menu) Clicked.");
                numStaves++;
                if (numStaves > 1) {
                    editDeleteStaff.setEnabled(true);
                    toolDeleteStaff.setEnabled(true);
                } else {
                    editDeleteStaff.setEnabled(false);
                    toolDeleteStaff.setEnabled(false);
                }
                contentLabel.setText("Number of Staves: " + numStaves);
                window.addToMusicView(new Staff());
                mainFrame.setPreferredSize(new Dimension(1000, 400 + numStaves*(50)));
                window.repaint();
            }
        });
        editDeleteStaff = new JMenuItem("Delete Staff");
        editDeleteStaff.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                bottomLabel.setText("Delete Staff (Menu) Button Clicked.");
                if (numStaves > 1) {
                    numStaves--;
                    editDeleteStaff.setEnabled(true);
                    window.removeStaff();
                    mainFrame.setPreferredSize(new Dimension(1000, 400 + numStaves*(50)));
                    window.repaint();
                } else {
                    editDeleteStaff.setEnabled(false);
                    toolDeleteStaff.setEnabled(false);
                }
                contentLabel.setText("Number of Staves: " + numStaves);
            }
        });
        edit.add(editNewStaff);
        edit.add(editDeleteStaff);

        // initializing the bottom label
        bottomLabel = new JLabel("Welcome to My Music Editor");
        mainFrame.getContentPane().add(bottomLabel, BorderLayout.PAGE_END);
        bottomLabel.setHorizontalAlignment(JLabel.CENTER);
        bottomLabel.setVerticalAlignment(JLabel.CENTER);

        // initalizing the tool menu
        toolPanel = new JPanel();
        toolPanel.setLayout(new BoxLayout(toolPanel, BoxLayout.PAGE_AXIS));
        // combining each set of buttons
        // Select and Pen Buttons
        selectAndPen = new JPanel();
        selectAndPen.setLayout(new FlowLayout(FlowLayout.CENTER));
        selectIcon = createImageIcon("images/selectButton.png");
        toolSelect = new JButton("Select", selectIcon);
        toolSelect.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (selectModeOn) {
                    selectModeOn = false;
                    penModeOn = false;
                    window.repaint();
                    bottomLabel.setText("Select Mode False.");
                    toolSelect.setOpaque(false);
                    toolSelect.setBackground(Color.WHITE);
                } else {
                    selectModeOn = true;
                    accidentalModeOn = false;
                    bottomLabel.setText("Select Mode True.");
                    toolSelect.setOpaque(true);
                    toolSelect.setBackground(Color.GREEN);
                    toolPen.setOpaque(false);
                    toolPen.setBackground(Color.WHITE);
                }
                penModeOn = false;
                window.requestFocusInWindow();
            }
        });

        penIcon = createImageIcon("images/penButton.png");
        toolPen = new JButton("Pen", penIcon);
        toolPen.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                bottomLabel.setText("Pen Button Clicked.");
                if (penModeOn) {
                    penModeOn = false;
                    selectModeOn = false;
                    window.repaint();
                    toolSelect.setOpaque(false);
                    toolPen.setOpaque(false);
                    toolPen.setBackground(Color.WHITE);
                } else {
                    penModeOn = true;
                    selectModeOn = false;
                    toolSelect.setOpaque(false);
                    toolSelect.setBackground(Color.WHITE);
                    window.repaint();
                    toolPen.setOpaque(true);
                    toolPen.setBackground(Color.RED);
                }
                window.requestFocusInWindow();
            }
        });
        selectAndPen.add(toolSelect);
        selectAndPen.add(toolPen);

        // New Staff and Delete Staff Buttons
        newStaffAndDeleteStaff = new JPanel();
        newStaffAndDeleteStaff.setLayout(new FlowLayout(FlowLayout.CENTER));
        newIcon = createImageIcon("images/newButton.png");
        toolNewStaff = new JButton("New Staff", newIcon);
        toolNewStaff.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                bottomLabel.setText("New Staff Button Clicked.");
                numStaves++;
                if (numStaves > 1) {
                    toolDeleteStaff.setEnabled(true);
                    editDeleteStaff.setEnabled(true);
                }
                contentLabel.setText("Number of Staves: " + numStaves);
                window.addToMusicView(new Staff());
                mainFrame.setPreferredSize(new Dimension(1000, 400 + numStaves*(50)));
                window.repaint();
                window.requestFocusInWindow();
            }
        });

        deleteIcon = createImageIcon("images/deleteButton.png");
        toolDeleteStaff = new JButton("Delete Staff", deleteIcon);
        toolDeleteStaff.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                bottomLabel.setText("Delete Staff Button Clicked.");
                numStaves--;
                if (numStaves == 1) {
                    editDeleteStaff.setEnabled(false);
                    toolDeleteStaff.setEnabled(false);
                }
                window.removeStaff();
                mainFrame.setPreferredSize(new Dimension(1000, 400 + numStaves*(50)));
                window.repaint();
                contentLabel.setText("Number of Staves: " + numStaves);
                window.requestFocusInWindow();
            }
        });
        newStaffAndDeleteStaff.add(toolNewStaff);
        newStaffAndDeleteStaff.add(toolDeleteStaff);

        // Play and Stop Buttons
        playAndStop = new JPanel();
        playAndStop.setLayout(new FlowLayout(FlowLayout.CENTER));
        playIcon = createImageIcon("images/playButton.png");
        toolPlay = new JButton("Play", playIcon);
        toolPlay.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                bottomLabel.setText("Play Button Clicked.");
                penModeOn = false;
                for (Staff s : musicView.getItems()) {
                    s.sortStaff();
                }
                /* Lets begin animating ! */
                toolPlay.setEnabled(false);
                window.karaokeMode();
                window.requestFocusInWindow();
            }
        });

        stopIcon = createImageIcon("images/pauseButton.png");
        toolStop = new JButton("Stop", stopIcon);
        toolStop.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                bottomLabel.setText("Stop Button Clicked.");
                penModeOn = false;
                window.requestFocusInWindow();
                window.stopKaraokeMode();
                toolPlay.setEnabled(true);
            }
        });
        playAndStop.add(toolPlay);
        playAndStop.add(toolStop);

        // adding the buttons and seperators to the top panel
        toolPanel.add(new JSeparator());
        toolPanel.add(selectAndPen);
        toolPanel.add(new JSeparator());
        toolPanel.add(newStaffAndDeleteStaff);
        toolPanel.add(new JSeparator());
        toolPanel.add(playAndStop);
        toolPanel.add(new JSeparator());

        // assembling the lower tool area
        // creating the Radio Buttons
        NoteButton = new JRadioButton("Note");
        NoteButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                turnPenModeOff();
                bottomLabel.setText("Note selected.");
                noteTypeInt = 1;
                accidentalModeOn = false;
                window.requestFocusInWindow();
            }
        });

        Rest = new JRadioButton("Rest");
        Rest.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                turnPenModeOff();
                bottomLabel.setText("Rest selected.");
                noteTypeInt = 2;
                accidentalModeOn = false;
                window.requestFocusInWindow();
            }
        });

        Flat = new JRadioButton("Flat");
        Flat.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                turnPenModeOff();
                bottomLabel.setText("Flat selected.");
                accidentalModeOn = true;
                noteTypeInt = 3;
                System.out.println("Accidental mode on.");
                window.requestFocusInWindow();
            }
        });

        Sharp = new JRadioButton("Sharp");
        Sharp.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                turnPenModeOff();
                bottomLabel.setText("Sharp selected.");
                accidentalModeOn = true;
                noteTypeInt = 4;
                System.out.println("Accidental mode on.");
                window.requestFocusInWindow();
            }
        });

        // Button Group
        noteTypes = new ButtonGroup();
        noteTypes.add(NoteButton); // only creates a logical group, NOT a panel/group
        noteTypes.add(Rest);
        noteTypes.add(Flat);
        noteTypes.add(Sharp);

        // organizing the new panels
        notesPanel = new JPanel();
        toolPanel2 = new JPanel(); // bottom half panel
        notesPanel.setLayout(new BoxLayout(notesPanel, BoxLayout.PAGE_AXIS));
        notesPanel.add(NoteButton); // add to panel
        notesPanel.add(Rest);
        notesPanel.add(Flat);
        notesPanel.add(Sharp);
        toolPanel2.add(notesPanel);

        // adding the slider
        noteLength = new JSlider(JSlider.VERTICAL, 1, 5, 1);
        labelTable = new Hashtable();
        noteLength.setPaintTicks(true);
        noteLength.setMajorTickSpacing(1);
        noteLength.addChangeListener(this);
        // adding labels to the slider
        labelTable.put((Integer) 1, new JLabel("Whole"));
        labelTable.put((Integer) 2, new JLabel("Half"));
        labelTable.put((Integer) 3, new JLabel("Quarter"));
        labelTable.put((Integer) 4, new JLabel("Eigth"));
        labelTable.put((Integer) 5, new JLabel("Sixteenth"));
        noteLength.setLabelTable(labelTable);
        noteLength.setPaintLabels(true);
        toolPanel2.add(noteLength);

        // Combining both panels to form the entire tool panel
        overallPanel = new JPanel();
        overallPanel.setLayout(new BoxLayout(overallPanel, BoxLayout.PAGE_AXIS));
        overallPanel.add(toolPanel);
        overallPanel.add(toolPanel2);

        // assembling the main content area
        contentLabel = new JLabel("Number of Staves: " + numStaves, JLabel.CENTER);
        for (int i = numStaves; i > 0; i--) {
            window.addToMusicView(new Staff());
        }

        mainContent = new JScrollPane(window, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
        mainContent.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        mainContent.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
        
        // assembling the Window!
        mainFrame.getContentPane().add(overallPanel, BorderLayout.WEST);
        mainFrame.getContentPane().add(mainContent, BorderLayout.CENTER);
        mainFrame.getContentPane().add(bottomLabel, BorderLayout.PAGE_END);
        mainFrame.setPreferredSize(new Dimension(1000, 400 + numStaves*(50)));
        mainFrame.setMinimumSize(mainFrame.getPreferredSize());
        mainFrame.setVisible(true);
        mainFrame.setJMenuBar(menuBar);
        window.requestFocusInWindow();
        mainFrame.pack();
        mainFrame.show();
    }
    
    /** Returns an ImageIcon, or null if the path was invalid. */
    // Taken and editted from the Oracle Java Documentation,
    // takes in image path and returns an Image Icon
private ImageIcon createImageIcon(String path) {
    java.net.URL imageURL = getClass().getResource(path);
    if (imageURL != null) {
        return new ImageIcon(imageURL);
    } else { // handling error
        System.out.println("Couldn't find image: " + path);
        return null;
    }
}
    /**
     * Main function to display the GUI.
     */
    public static void main(String[] args) {
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                musicEditor m = new musicEditor();
            }
        });
    }

    /*
     * Takes the value from the slider and
     * converts it to an int, which can be used
     * to identify length of notes.
     */
    public void stateChanged(ChangeEvent e) {
        JSlider source = (JSlider)e.getSource();
        if (!source.getValueIsAdjusting()) {
            sliderVal = (int)source.getValue();
            bottomLabel.setText("Value of slider is " + sliderVal);
        }
    }

    public static int getSliderVal() {
        return sliderVal;
    }

    public static int getNoteTypeInt() {
        return noteTypeInt;
    }

    public static Boolean getSelectModeOn() {
        return selectModeOn;
    }

    public static Boolean getPenModeOn() {
        return penModeOn;
    }

    public static void setBottomMessage(String s) {
        bottomLabel.setText(s);
    }

    public static Boolean getAccidentalModeOn() {
        return accidentalModeOn;
    }

    public static void turnPenModeOff() {
        penModeOn = false;
        toolPen.setOpaque(false);
        toolPen.setBackground(Color.WHITE);
    }

}
