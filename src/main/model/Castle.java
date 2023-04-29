package model;

//Create an enemy or ally castle
public class Castle implements Pieces {

    int health = 1;
    boolean enemy;
    String label;

    int posX;
    int posY;
    int reward = 10;

    // EFFECTS: creates new Castle object
    public Castle(boolean type, int x, int y) {
        this.enemy = type;
        this.posX = x;
        this.posY = y;
        if (type) {
            label = "EnemyCastle";
        } else {
            label = "Castle";
        }
    }

    // EFFECTS: checks if unit is an enemy
    public boolean seeIfEnemy() {
        return this.enemy;
    }

    // EFFECTS: returns label with appended whitespace
    public String toString() {
        return this.label + " ";
    }

    // EFFECTS: returns label
    public String getLabel() {
        return this.label;
    }

    // EFFECTS: returns label
    public String getDisplayName() {
        return this.label;
    }

    // EFFECTS: returns health value of unit
    public int getHealth() {
        return this.health;
    }

    // EFFECTS: sets health of unit to parameter
    public void setHealth(int health) {
        this.health = health;
    }

    // EFFECTS: sets enemy boolean to parameter
    public void setEnemy(boolean enemy) {
        this.enemy = enemy;
    }

    // EFFECTS: returns damage value of unit
    public int getDamage() {
        return 0;
    }

    // EFFECTS: returns x coord of unit
    public int getPosX() {
        return this.posX;
    }

    // EFFECTS: returns y coord of unit
    public int getPosY() {
        return this.posY;
    }

    // EFFECTS: sets x coord of unit to parameter
    public void setPosX(int xcoord) {
        this.posX = xcoord;
    }

    // EFFECTS: set y coord of unit to parameter
    public void setPosY(int ycoord) {
        this.posY = ycoord;
    }

    // EFFECTS: returns if unit has taken its turn
    public boolean seeIfMoved() {
        return true;
    }

    // EFFECTS: sets moved boolean to parameter
    public void setMoved(boolean moved) {

    }

    // EFFECTS: returns reward money for killing unit
    public int getReward() {
        return this.reward;
    }
}
