package WizardTD;

public class Ui {
    private Map map;
    private int waveSeconds;

    public Ui(Map map){
        this.map = map;
    }

    public int updateWaveSeconds(){
        return (int) Math.floorDiv((int)this.map.getWaveTime(), App.FPS);
    }

    public void waveCountdown(App app){
        if(!this.map.getLastWave()){ // if not last wave
            app.fill(0);
            app.textSize(25);
            app.text("Wave " + (this.map.getWaveNumber() + 2) + " starts: " + this.waveSeconds, 10, 30);
            // +2 to change base from 0 to 1 and refer to next wave
        }
    }

    public void manaBar(App app){
        float manaProp = (float)(this.map.getMana().getCurrMana() / this.map.getMana().getCap());

        // blue bit
        app.stroke(0);
        app.strokeWeight(2);
        app.fill(5, 210, 215); 
        app.rect(App.MANAX, App.MANAY, App.MANALENGTH * manaProp, App.MANAWIDTH);
        
        // white bit
        app.fill(255, 255, 255); 
        app.rect(
            App.MANAX + (App.MANALENGTH * manaProp), 
            App.MANAY, //
            (App.MANALENGTH * (1 - manaProp)), 
            App.MANAWIDTH);
    }

    public void manaText(App app){
        app.fill(0);
        app.textSize(17);
        app.text("MANA:", App.MANATEXTX, App.MANATEXTY);
        app.text((int)this.map.getMana().getCurrMana() + " / " + (int)this.map.getMana().getCap(), 
                 App.MANATEXTX + App.MANACURRSHIFT, App.MANATEXTY);
    }

    public void tick(){
        this.waveSeconds = this.updateWaveSeconds(); // convert wave time to seconds
    }

    public void draw(App app){

        this.waveCountdown(app);
        this.manaBar(app);
        this.manaText(app);


        
       
        


        // mana text
        
    }
}
