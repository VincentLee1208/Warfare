package model;

import org.json.JSONObject;
import persistence.Writable;

import java.util.ArrayList;
import java.util.List;

// Player object for game
public class Player implements Writable {
    boolean playerTurn;
    int money;

    protected List<Soldier> soldiers;
    protected List<Archer> archers;
    int soldiersremaining;

    private boolean tutorialCompleted = false;
    private boolean level1Completed = false;

    // MODIFIES: this
    // EFFECTS: create new player object with default values
    public Player() {
        this.money = 25;
        soldiers = new ArrayList<>();
        archers = new ArrayList<>();

        //soldiersremaining = soldiers.size();
        soldiersremaining = 0;
        playerTurn = true;
    }

    // EFFECTS: returns money value
    public int getMoney() {
        return this.money;
    }

    // MODIFIES: this
    // EFFECTS: sets money value to parameter
    public void setMoney(int newmoney) {
        this.money = newmoney;
    }

    // EFFECTS: returns list of soldiers
    public List<Soldier> getSoldiers() {
        return soldiers;
    }

    // EFFECTS: returns list of archers
    public List<Archer> getArchers() {
        return archers;
    }

    // EFFECTS: buys selected unit
    public void buyUnits(String unit) {
        if (unit.equals("Soldier")) {
            buySoldier();
        } else {
            buyArcher();
        }
    }

    // MODIFIES: this
    // EFFECTS: buy a soldier unit, add to list, and decrease money
    public void buySoldier() {
        if (this.getMoney() >= 5) {
            //Check that player doesn't have more than 10 soldiers already
            if (this.getSoldiers().size() < 10) {
                Soldier newsoldier = new Soldier(false, 0, 0);
                this.getSoldiers().add(newsoldier);
                this.setMoney(this.money - newsoldier.getCost());
                System.out.println("Soldier bought!\n");

            } else {
                System.out.println("You have the maximum amount of Soldier units already!");
            }
        } else {
            System.out.println("You don't have enough coins!");
        }
    }

    // MODIFIES: this
    // EFFECTS: buy an archer unit, add to list, and decrease money
    public void buyArcher() {
        if (this.getMoney() >= 5) {
            //Check that player doesn't have more than 10 soldiers already
            if (this.getArchers().size() < 5) {
                Archer newarcher = new Archer(false, 0, 0);
                this.getArchers().add(newarcher);
                this.setMoney(this.money - newarcher.getCost());
                System.out.println("Archer bought!\n");
            } else {
                System.out.println("You have the maximum amount of Archer units already!");
            }
        } else {
            System.out.println("You don't have enough coins!");
        }
    }

    // MODIFIES: this
    // EFFECTS: sets tutorial boolean to parameter
    public void setTutorial(boolean completed) {
        this.tutorialCompleted = completed;
    }

    // MODIFIES: this
    // EFFECTS: sets levelone boolean to parameter
    public void setLevelOne(boolean completed) {
        this.level1Completed = completed;
    }

    // EFFECTS: returns tutorial boolean
    public boolean getTutorial() {
        return this.tutorialCompleted;
    }

    // EFFECTS: returns levelone boolean
    public boolean getLevelOne() {
        return this.level1Completed;
    }

    // EFFECTS: changes player variables to JSON
    @Override
    public JSONObject toJson() {
        JSONObject json = new JSONObject();
        json.put("money",getMoney());
        json.put("tutorialcompleted", this.tutorialCompleted);
        json.put("levelonecompleted",this.level1Completed);
        json.put("soldiers", soldiers.size());
        json.put("archers", archers.size());

        return json;
    }

}
