package WizardTD;

import processing.core.PImage;

public class Path extends WizOrPath {
    private int pathType; // 0 straight, 1 rAngle, 2 T, 3 cross
    private int rotates; // how many 90 degree anticlockwise rotations?

    /**
     * Generic constructor for path
     * @param x x cordinate of path [0,19]
     * @param y y cordinate of path [0,19]
     * @param map map it is generated from
     */
    public Path(int x, int y, Map map)
    {
        super(x, y, map);
    }

    /**
     * Calls pathTypeRotate() and creates sprite based on diagnosis
     */
    public void updatePath(){ 
        this.pathTypeRotate();
        this.createImage();
    }

    /**
     * Based on adjecent paths: diagnoses path rotation and type.
     * Path type 0 = straight, 1 = rAngle, 2 = T, 3 = cross.
     * Rotates is how many 90 degree anticlockwise rotations the source sprite needs.
     * Originally used modular arithmetic to create diagnosis, but this is easier to understand.
     */
    public void pathTypeRotate()
    {
        boolean left = false;
        boolean right = false;
        boolean up = false;
        boolean down = false;

        if(
            this.terminal == Direction.LEFT || this.adj.get(Direction.LEFT) instanceof WizOrPath
        ){
            left = true;
        }
        if(
            this.terminal == Direction.RIGHT || this.adj.get(Direction.RIGHT) instanceof WizOrPath
        ){
            right = true;
        }
        if(
            this.terminal == Direction.UP || this.adj.get(Direction.UP) instanceof WizOrPath
        ){
            up = true;
        }
        if(
            this.terminal == Direction.DOWN || this.adj.get(Direction.DOWN) instanceof WizOrPath
        ){
            down = true;
        }

        if(left && right && up && down){
            this.pathType = 3;
            this.rotates = 0;
            return;
        } else if(left && right && up){
            this.pathType = 2;
            this.rotates = 2;
            return;
        } else if(left && right && down){
            this.pathType = 2;
            this.rotates = 0;
            return;
        } else if(left && up && down){
            this.pathType = 2;
            this.rotates = 3;
            return;
        } else if(right && up && down){
            this.pathType = 2;
            this.rotates = 1;
            return;
        } else if(left && right){
            this.pathType = 0;
            this.rotates = 0;
            return;
        } else if(up && down){
            this.pathType = 0;
            this.rotates = 1;
            return;
        } else if(left && up){
            this.pathType = 1;
            this.rotates = 3;
            return;
        } else if(left && down){
            this.pathType = 1;
            this.rotates = 0;
            return;
        } else if(right && up){
            this.pathType = 1;
            this.rotates = 2;
            return;
        } else if(right && down){
            this.pathType = 1;
            this.rotates = 1;
            return;
        } else{
            System.out.println("Error diagnosing path type " + this);
            return;
        }
    }

    /**
     * Creates sprite based on pathType and rotates
     */
    public void createImage()
    {
        PImage noRotate;

        noRotate = this.map.getApp().loadImage( // assume string int concacenation
            "src/main/resources/WizardTD/path" + this.pathType + ".png"
        );
        this.sprite = this.map.getApp().rotateImageByDegrees(noRotate, this.rotates * -90);
    }
}