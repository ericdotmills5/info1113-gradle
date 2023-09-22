package WizardTD;

import java.util.ArrayList;
import processing.core.PApplet;
import processing.core.PImage;

public class Monster {
    public static final int CELLSIZE = App.CELLSIZE;

    private int pixelX;
    private int pixelY;
    private int tileX;
    private int tileY;
    private int pixSpeed;
    private int tileSpeed;
    private ArrayList<Direction> route;
    private PImage sprite;

    public Monster(int tileX, int tileY, int tileSpeed, ArrayList<Direction> route, App app){
        this.tileSpeed = tileSpeed;
        this.pixSpeed = tileSpeed * Tile.CELLSIZE / App.FPS;
        this.tileX = tileX;
        this.tileY = tileY;
        this.route = route;
        this.pixelX = tileX * CELLSIZE;
        this.pixelY = tileY * CELLSIZE;
        this.sprite = app.loadImage("src/main/resources/WizardTD/gremlin.png");

        System.out.println("Monster created at " + this.tileX + ", " + this.tileY);
    }

    public void tick(){
        if(this.route.size() > 0){
            switch(this.route.get(0)){
                case UP:
                    this.pixelY -= this.pixSpeed;
                    break;
                case DOWN:
                    this.pixelY += this.pixSpeed;
                    break;
                case LEFT:
                    this.pixelX -= this.pixSpeed;
                    break;
                case RIGHT:
                    this.pixelX += this.pixSpeed;
                    break;
                case NONE:
                    System.out.println("Monster has no route");
            }
            if(this.pixelX % CELLSIZE == 0 && this.pixelY % CELLSIZE == 0){
                this.tileX = this.pixelX / CELLSIZE;
                this.tileY = this.pixelY / CELLSIZE;
                this.route.remove(0);
            }
        }
    }

    public void draw(PApplet app){
        app.image(this.sprite, this.pixelX, this.pixelY);
    }
}
