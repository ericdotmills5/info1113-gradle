package WizardTD;

import processing.core.PApplet;

abstract class WizOrPath extends Tile{
    protected int wizDist = 0;
    protected Tile[] adj = new Tile[5];
    protected int terminal; // Not: 0, edge on right:1, edge up: 2...
    protected int optimal; // from wiz dist

    public WizOrPath(int x, int y, Map map){
        super(x, y, map);
    }

    public void assignProperties(){
        this.terminal = this.findTerminality();
        this.adj = this.buildAdj();
    }

    public void determineWizDists(){
        for(Tile i: this.adj){ // for everyone around me
            if(
                i instanceof Path // if the adjacent tile is a path
                && (((Path)i).wizDist == 0 // and it either has no distance
                || ((Path)i).wizDist > this.wizDist + 1)){ // or it has a bigger distance

                ((Path)i).wizDist = this.wizDist + 1; // give it my better distance + 1
                System.out.println(i + " Distance: " + ((Path)i).wizDist);

                for(int j = 0; j < 5; j++){ // determine his optimal direction
                    if(((Path)i).adj[j] == this){ // if your currently iterating over me
                        ((Path)i).optimal = j; // then im the optimal direction
                        System.out.println(i + " optimal path direction " + j);
                    }
                }
                ((Path)i).determineWizDists(); // ask his friends to do it
            }
        }
    }

    public int findTerminality(){ // returns int characterising edge on screen
        switch(this.x){ // assume no paths on corners
            case 19:
                return 1;
            case 0:
                return 3;
        }
        switch(this.y){
            case 0:
                return 2;
            case 19:
                return 4;
        }
        return 0;
    }

    public Tile[] buildAdj(){
        Tile[] adj = new Tile[5];
        adj[0] = this; // probably useless, enter itself
        
        if(this.terminal != 1){ // enter tile to the right
            adj[1] = this.map.getLand()[this.x + 1][this.y];
        }
        if(this.terminal != 2){ // enter tile above
            adj[2] = this.map.getLand()[this.x][this.y - 1]; 
        }
        if(this.terminal != 3){ // enter left
            adj[3] = this.map.getLand()[this.x - 1][this.y]; 
        }
        if(this.terminal != 4){ // enter bellow
            adj[4] = this.map.getLand()[this.x][this.y + 1]; 
        }
        
        System.out.print(this + " is adjacent to: ");
        for(Tile i: adj){
            System.out.print(i + "; ");
        }
        System.out.println();

        return adj;
    }
}

class Wizard extends WizOrPath{
    public Wizard(int x, int y, Map map){
        super(x, y, map);
        this.wizDist = 0;
        this.optimal = 0;
        this.sprite = map.getApp().loadImage("src/main/resources/WizardTD/wizard_house.png");
    }

    @Override
    public void draw(PApplet app){
        app.image(
            this.sprite, this.x * tileSize + wizShiftX, this.y * tileSize + wizShiftY
            ); // wizard house needs to be shifted
    }
}
