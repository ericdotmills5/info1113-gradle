package WizardTD;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;

public class Tower extends Tile implements Tick {
    private int rangeLevel; 
    private int firingSpeedLevel;
    private int damageLevel;
    private int lowestLevel;
    private double range; // pixel radius
    private double firingSpeed; // fireballs per second
    private double damage; // self explanatory
    private double initialTowerDamage;
    private int framesCounter = 0;
    private int centerX;
    private int centerY;
    private ArrayList<Fireball> projectiles = new ArrayList<Fireball>();
    private static final String spritePathToBeOveriden = "src/main/resources/WizardTD/tower0.png";

    /**
     * Constructor for Tower
     * @param x x tile coordinate [0, 19]
     * @param y y tile coordinate [0, 19]
     * @param initialRange initial pixel radius of tower
     * @param initialFiringSpeed initial fireballs per second
     * @param initialDamage initial damage per fireball
     * @param initialRangeLevel initial range level of tower as it was placed
     * @param initialFiringSpeedLevel initial firing speed level of tower as it was placed
     * @param initialDamageLevel initial damage level of tower as it was placed
     * @param map map it is generated from
     */
    Tower(int x, int y, double initialRange, 
          double initialFiringSpeed, double initialDamage, 
          boolean initialRangeLevel, boolean initialFiringSpeedLevel,
          boolean initialDamageLevel, Map map
    ) {
        super(x, y, map, Tower.spritePathToBeOveriden);
        this.centerX = x * App.CELLSIZE + App.CELLSIZE / 2;
        this.centerY = y * App.CELLSIZE + App.CELLSIZE / 2 + App.TOPBAR;
        this.range = initialRange;
        this.firingSpeed = initialFiringSpeed;
        // convert to fireballs per second to fireballs per frame
        this.damage = initialDamage;
        this.initialTowerDamage = initialDamage;

        if (initialRangeLevel) {
            this.upgradeRange();
        }
        if (initialFiringSpeedLevel) {
            this.upgradeFiringSpeed();
        }
        if (initialDamageLevel) {
            this.upgradeDamage();
        }
        System.out.println("Created: " + this);
    }

    /**
     * getter for range upgrade level
     * @return range upgrade level
     */
    public int getRangeLevel() {
        return this.rangeLevel;
    }

    /**
     * getter for firing speed upgrade level
     * @return firing speed upgrade level
     */
    public int getSpeedLevel() {
        return this.firingSpeedLevel;
    }

    /**
     * getter for damage upgrade level
     * @return damage upgrade level
     */
    public int getDamageLevel() {
        return this.damageLevel;
    }

    /**
     * getter for range upgrade cost
     * @return cost of range upgrade
     */
    public double getRangeCost() {
        return 20 + 10 * this.rangeLevel;
    }

    /**
     * getter for firing speed upgrade cost
     * @return cost of firing speed upgrade
     */
    public double getFiringSpeedCost() {
        return 20 + 10 * this.firingSpeedLevel;
    }

    /**
     * getter for damage upgrade cost
     * @return cost of damage upgrade
     */
    public double getDamageCost() {
        return 20 + 10 * this.damageLevel;
    }

    /**
     * getter for range in pixels
     * @return range in pixels
     */
    public double getRange() {
        return this.range;
    }

    /**
     * getter for firing speed in fireballs per second
     * @return firing speed in fireballs per second
     */
    public ArrayList<Fireball> getProjectiles() {
        return this.projectiles;
    }

    /**
     * loads appropriate sprite if the lowest level has changed
     */
    public void findLowestLevel(App inputApp) {
        if (this.rangeLevel <= this.firingSpeedLevel && this.rangeLevel <= this.damageLevel) {
            this.lowestLevel = this.rangeLevel;
        } else if (
            this.firingSpeedLevel <= this.rangeLevel && this.firingSpeedLevel <= this.damageLevel
        ) {
            this.lowestLevel = this.firingSpeedLevel;
        } else {
            this.lowestLevel = this.damageLevel;
        }
        if (this.lowestLevel > 2) {
            this.lowestLevel = 2;
        } // level 2 sprite is the lowest level sprite
        
        this.sprite = inputApp.loadImage(
            "src/main/resources/WizardTD/tower" + this.lowestLevel + ".png"
        );
    }   

    /**
     * upgrades range by 1 level
     */
    public void upgradeRange() {
        this.rangeLevel++;
        this.range += 32;
        System.out.println("upgraded range to " + this);
    }

    /**
     * upgrades firing speed by 1 level
     */
    public void upgradeFiringSpeed() {
        this.firingSpeedLevel++;
        this.firingSpeed += 0.5;
        System.out.println("upgraded speed to " + this);
    }

    /**
     * upgrades damage by 1 level
     */
    public void upgradeDamage() {
        this.damageLevel++;
        this.damage += this.initialTowerDamage / 2;
        System.out.println("upgraded damage to " + this);
    }

