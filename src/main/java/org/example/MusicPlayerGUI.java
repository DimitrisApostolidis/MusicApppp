package org.example;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;

public class MusicPlayerGUI extends JFrame {

    public static final Color FRAME_COLOR = Color.BLACK;
    public static final Color FRAME_TEXT = Color.WHITE;


    public MusicPlayerGUI() {
        super("Music Player");
        setSize(400, 600);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);
        setLayout(null);
        getContentPane().setBackground(FRAME_COLOR);
        addGuiComponents();
    }
    private void addGuiComponents() {
        addToolbar();

        JLabel songImage = new JLabel(loadImage("src/main/java/assets/record.png")); //load song image
        songImage.setBounds(0, 50, getWidth() - 20, 225);
        add(songImage);

        JLabel songTitle = new JLabel("Song Title"); //Song Name
        songTitle.setBounds(0, 285, getWidth() - 10, 30);
        songTitle.setFont(new Font("Dialog", Font.BOLD, 24));
        songTitle.setForeground(FRAME_TEXT);
        songTitle.setHorizontalAlignment(SwingConstants.CENTER);
        add(songTitle);

        JLabel songArtist = new JLabel("Artist"); //Artist Name
        songArtist.setBounds(0, 315, getWidth() - 10, 30);
        songArtist.setFont(new Font("Dialog", Font.PLAIN, 24));
        songArtist.setForeground(FRAME_TEXT);
        songArtist.setHorizontalAlignment(SwingConstants.CENTER);
        add(songArtist);

        JSlider playbackSlider = new JSlider(JSlider.HORIZONTAL, 0, 100, 0); //Playback slider
        playbackSlider.setBounds(getWidth()/2 - 300/2, 365, 300, 40);
        playbackSlider.setBackground(null);
        add(playbackSlider);

        addPlaybackBtns();
    }
    private void addToolbar() {
        JToolBar toolbar = new JToolBar(); //ToolBar
        toolbar.setBounds(0, 0, getWidth(), 20);
        toolbar.setFloatable(false);

        JMenuBar menuBar = new JMenuBar(); // Δημιουργία του JMenuBar
        toolbar.add(menuBar); // Προσθήκη του JMenuBar στο toolbar

        JMenu songMenu = new JMenu("Song"); // Δημιουργία του "Song" menu
        menuBar.add(songMenu); // Προσθήκη του Song menu στο menuBar

        JMenu loadMenu = new JMenu("Load"); // Δημιουργία του "Load" menu
        songMenu.add(loadMenu); // Προσθήκη του Load στο Song menu

        JMenu playListMenu = new JMenu("Play List"); // Δημιουργία του "Play List" menu
        loadMenu.add(playListMenu); // Προσθήκη του Play List στο Load menu

        JMenuItem createPlaylist = new JMenuItem("Create Playlist"); // Δημιουργία του Create Playlist menu item
        playListMenu.add(createPlaylist); // Προσθήκη του στο Play List menu

        JMenuItem loadPlaylist = new JMenuItem("Load Playlist"); // Δημιουργία του Load Playlist menu item
        playListMenu.add(loadPlaylist); // Προσθήκη του στο Play List menu

        add(toolbar);
    }

    private void addPlaybackBtns() {
        JPanel playbackBtns = new JPanel();
        playbackBtns.setBounds(0, 435, getWidth() - 10, 80);
        playbackBtns.setBackground(null);

        JButton prevButton = new JButton(loadImage("src/main/java/assets/previous.png")); //Previous Button
        prevButton.setBorderPainted(false);
        prevButton.setBackground(null);
        playbackBtns.add(prevButton);

        JButton playButton = new JButton(loadImage("src/main/java/assets/play.png")); //Play Button
        playButton.setBorderPainted(false);
        playButton.setBackground(null);
        playbackBtns.add(playButton);

        JButton pauseButton = new JButton(loadImage("src/main/java/assets/pause.png")); //Pause Button
        pauseButton.setBorderPainted(false);
        pauseButton.setBackground(null);
        pauseButton.setVisible(false);
        playbackBtns.add(pauseButton);

        JButton nextButton = new JButton(loadImage("src/main/java/assets/next.png")); //Next Button
        nextButton.setBorderPainted(false);
        nextButton.setBackground(null);
        playbackBtns.add(nextButton);

        add(playbackBtns);

    }

    private ImageIcon loadImage(String imagePath) {
        try{
            BufferedImage image = ImageIO.read(new File(imagePath));
            return new ImageIcon(image);
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }
}
