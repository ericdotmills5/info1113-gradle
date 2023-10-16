package WizardTD;

import processing.core.PApplet;
import processing.core.PImage;
import processing.data.JSONObject;
//import processing.data.JSONArray;
//import processing.data.JSONObject;
import processing.event.MouseEvent;
import java.nio.file.Files;
import java.nio.file.Paths;

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
    public static final int WIZ_SHIFT_X = -8; // 8 pixels left
    public static final int WIZ_SHIFT_Y = -5; // 5 pixels up
    public static final int GHOST_SHIFT_X = 5; // 5 pixels right
    public static final int GHOST_SHIFT_Y = 5; // 5 pixels down
    public static final int HEALTH_SHIFT_X = -5; // 5 pixels left
    public static final int HEALTH_SHIFT_Y = -5; // 6 pixels up
    public static final int HEALTH_LENGTH = 30; // 30 pixels long
    public static final int HEALTH_WIDTH = 2; // 5 pixels tall
    public static final int MANA_X = 375;
    public static final int MANA_Y = 10;
    public static final int MANA_LENGTH = 320;
    public static final int MANA_WIDTH = 18;
    public static final int MANA_TEXT_X = MANA_X - 60;
    public static final int MANA_TEXT_Y = MANA_Y + 16;
    public static final int MANA_CURR_SHIFT = 173;
    public static final int LOST_X = 240;
    public static final int LOST_Y = 227;
    public static final int BUTTON_X = CELLSIZE * BOARD_WIDTH + 6;
    public static final int BUTTON_SIZE = 42; // square
    public static final int BUTTON_SPACING = 10;
    public static final int BUTTON_TEXT_0_SIZE = 25;
    public static final int BUTTON_TEXT_12_SIZE = 12;
    public static final int BUTTON_TEXT_SHIFT_X = 5;
    public static final int BUTTON_TEXT_0_SHIFT_Y = 30;
    public static final int BUTTON_TEXT_1_SHIFT_Y = 15;
    public static final int BUTTON_TEXT_2_SHIFT_Y = BUTTON_TEXT_1_SHIFT_Y + 20;
    public static final int BUTTON_HOVER_LENGTH = 72; // assume 4 digit price max
    public static final int BUTTON_HOVER_HEIGHT = 20;
    public static final int BUTTON_HOVER_X = CELLSIZE * BOARD_WIDTH - BUTTON_HOVER_LENGTH - 7;
    public static final int BUTTON_HOVER_TEXT_X = BUTTON_HOVER_X + 4;
    public static final int BUTTON_HOVER_TEXT_SIZE = BUTTON_TEXT_12_SIZE;
    public static final int BUTTON_HOVER_TEXT_SHIFT_Y = BUTTON_TEXT_1_SHIFT_Y;
    public static final int TOWER_FIRST_UPGRADE_SHIFT_X = 5;
    public static final int TOWER_FIRST_UPGRADE_SHIFT_Y = 2;
    public static final int RANGE_UPGRADE_DIAMETER = 6;
    public static final int TOWER_FIRST_UPGRADE_DMG_SHIFT_Y = 25;
    public static final int TOWER_UPGRADE_CIRCLE_DIST = 2;
    public static final int TOWER_UPGRADE_CROSS_DIST = 5;
    public static final int TOWER_DAMAGE_CROSS_LENGTH_X = RANGE_UPGRADE_DIAMETER - 3;
    public static final int TOWER_DAMAGE_CROSS_LENGTH_Y = RANGE_UPGRADE_DIAMETER;
    public static final int TOWER_SPEED_SQUARE_SHIFT = 5;
    public static final int TOWER_SPEED_SQUARE_LENGTH = 20;
    public static final int UPGRADE_BUBBLE_X = CELLSIZE * BOARD_WIDTH + 10;
    public static final int UPGRADE_BUBBLE_Y = 16 * CELLSIZE + TOPBAR;
    public static final int UPGRADE_BUBBLE_LENGTH = BUTTON_SIZE * 2;
    public static final int UPGRADE_BUBBLE_HEIGHT = BUTTON_SIZE / 2;
    public static final int UPGRADE_BUBBLE_TEXT_SIZE = 12;
    public static final int UPGRADE_BUBBLE_TEXT_SHIFT_X = 3;
    public static final int UPGRADE_BUBBLE_TEXT_SHIFT_Y = UPGRADE_BUBBLE_HEIGHT / 2 + 5;
    public static final int SPRITE_SHIFT = GHOST_SHIFT_X + 10; // sprite is 20x20 pixels
    public static final int PROJ_SPEED = 5;
    public static final int MONSTER_RADIUS = 20 / 2; // monster is 20x20 pixels
    public static final int FIREBALL_RADIUS = 6 / 3; // fireball is 6x6 pixels
    public static final char[] BUTTONS = {' ', 'F', 'P', 'T', '1', '2', '3', 'M', '4'};
    public static final int NUMBER_OF_BUTTONS = BUTTONS.length - 1; // exclude 1st space bar
    public static final int FPS = 60;

    public static int WIDTH = CELLSIZE*BOARD_WIDTH+SIDEBAR;
    public static int HEIGHT = BOARD_WIDTH*CELLSIZE+TOPBAR;

    public int doubleRate = 1; // 2 or 1
    public int pauseRate = 1; // 1 or 0
    public int rate = doubleRate * pauseRate;
    public boolean onWinScreen = false;
    public boolean onLossScreen = false;
    public String configPath;
    public JSONObject config;
    public Map map;
    public Ui ui;
    public Iterable<String> mapIterable;

    public double poisonCost;
    public double poisonFrames;
    public double poisonDamage;
    
    public Monster monster; // testing

    public Random random = new Random();

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
     * Load all resources such as images. 
     * Initialise the elements such as the player, enemies and map elements.
     */
	@Override
    public void setup() {
        frameRate(FPS);
        this.configPath = "config.json";
        this.config = readJSON(configPath);
        this.createStuff();
    }

    /**
     * initialise map and ui classes.
     */
    public void createStuff() {
        if (this.config.hasKey("poison_cost")) {
            this.poisonCost = this.config.getDouble("poison_cost");
        } else {
            this.poisonCost = 100;
        }
        if (this.config.hasKey("poison_time")) {
            this.poisonFrames = this.config.getDouble("poison_time") * FPS;
        } else {
            this.poisonFrames = 5 * FPS;
        }
        if (this.config.hasKey("poison_damage_per_second")) {
            this.poisonDamage = this.config.getDouble("poison_damage_per_second");
        } else {
            this.poisonDamage = 1;
        }
        // default values in case these fields DNE

        // read map
        Scanner scan = fileIO(this.config.getString("layout"));
        this.mapIterable = scan2Iterable(scan);
        scan.close();

        // initialise map and ui classes
        this.map = new Map(this.mapIterable, this, this.config);
        this.ui = new Ui(this.map);
    }

    /**
     * Read file into scanner object. Used to read map file.
     * @param loc String of file location.
     * @return Scanner object of file.
     */
    static Scanner fileIO(String loc) { 
        File f = new File(loc);
        Scanner scan;
        try {
            scan = new Scanner(f);
        } catch (FileNotFoundException e) {
            System.out.println("File not found!");
            return null;
        }
        return scan;
    }

    /**
     * Read JSON file into JSONObject object.
     * @param path String of file location.
     * @return JSONObject object of file.
     */
    static JSONObject readJSON(String path) {
        String json = "";
        try {
            json = new String(Files.readAllBytes(Paths.get(path)));
        } catch (IOException e) {
            System.out.println("File not found!");
            return null;
        }
        return JSONObject.parse(json);
    }

    /**
     * Read scanner object into iterable object. Used to read map file.
     * @param scan Scanner object of file, use fileIO() to get.
     * @return Iterable object of file of read scanner.
     */
    static Iterable<String> scan2Iterable(Scanner scan) {
        ArrayList<String> lines = new ArrayList<String>();
        while (scan.hasNextLine()) {
            lines.add(scan.nextLine());
        }
        return lines;
    }

    /**
     * Calculate distance between two points.
     * @param x1 x coordinate of first point.
     * @param y1 y coordinate of first point.
     * @param x2 x coordinate of second point.
     * @param y2 y coordinate of second point.
     * @return double distance between two points.
     */
    static double scalarDistance(double x1, double y1, double x2, double y2) {
        return Math.sqrt(Math.pow(x1-x2, 2) + Math.pow(y1-y2, 2));
    }

    /**
     * Receive key pressed signal from the keyboard.
     */
	@Override
    public void keyPressed() {

        if (this.onLossScreen) {
            if (this.keyCode == 'R') {
                this.onLossScreen = false;
                this.doubleRate = 1;
                this.pauseRate = 1;
                this.rate = doubleRate * pauseRate;
                this.map = new Map(this.mapIterable, this, this.config);
                this.ui = new Ui(this.map);
                
            }
        } else if (this.onWinScreen) {
            // not allowed to press bellow BUTTONS
        } else {
            for(int i = 1; i < App.BUTTONS.length; i++) {
                if (this.keyCode == App.BUTTONS[i]) {
                    this.ui.toggleSwitch(this, i);
                }
            }
        }
    }

    /**
     * Receive key released signal from the keyboard.
     */
	@Override
    public void keyReleased() {

    }

    /**
     * Receive mouse pressed signal from the mouse.
     * Used to indicate clicking to ui class.
     */
    @Override
    public void mousePressed(MouseEvent e) 
    { // hover over grey + cost
        for (int buttonNO = 1; buttonNO <= App.NUMBER_OF_BUTTONS; buttonNO++)
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
    
    /**
     * tells ui class which button is being hovered over
     */
    public void mouseHover() {
        for (int buttonNO = 1; buttonNO <= App.NUMBER_OF_BUTTONS; buttonNO++) {
            if (isMouseOverButton(buttonNO))
            {
                this.ui.setHoveredButton(buttonNO, true);
            } else
            {
                this.ui.setHoveredButton(buttonNO, false);
            }
        }
    }

    /**
     * Check if mouse is over a button. Based on x and y coordinates of mouse.
     * Assumes BUTTONS are in a column with equally spaced entries.
     * @param buttonNO int of button number.
     * @return boolean of whether mouse is over button.
     */
    public boolean isMouseOverButton(int buttonNO) {
        return (this.mouseX > BUTTON_X &&
                this.mouseX < BUTTON_X + BUTTON_SIZE &&
                this.mouseY > TOPBAR + buttonNO * BUTTON_SPACING + (buttonNO-1)*BUTTON_SIZE &&
                this.mouseY < buttonNO * (BUTTON_SPACING + BUTTON_SIZE) + TOPBAR);
    }

    public void tick() {
        this.map.tick();
        this.ui.tick();
        this.mouseHover();
    }

    /**
     * Draw all elements in the game by current frame.
     */
	@Override
    public void draw() {
        if (!this.onLossScreen && !this.onWinScreen) {
            // tick
            this.tick();

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
        if (this.onLossScreen) {
            this.fill(0, 255, 0);
            this.textSize(35);
            this.text("YOU LOST", LOST_X, LOST_Y);
            this.textSize(20);
            this.text("Press 'r' to restart", LOST_X - 7, LOST_Y + 30);
        } 
        if (this.onWinScreen) {
            this.fill(255, 0, 255);
            this.textSize(35);
            this.text("YOU WIN", LOST_X, LOST_Y);
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
