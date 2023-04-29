package model;

public interface Pieces {
    String getLabel();

    String getDisplayName();

    boolean seeIfEnemy();

    int getHealth();

    void setHealth(int health);

    int getDamage();

    int getPosX();

    int getPosY();

    void setPosX(int xcoord);

    void setPosY(int ycoord);

    boolean seeIfMoved();

    void setMoved(boolean moved);

    void setEnemy(boolean enemy);

    int getReward();
}
