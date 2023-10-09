package WizardTD;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class Testing {

    @Test
    public void testSetup() {
        String[] args = {"WizardTD.App"};
        App app = new App();

        app.setup();

        assertNotNull(app.map);
    }
}