package WizardTD;

public class Shrub extends Tile {
    private static final String spritePath = "src/main/resources/WizardTD/shrub.png";
    /**
     * Only unique thing about shrub is its sprite, otherwise generic constructer for tile
     * @param x x tile cordinates [0, 19]
     * @param y y tile cordinates [0, 19]
     * @param map map class it is generated from
     */
    public Shrub(int x, int y, Map map) {
        super(x, y, map, Shrub.spritePath);
    }
}
