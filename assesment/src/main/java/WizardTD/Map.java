package WizardTD;

import processing.core.PApplet;
import processing.data.JSONObject;
import processing.data.JSONArray;

import java.util.*;

/*
 * Edge cases:
 *  sumarise long if else statements
 *  google java style guide * 
 *  clean up code with functions * 
 * 
 * check if integer config values can take floats
 * check pasue/ff doesnt mess stuff up (check logic)
 * check mana spell math on all calculations
*/

/**
* Enum for directions
*/
enum Direction{
    UP, DOWN, LEFT, RIGHT, NONE;
}

public class Map {
    static final int BOARD_WIDTH = App.BOARD_WIDTH;
    static final int FPS = App.FPS;

    private Tile[][] land = new Tile[BOARD_WIDTH][BOARD_WIDTH];
    private int[] wizCordsXY = new int[2];
    private App app;
    private Wizard wizard;
    private HashMap<Path, ArrayList<Direction>> routes = 
    new HashMap<Path, ArrayList<Direction>>(); // terminal path, assosiated route
    private JSONObject data;
    private ArrayList<Wave> waveList = new ArrayList<>(); // turn into wave list for many waves
    private int waveNumber = 0;
    private double waveTime;
    private boolean lastWave = false;
    private Mana mana;
    private ArrayList<Tower> towerList = new ArrayList<>();
    private boolean poison = false;
    private double poisonFrames;

    /**
     * Draws map onto screen, creates routes for each spawn, creates waves, creates mana object
     * @param mapIterable iterable of strings representing map
     * @param app app object
     */
    public Map(Iterable<String> mapIterable, App app, JSONObject data)
    {
        this.poisonFrames = app.poisonFrames;
        this.app = app;
        this.land = this.iterator2Matrix(mapIterable.iterator());
        System.out.println("matrix made");

        this.updateAllPaths();

        System.out.println("Paths rotated");

        this.wizard.determineWizDists();

        this.createRoutes();

        this.data = data;

        JSONObject tempWave = this.data.getJSONArray("waves").getJSONObject(this.waveNumber);
        this.waveList.add(new Wave(
            tempWave, this.routes, this.app
        )); // add pre wave pause for 1st wave
        this.waveTime = this.addWaveTimes() + tempWave.getDouble("pre_wave_pause") * FPS;

        this.mana = new Mana(
            this.data.getDouble("initial_mana"), 
            this.data.getDouble("initial_mana_cap"), 
            this.data.getDouble("initial_mana_gained_per_second"), 
            this.data.getDouble("mana_pool_spell_initial_cost"),
            this.data.getDouble("mana_pool_spell_cost_increase_per_use"),
            this.data.getDouble("mana_pool_spell_cap_multiplier"),
            this.data.getDouble("mana_pool_spell_mana_gained_multiplier")
        );

        System.out.println(this.waveTime);
    } 

    public Tile[][] getLand() {
        return this.land;
    }

    public ArrayList<Tower> getTowerList() {
        return this.towerList;
    }

    public App getApp() {
        return this.app;
    }

    public boolean getLastWave() {
        return this.lastWave;
    }

    public int getWaveNumber() {
        return this.waveNumber;
    }

    public ArrayList<Wave> getWaves() {
        return this.waveList;
    }

    public double getWaveTime() {
        return this.waveTime;
    }

    public Mana getMana() {
        return this.mana;
    }

    public double getTowerCost() {
        return this.data.getDouble("tower_cost");
    }

    public double getInitialTowerRange() {
        return this.data.getDouble("initial_tower_range");
    }

    public double getInitialTowerFiringSpeed()
    {
        return this.data.getDouble("initial_tower_firing_speed");
    }

    public double getInitialTowerDamage()
    {
        return this.data.getDouble("initial_tower_damage");
    }

    public boolean getPoison()
    {
        return this.poison;
    }

    public HashMap<Path, ArrayList<Direction>> getRoutes()
    {
        return this.routes;
    }

    public JSONObject getData()
    {
        return this.data;
    }

    /**
     * Toggles poison on and off based on whether player has enough mana
     */
    public void togglePoison() {
        if (!this.poison && this.mana.updateMana(-1 * this.app.poisonCost)) {
            this.poison = true;
        }
    }

