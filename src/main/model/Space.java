package model;

//Create space pieces to fill in grid
public class Space implements Pieces {

    boolean enemy = true;
    String label = "Space";
    String displayname = "Space       ";

    // EFFECTS: returns label string with appended whitespace
    public String toString() {
        return this.label + "       ";
    }

    // EFFECTS: returns label
    public String getLabel() {
        return this.label;
    }

    // EFFECTS: returns displayname
    public String getDisplayName() {
        return this.displayname;
    }

    // EFFECTS: checks if unit is an enemy
    public boolean seeIfEnemy() {
        return this.enemy;
    }

    // EFFECTS: returns health value of piece
    public int getHealth() {
        return 0;
    }

    // EFFECTS: sets health of unit to parameter
    public void setHealth(int health) {

    }

    // EFFECTS: returns damage value of unit
    public int getDamage() {
        return 0;
    }

    // EFFECTS: returns x coord of unit
    public int getPosX() {
        return 0;
    }

    // EFFECTS: returns y coord of unit
    public int getPosY() {
        return 0;
    }

    // EFFECTS: sets x coord of unit to parameter
    public void setPosX(int xcoord) {

    }

    // EFFECTS: set y coord of unit to parameter
    public void setPosY(int ycoord) {

    }

    // EFFECTS: returns if unit has taken its turn
    public boolean seeIfMoved() {
        return true;
    }

    // EFFECTS: sets moved boolean to parameter
    public void setMoved(boolean moved) {

    }

    // EFFECTS: sets enemy boolean to parameter
    public void setEnemy(boolean enemy) {
        this.enemy = enemy;
    }

    // EFFECTS: returns reward money for killing unit
    public int getReward() {
        return 0;
    }
}
