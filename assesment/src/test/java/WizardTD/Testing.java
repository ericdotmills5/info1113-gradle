package WizardTD;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterAll;
import processing.core.PImage;
import java.util.ArrayList;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import static org.junit.jupiter.api.Assertions.*;

class TestApp extends App {

    @Override
    public void image(PImage img, float x, float y){
        // do nothing
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
    private TestApp testApp;
    public static final ByteArrayOutputStream mute = new ByteArrayOutputStream();
    public static final PrintStream originalOut = System.out;

    // ASSUME CONFIGTEST FILE ISNT CHANGED FROM WHAT I SUBMITTED

    /**
     * mute print statements as to not spam the report files
     */
    @BeforeAll
    public static void muteSystemOut() {
        System.setOut(new PrintStream(Testing.mute));
    }

    /**
     * unmute print statements
     */
    @AfterAll
    public static void unmuteSystemOut() {
        System.setOut(Testing.originalOut);
    }

    /**
     * setup map each time, specifically level5.txt with configTest.json
     * 1. create test app
     * 2. setup game, including map, ui
     * 3. give player 9999 mana for other tests which use mana
     * 4. place tower for testing
     */
    @BeforeEach
    public void setUpAppMap() {
        // step 1
        this.testApp = new TestApp();
        this.testApp.config = readJSON("configTest.json");
        
        // step 2
        String[] args = {"temp"};
        App.runSketch(args, this.testApp);
        this.testApp.createStuff();

        // step 3
        this.testApp.map.getMana().updateMana(9999);

        // step 4
        this.testApp.mouseX = App.CELLSIZE + 2;
        this.testApp.mouseY = App.CELLSIZE + 1 + App.TOPBAR;
        this.testApp.ui.toggleSwitch(testApp, 3);
        this.testApp.ui.toggleSwitch(testApp, 4);
        this.testApp.ui.toggleSwitch(testApp, 5);
        this.testApp.ui.toggleSwitch(testApp, 6);
        this.testApp.ui.click(this.testApp);
    }

    // common cases

    /**
     * checking that the code doesnt immediatly crash when ticked for the first time
     */
    @Test
    public void tickCheck() {
        this.testApp.tick();
        assertNotNull(this.testApp.map);
    } 

    /**
     * checking that the code doesnt immediatly crash when drawn for the first time
     */
    @Test
    public void drawMap() {
        this.testApp.draw();
        assertNotNull(this.testApp.map);
    } 
    
    /**
     * check that the game is able to go from wave 1 to wave 2
     */
    @Test
    public void createWave() {
        this.testApp.map.nextWave();
        assertEquals(this.testApp.map.getWaves().size(), 2); // assume pre wave pause != 0
    }

    /**
     * test whether the game is able to activate poison and buy a mana spell through the ui
     */
    @Test
    public void switchManaAndPoison() {
        this.testApp.ui.toggleSwitch(this.testApp, 7);
        this.testApp.ui.toggleSwitch(this.testApp, 8);

        assertEquals(this.testApp.map.getMana().getPoolCost(), 250);
        assertTrue(this.testApp.map.getPoison());
    }

    /**
     * test whether the game is able to create a random monster
     */
    @Test
    public void spawnRandomMonster() {
        Wave wave = new Wave(this.testApp.map.getData().getJSONArray("waves").getJSONObject(0), this.testApp.map.getRoutes());
        wave.createRandomMonster(this.testApp);

        assertEquals(wave.getMonsters().size(), 1);
    }

    /**
     * test monster naturally making it to the wizard house and losing while fast forward
     */
    @Test
    public void monsterMakesItToWizardWhileFastForward() {
        ArrayList<Direction> route = new ArrayList<Direction>();
        route.add(Direction.RIGHT);

        this.testApp.keyCode = 'F';
        this.testApp.keyPressed();

        this.testApp.map.getWaves().add(new Wave(this.testApp.map.getData().getJSONArray("waves").getJSONObject(0), this.testApp.map.getRoutes()));
        this.testApp.map.getWaves().get(0).getMonsters().add(new Monster(
            0, 1, 5, 1500, 2, route, this.testApp, 5
        ));
        for (int i = 0; i < 150; i++) {
            this.testApp.draw();
        }
        assertTrue(this.testApp.onLossScreen);
        assertTrue(this.testApp.rate == 2); // check if in fast forward mode
    }

    /**
     * test whether game is able to restart after losing
     * the way to check the game has restarted is by:
     * 1. setting the initial game to wave 2
     * 2. losing (and hense restarting with the ui)
     * 3. checking if the game is now on wave 1, having restarted
     */
    @Test
    public void loseAndThenClickR() {
        //step 1
        this.testApp.map.nextWave();
        assertEquals(this.testApp.map.getWaves().size(), 2);

        // step 2
        this.testApp.onLossScreen = true;
        this.testApp.draw();
        this.testApp.keyCode = 'R';
        this.testApp.keyPressed();
        this.testApp.draw();

        // step 3
        assertEquals(this.testApp.map.getWaves().size(), 1);
    }

    /**
     * test if the poison, when activated, damages monsters
     * we do this by:
     * 1. creating a monster with 1 health
     * 2. activating poison, which kills the monster (hopefully)
     * 3. ticking through the death animation
     * 4. checking if the monster still exists by checking if the monster array is empty
     */
    @Test
    public void poisonScreenAndDamageMonster() {
        // step 1
        ArrayList<Direction> route = new ArrayList<Direction>();
        route.add(Direction.UP);

        this.testApp.map.getWaves().add(new Wave(this.testApp.map.getData().getJSONArray("waves").getJSONObject(0), this.testApp.map.getRoutes()));
        this.testApp.map.getWaves().get(0).getMonsters().add(new Monster(
            0, 1, 5, 1, 2, route, this.testApp, 5
        ));

        // step 2
        this.testApp.keyCode = '4';
        this.testApp.keyPressed();

        // step 3
        for (int i = 0; i < 100; i++) {
            this.testApp.draw();
        }

        // step 4
        assertEquals(this.testApp.map.getWaves().get(0).getMonsters().size(), 0);
    }

    /**
     * checking if upgrading the tower with the ui works
     * specific upgrade branchs (in this order):
     * 1. check setup upgrades went through (upgraded all of range, speed and damage in setup)
     * 2. upgrade just range
     * 3. upgrade just range and damage
     * 4. upgrade no upgrades (but still clicking it; edge case)
     */
    @Test
    public void testSomeUpgradeBranches() {
        // step 1
        assertEquals(this.testApp.map.getTowerList().get(0).getRangeLevel(), 2);
        assertEquals(this.testApp.map.getTowerList().get(0).getDamageLevel(), 2);
        assertEquals(this.testApp.map.getTowerList().get(0).getSpeedLevel(), 2);

        // step 2
        this.testApp.ui.upgradeRange = false;
        this.testApp.ui.upgradeDamage = false;
        this.testApp.ui.upgradeSpeed = false;
        this.testApp.ui.click(this.testApp);

        assertEquals(this.testApp.map.getTowerList().get(0).getRangeLevel(), 2);
        assertEquals(this.testApp.map.getTowerList().get(0).getDamageLevel(), 2);
        assertEquals(this.testApp.map.getTowerList().get(0).getSpeedLevel(), 2);

        // step 3
        this.testApp.ui.upgradeRange = true;
        this.testApp.ui.upgradeDamage = false;
        this.testApp.ui.upgradeSpeed = false;
        this.testApp.ui.click(this.testApp);

        assertEquals(this.testApp.map.getTowerList().get(0).getRangeLevel(), 3);
        assertEquals(this.testApp.map.getTowerList().get(0).getDamageLevel(), 2);
        assertEquals(this.testApp.map.getTowerList().get(0).getSpeedLevel(), 2);

        // step 4
        this.testApp.ui.upgradeRange = true;
        this.testApp.ui.upgradeDamage = false;
        this.testApp.ui.upgradeSpeed = true;
        this.testApp.ui.click(this.testApp);

        assertEquals(this.testApp.map.getTowerList().get(0).getRangeLevel(), 4);
        assertEquals(this.testApp.map.getTowerList().get(0).getDamageLevel(), 2);
        assertEquals(this.testApp.map.getTowerList().get(0).getSpeedLevel(), 3);

        this.testApp.draw();
    }

    /**
     * let tower kill a 1hp monster
     */
    @Test
    public void shootMonsterTest() {
        ArrayList<Direction> route = new ArrayList<Direction>();
        route.add(Direction.DOWN);

        this.testApp.map.getWaves().add(new Wave(this.testApp.map.getData().getJSONArray("waves").getJSONObject(2), this.testApp.map.getRoutes()));
        this.testApp.map.getWaves().get(0).getMonsters().add(new Monster(
            0, 1, 5, 1, 2, route, this.testApp, 5
        ));

        for (int i = 0; i < 100; i++) {
            this.testApp.draw();
        }

        assertEquals(this.testApp.map.getWaves().get(0).getMonsters().size(), 0);
    }

    // edge cases

    /**
     * spawn monster with empty path in the second wave and large health to guarantee loss
     * simulates wizard house being on the edge of the map, and a monster spawning on top of it
     */
    @Test
    public void spawnMonsterWithNewPathAndInSecondWave() {
        ArrayList<Direction> route = new ArrayList<Direction>();

        // spawn monster
        this.testApp.map.getWaves().add(new Wave(this.testApp.map.getData().getJSONArray("waves").getJSONObject(0), this.testApp.map.getRoutes()));
        this.testApp.map.getWaves().get(0).getMonsters().add(new Monster(
            0, 1, 50, 1500, 2, route, this.testApp, 5
        ));

        // tick till monster has probably reached wizard house
        for (int i = 0; i < 22; i++) {
            this.testApp.map.getWaves().get(0).tick(this.testApp);
        }
        this.testApp.draw();

        // check if we've lost
        assertTrue(this.testApp.onLossScreen);
    } 

    /**
     * check whether monster that spawns immediately dies
     * 1. spawn monster with 0 hp
     * 2. check if it was spawned (probably in death sequence at this stage since spawned "dead")
     * 3. tick through death sequence
     * 4. check if it was removed
     */
    @Test
    public void spawnMonsterWithNoHealth() {
        ArrayList<Direction> route = new ArrayList<Direction>();
        route.add(Direction.LEFT);

        // step 1
        this.testApp.map.getWaves().add(new Wave(this.testApp.map.getData().getJSONArray("waves").getJSONObject(0), this.testApp.map.getRoutes()));
        this.testApp.map.getWaves().get(0).getMonsters().add(new Monster(
            0, 1, 5, 0, 2, route, this.testApp, 5
        ));

        // step 2
        assertEquals(this.testApp.map.getWaves().get(0).getMonsters().size(), 1);

        // step 3
        for (int i = 0; i < 22; i++) {
            this.testApp.map.getWaves().get(0).tick(this.testApp);
        }

        // step 4
        assertEquals(this.testApp.map.getWaves().get(0).getMonsters().size(), 0);
    }
}