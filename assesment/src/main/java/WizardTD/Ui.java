package WizardTD;

public class Ui {
    private Map map;
    private int waveSeconds;
    private boolean ff = false;
    private boolean ffHov = false;
    private boolean paused = false;
    private boolean pausedHov = false;
    private boolean placeTower = false;
    private boolean placeTowerHov = false;
    private boolean upgradeRange = false;
    private boolean upgradeRangeHov = false;
    private boolean upgradeSpeed = false;
    private boolean upgradeSpeedHov = false;
    private boolean upgradeDamage = false;
    private boolean upgradeDamageHov = false;
    private boolean manaPoolHov = false;
    private boolean poisonHov = false;

    public Ui(Map map)
    {
        this.map = map;
    }

    public int updateWaveSeconds()
    {
        return (int) Math.floorDiv((int)this.map.getWaveTime(), App.FPS);
    }

    static public boolean isMouseInMap(int x, int y) {
        return x > 0 && x < App.CELLSIZE * App.BOARD_WIDTH && y > App.TOPBAR && y < App.HEIGHT;
    }

    public void waveCountdown(App app)
    {
        if(!this.map.getLastWave()){ // if not last wave
            app.fill(0);
            app.textSize(25);
            app.text("Wave " + (this.map.getWaveNumber() + 2) + " starts: " + this.waveSeconds, 10, 30);
            // +2 to change base from 0 to 1 and refer to next wave
        }
    }

    public void manaBar(App app)
    {
        float manaProp = (float)(this.map.getMana().getCurrMana() / this.map.getMana().getCap());

        // blue bit
        app.stroke(0);
        app.strokeWeight(2);
        app.fill(5, 210, 215); 
        app.rect(App.MANAX, App.MANAY, App.MANALENGTH * manaProp, App.MANAWIDTH);
        
        // white bit
        app.fill(255, 255, 255); 
        app.rect(
            App.MANAX + (App.MANALENGTH * manaProp), 
            App.MANAY, //
            (App.MANALENGTH * (1 - manaProp)), 
            App.MANAWIDTH);
    }

    public void manaText(App app)
    {
        app.fill(0);
        app.textSize(17);
        app.text("MANA:", App.MANATEXTX, App.MANATEXTY);
        app.text((int)this.map.getMana().getCurrMana() + " / " + (int)this.map.getMana().getCap(), 
                 App.MANATEXTX + App.MANACURRSHIFT, App.MANATEXTY);
    }

    public void toggleSwitch(App app, int buttonNO)
    {
        switch(buttonNO){
            case 1: // fast forward
                this.ff = !this.ff;
                app.doubleRate = app.doubleRate == 1 ? 2 : 1;
                app.rate = app.doubleRate * app.pauseRate;
                break;
            case 2: // pause
                this.paused = !this.paused;
                app.pauseRate = this.paused ? 0 : 1;
                app.rate = app.doubleRate * app.pauseRate;
                break;
            case 3: // place tower
                this.placeTower = !this.placeTower;
                break;
            case 4: // upgrade range
                this.upgradeRange = !this.upgradeRange;
                break;
            case 5: // upgrade speed
                this.upgradeSpeed = !this.upgradeSpeed;
                break;
            case 6: // upgrade damage
                this.upgradeDamage = !this.upgradeDamage;
                break;
            case 7: // upgrade mana pool
                this.map.getMana().clickPoolSpell();
                break;
            case 8:
                this.map.togglePoison();
        }
    }

    public void setHoveredButton(int buttonNO, boolean hover)
    {
        switch(buttonNO){
            case 1: // fast forward
                this.ffHov = hover;
                break;
            case 2: // pause
                this.pausedHov = hover;
                break;
            case 3: // place tower
                this.placeTowerHov = hover;
                break;
            case 4: // upgrade range
                this.upgradeRangeHov = hover;
                break;
            case 5: // upgrade speed
                this.upgradeSpeedHov = hover;
                break;
            case 6: // upgrade damage
                this.upgradeDamageHov = hover;
                break;
            case 7: // upgrade mana pool
                this.manaPoolHov = hover;
                break;
            case 8:
                this.poisonHov = hover;
        }
    }
    
