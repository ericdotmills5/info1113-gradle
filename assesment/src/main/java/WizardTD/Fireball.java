package WizardTD;

import processing.core.PImage;

public class Fireball implements Exists, Draw, Tick {
    private int pixelX;
    private int pixelY;
    private PImage sprite;
    private Monster target;
    private double damage;
    private boolean exists;
    
    /**
     * Constructor for Fireball
     * @param x x-coordinate of top lkeft of sprite
     * @param y y-coordinate of top left of sprite
     * @param target monster to target
     * @param damage damage to deal to target
     * @param app App object
     */
    public Fireball(int x, int y, Monster target, double damage, App app) {
        this.pixelX = x + App.FIREBALL_RADIUS; // define position based 
        this.pixelY = y + App.FIREBALL_RADIUS; // on center of sprite
        this.target = target;
        this.damage = damage;
        this.sprite = app.loadImage("src/main/resources/WizardTD/fireball.png");
        this.becomeExistant();
    }

    public void becomeExistant() {
        this.exists = true;
    }

    /**
     * used to check if fireball should be removed
     * @return false if fireball should be removed
     */
    public boolean exists() {
        return this.exists;
    }

    /**
     * Move towards target and check/deal damage if hit
     * @param inputApp app object to get general info about game
     */
    public void tick(App inputApp) {
        double targetCentreX = this.target.getPixelX() + App.SPRITE_SHIFT;
        double targetCentreY = this.target.getPixelY() + App.SPRITE_SHIFT;
        
        // move towards target
        this.pixelX += App.PROJ_SPEED / App.scalarDistance(
            this.pixelX, this.pixelY, 
            targetCentreX, targetCentreY
            ) * (targetCentreX - this.pixelX);

        this.pixelY += App.PROJ_SPEED / App.scalarDistance(
            this.pixelX, this.pixelY, 
            targetCentreX, targetCentreY
            ) * (targetCentreY - this.pixelY);

        // check if hit target
        if (App.scalarDistance(
            this.pixelX, this.pixelY, 
            targetCentreX, targetCentreY
            ) <= App.MONSTER_RADIUS) {
            this.target.takeDamage(this.damage);
            this.exists = false;
        }
    }

    /**
     * Draw fireball sprite
     * @param inputApp App object to draw with
     */
    public void draw(App inputApp) {
        inputApp.image(this.sprite, this.pixelX, this.pixelY);
    }
}
