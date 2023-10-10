package WizardTD;

import org.junit.jupiter.api.Test;

import processing.core.PApplet;

import org.junit.jupiter.api.BeforeAll;

import static org.junit.jupiter.api.Assertions.*;

public class Testing extends App {
    private static PApplet app;

    @BeforeAll
    public static void setUp() {
        
        
        String[] args = {"WizardTD.App"};
        App.main(args);
        app = new App();
        app.setup();
    }

    @Test
    public void test() {
        assertNotNull(app);
    }
    
    
}