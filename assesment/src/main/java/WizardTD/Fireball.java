package WizardTD;

import processing.core.PImage;

public class Fireball{
    private int pixelX;
    private int pixelY;
    private PImage sprite;
    private App app;
    private Monster target;
    private double damage;
    private boolean exists = true;
    
    public Fireball(int x, int y, Monster target, double damage, App app)
    {
        this.pixelX = x + App.FIREBALL_RADIUS; // define position based 
        this.pixelY = y + App.FIREBALL_RADIUS; // on center of sprite
        this.app = app;
        this.target = target;
        this.damage = damage;
        this.sprite = app.loadImage("src/main/resources/WizardTD/fireball.png");
    }

    public boolean getExists() {
        return this.exists;
    }

    public void tick() {
        // move towards target
        double targetCentreX = this.target.getPixelX() + App.SPRITE_SHIFT;
        double targetCentreY = this.target.getPixelY() + App.SPRITE_SHIFT;
        
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

    public void draw() {
        this.app.image(this.sprite, this.pixelX, this.pixelY);
    }
}
