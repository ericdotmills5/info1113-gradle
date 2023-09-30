package WizardTD;

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

    Tower(int x, int y, double initialRange, 
          double initialFiringSpeed, double initialDamage, 
          boolean initialRangeLevel, boolean initialFiringSpeedLevel,
          boolean initialDamageLevel, Map map)
    {
        super(x, y, map);
        this.range = initialRange;
        this.firingSpeed = initialFiringSpeed;
        this.damage = initialDamage;
        this.initialTowerDamage = initialDamage;

        if(initialRangeLevel)
        {
            this.upgradeRange();
        }
        if(initialFiringSpeedLevel)
        {
            this.upgradeFiringSpeed();
        }
        if(initialDamageLevel)
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

    public void findLowestLevel()
    {
        if(this.rangeLevel <= this.firingSpeedLevel && this.rangeLevel <= this.damageLevel){
            this.lowestLevel = this.rangeLevel;
        } else if(this.firingSpeedLevel <= this.rangeLevel && this.firingSpeedLevel <= this.damageLevel){
            this.lowestLevel = this.firingSpeedLevel;
        } else{
            this.lowestLevel = this.damageLevel;
        }
        if(this.lowestLevel > 2){
            this.lowestLevel = 2;
        } // level 2 sprite is the lowest level sprite
        
        this.sprite = 
        this.map.getApp().loadImage("src/main/resources/WizardTD/tower" + this.lowestLevel + ".png");
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

    public String toString()
    {
        return this.rangeLevel + " " + this.firingSpeedLevel + " " + 
               this.damageLevel + " tower at (" + this.x + ", " + this.y + ")";
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
        if(this.firingSpeedLevel - this.lowestLevel >= 1)
        { // only draw if upgraded past sprite
            app.stroke(120, 180, 255); // light blue
            app.strokeWeight(this.firingSpeedLevel - this.lowestLevel); // stroke weight increases with level
            app.rect(
                tileX + App.TOWERSPEEDSQUARESHIFT, tileY + App.TOWERSPEEDSQUARESHIFT,
                App.TOWERSPEEDSQUARELENGTH, App.TOWERSPEEDSQUARELENGTH
            );
        }

        // color + stroke weight setup for both dmg and range
        app.stroke(255, 0, 255); // purple
        app.strokeWeight(1);
        
        // range indicators
        for(int i = 0; i < this.rangeLevel - this.lowestLevel; i++){
            app.ellipse( // create above many circles
                tileX + App.TOWERFIRSTUPGRADESHIFTX + 
                i * (App.RANGEUPGRADEDIAMETER + App.TOWERUPGRADECIRCLEDIST),
                tileY + App.TOWERFIRSTUPGRADESHIFTY, 
                App.RANGEUPGRADEDIAMETER, App.RANGEUPGRADEDIAMETER
            );
        } // each successive range upgrade indicator moves right

        // damage indicators
        for(int i = 0; i < this.damageLevel - this.lowestLevel; i++){
            app.line( // create above many crosses
                tileX + i * (App.TOWERDAMAGECROSSLENGTHX + App.TOWERUPGRADECROSSDIST),
                tileY + App.TOWERFIRSTUPGRADEDMGSHIFTY,
                tileX + App.TOWERFIRSTUPGRADESHIFTX +
                (i+1) * App.TOWERDAMAGECROSSLENGTHX + i * App.TOWERUPGRADECROSSDIST,
                tileY + App.TOWERFIRSTUPGRADEDMGSHIFTY + App.TOWERDAMAGECROSSLENGTHY
            );
            app.line(
                tileX + i * (App.TOWERDAMAGECROSSLENGTHX + App.TOWERUPGRADECROSSDIST),
                tileY + App.TOWERFIRSTUPGRADEDMGSHIFTY + App.TOWERDAMAGECROSSLENGTHY,
                tileX + App.TOWERFIRSTUPGRADESHIFTX +
                (i+1) * App.TOWERDAMAGECROSSLENGTHX + i * App.TOWERUPGRADECROSSDIST,
                tileY + App.TOWERFIRSTUPGRADEDMGSHIFTY
            );
        } // each successive damage upgrade indicator moves right

        
        

        
        
        
    }
}
