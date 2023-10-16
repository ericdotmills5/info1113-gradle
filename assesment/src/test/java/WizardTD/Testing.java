package WizardTD;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import processing.core.PImage;
import processing.core.PGraphics;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class TestApp extends App{
    
    @Override
    public PImage loadImage(String path){
        return null;
    }

    @Override
    public void image(PImage img, float x, float y){
        // do nothing
    }

    @Override
    public PImage rotateImageByDegrees(PImage pimg, double angle) {
        return null;
    }

    @Override
    public void noStroke() {
        // do nothing
    }

    @Override
    public void fill(float r, float g, float b) {
        // do nothing
    }

    @Override
    public void rect(float x, float y, float w, float h) {
        // do nothing
    }

    @Override
    public void ellipse(float x, float y, float w, float h) {
        // do nothing
    }

    @Override
    public void stroke(float r, float g, float b) {
        // do nothing
    }

    @Override
    public void strokeWeight(float w) {
        // do nothing
    }

    @Override
    public void line(float x1, float y1, float x2, float y2) {
        // do nothing
    }

    @Override
    public void textSize(float s) {
        // do nothing
    }

    @Override
    public void text(String s, float x, float y) {
        // do nothing
    }

    @Override
    public void noFill() {
        // do nothing
    }
}

public class Testing extends App {
    TestApp testApp;

    /**
     * ASSUME CONFIGTEST FILE ISNT CHANGED FROM WHAT I SUBMITTED
     */
    @BeforeEach
    public void setUpAppMap() {
        testApp = new TestApp();
        testApp.config = readJSON("configTest.json");
        testApp.createStuff();

        testApp.mouseX = 50;
        testApp.mouseY = 50 + App.TOPBAR;
        testApp.ui.placeTower = true;
        testApp.ui.upgradeRange = true;
        testApp.ui.upgradeDamage = true;
        testApp.ui.upgradeSpeed = true;
        testApp.ui.click(testApp);

        assertNotNull(testApp.map);
    }

    @Test
    public void tickCheck(){
        testApp.tick();
        assertNotNull(testApp.map);
    } // checking that the code doesnt immediatly crash when ticked for the first time
    
    @Test
    public void createWave(){
        testApp.map.nextWave();
        assertEquals(testApp.map.getWaves().size(), 1);
    } // checking that a wave is created

    @Test
    public void switchManaAndPoison(){
        testApp.ui.toggleSwitch(testApp, 7);
        testApp.ui.toggleSwitch(testApp, 8);
    } // checking if the buttons work as they should

    @Test
    public void drawMap(){
        testApp.draw();
    }

    @Test
    public void spawnRandomMonster(){
        Wave wave = new Wave(testApp.map.getData().getJSONArray("waves").getJSONObject(0), testApp.map.getRoutes(), testApp);
        wave.createRandomMonster();
    }

    @Test
    public void spawnMonsterAndShoot(){
        ArrayList<Direction> route = new ArrayList<Direction>();
        route.add(Direction.DOWN);

        testApp.map.getWaves().add(new Wave(testApp.map.getData().getJSONArray("waves").getJSONObject(0), testApp.map.getRoutes(), testApp));
        testApp.map.getWaves().get(0).getMonsters().add(new Monster(
            0, 1, 5, 10, 2, route, testApp, 5
        ));
        testApp.draw();
        testApp.map.getTowerList().get(0).setFramesCounter(9999);
        for(int i = 0; i < 150; i++){
            testApp.map.tick();
        }
        
    }

    @Test
    public void spawnMonsterWithNoHealth(){
        ArrayList<Direction> route = new ArrayList<Direction>();
        route.add(Direction.LEFT);

        testApp.map.getWaves().add(new Wave(testApp.map.getData().getJSONArray("waves").getJSONObject(0), testApp.map.getRoutes(), testApp));
        testApp.map.getWaves().get(0).getMonsters().add(new Monster(
            0, 1, 5, 0, 2, route, testApp, 5
        ));
        for(int i = 0; i < 21; i++){
            testApp.map.getWaves().get(0).tick();
        }
    }

    @Test
    public void monsterMakesItToWizardWhileFastForward(){
        ArrayList<Direction> route = new ArrayList<Direction>();
        route.add(Direction.RIGHT);

        testApp.keyCode = 'F';
        testApp.keyPressed();

        testApp.map.getWaves().add(new Wave(testApp.map.getData().getJSONArray("waves").getJSONObject(0), testApp.map.getRoutes(), testApp));
        testApp.map.getWaves().get(0).getMonsters().add(new Monster(
            0, 1, 5, 1, 2, route, testApp, 5
        ));
        for(int i = 0; i < 150; i++){
            testApp.draw();
        }
    }

    @Test
    public void loseAndThenClickR(){
        testApp.onLossScreen = true;
        testApp.draw();
        testApp.keyCode = 'R';
        testApp.keyPressed();
        testApp.draw();
    }

    @Test
    public void poisonScreenAndDamageMonster(){
        ArrayList<Direction> route = new ArrayList<Direction>();
        route.add(Direction.UP);

        testApp.map.getWaves().add(new Wave(testApp.map.getData().getJSONArray("waves").getJSONObject(0), testApp.map.getRoutes(), testApp));
        testApp.map.getWaves().get(0).getMonsters().add(new Monster(
            0, 1, 5, 1, 2, route, testApp, 5
        ));

        testApp.keyCode = '4';
        testApp.keyPressed();

        for(int i = 0; i < 150; i++){
            testApp.draw();
        }
    }

    @Test
    public void testAllUpgradeBranches(){
        testApp.ui.upgradeRange = false;
        testApp.ui.upgradeDamage = false;
        testApp.ui.upgradeSpeed = false;
        testApp.ui.click(testApp);

        testApp.ui.upgradeRange = true;
        testApp.ui.upgradeDamage = false;
        testApp.ui.upgradeSpeed = false;
        testApp.ui.click(testApp);

        testApp.ui.upgradeRange = true;
        testApp.ui.upgradeDamage = false;
        testApp.ui.upgradeSpeed = true;
        testApp.ui.click(testApp);

        testApp.draw();
    }

    @Test
    public void rotateImage(){
        PGraphics graphics = new PGraphics();
        graphics.setSize(100, 50);
        PImage img = graphics.get();
        testApp.rotateImageByDegrees(img, 45);
    }
}