    /**
     * shoots a fireball at a random enemy in range in 3 steps:  
     * 1. creating a list of enemies in range
     * 2. randomly selecting one
     * 3. creating a fireball object targeting the random enemy
     */
    public void shoot(App inputApp) {
        // create list of enemies in range
        ArrayList<Monster> enemiesInRange = new ArrayList<Monster>();
        for (Wave wave: this.map.getWaves()) {
            for (Monster monster: wave.getMonsters()) {
                double spriteCentreX = monster.getPixelX() + App.SPRITE_SHIFT;
                double spriteCentreY = monster.getPixelY() + App.SPRITE_SHIFT;

                if (App.scalarDistance(
                    this.centerX, this.centerY, 
                    spriteCentreX, spriteCentreY
                    ) <= this.range) {
                    enemiesInRange.add(monster);
                }
            }
        }

        // only shoot if enemies in range exist
        if (enemiesInRange.size() > 0) {
            // randomly select one
            Random rand = new Random();
            int randIndex = rand.nextInt(enemiesInRange.size());
            Monster target = enemiesInRange.get(randIndex);

            // create fireball, targeting that enemy
            projectiles.add(new Fireball(
                this.centerX, this.centerY, target, this.damage, inputApp
            ));
            System.out.println("Shot fired");
        }
    }

    /**
     * collates all data about tower into a string for debugging
     * @return string of useful data about tower
     */
    @Override
    public String toString() {
        return this.rangeLevel + " " + this.firingSpeedLevel + " " + 
               this.damageLevel + " tower at (" + this.x + ", " + this.y + ")";
    }

    /**
     * tower shoots after delay, ticks all fireballs and removes fireball objects if theyve hit
     * @param inputApp app object to pass to fireballs shot
     */
    public void tick(App inputApp) {
        // shoot if enough frames have passed
        double framesPerFireball = App.FPS / this.firingSpeed;
        if (this.framesCounter > framesPerFireball) {
            this.shoot(inputApp);
            this.framesCounter = 0;
        }
        this.framesCounter += inputApp.rate;

        // tick and remove all fireballs
        // Iterator<Fireball> fireballIterator = this.projectiles.iterator();
        for (int i = this.projectiles.size() - 1; i >= 0; i--) {
            Fireball fireball = this.projectiles.get(i);
            fireball.tick(inputApp);

            if (!(fireball.exists())) {
                this.projectiles.remove(i);
            }
        }
    }

    /**
     * draws tower onto the screen and upgrade indicators
     * @param inputApp app object to draw with
     */
    @Override
    public void draw(App inputApp) {
        this.findLowestLevel(inputApp); // and figure out which sprite to use
        inputApp.image(this.sprite, this.x * App.CELLSIZE, this.y * App.CELLSIZE + App.TOPBAR);

        int tileX = this.x * App.CELLSIZE;
        int tileY = this.y * App.CELLSIZE + App.TOPBAR;
        inputApp.noFill();

        // fire rate square
        if (this.firingSpeedLevel - this.lowestLevel >= 1) { // only draw if upgraded past sprite
            inputApp.stroke(120, 180, 255); // light blue
            inputApp.strokeWeight((this.firingSpeedLevel - this.lowestLevel)* 2); 
            // stroke weight increases with level
            inputApp.rect(
                tileX + App.TOWER_SPEED_SQUARE_SHIFT, tileY + App.TOWER_SPEED_SQUARE_SHIFT,
                App.TOWER_SPEED_SQUARE_LENGTH, App.TOWER_SPEED_SQUARE_LENGTH
            );
        }

        // color + stroke weight setup for both dmg and range
        inputApp.stroke(255, 0, 255); // purple
        inputApp.strokeWeight(1);
        
        // range indicators
        for (int i = 0; i < this.rangeLevel - this.lowestLevel; i++) {
            inputApp.ellipse( // create above many circles
                tileX + App.TOWER_FIRST_UPGRADE_SHIFT_X + 
                i * (App.RANGE_UPGRADE_DIAMETER + App.TOWER_UPGRADE_CIRCLE_DIST),
                tileY + App.TOWER_FIRST_UPGRADE_SHIFT_Y, 
                App.RANGE_UPGRADE_DIAMETER, App.RANGE_UPGRADE_DIAMETER
            );
        } // each successive range upgrade indicator moves right

        // damage indicators
        for (int i = 0; i < this.damageLevel - this.lowestLevel; i++) {
            inputApp.line( // create above many crosses
                tileX + i * (App.TOWER_DAMAGE_CROSS_LENGTH_X + App.TOWER_UPGRADE_CROSS_DIST),
                tileY + App.TOWER_FIRST_UPGRADE_DMG_SHIFT_Y,
                tileX + App.TOWER_FIRST_UPGRADE_SHIFT_X +
                (i+1) * App.TOWER_DAMAGE_CROSS_LENGTH_X + i * App.TOWER_UPGRADE_CROSS_DIST,
                tileY + App.TOWER_FIRST_UPGRADE_DMG_SHIFT_Y + App.TOWER_DAMAGE_CROSS_LENGTH_Y
            );
            inputApp.line(
                tileX + i * (App.TOWER_DAMAGE_CROSS_LENGTH_X + App.TOWER_UPGRADE_CROSS_DIST),
                tileY + App.TOWER_FIRST_UPGRADE_DMG_SHIFT_Y + App.TOWER_DAMAGE_CROSS_LENGTH_Y,
                tileX + App.TOWER_FIRST_UPGRADE_SHIFT_X +
                (i+1) * App.TOWER_DAMAGE_CROSS_LENGTH_X + i * App.TOWER_UPGRADE_CROSS_DIST,
                tileY + App.TOWER_FIRST_UPGRADE_DMG_SHIFT_Y
            );
        } // each successive damage upgrade indicator moves right
    }
}
