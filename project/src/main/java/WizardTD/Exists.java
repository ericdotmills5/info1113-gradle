package WizardTD;

public interface Exists {

    /**
     * will be used to remind me to implement existence for monster and fireball
     */
    public void becomeExistant();

    /**
     * if false, marks fireball/monster for deletion
     * @return whether the monster should still exist in memory
     */
    public boolean exists();
}
