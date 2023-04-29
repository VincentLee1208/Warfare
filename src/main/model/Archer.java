package model;

//Create an enemy or ally archer object
public class Archer implements Pieces {
    int cost = 5;
    int health = 1;
    int damage = 1;
    String name;
    boolean enemy;
    int posX;
    int posY;
    int reward = 3;

    boolean moved = false;

    public Archer(boolean type, int x, int y) {
        this.enemy = type;
        this.posX = x;
        this.posY = y;
        if (type) {
            name = "EnemyArcher";
        } else {
            name = "AllyArcher";
        }
    }

    // EFFECTS: checks if unit is an enemy
    public boolean seeIfEnemy() {
        return this.enemy;
    }

    // EFFECTS: returns x coord of unit
    public int getPosX() {
        return this.posX;
    }

    // EFFECTS: returns y coord of unit
    public int getPosY() {
        return this.posY;
    }

    // EFFECTS: returns health value of unit
    public int getHealth() {
        return this.health;
    }

    // EFFECTS: sets health of unit to parameter
    public void setHealth(int health) {
        this.health = health;
    }

    // EFFECTS: returns damage value of unit
    public int getDamage() {
        return this.damage;
    }

    // EFFECTS: returns cost of unit
    public int getCost() {
        return this.cost;
    }

    // EFFECTS: sets x coord of unit to parameter
    public void setPosX(int xcoord) {
        this.posX = xcoord;
    }

    // EFFECTS: set y coord of unit to parameter
    public void setPosY(int ycoord) {
        this.posY = ycoord;
    }

    // EFFECTS: returns name
    public String getLabel() {
        return this.name;
    }

    // EFFECTS: returns name
    public String getDisplayName() {
        return this.name;
    }

    // EFFECTS: returns if unit has taken its turn
    public boolean seeIfMoved() {
        return !this.moved;
    }

    // EFFECTS: sets moved boolean to parameter
    public void setMoved(boolean moved) {
        this.moved = moved;
    }

    // EFFECTS: sets enemy boolean to parameter
    public void setEnemy(boolean enemy) {
        this.enemy = enemy;
    }


    // EFFECTS: returns reward money for killing unit
    public int getReward() {
        return this.reward;
    }
}