    public void buttonDraw(App app, int buttonNO)
    {
        Boolean light = null; // whether button is lit up
        Boolean hover = null; // whether mouse is hovering over button
        Boolean hasHoverText = null; // whether button has hover text
        Integer cost = null; // cost in hover box
        String text0 = null; // text on button
        String text1 = null; // text right of button (1st line)
        String text2 = null; // text right of button (2nd line)

        // determine which button were drawing
        switch(buttonNO){
            case 1:
                light = this.ff;
                hover = this.ffHov;
                hasHoverText = false; 
                // hover text cost not needed since no hover box
                text0 = "FF";
                text1 = "2x speed";
                text2 = " ";
                break;
            case 2:
                light = this.paused;
                hover = this.pausedHov;
                hasHoverText = false;
                // hover text cost not needed since no hover box
                text0 = "P";
                text1 = "PAUSE";
                text2 = " ";
                break;
            case 3:
                light = this.placeTower;
                hover = this.placeTowerHov;
                hasHoverText = true;
                cost = (int)this.map.getTowerCost();
                text0 = "T";
                text1 = "Build";
                text2 = "tower";
                break;
            case 4:
                light = this.upgradeRange;
                hover = this.upgradeRangeHov;
                hasHoverText = false;
                
                text0 = "U1";
                text1 = "Upgrade";
                text2 = "range";
                break;
            case 5:
                light = this.upgradeSpeed;
                hover = this.upgradeSpeedHov;
                hasHoverText = false;
                
                text0 = "U2";
                text1 = "Upgrade";
                text2 = "speed";
                break;
            case 6:
                light = this.upgradeDamage;
                hover = this.upgradeDamageHov;
                hasHoverText = false;
                
                text0 = "U3";
                text1 = "Upgrade";
                text2 = "damage";
                break;
            case 7:
                light = false; // mana pool cannot be toggled
                hover = this.manaPoolHov;
                hasHoverText = true;
                cost = (int)this.map.getMana().getPoolCost();
                text0 = "M";
                text1 = "Mana pool";
                text2 = "cost: " + cost; // assume 4 digit price max
                break;
            case 8:
                light = this.map.getPoison();
                hover = this.poisonHov;
                hasHoverText = true;
                cost = (int)this.map.getApp().poisonCost;
                text0 = "U4";
                text1 = "Poison all";
                text2 = "cost: " + cost; // assume 4 digit price max
                break;
        }

        // button outline
        if(light){
            app.fill(255, 255, 0); // yellow
        } else if(hover) { 
            app.fill(200, 200, 200); // grey
        }else {
            app.noFill(); // hollow
        }
        int y = App.TOPBAR + buttonNO * App.BUTTONSPACING + (buttonNO-1)*App.BUTTONSIZE;

        app.stroke(0, 0, 0);
        app.strokeWeight(2);
        app.rect(App.BUTTONX, y, App.BUTTONSIZE, App.BUTTONSIZE);

        // text0
        app.fill(0, 0, 0);
        app.textSize(App.BUTTONTEXT0SIZE);
        app.text(text0, App.BUTTONX + App.BUTTONTEXTSHIFTX, y + App.BUTTONTEXT0SHIFTY);

        // text1
        app.textSize(App.BUTTONTEXT12SIZE);
        app.text(text1, App.BUTTONX + App.BUTTONSIZE + App.BUTTONTEXTSHIFTX, y + App.BUTTONTEXT1SHIFTY);

        // text 2
        app.text(text2, App.BUTTONX + App.BUTTONSIZE + App.BUTTONTEXTSHIFTX, y + App.BUTTONTEXT2SHIFTY);

        // hover
        if(hasHoverText && hover)
        {
            // hover box
            app.fill(255, 255, 255); // white
            app.rect(App.BUTTONHOVERX, y, App.BUTTONHOVERLENGTH, App.BUTTONHOVERHEIGHT);

            // hover text
            app.fill(0, 0, 0); // black
            app.textSize(App.BUTTONHOVERTEXTSIZE);
            app.text("Cost: " + cost, App.BUTTONHOVERTEXTX, y + App.BUTTONHOVERTEXTSHIFTY);
            
        }
    }

    public void click(App app){
        if(isMouseInMap(app.mouseX, app.mouseY)){
            if(this.placeTower)
            {
                this.map.place(app.mouseX, app.mouseY, this.upgradeRange, this.upgradeSpeed, this.upgradeDamage);
            } 
            // upgrade even if in tower mode
            this.map.upgrade(app.mouseX, app.mouseY, this.upgradeRange, this.upgradeSpeed, this.upgradeDamage);
            
        }
    }

    public void hoverPlace(App app){
        app.noFill();
        app.stroke(0, 0, 0); // black
        app.strokeWeight(2);
        int x1 = app.mouseX - App.CELLSIZE / 2; // shift to center of cell
        int y1 = app.mouseY - App.CELLSIZE / 2;
        int x2 = app.mouseX + App.CELLSIZE / 2;
        int y2 = app.mouseY + App.CELLSIZE / 2;
        int circleGrow = 10;
        int crossGrow = -10; // shift crosshair to center of cell

        if(this.placeTower){
            app.image(
                app.loadImage("src/main/resources/WizardTD/towerGrey.png"), 
                x1, y1
            );
        }
        if(this.upgradeSpeed){
            app.rect(x1, y1, App.CELLSIZE, App.CELLSIZE);
        }
        if(this.upgradeRange){
            app.ellipse(app.mouseX, app.mouseY, App.CELLSIZE + circleGrow, App.CELLSIZE + circleGrow);
        } // + 5 makes it distinguishable from square
        if(this.upgradeDamage){
            app.line(x1 - crossGrow, y1 - crossGrow, x2 + crossGrow, y2 + crossGrow);
            app.line(x1 - crossGrow, y2 + crossGrow, x2 + crossGrow, y1 - crossGrow);
        }// +- 5 makes it distinguishable from square
    }

