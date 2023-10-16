package WizardTD;

import processing.core.PApplet;

public class Wizard extends WizOrPath {
    private static final String spritePath = "src/main/resources/WizardTD/wizard_house.png";
    /**
     * Wizard is basically special case of path because:
     * 1. It has a wizard distance of 0 (thus not optimal direction)
     * 2. It has a cool sprite
     * @param x tile x coordinate [0, 19]
     * @param y tile y coordinate [0, 19]
     * @param map map it is generated from
     */
    public Wizard(int x, int y, Map map) {
        super(x, y, map, Wizard.spritePath);
        this.wizDist = 0;
        this.optimal = Direction.NONE;
    }
    /**
     * Draw function needs to be overriden because wizard house needs to be shifted
     */
    @Override
    public void draw(PApplet app) {
        app.image(
            this.sprite, this.x * CELLSIZE + App.WIZ_SHIFT_X, 
            this.y * CELLSIZE + App.WIZ_SHIFT_Y + App.TOPBAR
        ); // wizard house needs to be shifted
    }
}
