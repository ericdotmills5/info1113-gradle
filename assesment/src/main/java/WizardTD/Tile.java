package WizardTD;

import processing.core.PApplet;
import processing.core.PImage;

abstract class Tile {
    public static final int CELLSIZE = App.CELLSIZE;
    public static final int WIZ_SHIFT_X = App.WIZ_SHIFT_X; // 8 pixels left
    public static final int WIZ_SHIFT_Y = App.WIZ_SHIFT_Y; // 5 pixels up
    public static final int TOPBAR = App.TOPBAR; // 40 pixels down

    protected int x; // tile coordinates
    protected int y;
    protected Map map;
    protected PImage sprite;

    /**
     * Constructor for abstract tile class
     * @param x x tile cordinates [0, 19]
     * @param y y tile cordinates [0, 19]
     * @param map map class it is generated from
     */
    public Tile(int x, int y, Map map, String spritePath) {
        this.x = x;
        this.y = y;
        this.map = map;
        this.sprite = map.getApp().loadImage(spritePath);
    }

    public int getX() {
        return this.x;
    }

    public int getY() {
        return this.y;
    }

    /**
     * @return String representation of tile (x, y) for debugging
     */
    public String toString() {
        return this.getClass().getName() + " @ x=" + this.x + ", y=" + this.y;
    }

    /**
     * Draws tile to screen, converting tile coordinates to pixel coordinates
     * @param app need app to draw with
     */
    public void draw(PApplet app) {
        app.image(this.sprite, this.x * CELLSIZE, this.y * CELLSIZE + TOPBAR);
    }
}

class Shrub extends Tile {
    private static final String spritePath = "src/main/resources/WizardTD/shrub.png";
    /**
     * Only unique thing about shrub is its sprite.
     * @param x x tile cordinates [0, 19]
     * @param y y tile cordinates [0, 19]
     * @param map map class it is generated from
     */
    public Shrub(int x, int y, Map map) {
        super(x, y, map, Shrub.spritePath);
    }
}

class Grass extends Tile{
    private static final String spritePath = "src/main/resources/WizardTD/grass.png";
    /**
     * Only unique thing about grass is its sprite.
     * @param x x tile cordinates [0, 19]
     * @param y y tile cordinates [0, 19]
     * @param map map class it is generated from
     */
    public Grass(int x, int y, Map map) {
        super(x, y, map, Grass.spritePath);
    }
}
