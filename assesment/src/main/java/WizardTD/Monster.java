package WizardTD;

import java.util.ArrayList;
import processing.core.PImage;

public class Monster implements Exists {
    private double pixelX;
    private double pixelY;
    private double pixSpeed;
    private double maxHealth; // double
    private double currHealth; // double
    private double armour;
    private App app;
    private double healthProp;
    private boolean alive = true;
    private boolean exists;
    private int deathTick = 0;
    private int tileNo = 0;
    private ArrayList<Direction> route = new ArrayList<>();
    private PImage sprite;
    private int moves;
    private double manaOnKill;
    private int tileX;
    private int tileY;
    private boolean firstTimeSpawning = true;

    /**
     * monster constructer
     * @param tileX x tile coordinate spawn
     * @param tileY y tile coordinate spawn
     * @param pixSpeed speed in pixels per frame
     * @param maxHealth initial health
     * @param armour armour multipler which resists damage
     * @param route list of directions to follow to wizard hut
     * @param app app object
     * @param manaOnKill mana to give player on kill
     */
    public Monster(
        int tileX, int tileY, double pixSpeed, double maxHealth, 
        double armour, ArrayList<Direction> route, App app, double manaOnKill
    ) {
        for(Direction dir: route) {
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
        this.pixelX = tileX * App.CELLSIZE + App.GHOST_SHIFT_X;
        this.pixelY = tileY * App.CELLSIZE + App.GHOST_SHIFT_Y + App.TOPBAR;
        this.sprite = app.loadImage("src/main/resources/WizardTD/gremlin.png");

        this.spawnShift();
        this.becomeExistant();

        System.out.println("Created " + this);
    }

    public void becomeExistant() {
        this.exists = true;
    }

    /**
     * used to check if monster object should be removed after death animation
     * @return false if monster should be removed
     */
    public boolean exists() {
        return this.exists;
    }

    /**
     * get x cordinate of monster in pixels
     * @return x cordinate of monster in pixels
     */
    public double getPixelX() {
        return this.pixelX;
    }

    /**
     * get y cordinate of monster in pixels
     * @return y cordinate of monster in pixels
     */
    public double getPixelY() {
        return this.pixelY;
    }

    /**
     * shift the monster off the screen when spawned
     */
    public void spawnShift() { // shift mosnter so it spawns off screen
        switch(((WizOrPath)this.app.map.getLand()[this.tileX][this.tileY]).getTerminals()[0]) {
            case UP:
                this.pixelY -= App.CELLSIZE;
                if (this.firstTimeSpawning) {
                this.route.add(0, Direction.DOWN);
                }
                break;
            case DOWN:
                this.pixelY += App.CELLSIZE;
                if (this.firstTimeSpawning) {
                this.route.add(0, Direction.UP);
                }
                break;
            case LEFT:
                this.pixelX -= App.CELLSIZE;
                if (this.firstTimeSpawning) {
                this.route.add(0, Direction.RIGHT);
                }
                break;
            case RIGHT:
                this.pixelX += App.CELLSIZE;
                if (this.firstTimeSpawning) {
                this.route.add(0, Direction.LEFT);
                }
                break;
            case NONE:
                System.out.println("Monster spawned on non terminal path!");
                break;
        } 
        this.firstTimeSpawning = false;
    }

    /**
     * deduct health from monster according to armour
     * @param damage damage to deal to monster before armour
     */
    public void takeDamage(double damage) { // remember armour
        this.currHealth -= damage * this.armour;
        System.out.println("Did " + damage + " damage to " + this);
    }

    /**
     * Does 3 things: 
     * 1. Move monster according to speed and direction in list
     * 2. round monster to nearest tile 
     * 3. check if monster has reached wizard hut and deduct mana/lose game
     */
    public void move() {
        if (this.tileNo < this.route.size() && this.alive) { // if directions are not empty
            switch(this.route.get(tileNo)) { // follow next direction
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
            double difference = this.pixSpeed * this.moves - App.CELLSIZE;

            if (difference >= 0) {
                switch(this.route.get(tileNo)) { // take off difference based on direction
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
        } else if (this.tileNo >= this.route.size() && this.alive) {

            // take me back to the beginning
            this.pixelX = tileX * App.CELLSIZE + App.GHOST_SHIFT_X;
            this.pixelY = tileY * App.CELLSIZE + App.GHOST_SHIFT_Y + App.TOPBAR;
            this.spawnShift();
            this.tileNo = 0;
            this.moves = 0;

            // deduct mana and potentially lose
            if (!this.app.map.getMana().updateMana(-1 * this.currHealth)) {
                this.app.map.getMana().makeManaZero(); // deduct all mana
                this.app.onLossScreen = true;
            }
        }
    }

    /**
     * change sprite throughout kill animation
     */
    public void changeSpriteDuringKillAnimation() {
        this.deathTick += this.app.rate; // kill animation twice as fast if ff, stops if pasued
            if (this.deathTick > 20) {
                this.exists = false; // will be deleted from spawn array
            } else if (this.deathTick > 16) {
                this.sprite = this.app.loadImage("src/main/resources/WizardTD/gremlin5.png");
            } else if (this.deathTick > 12) {
                this.sprite = this.app.loadImage("src/main/resources/WizardTD/gremlin4.png");
            } else if (this.deathTick > 8) {
                this.sprite = this.app.loadImage("src/main/resources/WizardTD/gremlin3.png");
            } else if (this.deathTick > 4) {
                this.sprite = this.app.loadImage("src/main/resources/WizardTD/gremlin2.png");
            } else if (this.deathTick > 0) {
                this.sprite = this.app.loadImage("src/main/resources/WizardTD/gremlin1.png");
            }     
    }

    /**
     * collates details of monster for debugging
     * @return details of monster as a string
     */
    @Override
    public String toString() {
        return this.currHealth + " hp monster at (" + this.tileX + ", " + this.tileY + ")";
    }

    /**
     * tick function for monster: 
     * 1. damage monster if poisoned
     * 2. update health proportion for draw method
     * 3. move monster
     * 4. kill monster if health is 0
     * 5. update sprite throughout kill animation
     * @param inputApp app to get speed from and other information about game
     */
    public void tick(App inputApp)
    {
        // poison
        if (inputApp.map.getPoison()) {
            this.takeDamage(inputApp.map.getPoisonDamage() * inputApp.rate); // influenced by armour
        }
        
        // health
        this.healthProp = this.currHealth / this.maxHealth;

        for(int i = 0; i < inputApp.rate; i++) {
            if (this.alive && this.currHealth <= 0) {
            this.alive = false;
            inputApp.map.getMana().updateMana(manaOnKill);
            } // kill monster based on rate

            this.move();
        } // move monster based on rate
        
        // kill animation
        if (!this.alive) {
            this.changeSpriteDuringKillAnimation();   
        }
    }
    
    /**
     * draw monster sprite and health bar
     * @param inputApp app to draw with
     */
    public void draw(App inputApp) {
        // monster sprite
        inputApp.image(this.sprite, (float)this.pixelX, (float)this.pixelY);

        // health bar
        if (this.alive) { // health bar only displays if alive
            inputApp.noStroke(); // no border
            inputApp.fill(0, 255, 0); // green bit
            inputApp.rect(
                (float)this.pixelX + App.HEALTH_SHIFT_X, (float)this.pixelY + App.HEALTH_SHIFT_Y,
                (int) (App.HEALTH_LENGTH * healthProp), App.HEALTH_WIDTH
            );
            
            inputApp.fill(255, 0, 0); // red bit
            inputApp.rect(
                (float)(this.pixelX + App.HEALTH_SHIFT_X + (App.HEALTH_LENGTH * healthProp)),
                (float)(this.pixelY + App.HEALTH_SHIFT_Y), 
                (float) (App.HEALTH_LENGTH * (1 - healthProp)), 
                App.HEALTH_WIDTH
            );
    
        }
    }
}
