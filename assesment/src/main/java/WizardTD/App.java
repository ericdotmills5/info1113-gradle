package WizardTD;

import processing.core.PApplet;
import processing.core.PImage;
import processing.data.JSONArray;
import processing.data.JSONObject;
import processing.event.MouseEvent;

import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;

import java.io.*;
import java.util.*;

public class App extends PApplet {

    public static final int CELLSIZE = 32;
    public static final int SIDEBAR = 120;
    public static final int TOPBAR = 40;
    public static final int BOARD_WIDTH = 20;
    public static final int wizShiftX = -8; // 8 pixels left
    public static final int wizShiftY = -5; // 5 pixels up
    public static final int ghostShiftX = 5; // 5 pixels right
    public static final int ghostShiftY = 5; // 5 pixels down
    public static final int healthShiftX = -5; // 5 pixels left
    public static final int healthShiftY = -5; // 6 pixels up
    public static final int healthLength = 30; // 30 pixels long
    public static final int healthWidth = 2; // 5 pixels tall
    public static final int MANAX = 375;
    public static final int MANAY = 10;
    public static final int MANALENGTH = 320;
    public static final int MANAWIDTH = 18;
    public static final int MANATEXTX = MANAX - 60;
    public static final int MANATEXTY = MANAY + 16;
    public static final int MANACURRSHIFT = 173;

    public static int WIDTH = CELLSIZE*BOARD_WIDTH+SIDEBAR;
    public static int HEIGHT = BOARD_WIDTH*CELLSIZE+TOPBAR;

    public Scanner scan;
    public static final int FPS = 60;
    public int rate = 1;
    public boolean onLossScreen = false;

    public String configPath;
    public static String lvlLoc;
    public Map map;
    public Ui ui;
    

    public Monster monster; /// testing

    public Random random = new Random();
	
    // Feel free to add any additional methods or attributes you want. Please put classes in different files.

    public App() {
        this.configPath = "config.json";
    }

    /**
     * Initialise the setting of the window size.
     */
	@Override
    public void settings() {
        size(WIDTH, HEIGHT);
    }

    /**
     * Load all resources such as images. Initialise the elements such as the player, enemies and map elements.
     */
	@Override
    public void setup() {
        frameRate(FPS);

        this.scan = fileIO(this.loadJSONObject(this.configPath).getString("layout"));
        this.map = new Map(scan, this);
        this.ui = new Ui(this.map);

        /*
        Path hello = (Path)this.map.getRoutes().keySet().toArray()[2];

        this.monster = new Monster(hello.x, hello.y, 1, 100, this.map.getRoutes().get(hello), this);
        // put him on the right spawn path, preferably from hashmap
        */
        

        // Load images during setup
		// Eg:
        // loadImage("src/main/resources/WizardTD/tower0.png");
        // loadImage("src/main/resources/WizardTD/tower1.png");
        // loadImage("src/main/resources/WizardTD/tower2.png");

    }

    static Scanner fileIO(String loc){ // read file into scanner obj
        File f = new File(loc);
        Scanner scan;
        try{
            scan = new Scanner(f);
        } catch (FileNotFoundException e){
            System.out.println("File not found!");
            return null; // this might be dangerous
        }
        return scan;
    }

    /**
     * Receive key pressed signal from the keyboard.
     */
	@Override
    public void keyPressed(){

        if(this.onLossScreen){
            if(keyCode == 'R'){
                this.onLossScreen = false;
                this.map = new Map(scan, this);
                this.ui = new Ui(this.map);
            }
        } else if(keyCode == 'M'){
            this.map.getMana().clickPoolSpell();
        } else if(keyCode == 'F'){
            if(this.rate == 2){ // will ff even if paused
                this.rate = 1;
            }else{
                this.rate = 2;
            }
        } else if(keyCode == 'P'){ // will pause even if ff
            if(this.rate == 0){
                this.rate = 1;
            }else{
                this.rate = 0;
            }
        }
    }

    /**
     * Receive key released signal from the keyboard.
     */
	@Override
    public void keyReleased(){

    }

    @Override
    public void mousePressed(MouseEvent e) {
        
    }

    @Override
    public void mouseReleased(MouseEvent e) {

    }

    /*@Override
    public void mouseDragged(MouseEvent e) {

    }*/

    /**
     * Draw all elements in the game by current frame.
     */
	@Override
    public void draw() {
        if(!this.onLossScreen){
            // tick
            this.map.tick();
            this.ui.tick();

            // draw
            // map
            this.map.draw(this);

            // background
            this.noStroke();
            this.fill(131, 111, 75); // brown background
            this.rect(0, 0, WIDTH, TOPBAR); // top bar
            this.rect(CELLSIZE*BOARD_WIDTH, 0, SIDEBAR, HEIGHT); // side bar

            // ui
            this.ui.draw(this);
        } else{
            this.fill(0, 0, 0);
            this.textSize(50);
            this.text("YOU LOST", 100, 100);

            // press r to restart
        }
        
        
        
    

    }

    public static void main(String[] args) {
        PApplet.main("WizardTD.App");
    }

    /**
     * Source: https://stackoverflow.com/questions/37758061/rotate-a-buffered-image-in-java
     * @param pimg The image to be rotated
     * @param angle between 0 and 360 degrees
     * @return the new rotated image 
     */
    public PImage rotateImageByDegrees(PImage pimg, double angle) { // rotates clockwise
        BufferedImage img = (BufferedImage) pimg.getNative();
        double rads = Math.toRadians(angle);
        double sin = Math.abs(Math.sin(rads)), cos = Math.abs(Math.cos(rads));
        int w = img.getWidth();
        int h = img.getHeight();
        int newWidth = (int) Math.floor(w * cos + h * sin);
        int newHeight = (int) Math.floor(h * cos + w * sin);

        PImage result = this.createImage(newWidth, newHeight, RGB);
        //BufferedImage rotated = new BufferedImage(newWidth, newHeight, BufferedImage.TYPE_INT_ARGB);
        BufferedImage rotated = (BufferedImage) result.getNative();
        Graphics2D g2d = rotated.createGraphics();
        AffineTransform at = new AffineTransform();
        at.translate((newWidth - w) / 2, (newHeight - h) / 2);

        int x = w / 2;
        int y = h / 2;

        at.rotate(rads, x, y);
        g2d.setTransform(at);
        g2d.drawImage(img, 0, 0, null);
        g2d.dispose();
        for (int i = 0; i < newWidth; i++) {
            for (int j = 0; j < newHeight; j++) {
                result.set(i, j, rotated.getRGB(i, j));
            }
        }

        return result;
    }
}
