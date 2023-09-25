package WizardTD;

import java.util.Scanner;
import processing.core.PApplet;
import processing.data.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

/*
 * Edge cases:
 *  Path on terminal corner
 *  Wizard hut on side
 *  Wizard hut on corner
 *  
*/

enum Direction{ // add directions to previously integer stuff
    UP, DOWN, LEFT, RIGHT, NONE;
}

public class Map {
    static final int BOARD_WIDTH = App.BOARD_WIDTH;

    private Tile[][] land = new Tile[BOARD_WIDTH][BOARD_WIDTH];
    private int[] wizCordsXY = new int[2];
    private App app;
    private Wizard wizard;
    private HashMap<Path, ArrayList<Direction>> routes = 
    new HashMap<Path, ArrayList<Direction>>(); // terminal path, assosiated route
    private JSONObject data;
    private Wave wave; // turn into wave list for many waves


    public Map(String fileLoc, App app){
        this.app = app;
        this.land = this.scan2Matrix(fileIO(fileLoc));
        System.out.println("matrix made");

        this.updateAllPaths();

        System.out.println("Paths rotated");

        this.wizard.determineWizDists();

        this.createRoutes();
        
        // assume this is only fileIO method call

        this.data = app.loadJSONObject(app.configPath);

        this.wave = new Wave(this.data.getJSONArray("waves").getJSONObject(1), this.routes, this.app);
        // test with 1st wave
    }

    public Tile[][] getLand(){
        return this.land;
    }

    public App getApp(){
        return this.app;
    }

    public HashMap<Path, ArrayList<Direction>> getRoutes(){
        return this.routes;
    }

    // q: how do i import a json file
    // a

    static Scanner fileIO(String loc){ // read file into scanner obj
        File f = new File(loc);
        Scanner scan;
        try{
            scan = new Scanner(f);
        } catch (FileNotFoundException e){
            System.out.println("File not found!");
            return null; // this might be dangerous
        }
        return scan;
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

    public void tick(){
        this.wave.tick();
    }

    public void draw(PApplet app){ // draw each element in matrix onto screen
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
        this.land[wizCordsXY[0]][wizCordsXY[1]].draw(app); // draw wizard house last so it is drawn on top layer

        this.wave.draw();
    }

    


}








