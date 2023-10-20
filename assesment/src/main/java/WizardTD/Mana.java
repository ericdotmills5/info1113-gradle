package WizardTD;

public class Mana implements Tick {
    double currMana;
    double cap;
    double regenRate;
    double poolCost;
    double poolCostIncrease;
    double capMultiplier;
    double manaMultiplier;
    double initialRegenRate;
    int counterOfFrames = 0;

    /**
     * constructer for mana
     * @param initial initial mana
     * @param cap initial mana cap
     * @param regenRate initial mana regen rate
     * @param poolCost initial mana pool cost
     * @param poolCostIncrease mana pool cost increase price
     * @param capMultiplier mana cap multiplier
     * @param manaMultiplier mana multiplier
     */
    public Mana(
        double initial, double cap, double regenRate, double poolCost, 
        double poolCostIncrease, double capMultiplier, double manaMultiplier
    ) {
        this.currMana = initial > cap ? cap : initial;
        this.cap = cap;
        this.regenRate = regenRate;
        this.initialRegenRate = regenRate;
        this.poolCost = poolCost;
        this.poolCostIncrease = poolCostIncrease;
        this.capMultiplier = capMultiplier;
        this.manaMultiplier = manaMultiplier;
    }

    /**
     * getter for current mana
     * @return current mana
     */
    public double getCurrMana() {
        return this.currMana;
    }

    /**
     * getter for mana cap
     * @return mana cap
     */
    public double getCap() {
        return this.cap;
    }

    /**
     * getter for mana pool cost
     * @return mana pool cost
     */
    public double getPoolCost() {
        return this.poolCost;
    }

    /**
     * used to activate the mana pool spell
     */
    public void clickPoolSpell() {
        if (updateMana(-1 * poolCost)) {
        // checks if mana is enough, and if so, deducts mana
            this.cap *= this.capMultiplier;
            this.regenRate += this.manaMultiplier * this.initialRegenRate;
            this.poolCost += this.poolCostIncrease;
        }
    }
    
    /**
     * double use:
     * 1. adds mana to current mana
     * 2. if this causes a negative mana, returns false to indicate cant afford
     * @param add amount of mana to add (subtracts negatives)
     * @return true if mana is added, false if not enough mana
     */
    public boolean updateMana(double add) {
        if (this.currMana + add <= 0) {
            return false;
        // either building is too expensive and won't be placed
        // or ghost kills player

        } else if (this.currMana + add > this.cap) {
            this.currMana = this.cap;
            return true;
        } else {
            this.currMana += add;
            return true;
        }
    }

    /**
     * used to make mana zero
     */
    public void makeManaZero() {
        this.currMana = 0;
    }

    /**
     * regenerate mana according to config
     * @param inputApp App object to check if fast forward or paused
     */
    public void tick(App inputApp) {
        this.counterOfFrames += inputApp.rate;

        if (this.counterOfFrames >= App.FPS) {
            this.updateMana(this.regenRate);
            this.counterOfFrames = 0;
        } // update mana every second
    }
}