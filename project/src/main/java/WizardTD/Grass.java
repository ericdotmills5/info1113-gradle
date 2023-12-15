package WizardTD;

public class Grass extends Tile {
    private static final String spritePath = "src/main/resources/WizardTD/grass.png";
    /**
     * Only unique thing about grass is its sprite, other wise generic constructer for tile
     * @param x x tile cordinates [0, 19]
     * @param y y tile cordinates [0, 19]
     * @param map map class it is generated from
     */
    public Grass(int x, int y, Map map) {
        super(x, y, map, Grass.spritePath);
    }
}
