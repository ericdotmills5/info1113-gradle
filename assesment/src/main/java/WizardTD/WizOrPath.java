package WizardTD;

import java.util.HashMap;

abstract class WizOrPath extends Tile {
    public static final int CELLSIZE = App.CELLSIZE;

    protected int wizDist = 0;
    protected HashMap<Direction, Tile> adj = new HashMap<Direction, Tile>();
    //protected Direction terminal; // direction of terminality
    protected Direction optimal; // from wiz dist
    protected Direction[] termArray = new Direction[2]; // array indicating terminal directions
    // eg. {left, NONE}: just left is off screen; {up, right}: tile is in the top right corner

    /**
     * Wizard and paths have similarities that deserve to be abstracted
     * @param x x tile cordinates [0, 19]
     * @param y y tile cordinates [0, 19]
     * @param map map class it is generated from
     */
    public WizOrPath(int x, int y, Map map, String spritePath) {
        super(x, y, map, spritePath);
    }

    /**
     * returns array of terminal directions
     * @return array of terminal directions
     */
    public Direction[] getTerminals() {
        return this.termArray;
    }

    /**
     * Determines:
     * 1. Whether it's a terminal tile
     * 2. Adjacent tiles
     */
    public void assignProperties() {
        this.termArray = this.findTerminality();
        this.adj = this.buildAdj();
    }

    /**
     * Based on a tile's position:
     * 1. Determine if it's a terminal tile
     * 2. If it is, return the direction the edge of the screen is in
     * @return the direction the edge of the screen is in
     */
    public Direction[] findTerminality() {
        Direction[] termArray = new Direction[2];
        int index = 0;

        switch(this.x) { // add directions to array if terminal
            case 19:
                termArray[index] = Direction.RIGHT;
                index++;
                break; // cant be both left and right
            case 0:
                termArray[index] = Direction.LEFT;
                index++;
        }
        switch(this.y) {
            case 0:
                termArray[index] = Direction.UP;
                index++;
                break; // cant be both up and down
            case 19:
                termArray[index] = Direction.DOWN;
                index++;
        } // assume max 2 terminals
        
        for (int i = index; i < 2; i++) { // fill rest of array with NONE
            termArray[i] = Direction.NONE;
        }
        System.out.println(this + " terminals: " + termArray[0] + " " + termArray[1]);

        return termArray;
    }

    /**
     * from terminal paths, returns a boolean array of directions that exist (not terminal) 
     * indexes in the array correspond to: {RIGHT EXISTS?, UP EXISTS?, LEFT EXISTS?, DOWN EXISTS?}
     * @return
     */
    public boolean[] findDirectionsThatExist() {
        boolean[] directionsThatExist = new boolean[4];
        // {RIGHT EXISTS?, UP EXISTS?, LEFT EXISTS?, DOWN EXISTS?}

        // fill with true
        for (int i = 0; i < 4; i++) {
            directionsThatExist[i] = true;
        }

        // check if terminal, ie DNE
        for (Direction dir: this.termArray) {
            if (dir == Direction.RIGHT) {
                directionsThatExist[0] = false;
            } else if (dir == Direction.UP) {
                directionsThatExist[1] = false;
            } else if (dir == Direction.LEFT) {
                directionsThatExist[2] = false;
            } else if (dir == Direction.DOWN) {
                directionsThatExist[3] = false;
            }
        }
        return directionsThatExist;
    }

    /**
     * Called by each terminal tile to create hash map of optimal path
     * @return hash map with directions as keys and current tile as value
     */
    public HashMap<Direction, Tile> buildAdj() {
        HashMap<Direction, Tile> adj = new HashMap<Direction, Tile>();

        // determine where terminalities are
        boolean[] directionsThatExist = this.findDirectionsThatExist();
        // {RIGHT EXISTS?, UP EXISTS?, LEFT EXISTS?, DOWN EXISTS?}
        
        if (directionsThatExist[0]) { // enter tile to the right
            adj.put(Direction.RIGHT, this.map.getLand()[this.x + 1][this.y]);
        } else { // otherwise, enter null
            adj.put(Direction.RIGHT, null);
        }
        if (directionsThatExist[1]) { // enter tile above
            adj.put(Direction.UP, this.map.getLand()[this.x][this.y - 1]);
        } else {
            adj.put(Direction.UP, null);
        }
        if (directionsThatExist[2]) { // enter left
            adj.put(Direction.LEFT, this.map.getLand()[this.x - 1][this.y]); 
        } else {
            adj.put(Direction.LEFT, null);
        }
        if (directionsThatExist[3]) { // enter bellow
            adj.put(Direction.DOWN, this.map.getLand()[this.x][this.y + 1]); 
        } else {
            adj.put(Direction.DOWN, null);
        }
        
        return adj;
    }

    /**
     * Method to determine directions to wizard, recursivly calling adjacent tiles.
     * This allows each path to know the optimal direction to the wizard. 
     * Sidenote, I came up with this pathfinding algorithm all by myself,
     * I am very proud of it! unless it already exists :(
     */
    public void determineWizDists() {
        for (Tile i: this.adj.values()) { // for everyone around me
            if (
                i instanceof Path // if the adjacent tile is a path
                && (((Path)i).wizDist == 0 // and it either has no distance
                || ((Path)i).wizDist > this.wizDist + 1) // or it has a bigger distance
                ) { 
                ((Path)i).wizDist = this.wizDist + 1; // give it my better distance + 1
                System.out.println(i + " Distance: " + ((Path)i).wizDist);
                
                // find the direction that leads to me and name it optimal
                System.out.println(((Path)i).adj.values());
                for (Direction j: ((Path)i).adj.keySet()) {
                    if (((Path)i).adj.get(j) == this) { // if your currently iterating over me
                        ((Path)i).optimal = j; // then im the optimal direction
                        System.out.println(i + " optimal path direction " + j);
                    }
                }
                ((Path)i).determineWizDists(); // ask his friends to do it
            }
        }
    }
}


