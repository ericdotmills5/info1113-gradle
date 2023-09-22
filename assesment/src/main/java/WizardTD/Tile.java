package WizardTD;

import processing.core.PApplet;
import processing.core.PImage;

abstract class Tile{
    public static final int CELLSIZE = App.CELLSIZE;
    public static final int wizShiftX = -8; // 8 pixels left
    public static final int wizShiftY = -5; // 5 pixels up

    protected int x;
    protected int y;
    protected Map map;
    protected PImage sprite;

    public Tile(int x, int y, Map map){
        this.x = x;
        this.y = y;
        this.map = map;
    }

    public String toString(){
        return this.getClass().getName() + " @ x=" + this.x + ", y=" + this.y;
    }

    public void tick(){

    }

    public void draw(PApplet app){
        app.image(this.sprite, this.x * CELLSIZE, this.y * CELLSIZE);
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
