package WizardTD;

public class Mana {
    double currMana;
    double cap;
    double regenRate;
    double poolCost;
    double poolCostIncrease;
    double capMultiplier;
    double manaMultiplier;
    double initialRegenRate;
    int counterOfFrames = 0;

    public Mana(double initial, double cap, double regenRate, double poolCost, double poolCostIncrease, double capMultiplier, double manaMultiplier){
        this.currMana = initial;
        this.cap = cap;
        this.regenRate = regenRate;
        this.initialRegenRate = regenRate;
        this.poolCost = poolCost;
        this.poolCostIncrease = poolCostIncrease;
        this.capMultiplier = capMultiplier;
        this.manaMultiplier = manaMultiplier;
    }

    public double getCurrMana(){
        return this.currMana;
    }

    public double getCap(){
        return this.cap;
    }

    public double getPoolCost(){
        return this.poolCost;
    }

    public void clickPoolSpell(){
        if(updateMana(-1 * poolCost)){
        // checks if mana is enough, and if so, deducts mana
            this.cap *= this.capMultiplier;
            this.regenRate += this.manaMultiplier * this.initialRegenRate;
            this.poolCost += this.poolCostIncrease;
        }
    }
        
    public boolean updateMana(double add){
        if(this.currMana + add <= 0){
            return false;
        // either building is too expensive and won't be placed
        // or ghost kills player

        } else if(this.currMana + add > this.cap){
            this.currMana = this.cap;
            return true;
        } else{
            this.currMana += add;
            return true;
        }
    }

    public void makeManaZero(){
        this.currMana = 0;
    }

    public void tick(App app){
        this.counterOfFrames += app.rate;

        if(this.counterOfFrames >= App.FPS){
            this.updateMana(this.regenRate);
            this.counterOfFrames = 0;
        } // update mana every second
    }
        
    
}
