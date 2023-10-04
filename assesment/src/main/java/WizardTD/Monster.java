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

    private double pixelX;
    private double pixelY;
    
    private double pixSpeed;
    private double maxHealth; // double
    private double currHealth; // double
    private double armour;
    private App app;
    private double healthProp;
    private boolean alive = true;
    private boolean exists = true;
    private int deathTick = 0;
    private int tileNo = 0;
    private ArrayList<Direction> route = new ArrayList<>();
    private PImage sprite;
    private int moves;
    private double manaOnKill;
    private int tileX;
    private int tileY;

    public Monster(
        int tileX, int tileY, double pixSpeed, double maxHealth, 
        double armour, ArrayList<Direction> route, App app, double manaOnKill
        ){
        for(Direction dir: route){
            this.route.add(dir);
        } // copy route as to not edit reference
        
        this.tileX = tileX;
        this.tileY = tileY;
        this.pixSpeed = pixSpeed;
        this.maxHealth = maxHealth;
        this.currHealth = maxHealth;
        this.armour = armour;
        this.app = app;
        this.manaOnKill = manaOnKill;
        this.pixelX = tileX * CELLSIZE + ghostShiftX;
        this.pixelY = tileY * CELLSIZE + ghostShiftY + TOPBAR;
        this.sprite = app.loadImage("src/main/resources/WizardTD/gremlin.png");

        this.route.add(0, this.route.get(0)); // duplicate the first element
        this.spawnShift();

        System.out.println("Created " + this);
    }

    public boolean getExists(){
        return this.exists;
    }

    public double getPixelX(){
        return this.pixelX;
    }

    public double getPixelY(){
        return this.pixelY;
    }

    public void spawnShift(){ // shift mosnter so it spawns off screen
        switch(this.route.get(0)){
            case UP:
                this.pixelY += CELLSIZE;
                break;
            case DOWN:
                this.pixelY -= CELLSIZE;
                break;
            case LEFT:
                this.pixelX += CELLSIZE;
                break;
            case RIGHT:
                this.pixelX -= CELLSIZE;
                break;
            case NONE:
                // do nothing
                break;
        } 
    }

    public void takeDamage(double damage){ // remember armour
        this.currHealth -= damage * this.armour;
        System.out.println("Did " + damage + " damage to " + this);
    }

    public void move(){
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
        } else if(this.tileNo >= this.route.size() && this.alive){

            // take me back to the beginning
            this.pixelX = tileX * CELLSIZE + ghostShiftX;
            this.pixelY = tileY * CELLSIZE + ghostShiftY + TOPBAR;
            this.spawnShift();
            this.tileNo = 0;
            this.moves = 0;

            // deduct mana and potentially lose
            if(!app.map.getMana().updateMana(-1 * this.currHealth)){
                app.map.getMana().makeManaZero(); // deduct all mana
                app.onLossScreen = true;
            }
        }
    }

    @Override
    public String toString(){
        return this.currHealth + " hp monster at (" + this.tileX + ", " + this.tileY + ")";
    }

    public void tick()
    {
        // poison
        if(this.app.map.getPoison()){
            this.takeDamage(this.app.poisonDamage * this.app.rate); // influenced by armour
        }
        
        // health
        this.healthProp = this.currHealth / this.maxHealth;

        for(int i = 0; i < app.rate; i++){
            if (this.alive && this.currHealth <= 0){
            this.alive = false;
            app.map.getMana().updateMana(manaOnKill);
            } // kill monster based on rate

            this.move();
        } // move monster based on rate
        

        // kill animation
        if(!this.alive){
            this.deathTick += app.rate; // kill animation twice as fast
            if(this.deathTick > 20){
                this.exists = false; // will be deleted from spawn array
            } else if(this.deathTick > 16){
                this.sprite = app.loadImage("src/main/resources/WizardTD/gremlin5.png"); 
            } else if(this.deathTick > 12){
                this.sprite = app.loadImage("src/main/resources/WizardTD/gremlin4.png");
            } else if(this.deathTick > 8){
                this.sprite = app.loadImage("src/main/resources/WizardTD/gremlin3.png");
            } else if(this.deathTick > 4){
                this.sprite = app.loadImage("src/main/resources/WizardTD/gremlin2.png");
            } else if(this.deathTick > 0){
                this.sprite = app.loadImage("src/main/resources/WizardTD/gremlin1.png");
            }            
        }
    }
    
    public void draw(PApplet app){
        // monster sprite
        app.image(this.sprite, (float)this.pixelX, (float)this.pixelY);

        // health bar
        if(this.alive){ // health bar only displays if alive
            app.noStroke(); // no border
            app.fill(0, 255, 0); // green bit
            app.rect((float)this.pixelX + healthShiftX, (float)this.pixelY + healthShiftY, (int) (healthLength * healthProp), healthWidth);
            
            app.fill(255, 0, 0); // red bit
            app.rect(
                (float)(this.pixelX + healthShiftX + (healthLength * healthProp)), 
                (float)(this.pixelY + healthShiftY), 
                (float) (healthLength * (1 - healthProp)), 
                healthWidth);
    
        }
    }
}
