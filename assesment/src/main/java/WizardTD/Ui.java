package WizardTD;

public class Ui {
    public Map map;
    public int waveSeconds;
    public boolean ff = false;
    public boolean ffHov = false;
    public boolean paused = false;
    public boolean pausedHov = false;
    public boolean placeTower = false;
    public boolean placeTowerHov = false;
    public boolean upgradeRange = false;
    public boolean upgradeRangeHov = false;
    public boolean upgradeSpeed = false;
    public boolean upgradeSpeedHov = false;
    public boolean upgradeDamage = false;
    public boolean upgradeDamageHov = false;
    public boolean manaPoolHov = false;
    public boolean poisonHov = false;

    /**
     * constructor for ui class
     * @param map map class it is generated with
     */
    public Ui(Map map) {
        this.map = map;
    }

    /**
     * finds out the wave frames and converts to wave seconds
     * @return new waves per second
     */
    public int updateWaveSeconds() {
        return (int) Math.floorDiv((int)this.map.getWaveTime(), App.FPS);
    }

    /**
     * checks if mouse is within confides of map
     * @param x x pixel of mouse
     * @param y y pixel of mouse
     * @return true if mouse is in map, false otherwise
     */
    static public boolean isMouseInMap(int x, int y) {
        return x > 0 && x < App.CELLSIZE * App.BOARD_WIDTH && y > App.TOPBAR && y < App.HEIGHT;
    }

    /**
     * draws wave countdown to top left as well as wave number
     * @param app app to draw with
     */
    public void waveCountdown(App app) {
        if (!this.map.getLastWave()) { // if not last wave
            app.fill(0, 0, 0);
            app.textSize(25);
            app.text(
                "Wave " + (this.map.getWaveNumber() + 2) + " starts: " + this.waveSeconds, 10, 30
            );
            // +2 to change base from 0 to 1 and refer to next wave
        }
    }

    /**
     * draws mana bar to screen
     * @param app app to draw with
     */
    public void manaBar(App app) {
        float manaProp = (float)(this.map.getMana().getCurrMana() / this.map.getMana().getCap());

        // blue bit
        app.stroke(0, 0, 0);
        app.strokeWeight(2);
        app.fill(5, 210, 215); 
        app.rect(App.MANA_X, App.MANA_Y, App.MANA_LENGTH * manaProp, App.MANA_WIDTH);
        
        // white bit
        app.fill(255, 255, 255); 
        app.rect(
            App.MANA_X + (App.MANA_LENGTH * manaProp), 
            App.MANA_Y, //
            (App.MANA_LENGTH * (1 - manaProp)), 
            App.MANA_WIDTH);
    }

    /**
     * draws mana text to screen
     * @param app app to draw with
     */
    public void manaText(App app) {
        app.fill(0, 0, 0);
        app.textSize(17);
        app.text("MANA:", App.MANA_TEXT_X, App.MANA_TEXT_Y);
        app.text((int)this.map.getMana().getCurrMana() + " / " + (int)this.map.getMana().getCap(), 
                App.MANA_TEXT_X + App.MANA_CURR_SHIFT, App.MANA_TEXT_Y);
    }

