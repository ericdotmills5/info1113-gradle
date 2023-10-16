package WizardTD;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import processing.data.JSONObject;
import java.util.Random;

public class Wave {
    private ArrayList<Monster> monsters = new ArrayList<Monster>();
    private HashMap<Path, ArrayList<Direction>> rotes = new HashMap<Path, ArrayList<Direction>>();
    private JSONObject waveData;
    private App app;
    private int framesPerSpawn;
    private int waveFrames;
    private int currentFrame;
    private ArrayList<Integer> monsterTypeCounts = new ArrayList<>();
    private int monstersRemaining = 0;
    private Path[] spawnPaths;
    private boolean waveComplete = false;

    /**
     * constructer for each wave
     * @param waveData JSON object containing wave data
     * @param rotes HashMap of spawns and their rotes to wizard tower
     * @param app App object to draw with
     */
    public Wave(JSONObject waveData, HashMap<Path, ArrayList<Direction>> rotes, App app) {
        this.rotes = rotes;
        this.waveData = waveData;
        this.app = app;
        this.waveFrames = (int)(waveData.getDouble("duration") * App.FPS);
        this.spawnPaths = rotes.keySet().toArray(new Path[rotes.size()]);

        for(int i = 0; i < waveData.getJSONArray("monsters").size(); i++) {
            this.monsterTypeCounts.add(
                waveData.getJSONArray("monsters").getJSONObject(i).getInt("quantity")
            );
            this.monstersRemaining += this.monsterTypeCounts.get(i);
        } // fill with number of each type of monster remaining, and total monsters remaining      

        if (this.monstersRemaining == 0) {
            this.framesPerSpawn = 0; // prevent division by 0 error
        } else {
        this.framesPerSpawn = this.waveFrames / this.monstersRemaining;
        }

        System.out.println("Wave created");
    }

    /**
     * getter for wave data
     * @return JSONObject of wave data
     */
    public JSONObject getData() {
        return this.waveData;
    }

    /**
     * getter for wave complete
     * @return true if wave is complete
     */
    public boolean getWaveComplete() {
        return this.waveComplete;
    }

    /**
     * getter for monsters array of all monsters spawned by wave
     * @return ArrayList of all monsters spawned by wave
     */
    public ArrayList<Monster> getMonsters() {
        return this.monsters;
    }

    /**
     * iterates through all monsters in array and ticks them by creating an iterator.
     * removes post death animation monsters from array (deleting them)
     */
    public void iterateThroughMonsters() {
        Iterator<Monster> monsterIterator = this.monsters.iterator(); 
        // used since updating elements as we iterate
        while(monsterIterator.hasNext()) { // tick all monsters in array
            Monster monster = monsterIterator.next();
            monster.tick();

            if (!(monster.getExists())) {
                monsterIterator.remove();
            } // remove monsters that finished death animation
        }
    }

    /**
     * creates a random monster of a random type based on how many monsters 
     * it has left to spawn and spawns it on a random path
     */
    public void createRandomMonster() {
        Random rand = new Random();
        int randMonsterType = rand.nextInt(this.monsterTypeCounts.size()); // choose random monster
        JSONObject type = this.waveData.getJSONArray("monsters").getJSONObject(randMonsterType);

        int randSpawnPath = rand.nextInt(this.spawnPaths.length); // choose random spawn path
        Path spawnPath = this.spawnPaths[randSpawnPath];

        this.monsters.add(new Monster(
            spawnPath.getX(), spawnPath.getY(), type.getDouble("speed"), 
            type.getDouble("hp"), type.getDouble("armour"), 
            this.rotes.get(spawnPath), this.app, type.getDouble("mana_gained_on_kill")
        )); // add new monster type with spawn to array
        
        monsterTypeCounts.set(randMonsterType, monsterTypeCounts.get(randMonsterType) - 1); 
        this.monstersRemaining -= 1; // remove monster from count

        for(int i = 0; i < monsterTypeCounts.size(); i++) {
            if (monsterTypeCounts.get(i) == 0) {
                monsterTypeCounts.remove(i); 
            } // remove monster type from count if none left
        }
    }

    /**
     * ticks wave by:
     * 1. iterating through all monsters in array
     * 2. generating new random monster type with random spawn path after spawn cooldown is up
     * 3. checking if wave is complete by searching for any monsters in array
     */
    public void tick() {
        this.currentFrame += app.rate;

        this.iterateThroughMonsters();

        // generate new random monster type with random spawn path if:
        // 1. there are still monsters left
        // 2. spawn cooldown is up
        // 3. there are paths to spawn on
        if (
            this.monstersRemaining > 0 && 
            this.currentFrame >= this.framesPerSpawn && 
            this.spawnPaths.length > 0
        ) {
            this.createRandomMonster();
            this.currentFrame = 0; // reset frame counter
        } 
        
        if (this.monstersRemaining == 0 && this.monsters.size() == 0) {
            this.waveComplete = true;
        } // if all monsters have been spawned and killed, wave will be removed from map array
    }

    /**
     * draws all monsters spawned by wave
     */
    public void draw() {
        for(Monster monster : this.monsters) {
            monster.draw(this.app); // draw all monsters in array
        }
    }
}
