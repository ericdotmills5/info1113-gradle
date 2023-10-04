package WizardTD;

import processing.core.PApplet;
import java.util.HashMap;

abstract class WizOrPath extends Tile {
    public static final int CELLSIZE = App.CELLSIZE;

    protected int wizDist = 0;
    protected HashMap<Direction, Tile> adj = new HashMap<Direction, Tile>();
    protected Direction terminal; // Not: 0, edge on right:1, edge up: 2...
    protected Direction optimal; // from wiz dist

    /**
     * Wizard and paths have similarities that deserve to be abstracted
     * @param x x tile cordinates [0, 19]
     * @param y y tile cordinates [0, 19]
     * @param map map class it is generated from
     */
    public WizOrPath(int x, int y, Map map)
    {
        super(x, y, map);
    }

    /**
     * Determines:
     * 1. Whether it's a terminal tile
     * 2. Adjacent tiles
     */
    public void assignProperties()
    {
        this.terminal = this.findTerminality();
        this.adj = this.buildAdj();
    }

    /**
     * Method to determine directions to wizard, recursivly calling adjacent tiles.
     * This allows each path to know the optimal direction to the wizard.
     */
    public void determineWizDists()
    {
        for(Tile i: this.adj.values()){ // for everyone around me
            if(
                i instanceof Path // if the adjacent tile is a path
                && (((Path)i).wizDist == 0 // and it either has no distance
                || ((Path)i).wizDist > this.wizDist + 1) // or it has a bigger distance
                ){ 
                ((Path)i).wizDist = this.wizDist + 1; // give it my better distance + 1
                System.out.println(i + " Distance: " + ((Path)i).wizDist);
                
                for(Direction j: ((Path)i).adj.keySet()){ // determine his optimal direction
                    if(((Path)i).adj.get(j) == this){ // if your currently iterating over me
                        ((Path)i).optimal = j; // then im the optimal direction
                        System.out.println(i + " optimal path direction " + j);
                    }
                }
                ((Path)i).determineWizDists(); // ask his friends to do it
            }
        }
    }

    /**
     * Based on a tile's position:
     * 1. Determine if it's a terminal tile
     * 2. If it is, return the direction the edge of the screen is in
     * @return the direction the edge of the screen is in
     */
    public Direction findTerminality()
    {
        switch(this.x){ // assume no paths on corners, for now
            case 19:
                return Direction.RIGHT;
            case 0:
                return Direction.LEFT;
        }
        switch(this.y){
            case 0:
                return Direction.UP;
            case 19:
                return Direction.DOWN;
        }
        return Direction.NONE;
    }

    /**
     * Called by each terminal tile to create hash map of optimal path
     * @return hash map with directions as keys and current tile as value
     */
    public HashMap<Direction, Tile> buildAdj()
    {
        HashMap<Direction, Tile> adj = new HashMap<Direction, Tile>();
        
        if(this.terminal != Direction.RIGHT){ // enter tile to the right
            adj.put(Direction.RIGHT, this.map.getLand()[this.x + 1][this.y]);
        } else{ // otherwise, enter null
            adj.put(Direction.RIGHT, null);
        }
        if(this.terminal != Direction.UP){ // enter tile above
            adj.put(Direction.UP, this.map.getLand()[this.x][this.y - 1]);
        } else{
            adj.put(Direction.UP, null);
        }
        if(this.terminal != Direction.LEFT){ // enter left
            adj.put(Direction.LEFT, this.map.getLand()[this.x - 1][this.y]); 
        } else{
            adj.put(Direction.LEFT, null);
        }
        if(this.terminal != Direction.DOWN){ // enter bellow
            adj.put(Direction.DOWN, this.map.getLand()[this.x][this.y + 1]); 
        } else{
            adj.put(Direction.DOWN, null);
        }
        
        return adj;
    }
}

class Wizard extends WizOrPath {

    /**
     * Wizard is special case of WizOrPath because:
     * 1. It has a wizard distance of 0 (thus not optimal direction)
     * 2. It has a cool sprite
     * @param x tile x coordinate [0, 19]
     * @param y tile y coordinate [0, 19]
     * @param map map it is generated from
     */
    public Wizard(int x, int y, Map map)
    {
        super(x, y, map);
        this.wizDist = 0;
        this.optimal = Direction.NONE;
        this.sprite = map.getApp().loadImage("src/main/resources/WizardTD/wizard_house.png");
    }
    /**
     * Draw function needs to be overriden because wizard house needs to be shifted
     */
    @Override
    public void draw(PApplet app){
        app.image(
            this.sprite, this.x * CELLSIZE + wizShiftX, this.y * CELLSIZE + wizShiftY + TOPBAR
            ); // wizard house needs to be shifted
    }
}
