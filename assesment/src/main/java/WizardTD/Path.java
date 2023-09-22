package WizardTD;

import processing.core.PImage;

public class Path extends WizOrPath{
    private int pathType; // 1 straight, 2 rAngle, 3 T, 4 cross
    private int rotates; // how many 90 degree anticlockwise rotations?

    public Path(int x, int y, Map map){
        super(x, y, map);
    }

    public void updatePath(){ 
        this.pathTypeRotate();
        this.sprite = this.createImage();
    }

    public void pathTypeRotate(){ // 0 1 2 3 0 index as opposed to 1 index
        boolean[] pathArray = new boolean[this.adj.length - 1]; // ignore itself
        int adjs = 0;
        int not = 0; // where path isnt for 3
        int evens = 0;

        for(int i = 0; i < pathArray.length; i++){ // look through adjacent tuple
            if(this.adj[i + 1] instanceof Path){
                pathArray[i] = true;
                adjs++;

                if(i % 2 == 0){
                    evens++;
                }
            } else{
                pathArray[i] = false;
                not = i;
            }
        }

        switch(adjs){ // diagnose path type and rotations
            case 4:
                this.pathType = 3;
                this.rotates = 0; // no need to rotate cross path
                return;
            case 3:
                this.pathType = 2;
                this.rotates = (not + 1) % 4 - 2; // might cause logic errors
                return;
            case 2: // assuming default is 2,3
                if(evens == 1){ // right angle path
                    this.pathType = 1;

                    if(pathArray[0]){ // if right adjacent path
                        if(pathArray[1]){ // and top path
                            this.rotates = 2;
                            return;
                        } else{ // right + bottom
                            this.rotates = 1;
                            return;
                        }
                    } else if(pathArray[1]){ // if top path
                        this.rotates = 3; // top + left
                        return;
                    } else{
                        this.rotates = 0; // must be left + bottom
                        return; 
                    }
                } else{ // straight path
                    this.pathType = 0;

                    if(evens == 2){ // up and down
                        this.rotates = 0;
                        return;
                    } else{ // odds 2, left and right 
                        this.rotates = 1;
                        return;
                    }
                }
            case 1: // terminal path
                this.pathType = 0;
                if(evens == 1){ // up OR down
                    this.rotates = 0;
                    return;
                }else{ // left OR right
                    this.rotates = 1;
                    return;
                } /// fix logic
            }
        System.out.println("Error diagnosing path type " + this);
        return;
    }

    public PImage createImage(){
        PImage noRotate;

        noRotate = this.map.getApp().loadImage( // assume string int concacenation
            "src/main/resources/WizardTD/path" + this.pathType + ".png", null
            );
        return this.map.getApp().rotateImageByDegrees(noRotate, this.rotates * -90);
    }
}
