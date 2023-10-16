package WizardTD;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;

import processing.core.PApplet;

public class Tower extends Tile {
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

    Tower(int x, int y, double initialRange, 
          double initialFiringSpeed, double initialDamage, 
          boolean initialRangeLevel, boolean initialFiringSpeedLevel,
          boolean initialDamageLevel, Map map)
    {
        super(x, y, map, Tower.spritePathToBeOveriden);
        this.centerX = x * CELLSIZE + CELLSIZE / 2;
        this.centerY = y * CELLSIZE + CELLSIZE / 2 + TOPBAR;
        this.range = initialRange;
        this.firingSpeed = initialFiringSpeed;
        // convert to fireballs per second to fireballs per frame
        this.damage = initialDamage;
        this.initialTowerDamage = initialDamage;

        if (initialRangeLevel)
        {
            this.upgradeRange();
        }
        if (initialFiringSpeedLevel)
        {
            this.upgradeFiringSpeed();
        }
        if (initialDamageLevel)
        {
            this.upgradeDamage();
        }
        System.out.println("Created: " + this);
    }

    public double getRangeCost()
    {
        return 20 + 10 * this.rangeLevel;
    }

    public double getFiringSpeedCost()
    {
        return 20 + 10 * this.firingSpeedLevel;
    }

    public double getDamageCost()
    {
        return 20 + 10 * this.damageLevel;
    }

    public double getRange()
    {
        return this.range;
    }

    public ArrayList<Fireball> getProjectiles()
    {
        return this.projectiles;
    }

    public void setFramesCounter(int framesCounter)
    {
        this.framesCounter = framesCounter;
    }

    public void findLowestLevel()
    {
        if (this.rangeLevel <= this.firingSpeedLevel && this.rangeLevel <= this.damageLevel) {
            this.lowestLevel = this.rangeLevel;
        } else if (
            this.firingSpeedLevel <= this.rangeLevel && this.firingSpeedLevel <= this.damageLevel
        ) {
            this.lowestLevel = this.firingSpeedLevel;
        } else{
            this.lowestLevel = this.damageLevel;
        }
        if (this.lowestLevel > 2) {
            this.lowestLevel = 2;
        } // level 2 sprite is the lowest level sprite
        
        this.sprite = this.map.getApp().loadImage(
            "src/main/resources/WizardTD/tower" + this.lowestLevel + ".png"
        );
    }   

    public void upgradeRange()
    {
        this.rangeLevel++;
        this.range += 32;
        System.out.println("upgraded range to " + this);
    }

    public void upgradeFiringSpeed()
    {
        this.firingSpeedLevel++;
        this.firingSpeed += 0.5;
        System.out.println("upgraded speed to " + this);
    }

    public void upgradeDamage()
    {
        this.damageLevel++;
        this.damage += this.initialTowerDamage / 2;
        System.out.println("upgraded damage to " + this);
    }

    public void shoot() {
        // create list of enemies in range
        ArrayList<Monster> enemiesInRange = new ArrayList<Monster>();
        for(Wave wave: this.map.getWaves()) {
            for(Monster monster: wave.getMonsters()) {
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
                this.centerX, this.centerY, target, this.damage, this.map.getApp()
            ));
            System.out.println("Shot fired");
        }
    }

    public String toString()
    {
        return this.rangeLevel + " " + this.firingSpeedLevel + " " + 
               this.damageLevel + " tower at (" + this.x + ", " + this.y + ")";
    }

    public void tick()
    {
        // shoot if enough frames have passed
        double framesPerFireball = App.FPS / this.firingSpeed;
        if (this.framesCounter > framesPerFireball) {
            this.shoot();
            this.framesCounter = 0;
        }
        this.framesCounter += this.map.getApp().rate;

        // tick and remove all fireballs
        Iterator<Fireball> fireballIterator = this.projectiles.iterator();
        while(fireballIterator.hasNext()) {
            Fireball fireball = fireballIterator.next();
            fireball.tick();

            if (!(fireball.getExists())) {
                fireballIterator.remove();
            }
        }
    }

    @Override
    public void draw(PApplet app)
    {
        this.findLowestLevel(); // and figure out which sprite to use
        app.image(this.sprite, this.x * CELLSIZE, this.y * CELLSIZE + TOPBAR);

        int tileX = this.x * CELLSIZE;
        int tileY = this.y * CELLSIZE + TOPBAR;
        app.noFill();

        // fire rate square
        if (this.firingSpeedLevel - this.lowestLevel >= 1)
        { // only draw if upgraded past sprite
            app.stroke(120, 180, 255); // light blue
            app.strokeWeight((this.firingSpeedLevel - this.lowestLevel)* 2); 
            // stroke weight increases with level
            app.rect(
                tileX + App.TOWER_SPEED_SQUARE_SHIFT, tileY + App.TOWER_SPEED_SQUARE_SHIFT,
                App.TOWER_SPEED_SQUARE_LENGTH, App.TOWER_SPEED_SQUARE_LENGTH
            );
        }

        // color + stroke weight setup for both dmg and range
        app.stroke(255, 0, 255); // purple
        app.strokeWeight(1);
        
        // range indicators
        for(int i = 0; i < this.rangeLevel - this.lowestLevel; i++) {
            app.ellipse( // create above many circles
                tileX + App.TOWER_FIRST_UPGRADE_SHIFT_X + 
                i * (App.RANGE_UPGRADE_DIAMETER + App.TOWER_UPGRADE_CIRCLE_DIST),
                tileY + App.TOWER_FIRST_UPGRADE_SHIFT_Y, 
                App.RANGE_UPGRADE_DIAMETER, App.RANGE_UPGRADE_DIAMETER
            );
        } // each successive range upgrade indicator moves right

        // damage indicators
        for(int i = 0; i < this.damageLevel - this.lowestLevel; i++) {
            app.line( // create above many crosses
                tileX + i * (App.TOWER_DAMAGE_CROSS_LENGTH_X + App.TOWER_UPGRADE_CROSS_DIST),
                tileY + App.TOWER_FIRST_UPGRADE_DMG_SHIFT_Y,
                tileX + App.TOWER_FIRST_UPGRADE_SHIFT_X +
                (i+1) * App.TOWER_DAMAGE_CROSS_LENGTH_X + i * App.TOWER_UPGRADE_CROSS_DIST,
                tileY + App.TOWER_FIRST_UPGRADE_DMG_SHIFT_Y + App.TOWER_DAMAGE_CROSS_LENGTH_Y
            );
            app.line(
                tileX + i * (App.TOWER_DAMAGE_CROSS_LENGTH_X + App.TOWER_UPGRADE_CROSS_DIST),
                tileY + App.TOWER_FIRST_UPGRADE_DMG_SHIFT_Y + App.TOWER_DAMAGE_CROSS_LENGTH_Y,
                tileX + App.TOWER_FIRST_UPGRADE_SHIFT_X +
                (i+1) * App.TOWER_DAMAGE_CROSS_LENGTH_X + i * App.TOWER_UPGRADE_CROSS_DIST,
                tileY + App.TOWER_FIRST_UPGRADE_DMG_SHIFT_Y
            );
        } // each successive damage upgrade indicator moves right
    }
}
