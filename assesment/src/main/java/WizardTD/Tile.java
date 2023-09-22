package WizardTD;

import processing.core.PApplet;
import processing.core.PImage;

abstract class Tile{
    public static final int tileSize = 32;
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

    public void tick(){

    }

    public void draw(PApplet app){
        app.image(this.sprite, this.x * tileSize, this.y * tileSize);
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