    public void upgradeBubble(App app, Tower tower){
        // bubble
        int upgrades = (this.upgradeRange ? 1 : 0) + (this.upgradeSpeed ? 1 : 0) + (this.upgradeDamage ? 1 : 0);
        int textTally = 0;
        int totalCost = (this.upgradeRange ? 1 : 0) * (int)tower.getRangeCost() + 
                        (this.upgradeSpeed ? 1 : 0) * (int)tower.getFiringSpeedCost() + 
                        (this.upgradeDamage ? 1 : 0) * (int)tower.getDamageCost();
        app.stroke(0, 0, 0); // black
        app.strokeWeight(2);
        app.fill(255, 255, 255); // white
        
        // shapes
        // "upgrade cost" rectangle
        app.rect(
            App.UPGRADEBUBBLEX, App.UPGRADEBUBBLEY, 
            App.UPGRADEBUBBLELENGTH, App.UPGRADEBUBBLEHEIGHT
        );

        // specific upgrade rectangle
        app.rect(
            App.UPGRADEBUBBLEX, App.UPGRADEBUBBLEY + App.UPGRADEBUBBLEHEIGHT, 
            App.UPGRADEBUBBLELENGTH, App.UPGRADEBUBBLEHEIGHT * upgrades
        );

        // total cost rectangle
        app.rect(
            App.UPGRADEBUBBLEX, App.UPGRADEBUBBLEY + App.UPGRADEBUBBLEHEIGHT * (upgrades + 1), 
            App.UPGRADEBUBBLELENGTH, App.UPGRADEBUBBLEHEIGHT
        );

        // text 
        app.fill(0, 0, 0); // black text
        app.textSize(App.UPGRADEBUBBLETEXTSIZE);
        app.text(
            "Upgrade cost", App.UPGRADEBUBBLEX + App.UPGRADEBUBBLETEXTSHIFTX, 
            App.UPGRADEBUBBLEY + App.UPGRADEBUBBLETEXTSHIFTY + textTally * (App.UPGRADEBUBBLEHEIGHT)
        );
        textTally++;

        if(this.upgradeRange){
            app.text(
                "range:     " + (int)tower.getRangeCost(), 
                App.UPGRADEBUBBLEX + App.UPGRADEBUBBLETEXTSHIFTX, 
                App.UPGRADEBUBBLEY + App.UPGRADEBUBBLETEXTSHIFTY + textTally * (App.UPGRADEBUBBLEHEIGHT)
            );
            textTally++;
        }
        if(this.upgradeSpeed){
            app.text(
                "speed:     " + (int)tower.getFiringSpeedCost(), 
                App.UPGRADEBUBBLEX + App.UPGRADEBUBBLETEXTSHIFTX, 
                App.UPGRADEBUBBLEY + App.UPGRADEBUBBLETEXTSHIFTY + textTally * (App.UPGRADEBUBBLEHEIGHT)
            );
            textTally++;
        }
        if(this.upgradeDamage){
            app.text(
                "damage: " + (int)tower.getDamageCost(), 
                App.UPGRADEBUBBLEX + App.UPGRADEBUBBLETEXTSHIFTX, 
                App.UPGRADEBUBBLEY + App.UPGRADEBUBBLETEXTSHIFTY + textTally * (App.UPGRADEBUBBLEHEIGHT)
            );
            textTally++;
        }

        app.text(
            "Total:      " + totalCost, 
            App.UPGRADEBUBBLEX + App.UPGRADEBUBBLETEXTSHIFTX, 
            App.UPGRADEBUBBLEY + App.UPGRADEBUBBLETEXTSHIFTY + textTally * (App.UPGRADEBUBBLEHEIGHT)
        );
    }

    public void tick()
    {
        this.waveSeconds = this.updateWaveSeconds(); // convert wave time to seconds
    }

    public void draw(App app)
    {
        this.waveCountdown(app); // wave timer

        // mana
        this.manaBar(app);
        this.manaText(app);

        // buttons
        for(int i = 1; i <= App.NUMBEROFBUTTONS; i++){
            this.buttonDraw(app, i);
        }

        // tower mode
        hoverPlace(app);

        // draw upgrade bubble in bottom right
        Tile potentialTower = this.map.mouse2Land(app.mouseX, app.mouseY);

        if(
            potentialTower instanceof Tower && 
            (this.upgradeRange || this.upgradeSpeed || this.upgradeDamage)
        ){
            Tower tower = (Tower) potentialTower;
            upgradeBubble(app, tower);
        }
    }
}
