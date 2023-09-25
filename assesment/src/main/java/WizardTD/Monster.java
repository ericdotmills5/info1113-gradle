package WizardTD;

import java.util.ArrayList;
import processing.core.PApplet;
import processing.core.PImage;

public class Monster {
    public static final int CELLSIZE = App.CELLSIZE;
    public static final int ghostShiftX = App.ghostShiftX; // 5 pixels right
    public static final int ghostShiftY = App.ghostShiftY; // 5 pixels down
    public static final int healthShiftY = App.healthShiftY; // 6 pixels up
    public static final int healthLength = App.healthLength; // 30 pixels long
    public static final int healthWidth = App.healthWidth; // 5 pixels wide

    private int pixelX;
    private int pixelY;
    private int tileX;
    private int tileY;
    private int pixSpeed;
    private int maxHealth;
    private int currHealth;
    private App app;
    private double healthProp;
    private boolean alive = true;
    private boolean exists = true;
    private int deathTick = 0;
    private int tileNo = 0;
    private ArrayList<Direction> route;
    private PImage sprite;

    public Monster(
        int tileX, int tileY, int pixSpeed, int maxHealth, ArrayList<Direction> route, App app
        ){
        this.pixSpeed = pixSpeed;
        this.tileX = tileX;
        this.tileY = tileY;
        this.route = route;
        this.maxHealth = maxHealth;
        this.currHealth = maxHealth;
        this.app = app;
        this.pixelX = tileX * CELLSIZE;
        this.pixelY = tileY * CELLSIZE;
        this.sprite = app.loadImage("src/main/resources/WizardTD/gremlin.png");

        System.out.println("Monster created at " + this.tileX + ", " + this.tileY);
    }

    public boolean getExists(){
        return this.exists;
    }

    public void tick(){
        // health
        this.healthProp = (double) this.currHealth / (double) this.maxHealth;

        if (this.currHealth <= 0){
            this.alive = false;
        } // kill monster

        // movement
        if(this.tileNo < this.route.size() && this.alive){ // if directions are not empty
            switch(this.route.get(tileNo)){ // follow next direction
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
            if(this.pixelX % CELLSIZE == 0 && this.pixelY % CELLSIZE == 0){ // every 32 pixels:
                this.tileX = this.pixelX / CELLSIZE; // update tile position
                this.tileY = this.pixelY / CELLSIZE;
                this.tileNo++; // follow next direction
            }
        }

        // kill animation
        if(!this.alive){
            this.deathTick += 1;
            switch(this.deathTick){
                case 1:
                    this.sprite = app.loadImage("src/main/resources/WizardTD/gremlin1.png");
                    break;
                case 2:
                    this.sprite = app.loadImage("src/main/resources/WizardTD/gremlin2.png");
                    break;
                case 3:
                    this.sprite = app.loadImage("src/main/resources/WizardTD/gremlin3.png");
                    break;
                case 4:
                    this.sprite = app.loadImage("src/main/resources/WizardTD/gremlin4.png");
                    break;
                default:
                    this.exists = false; // will be deleted from spawn array
                
            }
        }
    }

    public void draw(PApplet app){
        // monster sprite
        app.image(this.sprite, this.pixelX + ghostShiftX, this.pixelY + ghostShiftY);

        // health bar
        if(this.alive){ // health bar only displays if alive
            app.fill(0, 255, 0); // green bit
            app.rect(this.pixelX, this.pixelY + healthShiftY, (int) (healthLength * healthProp), healthWidth);
            
            app.fill(255, 0, 0); // red bit
            app.rect(this.pixelX + (int) (healthLength * healthProp), this.pixelY + healthShiftY, (int) (healthLength * (1 - healthProp)), healthWidth);
    
        }
    }
}