    /**
     * Converts iterable of strings into matrix of tiles that can be drawn and iterated through
     * @param scan iterable of strings representing map
     * @return matrix of tiles
     */
    public Tile[][] iterator2Matrix(Iterator<String> scan) {
        Tile[][] matrix = new Tile[BOARD_WIDTH][BOARD_WIDTH]; // assume level is sqrmapsize
        int i;
        
        for(int j = 0; j < BOARD_WIDTH; j++) { // iterate through each line
            i = 0;
            for(char c: scan.next().toCharArray()) { // iterate through each letter
                switch(c) {
                    case 'S':
                        matrix[i][j] = new Shrub(i, j, this);
                        break;
                    case 'X':
                        matrix[i][j] = new Path(i, j, this);
                        break;
                    case 'W':
                        this.wizard = new Wizard(i, j, this);
                        matrix[i][j] = this.wizard;
                        this.wizCordsXY[0] = i;
                        this.wizCordsXY[1] = j;
                        break;
                    case ' ':
                        matrix[i][j] = new Grass(i, j, this); // assume everything else is grass
                        break; // intentionally dont handle _ case easier bug fix
                }
                i++;
            }
            while(i < BOARD_WIDTH) { // fill trailing empty text with grass
                matrix[i][j] = new Grass(i, j, this);
                i++;
            }
            System.out.println("row " + j + " read");
        }
        return matrix;
    }

    /**
     * Finds what wave time needs to be put onto the screen
     * Does so by adding current wave time with next wave prewave pause directly from config
     * @return wave time to be put onto screen
     */
    public double addWaveTimes()
    { 
        JSONArray waves = this.data.getJSONArray("waves");
        if (waves.size() == 1) { // force wave to be negative (endless)
            this.lastWave = true;
            return -1 * (waves.getJSONObject(this.waveNumber).getDouble("pre_wave_pause")*FPS +1);
        }
        return (waves.getJSONObject(this.waveNumber).getDouble("duration") 
        + waves.getJSONObject(this.waveNumber + 1).getDouble("pre_wave_pause")) * FPS;
    }

    /**
     * Iterates through each tile in matrix and assigns properties to each path
     */
    public void updateAllPaths()
    { 
        for(Tile[] row: this.land) {
            for(Tile entry: row) {
                if (entry instanceof WizOrPath) {
                    ((WizOrPath)entry).assignProperties(); // type cast into path type

                    // if it is a terminal path (and not a wizard hut), it is now a spawn
                    if (entry instanceof Path && ((Path)entry).termArray[0] != Direction.NONE) {
                        this.routes.put((Path)entry, null); 
                        // add it to the spawn list, no route yet
                        System.out.println(entry + " is a spawn");
                    }
                }
                if (entry instanceof Path) {
                    ((Path)entry).updatePath();
                    System.out.println(entry + " assigned rotation");
                }
            }
        }
    }

    /**
     * create route from terminal paths (spawns) to wizard via optimal paths.
     * @param spawn spawn to create route from
     * @return list of optimal path directions to get to wizard
     */
    public ArrayList<Direction> createRoute(Path spawn)
    { 
        ArrayList<Direction> route = new ArrayList<Direction>();
        WizOrPath current = spawn;

        while(!(current instanceof Wizard)) {
            System.out.println(current + " optimal direction: " + ((Path)current).optimal);
            Direction currentDirection = ((Path)current).optimal;
            route.add(currentDirection); // add optimal direction to route
            current = (WizOrPath)current.adj.get(currentDirection); // move to next path
        }
        return route;
    }

    /**
     * create routes for each spawn by calling createRoute on each terminal path
     */
    public void createRoutes() 
    { 
        for(Path spawn: this.routes.keySet()) {
            this.routes.put(spawn, this.createRoute(spawn));
        }
    }

    /**
     * Creates next wave and adds it to wave list.
     * If its the last wave, set last wave to true.
     * Otherwise, set wave time to be the next wave time.
     */
    public void nextWave()
    {
        this.waveNumber++;
        this.waveList.add(new Wave(
            this.data.getJSONArray("waves").getJSONObject(waveNumber), this.routes, this.app
        ));

        if (this.waveNumber == this.data.getJSONArray("waves").size() - 1) { // if its the last wave
            this.lastWave = true;
        } else{ // otherwise (if its not the last wave)
            this.waveTime = this.addWaveTimes();
        }
        
    }

    /**
     * Converts mouse coordinates to tile coordinates.
     * Assume only for map, not ui.
     * Used to determine where to place towers, and which to upgrade.
     * @param x mouse x coordinate
     * @param y mouse y coordinate
     * @return array of tile coordinates
     */
    static int[] mouse2Tile(int x, int y)
    {
        int[] tileCords = new int[2];
        tileCords[0] = Math.floorDiv(x, Tile.CELLSIZE);
        tileCords[1] = Math.floorDiv(y - Tile.TOPBAR, Tile.CELLSIZE);
        return tileCords;
    }

