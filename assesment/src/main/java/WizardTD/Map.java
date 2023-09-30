package WizardTD;

import processing.core.PApplet;
import processing.data.JSONObject;
import processing.data.JSONArray;

import java.util.*;

/*
 * Edge cases:
 *  Path on terminal corner
 *  Wizard hut on side
 *  Wizard hut on corner
 *  restart upon loss takes too long to restart
 *  sumarise long if else statements
 * 
 * check ghost speeds are actually correct
 * check if integer config values can take floats
 * check if your passing any lists in by reference
 * check pasue/ff doesnt mess stuff up (check logic)
 * check mana spell math on all calculations
*/

enum Direction{ // add directions to previously integer stuff
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


    public Map(Iterable<String> mapIterable, App app){
        this.app = app;
        this.land = this.iterator2Matrix(mapIterable.iterator());
        System.out.println("matrix made");

        this.updateAllPaths();

        System.out.println("Paths rotated");

        this.wizard.determineWizDists();

        this.createRoutes();
        
        // assume this is only fileIO method call

        this.data = app.loadJSONObject(app.configPath);

        this.waveList.add(new Wave(
            this.data.getJSONArray("waves").getJSONObject(waveNumber), this.routes, this.app
        )); // add pre wave pause for 1st wave
        this.waveTime = this.addWaveTimes() + this.data.getJSONArray("waves").getJSONObject(this.waveNumber).getDouble("pre_wave_pause") * FPS;

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

    public Tile[][] getLand(){
        return this.land;
    }

    public App getApp(){
        return this.app;
    }

    public boolean getLastWave(){
        return this.lastWave;
    }

    public int getWaveNumber(){
        return this.waveNumber;
    }

    public double getWaveTime(){
        return this.waveTime;
    }

    public Mana getMana(){
        return this.mana;
    }

    public double getTowerCost(){
        return this.data.getDouble("tower_cost");
    }

    public double getInitialTowerRange(){
        return this.data.getDouble("initial_tower_range");
    }

    public double getInitialTowerFiringSpeed(){
        return this.data.getDouble("initial_tower_firing_speed");
    }

    public double getInitialTowerDamage(){
        return this.data.getDouble("initial_tower_damage");
    }

    public Tile[][] iterator2Matrix(Iterator<String> scan){
        Tile[][] matrix = new Tile[BOARD_WIDTH][BOARD_WIDTH]; // assume level is sqrmapsize
        int i;
        
        for(int j = 0; j < BOARD_WIDTH; j++){ // iterate through each line
            i = 0;
            for(char c: scan.next().toCharArray()){ // iterate through each letter
                switch(c){
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
            while(i < BOARD_WIDTH){ // fill trailing empty text with grass
                matrix[i][j] = new Grass(i, j, this);
                i++;
            }
            System.out.println("row " + j + " read");
        }
        return matrix;
    }

    public double addWaveTimes(){ // current wave time + next wave prewave pause * fps
        JSONArray waves = this.data.getJSONArray("waves");
        if(waves.size() == 1){ // force wave to be negative (endless)
            this.lastWave = true;
            return -1 * (this.data.getJSONArray("waves").getJSONObject(this.waveNumber).getDouble("pre_wave_pause") * FPS + 1);
        }
        return (this.data.getJSONArray("waves").getJSONObject(this.waveNumber).getDouble("duration") 
        + data.getJSONArray("waves").getJSONObject(this.waveNumber + 1).getDouble("pre_wave_pause")) * FPS;
    }

    public HashMap<Path, ArrayList<Direction>> getRoutes(){
        return this.routes;
    }

    public void updateAllPaths(){ // iterate through paths to find type and orientation and terminal paths
        for(Tile[] row: this.land){
            for(Tile entry: row){
                if(entry instanceof WizOrPath){
                    ((WizOrPath)entry).assignProperties(); // type cast into path type

                    if(((WizOrPath)entry).terminal != Direction.NONE){ // if its a spawn
                        this.routes.put((Path)entry, null); // add it to the spawn list, no route yet
                    }
                }
                if(entry instanceof Path){
                    ((Path)entry).updatePath();
                    System.out.println(entry + " assigned rotation");
                }
            }
        }
    }

    // create route from terminal paths (spawns) to wizard via optimal paths
    public ArrayList<Direction> createRoute(Path spawn){ 
        ArrayList<Direction> route = new ArrayList<Direction>();
        WizOrPath current = spawn;

        while(!(current instanceof Wizard)){
            Direction currentDirection = ((Path)current).optimal;
            route.add(currentDirection); // add optimal direction to route
            current = (WizOrPath)current.adj.get(currentDirection); // move to next path
        }
        return route;
    }

    public void createRoutes(){ // create routes for each spawn
        for(Path spawn: this.routes.keySet()){
            this.routes.put(spawn, this.createRoute(spawn));
        }
    }

    public void nextWave(){
        this.waveNumber++;
        this.waveList.add(new Wave(
            this.data.getJSONArray("waves").getJSONObject(waveNumber), this.routes, this.app
        ));

        if(this.waveNumber == this.data.getJSONArray("waves").size() - 1){ // if its the last wave
            this.lastWave = true;
        } else{ // otherwise (if its not the last wave)
            this.waveTime = this.addWaveTimes();
        }
        
    }

    static int[] mouse2Tile(int x, int y)
    { // assume only called within map and not in ui
        int[] tileCords = new int[2];
        tileCords[0] = Math.floorDiv(x, Tile.CELLSIZE);
        tileCords[1] = Math.floorDiv(y - Tile.TOPBAR, Tile.CELLSIZE);
        return tileCords;
    }

    public boolean place(int x, int y, boolean initialRangeLevel, boolean initialFiringSpeedLevel, boolean initialDamageLevel)
    {
        int noOfUpgrades = (initialRangeLevel ? 1 : 0) + (initialFiringSpeedLevel ? 1 : 0) + (initialDamageLevel ? 1 : 0);
        int[] tileCords = mouse2Tile(x, y);
        
        /*boolean range = false;
        boolean speed = false;
        boolean dmg = false;*/

        if(this.land[tileCords[0]][tileCords[1]] instanceof Grass)
        { // if its grass
            while(
                noOfUpgrades >= 0 &&
                !this.mana.updateMana(-1 * (this.getTowerCost() + 20 * noOfUpgrades))
                ){ // reduce price until affordable
                noOfUpgrades--;
            } if(noOfUpgrades < 0){
                return false;
            } // if not affordable, return false

            /*if(noOfUpgrades == 3){
                range = true;
                speed = true;
                dmg = true;
            } else if(noOfUpgrades == 2)
            {
                if(initialRangeLevel){
                    range = true;
                } 
                if(initialFiringSpeedLevel){
                    speed = true;
                } 
                if(initialDamageLevel && !(range && speed)){
                    dmg = true;
                }
            } else if(noOfUpgrades == 1)
            {
                if(initialRangeLevel){
                    range = true;
                } else if(initialFiringSpeedLevel){
                    speed = true;
                } else{
                    dmg = true;
                }
            }  // set booleans to true if they are to be upgraded */
            
            boolean[] upgrades = this.determineUpgrades(noOfUpgrades, initialRangeLevel, initialFiringSpeedLevel, initialDamageLevel);
            this.land[tileCords[0]][tileCords[1]] = new Tower(
                tileCords[0], tileCords[1], 
                this.getInitialTowerRange(),
                this.getInitialTowerFiringSpeed(),
                this.getInitialTowerDamage(),
                upgrades[0], upgrades[1], upgrades[2], this
            );

            return true;
        }
        return false;
    }

    public boolean[] determineUpgrades(int noOfUpgrades, boolean initialRangeLevel, boolean initialFiringSpeedLevel, boolean initialDamageLevel)
    {
        boolean range = false;
        boolean speed = false;
        boolean dmg = false;
        // set booleans to true if they are to be upgraded

        if(noOfUpgrades == 3){
            range = true;
            speed = true;
            dmg = true;
        } else if(noOfUpgrades == 2)
        {
            if(initialRangeLevel){
                range = true;
            } 
            if(initialFiringSpeedLevel){
                speed = true;
            } 
            if(initialDamageLevel && !(range && speed)){
                dmg = true;
            }
        } else if(noOfUpgrades == 1)
        {
            if(initialRangeLevel){
                range = true;
            } else if(initialFiringSpeedLevel){
                speed = true;
            } else{
                dmg = true;
            }
        } // else if noOfUpgrades == 0, leave everything false  

        return new boolean[] {range, speed, dmg};
    }

    public void upgrade(int x, int y, boolean range, boolean speed, boolean dmg){
        int[] tileCords = mouse2Tile(x, y);
        if(this.land[tileCords[0]][tileCords[1]] instanceof Tower)
        {
            Tower tower = (Tower)this.land[tileCords[0]][tileCords[1]];
            int rangeInt = range ? 1 : 0;
            int speedInt = speed ? 1 : 0;
            int dmgInt = dmg ? 1 : 0;

            if(
                range && speed && dmg &&
                this.mana.updateMana(
                    -1 * (rangeInt * tower.getRangeCost() + 
                    speedInt * tower.getFiringSpeedCost() + 
                    dmgInt * tower.getDamageCost())
                )
            ){
                tower.upgradeRange();
                tower.upgradeFiringSpeed();
                tower.upgradeDamage();
            } else if(
                range && speed &&
                this.mana.updateMana(
                    -1 * (rangeInt * tower.getRangeCost() + 
                    speedInt * tower.getFiringSpeedCost())
                )
            ){
                tower.upgradeRange();
                tower.upgradeFiringSpeed();
            } else if(
                range && dmg &&
                this.mana.updateMana(
                    -1 * (rangeInt * tower.getRangeCost() + 
                    dmgInt * tower.getDamageCost())
                )
            ){
                tower.upgradeRange();
                tower.upgradeDamage();
            } else if(
                speed && dmg &&
                this.mana.updateMana(
                    -1 * (speedInt * tower.getFiringSpeedCost() + 
                    dmgInt * tower.getDamageCost())
                )
            ){
                tower.upgradeFiringSpeed();
                tower.upgradeDamage();
            } else if(
                range &&
                this.mana.updateMana(
                    -1 * (rangeInt * tower.getRangeCost())
                )
            ){
                tower.upgradeRange();
            } else if(
                speed &&
                this.mana.updateMana(
                    -1 * (speedInt * tower.getFiringSpeedCost())
                )
            ){
                tower.upgradeFiringSpeed();
            } else if(
                dmg &&
                this.mana.updateMana(
                    -1 * (dmgInt * tower.getDamageCost())
                )
            ){
                tower.upgradeDamage();
            } // else do nothing
            // cycle through upgrades you could afford to do in order of range, speed, dmg
        }
    }

    public void tick(){

        // tick each wave
        if(!(waveNumber == 0 && this.waveTime > this.addWaveTimes()))
        { // after 1st pre wave time
            Iterator<Wave> waveIterator = this.waveList.iterator(); // use for hasNext() method
            while(waveIterator.hasNext()){ // tick all waves in array
                Wave wave = waveIterator.next();
                wave.tick();

                if(wave.getWaveComplete()){
                    waveIterator.remove();
                } // remove waves with all monsters spawned and killed
            }
            this.waveTime -= app.rate;

            if (this.lastWave && this.waveList.size() == 0) { 
                this.app.onWinScreen = true;
            } // win, if its the last wave and all monsters are dead

            if(this.waveTime < 0 && !this.lastWave){
                this.nextWave();
            }   
        } else{
            this.waveTime -= app.rate;
            System.out.println("Pre wave time: " + this.waveTime);
        }

        this.mana.tick(this.app);
    }

    public void draw(PApplet app){ // draw each element in matrix onto screen

        // draw each tile
        for(Tile[] row: this.land){
            for(Tile entry: row){
                if(!(entry instanceof Wizard)){ // draw it if its not a wizard house
                    entry.draw(app); 
                } else{ // if it is a wizard house, draw grass under the wizard house
                    Tile wizGrass = new Grass(wizCordsXY[0], wizCordsXY[1], this); // change this
                    wizGrass.draw(app);
                }
            }
        }
        
        // draw each wave
        for(Wave wave: this.waveList){
            wave.draw();
        }

        this.land[wizCordsXY[0]][wizCordsXY[1]].draw(app); // draw wizard house last so it is drawn on top layer
    }
}








