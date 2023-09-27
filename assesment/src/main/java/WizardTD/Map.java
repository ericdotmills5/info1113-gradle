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
 *  monsters that take you down to 0hp kill you (currently can live with 0 mana)
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


    public Map(Scanner scan, App app){
        this.app = app;
        this.land = this.scan2Matrix(scan);
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

    public Tile[][] scan2Matrix(Scanner scan){
        Tile[][] matrix = new Tile[BOARD_WIDTH][BOARD_WIDTH]; // assume level is sqrmapsize
        int i;
        
        for(int j = 0; j < BOARD_WIDTH; j++){ // iterate through each line
            i = 0;
            for(char c: scan.nextLine().toCharArray()){ // iterate through each letter
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
        scan.close(); // close scanner
        return matrix;
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

    public void tick(){

        // tick each wave
        if(!(waveNumber == 0 && this.waveTime > this.addWaveTimes())){ // after 1st pre wave time
            for(Wave wave: this.waveList){
                wave.tick();
            }
            this.waveTime -= app.rate;

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