    /**
     * Places tower on map (in matrix) if player can afford it, and grass tile.
     * Deducts mana and provides initial upgrades accordingly.
     * @param x mouse x coordinate
     * @param y mouse y coordinate
     * @param initialRangeLevel whether tower was placed in range mode
     * @param initialFiringSpeedLevel whether tower was placed in firing speed mode
     * @param initialDamageLevel whether tower was placed in damage mode
     * @return whether tower was placed
     */
    public boolean place(
        int x, int y, boolean initialRangeLevel, 
        boolean initialFiringSpeedLevel, boolean initialDamageLevel)
    {
        int noOfUpgrades = (initialRangeLevel ? 1 : 0) + 
                           (initialFiringSpeedLevel ? 1 : 0) + 
                           (initialDamageLevel ? 1 : 0);
        int[] tileCords = mouse2Tile(x, y);

        if (this.land[tileCords[0]][tileCords[1]] instanceof Grass)
        { // if its grass
            while(
                noOfUpgrades >= 0 &&
                !this.mana.updateMana(-1 * (this.getTowerCost() + 20 * noOfUpgrades))
                ) { // reduce price until affordable
                noOfUpgrades--;
            } if (noOfUpgrades < 0) {
                return false;
            } // if not affordable, return false
            
            boolean[] upgrades = this.determineUpgrades(
                noOfUpgrades, initialRangeLevel, initialFiringSpeedLevel, initialDamageLevel
                );
            Tower tower = new Tower(
                tileCords[0], tileCords[1], 
                this.getInitialTowerRange(),
                this.getInitialTowerFiringSpeed(),
                this.getInitialTowerDamage(),
                upgrades[0], upgrades[1], upgrades[2], this
            );
            this.land[tileCords[0]][tileCords[1]] = tower;
            this.towerList.add(tower);

            return true;
        }
        return false;
    }

    /**
     * Applies appropriate upgrade order.
     * This is based on requested upgrades and number upgrades player can afford.
     * @param noOfUpgrades number of upgrades player can afford
     * @param initialRangeLevel whether player wants to upgrade range
     * @param initialFiringSpeedLevel whether player wants to upgrade firing speed
     * @param initialDamageLevel whether player wants to upgrade damage
     * @return array of booleans representing which upgrades to apply
     */
    public boolean[] determineUpgrades(
        int noOfUpgrades, boolean initialRangeLevel, 
        boolean initialFiringSpeedLevel, boolean initialDamageLevel
        )
    {
        boolean range = false;
        boolean speed = false;
        boolean dmg = false;
        // set booleans to true if they are to be upgraded

        if (noOfUpgrades == 3) {
            range = true;
            speed = true;
            dmg = true;
        } else if (noOfUpgrades == 2)
        {
            if (initialRangeLevel) {
                range = true;
            } 
            if (initialFiringSpeedLevel) {
                speed = true;
            } 
            if (initialDamageLevel && !(range && speed)) {
                dmg = true;
            }
        } else if (noOfUpgrades == 1)
        {
            if (initialRangeLevel) {
                range = true;
            } else if (initialFiringSpeedLevel) {
                speed = true;
            } else{
                dmg = true;
            }
        } // else if noOfUpgrades == 0, leave everything false  

        return new boolean[] {range, speed, dmg};
    }

    /**
     * This method does multiple things:
     * Which tower is to be upgraded
     * What upgrades are to be applied based on what can be afforded
     * How much mana to deduct, and doing so
     * @param x mouse x coordinate
     * @param y mouse y coordinate
     * @param range whether in range mode
     * @param speed whether in speed mode
     * @param dmg whether in damage mode
     */
    public void upgrade(int x, int y, boolean range, boolean speed, boolean dmg)
    {
        int[] tileCords = mouse2Tile(x, y);
        if (this.land[tileCords[0]][tileCords[1]] instanceof Tower)
        {
            Tower tower = (Tower)this.land[tileCords[0]][tileCords[1]];
            int rangeInt = range ? 1 : 0;
            int speedInt = speed ? 1 : 0;
            int dmgInt = dmg ? 1 : 0;

            if (
                range && speed && dmg &&
                this.mana.updateMana(
                    -1 * (rangeInt * tower.getRangeCost() + 
                    speedInt * tower.getFiringSpeedCost() + 
                    dmgInt * tower.getDamageCost())
                )
            ) {
                tower.upgradeRange();
                tower.upgradeFiringSpeed();
                tower.upgradeDamage();
            } else if (
                range && speed &&
                this.mana.updateMana(
                    -1 * (rangeInt * tower.getRangeCost() + 
                    speedInt * tower.getFiringSpeedCost())
                )
            ) {
                tower.upgradeRange();
                tower.upgradeFiringSpeed();
            } else if (
                range && dmg &&
                this.mana.updateMana(
                    -1 * (rangeInt * tower.getRangeCost() + 
                    dmgInt * tower.getDamageCost())
                )
            ) {
                tower.upgradeRange();
                tower.upgradeDamage();
            } else if (
                speed && dmg &&
                this.mana.updateMana(
                    -1 * (speedInt * tower.getFiringSpeedCost() + 
                    dmgInt * tower.getDamageCost())
                )
            ) {
                tower.upgradeFiringSpeed();
                tower.upgradeDamage();
            } else if (
                range &&
                this.mana.updateMana(
                    -1 * (rangeInt * tower.getRangeCost())
                )
            ) {
                tower.upgradeRange();
            } else if (
                speed &&
                this.mana.updateMana(
                    -1 * (speedInt * tower.getFiringSpeedCost())
                )
            ) {
                tower.upgradeFiringSpeed();
            } else if (
                dmg &&
                this.mana.updateMana(
                    -1 * (dmgInt * tower.getDamageCost())
                )
            ) {
                tower.upgradeDamage();
            } // else do nothing
            // cycle through upgrades you could afford to do in order of range, speed, dmg
        }
    }