    /**
     * toggles buttons on and off and controls some logic
     * @param app app it was called by
     * @param buttonNO which button was activated
     */
    public void toggleSwitch(App app, int buttonNO) {
        switch(buttonNO) {
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

    /**
     * indicates to class that mouse is hovering over button
     * @param buttonNO which button is being hovered over
     * @param hover whether mouse is hovering over button
     */
    public void setHoveredButton(int buttonNO, boolean hover) {
        switch(buttonNO) {
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
    
    /**
     * draws all buttons to screen as well as whether they light up or are hovered over
     * @param app app to draw with
     * @param buttonNO which button to draw
     */
    public void buttonDraw(App app, int buttonNO) {
        Boolean light = null; // whether button is lit up
        Boolean hover = null; // whether mouse is hovering over button
        Boolean hasHoverText = null; // whether button has hover text
        Integer cost = null; // cost in hover box
        String text0 = null; // text on button
        String text1 = null; // text right of button (1st line)
        String text2 = null; // text right of button (2nd line)

        // determine which button were drawing
        switch(buttonNO) {
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
        if (light) {
            app.fill(255, 255, 0); // yellow
        } else if (hover) { 
            app.fill(200, 200, 200); // grey
        } else {
            app.noFill(); // hollow
        }
        int y = App.TOPBAR + buttonNO * App.BUTTON_SPACING + (buttonNO-1)*App.BUTTON_SIZE;

        app.stroke(0, 0, 0);
        app.strokeWeight(2);
        app.rect(App.BUTTON_X, y, App.BUTTON_SIZE, App.BUTTON_SIZE);

        // text0
        app.fill(0, 0, 0);
        app.textSize(App.BUTTON_TEXT_0_SIZE);
        app.text(text0, App.BUTTON_X + App.BUTTON_TEXT_SHIFT_X, y + App.BUTTON_TEXT_0_SHIFT_Y);

        // text1
        app.textSize(App.BUTTON_TEXT_12_SIZE);
        app.text(
            text1, App.BUTTON_X + App.BUTTON_SIZE + App.BUTTON_TEXT_SHIFT_X, 
            y + App.BUTTON_TEXT_1_SHIFT_Y
        );

        // text 2
        app.text(
            text2, App.BUTTON_X + App.BUTTON_SIZE + App.BUTTON_TEXT_SHIFT_X, 
            y + App.BUTTON_TEXT_2_SHIFT_Y
        );

        // hover
        if (hasHoverText && hover) {
            // hover box
            app.fill(255, 255, 255); // white
            app.rect(App.BUTTON_HOVER_X, y, App.BUTTON_HOVER_LENGTH, App.BUTTON_HOVER_HEIGHT);

            // hover text
            app.fill(0, 0, 0); // black
            app.textSize(App.BUTTON_HOVER_TEXT_SIZE);
            app.text("Cost: " + cost, App.BUTTON_HOVER_TEXT_X, y + App.BUTTON_HOVER_TEXT_SHIFT_Y);
            
        }
    }

    /**
     * handles mouse click to specifically place towers and/or upgrade towers
     * @param app app it was called by
     */
    public void click(App app) {
        if (isMouseInMap(app.mouseX, app.mouseY)) {
            if (this.placeTower) {
                this.map.place(
                    app.mouseX, app.mouseY, this.upgradeRange, 
                    this.upgradeSpeed, this.upgradeDamage
                );
            } 
            // upgrade even if in tower mode
            this.map.upgrade(
                app.mouseX, app.mouseY, this.upgradeRange, this.upgradeSpeed, this.upgradeDamage
            );
        }
    }

    /**
     * handles drawing of cursor based on what mode ui is in 
     * eg. if in place tower mode, draws greyscale tower to indicate where it will be placed
     * @param app app to draw with
     */
    public void hoverPlace(App app) {
        app.noFill();
        app.stroke(0, 0, 0); // black
        app.strokeWeight(2);
        int x1 = app.mouseX - App.CELLSIZE / 2; // shift to center of cell
        int y1 = app.mouseY - App.CELLSIZE / 2;
        int x2 = app.mouseX + App.CELLSIZE / 2;
        int y2 = app.mouseY + App.CELLSIZE / 2;
        int circleGrow = 10;
        int crossGrow = -10; // shift crosshair to center of cell

        if (this.placeTower) {
            app.image(
                app.loadImage("src/main/resources/WizardTD/towerGrey.png"), 
                x1, y1
            );
        }
        if (this.upgradeSpeed) {
            app.rect(x1, y1, App.CELLSIZE, App.CELLSIZE);
        }
        if (this.upgradeRange) {
            app.ellipse(app.mouseX, app.mouseY, 
            App.CELLSIZE + circleGrow, App.CELLSIZE + circleGrow
            );
        } // + 5 makes it distinguishable from square
        if (this.upgradeDamage) {
            app.line(x1 - crossGrow, y1 - crossGrow, x2 + crossGrow, y2 + crossGrow);
            app.line(x1 - crossGrow, y2 + crossGrow, x2 + crossGrow, y1 - crossGrow);
        }// +- 5 makes it distinguishable from square
    }

    /**
     * draws upgrade bubble in bottom right
     * @param app app to draw with
     * @param tower tower hovering over
     */
    public void upgradeBubble(App app, Tower tower) {
        // bubble
        boolean wantAffordRange = this.upgradeRange 
                                  && (int)tower.getRangeCost() < 
                                  (int)this.map.getMana().getCurrMana();
        int wARange = (wantAffordRange) ? 1 : 0;
        boolean wantAffordSpeed = this.upgradeSpeed && 
                                  (int)tower.getFiringSpeedCost() + 
                                  wARange * (int)tower.getRangeCost() < 
                                  (int)this.map.getMana().getCurrMana();
        int wASpeed = (wantAffordSpeed) ? 1 : 0;
        boolean wantAffordDamage = this.upgradeDamage && 
                                   (int)tower.getDamageCost() + 
                                   wASpeed * (int)tower.getFiringSpeedCost() + 
                                   wARange * (int)tower.getRangeCost() < 
                                   (int)this.map.getMana().getCurrMana();
        int wADamage = (wantAffordDamage) ? 1 : 0;
        int upgrades = wADamage + wASpeed + wARange;
        int textTally = 0;
        int totalCost = wARange * (int)tower.getRangeCost() + 
                        wASpeed * (int)tower.getFiringSpeedCost() + 
                        wADamage * (int)tower.getDamageCost();
        app.stroke(0, 0, 0); // black
        app.strokeWeight(2);
        app.fill(255, 255, 255); // white
        
        // shapes
        // "upgrade cost" rectangle
        app.rect(
            App.UPGRADE_BUBBLE_X, App.UPGRADE_BUBBLE_Y, 
            App.UPGRADE_BUBBLE_LENGTH, App.UPGRADE_BUBBLE_HEIGHT
        );

        // specific upgrade rectangle
        app.rect(
            App.UPGRADE_BUBBLE_X, App.UPGRADE_BUBBLE_Y + App.UPGRADE_BUBBLE_HEIGHT, 
            App.UPGRADE_BUBBLE_LENGTH, App.UPGRADE_BUBBLE_HEIGHT * upgrades
        );

        // total cost rectangle
        app.rect(
            App.UPGRADE_BUBBLE_X, 
            App.UPGRADE_BUBBLE_Y + App.UPGRADE_BUBBLE_HEIGHT * (upgrades + 1),
            App.UPGRADE_BUBBLE_LENGTH, App.UPGRADE_BUBBLE_HEIGHT
        );

        // text 
        app.fill(0, 0, 0); // black text
        app.textSize(App.UPGRADE_BUBBLE_TEXT_SIZE);
        app.text(
            "Upgrade cost", App.UPGRADE_BUBBLE_X + App.UPGRADE_BUBBLE_TEXT_SHIFT_X, 
            App.UPGRADE_BUBBLE_Y + App.UPGRADE_BUBBLE_TEXT_SHIFT_Y + 
            textTally * (App.UPGRADE_BUBBLE_HEIGHT)
        );
        textTally++;

        if (wARange == 1) {
            app.text(
                "range:     " + (int)tower.getRangeCost(), 
                App.UPGRADE_BUBBLE_X + App.UPGRADE_BUBBLE_TEXT_SHIFT_X, 
                App.UPGRADE_BUBBLE_Y + App.UPGRADE_BUBBLE_TEXT_SHIFT_Y + 
                textTally * (App.UPGRADE_BUBBLE_HEIGHT)
            );
            textTally++;
        }
        if (wASpeed == 1) {
            app.text(
                "speed:     " + (int)tower.getFiringSpeedCost(), 
                App.UPGRADE_BUBBLE_X + App.UPGRADE_BUBBLE_TEXT_SHIFT_X, 
                App.UPGRADE_BUBBLE_Y + App.UPGRADE_BUBBLE_TEXT_SHIFT_Y + 
                textTally * (App.UPGRADE_BUBBLE_HEIGHT)
            );
            textTally++;
        }
        if (wADamage == 1) {
            app.text(
                "damage: " + (int)tower.getDamageCost(), 
                App.UPGRADE_BUBBLE_X + App.UPGRADE_BUBBLE_TEXT_SHIFT_X, 
                App.UPGRADE_BUBBLE_Y + App.UPGRADE_BUBBLE_TEXT_SHIFT_Y + 
                textTally * (App.UPGRADE_BUBBLE_HEIGHT)
            );
            textTally++;
        }

        app.text(
            "Total:      " + totalCost, 
            App.UPGRADE_BUBBLE_X + App.UPGRADE_BUBBLE_TEXT_SHIFT_X, 
            App.UPGRADE_BUBBLE_Y + App.UPGRADE_BUBBLE_TEXT_SHIFT_Y + 
            textTally * (App.UPGRADE_BUBBLE_HEIGHT)
        );
    }

    /**
     * tick method only needs to update the wave seconds for top right counter
     */
    public void tick() {
        this.waveSeconds = this.updateWaveSeconds(); // convert wave time to seconds
    }

    /**
     * draws: 
     * 1. wave countdown + wave number
     * 2. mana bar + mana text
     * 3. buttons and their logic (yellow/grey)
     * 4. tower mode aka cursor logic
     * 5. upgrade bubble in bottom right
     * @param app
     */
    public void draw(App app) {
        this.waveCountdown(app); // wave timer

        // mana
        this.manaBar(app);
        this.manaText(app);

        // BUTTONS
        for(int i = 1; i <= App.NUMBER_OF_BUTTONS; i++) {
            this.buttonDraw(app, i);
        }

        // tower mode
        hoverPlace(app);

        // draw upgrade bubble in bottom right
        Tile potentialTower = this.map.mouse2Land(app.mouseX, app.mouseY);

        if (
            potentialTower instanceof Tower && 
            (this.upgradeRange || this.upgradeSpeed || this.upgradeDamage)
        ) {
            Tower tower = (Tower) potentialTower;
            upgradeBubble(app, tower);
        }
    }
}