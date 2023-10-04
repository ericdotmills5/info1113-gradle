package WizardTD;

import processing.core.PApplet;
import processing.core.PImage;
import processing.data.JSONObject;
//import processing.data.JSONArray;
//import processing.data.JSONObject;
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
    public static final int LOSTX = 240;
    public static final int LOSTY = 227;
    public static final int BUTTONX = CELLSIZE * BOARD_WIDTH + 6;
    public static final int BUTTONSIZE = 42; // square
    public static final int BUTTONSPACING = 10;
    public static final int BUTTONTEXT0SIZE = 25;
    public static final int BUTTONTEXT12SIZE = 12;
    public static final int BUTTONTEXTSHIFTX = 5;
    public static final int BUTTONTEXT0SHIFTY = 30;
    public static final int BUTTONTEXT1SHIFTY = 15;
    public static final int BUTTONTEXT2SHIFTY = BUTTONTEXT1SHIFTY + 20;
    public static final int BUTTONHOVERLENGTH = 72; // assume 4 digit price max
    public static final int BUTTONHOVERHEIGHT = 20;
    public static final int BUTTONHOVERX = CELLSIZE * BOARD_WIDTH - BUTTONHOVERLENGTH - 7;
    public static final int BUTTONHOVERTEXTX = BUTTONHOVERX + 4;
    public static final int BUTTONHOVERTEXTSIZE = BUTTONTEXT12SIZE;
    public static final int BUTTONHOVERTEXTSHIFTY = BUTTONTEXT1SHIFTY;
    public static final int TOWERFIRSTUPGRADESHIFTX = 5;
    public static final int TOWERFIRSTUPGRADESHIFTY = 2;
    public static final int RANGEUPGRADEDIAMETER = 6;
    public static final int TOWERFIRSTUPGRADEDMGSHIFTY = 25;
    public static final int TOWERUPGRADECIRCLEDIST = 2;
    public static final int TOWERUPGRADECROSSDIST = 5;
    public static final int TOWERDAMAGECROSSLENGTHX = RANGEUPGRADEDIAMETER - 3;
    public static final int TOWERDAMAGECROSSLENGTHY = RANGEUPGRADEDIAMETER;
    public static final int TOWERSPEEDSQUARESHIFT = 5;
    public static final int TOWERSPEEDSQUARELENGTH = 20;
    public static final int UPGRADEBUBBLEX = CELLSIZE * BOARD_WIDTH + 10;
    public static final int UPGRADEBUBBLEY = 16 * CELLSIZE + TOPBAR;
    public static final int UPGRADEBUBBLELENGTH = BUTTONSIZE * 2;
    public static final int UPGRADEBUBBLEHEIGHT = BUTTONSIZE / 2;
    public static final int UPGRADEBUBBLETEXTSIZE = 12;
    public static final int UPGRADEBUBBLETEXTSHIFTX = 3;
    public static final int UPGRADEBUBBLETEXTSHIFTY = UPGRADEBUBBLEHEIGHT / 2 + 5;
    public static final int SPRITESHIFT = ghostShiftX + 10; // sprite is 20x20 pixels
    public static final int PROJSPEED = 5;
    public static final int MONSTERRADIUS = 20 / 2; // monster is 20x20 pixels
    public static final int FIREBALLRADIUS = 6 / 3; // fireball is 6x6 pixels
    public static final char[] buttons = {' ', 'F', 'P', 'T', '1', '2', '3', 'M', 'P'};
    public static final int NUMBEROFBUTTONS = buttons.length - 1; // exclude 1st space bar


    public static int WIDTH = CELLSIZE*BOARD_WIDTH+SIDEBAR;
    public static int HEIGHT = BOARD_WIDTH*CELLSIZE+TOPBAR;

    public static final int FPS = 60;
    public int doubleRate = 1; // 2 or 1
    public int pauseRate = 1; // 1 or 0
    public int rate = doubleRate * pauseRate;
    public boolean onWinScreen = false;
    public boolean onLossScreen = false;

    public String configPath;
    public JSONObject config;
    public static String lvlLoc;
    public Map map;
    public Ui ui;
    public Iterable<String> mapIterable;

    public double poisonCost;
    public double poisonFrames;
    public double poisonDamage;
    
    public Monster monster; // testing

    public Random random = new Random();
	
    // Feel free to add any additional methods or attributes you want. Please put classes in different files.

    public App() {
        
    }

    /**
     * Initialise the setting of the window size.
     */
	@Override
    public void settings() {
        size(WIDTH, HEIGHT); 
        // glitched, probably due to window decorations on windows
    }

    /**
     * Load all resources such as images. Initialise the elements such as the player, enemies and map elements.
     */
	@Override
    public void setup() {
        this.configPath = "config.json";
        this.config = loadJSONObject(this.configPath);
        this.poisonCost = this.config.getDouble("poison_cost");
        this.poisonFrames = this.config.getDouble("poison_time") * FPS;
        this.poisonDamage = this.config.getDouble("poison_damage_per_second");


        frameRate(FPS);
        Scanner scan = fileIO(this.loadJSONObject(this.configPath).getString("layout"));
        this.mapIterable = scan2Iterable(scan);
        scan.close();
        this.map = new Map(this.mapIterable, this);
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

    static Iterable<String> scan2Iterable(Scanner scan){ // read lines from scanner obj
        ArrayList<String> lines = new ArrayList<String>();
        while(scan.hasNextLine()){
            lines.add(scan.nextLine());
        }
        return lines;
    }

    static double scalarDistance(double x1, double y1, double x2, double y2){
        return Math.sqrt(Math.pow(x1-x2, 2) + Math.pow(y1-y2, 2));
    }

    /**
     * Receive key pressed signal from the keyboard.
     */
	@Override
    public void keyPressed(){

        if(this.onLossScreen){
            if(keyCode == 'R'){
                this.onLossScreen = false;
                this.map = new Map(this.mapIterable, this);
                this.ui = new Ui(this.map);
            }
        } else if(this.onWinScreen){
            // not allowed to press bellow buttons
        } else {
            for(int i = 1; i < App.buttons.length; i++){
                if(keyCode == App.buttons[i]){
                    this.ui.toggleSwitch(this, i);
                }
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
    public void mousePressed(MouseEvent e) 
    { // hover over grey + cost
        for (int buttonNO = 1; buttonNO <= App.NUMBEROFBUTTONS; buttonNO++)
        {
            if (isMouseOverButton(buttonNO))
            {
                this.ui.toggleSwitch(this, buttonNO);
            }
        }
        this.ui.click(this);
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        
    }
    
    public void mouseHover() {
        for (int buttonNO = 1; buttonNO <= App.NUMBEROFBUTTONS; buttonNO++){
            if (isMouseOverButton(buttonNO))
            {
                this.ui.setHoveredButton(buttonNO, true);
            } else
            {
                this.ui.setHoveredButton(buttonNO, false);
            }
        }
    }

    public boolean isMouseOverButton(int buttonNO){
        return (mouseX > BUTTONX &&
                mouseX < BUTTONX + BUTTONSIZE &&
                mouseY > TOPBAR + buttonNO * BUTTONSPACING + (buttonNO-1)*BUTTONSIZE &&
                mouseY < buttonNO * (BUTTONSPACING + BUTTONSIZE) + TOPBAR);
    }

    /**
     * Draw all elements in the game by current frame.
     */
	@Override
    public void draw() {
        if(!this.onLossScreen && !this.onWinScreen){
            // tick
            this.map.tick();
            this.ui.tick();
            this.mouseHover();

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
        } 
        if(this.onLossScreen) {
            this.fill(0, 255, 0);
            this.textSize(35);
            this.text("YOU LOST", LOSTX, LOSTY);
            this.textSize(20);
            this.text("Press 'r' to restart", LOSTX - 7, LOSTY + 30);
        } 
        if (this.onWinScreen) {
            this.fill(255, 0, 255);
            this.textSize(35);
            this.text("YOU WIN", LOSTX, LOSTY);
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