    /**
     * Draws range circle around tower cursor is over under ui, over tiles
     * @param app needs app to draw
     */
    public void drawRangeCircle(App app)
    {
        Tile potentialTower = this.mouse2Land(app.mouseX, app.mouseY);

        if (potentialTower instanceof Tower)
        {
            Tower tower = (Tower)potentialTower;
            // draw tower
            tower.draw(this.app);

            // draw circle around tower
            int centerX = App.CELLSIZE * tower.getX() + App.CELLSIZE / 2;
            int centerY = App.CELLSIZE * tower.getY() + App.TOPBAR + App.CELLSIZE / 2;
            float diameter = (float)(tower.getRange() * 2);
            app.noFill();
            app.stroke(255, 255, 0); // yellow
            app.strokeWeight(2);
            app.ellipse(centerX, centerY, diameter, diameter);
        }
    }

    /**
     * Converts mouse coordinates to corresponding tile object
     * @param x mouse x coordinate
     * @param y mouse y coordinate
     * @return corresponding tile object
     */
    public Tile mouse2Land(int x, int y)
    {
        if (Ui.isMouseInMap(x, y)) {
            int[] tileCords = mouse2Tile(x, y);
            return this.land[tileCords[0]][tileCords[1]];
        } else{
            return null; // potential null pointer exception
        }
    }

    /**
     * Applies map logic each frame to:
     * each wave (+ wave time),
     * mana,
     * towers,
     * screen poison.
     */
    public void tick()
    {
        // tick each wave
        if (!(waveNumber == 0 && this.waveTime > this.addWaveTimes()))
        { // after 1st pre wave time
            Iterator<Wave> waveIterator = this.waveList.iterator(); // use for hasNext() method
            while(waveIterator.hasNext()) { // tick all waves in array
                Wave wave = waveIterator.next();
                wave.tick();

                if (wave.getWaveComplete()) {
                    waveIterator.remove();
                } // remove waves with all monsters spawned and killed
            }
            this.waveTime -= app.rate;

            if (this.lastWave && this.waveList.size() == 0) { 
                this.app.onWinScreen = true;
            } // win, if its the last wave and all monsters are dead

            if (this.waveTime < 0 && !this.lastWave) {
                this.nextWave();
            }   
        } else{
            this.waveTime -= app.rate;
            System.out.println("Pre wave time: " + this.waveTime);
        }

        // tick mana
        this.mana.tick(this.app); 

        // tick towers;
        for(Tower tower: this.towerList) {
            tower.tick();
        }

        // poison
        if (this.poison && this.poisonFrames <= 0) {
            this.poison = false;
            this.poisonFrames = this.app.poisonFrames;
        } else{
            this.poisonFrames -= this.app.rate;
        }
    }

    /**
     * Draws map onto screen, from bottom to top:
     * each tile in matrix,
     * each wave,
     * wizard house (seperate from matrix as to be drawn above monsters and other tiles),
     * range circles around tower cursor is over under ui
     * all towers' fireballs'
     * @param app needs app to draw
     */
    public void draw(PApplet app)
    { 
        // draw each tile in matrix onto screen
        for(Tile[] row: this.land) {
            for(Tile entry: row) {
                if (!(entry instanceof Wizard)) { // draw it if its not a wizard house
                    entry.draw(app); 
                } else{ // if it is a wizard house, draw grass under the wizard house
                    Tile wizGrass = new Grass(wizCordsXY[0], wizCordsXY[1], this); // change this
                    wizGrass.draw(app);
                }
            }
        }
        
        // draw each wave
        for(Wave wave: this.waveList) {
            wave.draw();
        }

        // draw wizard house above monsters and other tiles
        this.land[wizCordsXY[0]][wizCordsXY[1]].draw(app); 

        // draw range circles around tower cursor is over under ui, over tiles
        drawRangeCircle(this.app);

        // draw all towers' fireballs'
        for(Tower tower: this.towerList) {
            for(Fireball fireball: tower.getProjectiles()) {
                fireball.draw();
            }
        }
    }
}