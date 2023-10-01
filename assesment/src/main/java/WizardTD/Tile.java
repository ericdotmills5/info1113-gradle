package WizardTD;

import processing.core.PApplet;
import processing.core.PImage;

abstract class Tile{
    public static final int CELLSIZE = App.CELLSIZE;
    public static final int wizShiftX = App.wizShiftX; // 8 pixels left
    public static final int wizShiftY = App.wizShiftY; // 5 pixels up
    public static final int TOPBAR = App.TOPBAR; // 40 pixels down

    protected int x; // tile coordinates
    protected int y;
    protected Map map;
    protected PImage sprite;

    public Tile(int x, int y, Map map){
        this.x = x;
        this.y = y;
        this.map = map;
    }

    public int getX(){
        return this.x;
    }

    public int getY(){
        return this.y;
    }

    public String toString(){
        return this.getClass().getName() + " @ x=" + this.x + ", y=" + this.y;
    }

    public void draw(PApplet app){
        app.image(this.sprite, this.x * CELLSIZE, this.y * CELLSIZE + TOPBAR);
    }
}

class Shrub extends Tile{
    public Shrub(int x, int y, Map map){
        super(x, y, map);
        this.sprite = map.getApp().loadImage("src/main/resources/WizardTD/shrub.png");
    }
}

class Grass extends Tile{
    public Grass(int x, int y, Map map){
        super(x, y, map);
        this.sprite = map.getApp().loadImage("src/main/resources/WizardTD/grass.png");
    }
}
