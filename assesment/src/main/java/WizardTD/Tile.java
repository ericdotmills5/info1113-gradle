package WizardTD;

import processing.core.PImage;

abstract class Tile implements Draw {
    
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

    /**
     * get tile x co ordinate
     * @return x cordinate [0,19]
     */
    public int getX() {
        return this.x;
    }

    /**
     * get tile y co ordinate
     * @return y cordinate [0,19]
     */
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
     * @param inputApp need app to draw with
     */
    public void draw(App inputApp) {
        inputApp.image(this.sprite, this.x * App.CELLSIZE, this.y * App.CELLSIZE + App.TOPBAR);
    }
}




