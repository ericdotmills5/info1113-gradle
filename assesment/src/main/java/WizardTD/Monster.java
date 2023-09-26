package WizardTD;

import java.util.ArrayList;
import processing.core.PApplet;
import processing.core.PImage;

public class Monster {
    public static final int CELLSIZE = App.CELLSIZE;
    public static final int ghostShiftX = App.ghostShiftX; // 5 pixels right
    public static final int ghostShiftY = App.ghostShiftY; // 5 pixels down
    public static final int healthShiftX = App.healthShiftX; // 5 pixels left
    public static final int healthShiftY = App.healthShiftY; // 6 pixels up
    public static final int healthLength = App.healthLength; // 30 pixels long
    public static final int healthWidth = App.healthWidth; // 5 pixels wide
    public static final int TOPBAR = App.TOPBAR; // 40 pixels down

    private int pixelX;
    private int pixelY;
    
    private int pixSpeed;
    private int maxHealth;
    private int currHealth;
    private double armour;
    private App app;
    private double healthProp;
    private boolean alive = true;
    private boolean exists = true;
    private int deathTick = 0;
    private int tileNo = 0;
    private ArrayList<Direction> route;
    private PImage sprite;
    private int moves;
    private int dir;

    public Monster(
        int tileX, int tileY, int pixSpeed, int maxHealth, double armour, ArrayList<Direction> route, App app
        ){
        this.pixSpeed = pixSpeed;
        
        this.route = route;
        this.maxHealth = maxHealth;
        this.currHealth = maxHealth;
        this.armour = armour;
        this.app = app;
        this.pixelX = tileX * CELLSIZE + ghostShiftX;
        this.pixelY = tileY * CELLSIZE + ghostShiftY + TOPBAR;
        this.sprite = app.loadImage("src/main/resources/WizardTD/gremlin.png");

        System.out.println("Monster created at " + tileX + ", " + tileY);
    }

    public boolean getExists(){
        return this.exists;
    }

    public void takeDamage(){ // remember armour

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
                    break;
            } 
            this.moves += 1;
            double difference = this.pixSpeed * this.moves - CELLSIZE;

            if(difference >= 0){
                switch(this.route.get(tileNo)){ // take off difference based on direction
                    case UP:
                        this.pixelY += difference;
                        break;
                    case DOWN:
                        this.pixelY -= difference;
                        break;
                    case LEFT:
                        this.pixelX += difference;
                        break;
                    case RIGHT:
                        this.pixelX -= difference;
                        break;
                    case NONE:
                        break; // do nothing
                }
                this.tileNo++;
                this.moves = 0; // reset pixels
            } // if monster has moved a full tile, move to next direction
            // after moving to next tile (in pixels), update tile position, round back if needed
            /*
            if((this.pixelX - ghostShiftX) >= CELLSIZE * (this.tileX + 1) || 
                
               
               (this.pixelY - ghostShiftY - TOPBAR) >= CELLSIZE * (this.tileY + 1)){
               
                this.tileX = this.pixelX / CELLSIZE; // update tile position
                this.tileY = this.pixelY / CELLSIZE;

                this.tileNo++; // follow next direction
                
                
                this.pixelX = this.tileX * CELLSIZE + ghostShiftX; // round back to tile
                this.pixelY = this.tileY * CELLSIZE + ghostShiftY + TOPBAR;
                
            } 
            */
        }

        // kill animation
        if(!this.alive){
            this.deathTick += 1;
            switch(this.deathTick){
                case 4:
                    this.sprite = app.loadImage("src/main/resources/WizardTD/gremlin1.png");
                    break;
                case 8:
                    this.sprite = app.loadImage("src/main/resources/WizardTD/gremlin2.png");
                    break;
                case 12:
                    this.sprite = app.loadImage("src/main/resources/WizardTD/gremlin3.png");
                    break;
                case 16:
                    this.sprite = app.loadImage("src/main/resources/WizardTD/gremlin4.png");
                    break;
                case 20:
                    this.sprite = app.loadImage("src/main/resources/WizardTD/gremlin5.png");
                    break;
                case 24:
                    this.exists = false; // will be deleted from spawn array
                    break;
                
            }
        }
    }

    public void draw(PApplet app){
        // monster sprite
        app.image(this.sprite, this.pixelX, this.pixelY);

        // health bar
        if(this.alive){ // health bar only displays if alive
            app.fill(0, 255, 0); // green bit
            app.rect(this.pixelX + healthShiftX, this.pixelY + healthShiftY, (int) (healthLength * healthProp), healthWidth);
            
            app.fill(255, 0, 0); // red bit
            app.rect(
                this.pixelX + healthShiftX + (int) (healthLength * healthProp), 
                this.pixelY + healthShiftY, 
                (int) (healthLength * (1 - healthProp)), 
                healthWidth);
    
        }
    }
}
