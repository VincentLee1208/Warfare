package ui;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Scanner;
import model.*;
import persistence.*;

import java.util.Arrays;
import java.util.List;

// Represents the game application
@SuppressWarnings({"checkstyle:LineLength", "checkstyle:SuppressWarnings"})
public class GameManager {
    private static final String JSON_STORE = "./data/player.json";
    private final Scanner input;
    private final JsonWriter jsonWriter;
    private final JsonReader jsonReader;
    Player player = new Player();

    String[][] levelMap;

    //List<String> commands = Arrays.asList("Soldier","Archer");

    TutorialLevel tutorial;
    protected boolean passtut = false;
    Pieces[][] gameGrid;

    // EFFECTS: Constructs a game manager and run the game
    public GameManager() {
        input = new Scanner(System.in);
        jsonWriter = new JsonWriter(JSON_STORE);
        jsonReader = new JsonReader(JSON_STORE);
    }

/*
    // MODIFIES: Player
    // EFFECTS: runs startGame or load save if necessary
    public void runGame() {
        System.out.println("Running");

        System.out.println("---------------Warfare----------------");
        System.out.println("To load game        - 'l'");
        System.out.println("To start a new game - 'n'");

        String in = getUserInputString();

        if (in.equals("n")) {
            startGame(player);
        } else {
            loadPlayer();
            loadSave(player);
        }


    }
    */

    // EFFECTS: starts game
    public void startGame(Player player) {
        //boolean passtut;
        tutorial = new TutorialLevel();
        levelMap = tutorial.getMap();
        gameGrid = tutorial.getGrid();
        /*
        passtut = prepPhase(player, tutorial.getGrid(), tutorial.getMap());

        if (passtut) {
            savePlayer();
            System.out.println("Autosaved Game");
            System.out.println("Congrats! You've finished the tutorial!");
            System.out.println("The units that survived the last battle will move on to the next level\n\n");


            System.out.println("---------------Level One----------------");
            LevelOne one = new LevelOne();
            buyPhase(player);
            prepPhase(player, one.getGrid(), one.getMap());
        }
        */
    }

    //EFFECTS: Checks that player has units left to place onto grid
    public boolean hasUnits(String unitType,Player player) {
        switch (unitType) {
            case "Soldier": {
                if (player.getSoldiers().size() > 0) {
                    return true;
                }
                System.out.println("No soldiers left!");
                return false;
            }
            case "Archer": {
                if (player.getArchers().size() > 0) {
                    return true;
                }
                System.out.println("No Archers left!");
                return false;
            }
            default:
                return false;
        }
    }

    // EFFECTS: call save player method
    void save() {
        savePlayer();
    }

    // EFFECTS: saves the player information to a file
    private void savePlayer() {
        try {
            jsonWriter.open();
            jsonWriter.write(player);
            jsonWriter.close();
            System.out.println("Saved Game!");
        } catch (FileNotFoundException e) {
            System.out.println("Unable to write to file");
        }
    }

    // MODIFIES: player
    // EFFECTS: loads in saved player file
    void loadPlayer() {
        try {
            player = jsonReader.read();
            System.out.println("Loaded saved player");
        } catch (IOException e) {
            System.out.println("Unable to read from file");
        }
    }

    // EFFECTS: return gameGrid
    public Pieces[][] getGameGrid() {
        return gameGrid;
    }

    // EFFECTS: returns player
    public Player getPlayer() {
        return player;
    }

    // EFFECTS: Get name of unit at x, y
    public String getUnitName(int x, int y) {
        if (tutorial.getGrid()[x][y].getLabel().equals("Space")) {
            return "";
        } else {
            return tutorial.getGrid()[x][y].getLabel();
        }
    }

    //EFFECTS: Check if unit can be placed at x, y
    public boolean canPlaceUnit(int x, int y) {
        if (tutorial.getGrid()[x][y].getLabel().equals("Space")) {
            return x >= tutorial.getGrid().length / 2;
        }
        return false;
    }


    public void printEventLog() {
        EventLog el = EventLog.getInstance();
        for (Event next : el) {
            System.out.println(next.toString());
        }
    }



    // EFFECTS: Place unit at x, y
    public void placeUnit(int x, int y, String unitName) {
        if (unitName.equals("Archer")) {
            Archer current = player.getArchers().get(0);
            current.setPosX(x);
            current.setPosY(y);

            gameGrid[x][y] = current;
            player.getArchers().remove(0);
            //EventLog.getInstance().logEvent(new Event("Added Archer to: [" + x + "][" + y + "]"));
        } else if (unitName.equals("Soldier")) {
            Soldier current = player.getSoldiers().get(0);
            current.setPosX(x);
            current.setPosY(y);

            gameGrid[x][y] = current;
            player.getSoldiers().remove(0);
            EventLog.getInstance().logEvent(new Event("Added Soldier to: [" + x + "][" + y + "]"));
        }
    }

    // EFFECTS: Check if unit at x, y is an ally
    public boolean isAllyUnit(int x, int y) {
        return !gameGrid[x][y].seeIfEnemy() && !gameGrid[x][y].getLabel().equals("Castle");
    }

    // EFFECTS: Take unit on x, y
    public void takeUnit(int x, int y) {
        String unitType = gameGrid[x][y].getLabel();

        switch (unitType) {
            case "AllySoldier": {
                Soldier current = new Soldier(false, 0, 0);
                player.getSoldiers().add(current);

                Space fill = new Space();
                gameGrid[x][y] = fill;
                EventLog.getInstance().logEvent(new Event("Removed Soldier from: [" + x + "][" + y + "]"));
                break;
            }
            case "AllyArcher": {
                Archer current = new Archer(false, 0, 0);
                player.getArchers().add(current);

                Space fill = new Space();
                gameGrid[x][y] = fill;
                EventLog.getInstance().logEvent(new Event("Removed Archer from: [" + x + "][" + y + "]"));
                break;
            }
        }
    }

    // EFFECTS: Move selected unit
    public void moveUnit(int currX, int currY, int newX, int newY) {
        Space fill = new Space();
        gameGrid[newX][newY] = gameGrid[currX][currY];
        gameGrid[currX][currY] = fill;

        gameGrid[newX][newY].setPosX(newX);
        gameGrid[newX][newY].setPosY(newY);
        gameGrid[newX][newY].setMoved(true);
        EventLog.getInstance().logEvent(new Event("Unit: [" + currX + "][" + currY + "] moved to [" + newX + "][" + newY + "]"));
    }

    // EFFECTS: Runs attack calculations for ally archer units
    @SuppressWarnings("checkstyle:MethodLength")
    public int[] attackUnitArcher(int currX, int currY, int targetX, int targetY) {
        EventLog.getInstance().logEvent(new Event("Unit: [" + currX + "][" + currY + "] attacked [" + targetX + "][" + targetY + "]"));
        int[] result = new int[2];
        int newhealth = gameGrid[targetX][targetY].getHealth() - gameGrid[currX][currY].getDamage();
        int myhealth = gameGrid[currX][currY].getHealth();
        if (currX - targetX == 1) {
            myhealth = gameGrid[currX][currY].getHealth() - (gameGrid[targetX][targetY].getDamage() / 2);
        }
        gameGrid[currX][currY].setMoved(true);
        if (newhealth > 0) {
            gameGrid[targetX][targetY].setHealth(newhealth);
            System.out.println("Target new health: " + newhealth);
            result[0] = 0;
            if (myhealth > 0) {
                gameGrid[currX][currY].setHealth(myhealth);
                System.out.println("User new health: " + myhealth);
                result[1] = 0;
            } else {
                Space fill = new Space();
                gameGrid[currX][currY] = fill;
                result[1] = 1;
            }
        } else {
            Pieces temp = gameGrid[targetX][targetY];
            player.setMoney(player.getMoney() + temp.getReward());
            Space fill = new Space();
            gameGrid[targetX][targetY] = fill;
            result[0] = 1;
            result[1] = 0;
        }
        return result;
    }

    // EFFECTS: Runs attack calculations for ally soldier units
    // First value of array for enemy unit
    // Second value of array for user unit
    // Returns 0 if unit has died
    // Returns 1 if unit survives
    @SuppressWarnings("checkstyle:MethodLength")
    public int[] attackUnitSoldier(int currX, int currY, int targetX, int targetY) {
        EventLog.getInstance().logEvent(new Event("Unit: [" + currX + "][" + currY + "] attacked [" + targetX + "][" + targetY + "]"));
        int[] result = new int[2];
        int newhealth = gameGrid[targetX][targetY].getHealth() - gameGrid[currX][currY].getDamage();
        int myhealth = gameGrid[currX][currY].getHealth() - (gameGrid[targetX][targetY].getDamage() / 2);
        if (newhealth > 0) {
            result[0] = 1;
            gameGrid[targetX][targetY].setHealth(newhealth);
            if (myhealth > 0) {
                result[1] = 1;
                gameGrid[currX][currY].setHealth(myhealth);
            } else {
                result[1] = 0;
                Space fill = new Space();
                gameGrid[currX][currY] = gameGrid[targetX][targetY];
                gameGrid[targetX][targetY] = fill;
            }
        } else {
            result[0] = 0;
            result[1] = 1;
            gameGrid[targetX][targetY] = gameGrid[currX][currY];
            Space fill = new Space();
            gameGrid[currX][currY] = fill;
            gameGrid[targetX][targetY].setPosX(targetX);
            gameGrid[targetX][targetY].setPosY(targetY);
        }
        return result;
    }

    // MODIFIES: gameGrid
    // EFFECTS: helper function to fill in space on gameGrid
    public void fillSpace(Pieces[][] gameGrid, Pieces p, int targetX, int targetY) {
        Space fill = new Space();
        gameGrid[targetX][targetY] = p;

        gameGrid[p.getPosX()][p.getPosY()] = fill;

        p.setPosX(targetX);
        p.setPosY(targetY);
    }


    // EFFECTS: returns 0 if enemy cannot attack or cannot move
    // 1 if enemy attack straight
    // 2 if enemy attack left
    // 3 if enemy attack right
    // 4 if enemy move straight
    // 5 if enemy move left
    // 6 if enemy move right
    public int chooseEnemyMove(int i, int j) {
        Pieces p = gameGrid[i][j];
        int decision = 0;
        if (p.getLabel().equals("EnemySoldier")) {
            if (canAttack(p) != 0) {
                decision = canAttack(p);
            }
        } else if (p.getLabel().equals("EnemyArcher")) {
            if (canAttackArcher(p) != 0) {
                decision = canAttackArcher(p);
            }
        }

        if (decision == 0) {
            if (canMove(p) != 0) {
                decision = canMove(p);
            }
        }
        return decision;
    }

    // Stub
    public int canAttackArcher(Pieces p) {
        return 0;
    }

    // EFFECTS: Checks where enemy unit can move to
    @SuppressWarnings("checkstyle:MethodLength")
    public int canMove(Pieces p) {
        int targetx = getTargetXCoord(p);
        int targety = getTargetYCoord();

        if (p.getLabel().equals("EnemySoldier")) {
            //if can move down
            if (p.getPosX() - targetx != -1) {
                //try to move down
                if (gameGrid[p.getPosX() + 1][p.getPosY()].getLabel().equals("Space")) {
                    return 4;
                }
                /*else if (targety > p.getPosY()) {
                    if (gameGrid[p.getPosX()][p.getPosY() + 1].getLabel().equals("Space")) {
                        return 6;
                    }
                } else if (targety < p.getPosY()) {
                    if (gameGrid[p.getPosX()][p.getPosY() - 1].getLabel().equals("Space")) {
                        return 5;
                    }
                }

                 */
                //if cannot move
            }
            /*else if (p.getPosY() - targety != 0) {
                if (targety > p.getPosY()) {
                    if (gameGrid[p.getPosX()][p.getPosY() + 1].getLabel().equals("Space")) {
                        return 6;
                    }
                } else if (targety < p.getPosY()) {
                    if (gameGrid[p.getPosX()][p.getPosY() - 1].getLabel().equals("Space")) {
                        return 5;
                    }
                }


            } */
            if (p.getPosY() - targety != 0) {
                if (targety > p.getPosY()) {
                    if (gameGrid[p.getPosX()][p.getPosY() + 1].getLabel().equals("Space")) {
                        return 6;
                    }
                } else if (targety < p.getPosY()) {
                    if (gameGrid[p.getPosX()][p.getPosY() - 1].getLabel().equals("Space")) {
                        return 5;
                    }
                }
            } else {
                return 0;
            }
        }
        return 0;
    }


    // EFFECTS: gets the target x coordinate of enemy unit
    public int getTargetXCoord(Pieces p) {
        int targetx = 0;
        for (Pieces[] r : gameGrid) {
            for (Pieces c : r) {
                //get x and y pos of player castle
                if (c.getLabel().equals("Castle")) {
                    targetx = c.getPosX();

                    if (p.getLabel().equals("EnemyArcher")) {
                        targetx -= 3;
                    }
                }
            }
        }

        return targetx;
    }

    // EFFECTS: gets the target y coordinate for enemy units
    public int getTargetYCoord() {
        int targety = 0;
        for (Pieces[] r : gameGrid) {
            for (Pieces c : r) {
                //get x and y pos of player castle
                if (c.getLabel().equals("Castle")) {
                    targety = c.getPosY();
                }
            }
        }

        return targety;
    }

    // EFFECTS: Checks which location enemy can attack in
    public int canAttack(Pieces p) {

        if (p.getPosX() + 1 <= gameGrid.length) {
            //attack straight
            if (!gameGrid[p.getPosX() + 1][p.getPosY()].seeIfEnemy()) {
                return 1;
            }
            //attack left
            if (p.getPosY() - 1 >= 0) {
                if (!gameGrid[p.getPosX() + 1][p.getPosY() - 1].seeIfEnemy()) {
                    return 2;
                }
            }
            //attack right
            if (p.getPosY() + 1 < gameGrid[0].length) {
                if (!gameGrid[p.getPosX() + 1][p.getPosY() + 1].seeIfEnemy()) {
                    return 3;
                }
            }
        }

        return 0;
    }

    // EFFECTS: Runs straight attack calculations for soldiers
    public int[] attackStraightSoldier(Pieces p) {
        int[] newLocation = new int[2];

        int newhealth = gameGrid[p.getPosX() + 1][p.getPosY()].getHealth() - p.getDamage();
        int myhealth = p.getHealth() - (gameGrid[p.getPosX() + 1][p.getPosY()].getDamage() / 2);

        //player unit survives
        if (newhealth > 0) {
            gameGrid[p.getPosX() + 1][p.getPosY()].setHealth(newhealth);
            newLocation[0] = p.getPosX();
            //enemy unit survives attack, stay in same position
            if (myhealth > 0) {
                p.setHealth(myhealth);
                newLocation[1] = p.getPosY();
            //enemy unit dies in attack
            } else {
                newLocation[0] = -1;
            }
        } else {
            newLocation[0] = p.getPosX() + 1;
            newLocation[1] = p.getPosY();

            gameGrid[p.getPosX() + 1][p.getPosY()] = p;
            Space fill = new Space();
            gameGrid[p.getPosX()][p.getPosY()] = fill;
            p.setPosX(p.getPosX() + 1);
        }

        return newLocation;
    }

    // EFFECTS: Runs left attack calculations for soldiers
    public int[] attackLeftSoldier(Pieces p) {
        int[] newLocation = new int[2];

        int newhealth = gameGrid[p.getPosX() + 1][p.getPosY() - 1].getHealth() - p.getDamage();
        int enemyhealth = p.getHealth() - (gameGrid[p.getPosX() + 1][p.getPosY() - 1].getDamage() / 2);

        if (newhealth > 0) {
            gameGrid[p.getPosX() + 1][p.getPosY() - 1].setHealth(newhealth);
            newLocation[0] = p.getPosX();
            if (enemyhealth > 0) {
                p.setHealth(enemyhealth);
                newLocation[1] = p.getPosY();
            } else {
                newLocation[0] = -1;
            }
        } else {
            newLocation[0] = p.getPosX() + 1;
            newLocation[1] = p.getPosY() - 1;

            gameGrid[p.getPosX() + 1][p.getPosY() - 1] = p;
            Space fill = new Space();
            gameGrid[p.getPosX()][p.getPosY()] = fill;
            p.setPosX(p.getPosX() + 1);
            p.setPosY(p.getPosY() - 1);
        }
        return newLocation;
    }

    // EFFECTS: Runs right attack calculations for soldiers
    public int[] attackRightSoldier(Pieces p) {
        int[] newLocation = new int[2];

        int newhealth = gameGrid[p.getPosX() + 1][p.getPosY() + 1].getHealth() - p.getDamage();
        int enemyhealth = p.getHealth() - (gameGrid[p.getPosX() + 1][p.getPosY() + 1].getDamage() / 2);

        if (newhealth > 0) {
            gameGrid[p.getPosX() + 1][p.getPosY() + 1].setHealth(newhealth);
            newLocation[0] = p.getPosX();
            if (enemyhealth > 0) {
                p.setHealth(enemyhealth);
                newLocation[1] = p.getPosY();
            } else {
                newLocation[0] = -1;
            }
        } else {
            newLocation[0] = p.getPosX() + 1;
            newLocation[1] = p.getPosY() + 1;

            gameGrid[p.getPosX() + 1][p.getPosY() + 1] = p;
            Space fill = new Space();
            gameGrid[p.getPosX()][p.getPosY()] = fill;
            p.setPosX(p.getPosX() + 1);
            p.setPosY(p.getPosY() + 1);
        }
        return newLocation;
    }

    // EFFECTS: Move enemy unit straight
    public void moveStraight(Pieces p) {
        Space fill = new Space();
        gameGrid[p.getPosX() + 1][p.getPosY()] = p;
        gameGrid[p.getPosX()][p.getPosY()] = fill;
        p.setPosX(p.getPosX() + 1);
    }

    // EFFECTS: Move enemy unit left
    public void moveLeft(Pieces p) {
        Space fill = new Space();
        gameGrid[p.getPosX()][p.getPosY() - 1] = p;
        gameGrid[p.getPosX()][p.getPosY()] = fill;
        p.setPosY(p.getPosY() - 1);
    }

    // EFFECTS: Move enemy unit right
    public void moveRight(Pieces p) {
        Space fill = new Space();
        gameGrid[p.getPosX()][p.getPosY() + 1] = p;
        gameGrid[p.getPosX()][p.getPosY()] = fill;
        p.setPosY(p.getPosY() + 1);
    }

    // EFFECTS: Check if enemy castle has been defeated
    public boolean checkEnemyCastle() {
        int x = tutorial.getEnemyCastleX();
        int y = tutorial.getEnemyCastleY();

        return !gameGrid[x][y].getLabel().equals("EnemyCastle");
    }

    public boolean checkAllyCastle() {
        int x = tutorial.getAllyCastleX();
        int y = tutorial.getAllyCastleY();

        return !gameGrid[x][y].getLabel().equals("Castle");
    }

    // EFFECTS: Check if space x, y is a legal space for player to grab
    public boolean isLegalSpace(int x, int y) {
        return !gameGrid[x][y].getLabel().equals("AllySoldier") && !gameGrid[x][y].getLabel().equals("AllyArcher") && !gameGrid[x][y].getLabel().equals("Space");
    }

    // EFFECTS: Check if x, y has space
    public boolean isSpace(int x, int y) {
        return gameGrid[x][y].getLabel().equals("Space");
    }

    /*
    // EFFECTS: Checks if unit at x , y is an enemy
    public int isEnemyUnit(int x, int y) {
        if (tutorial.getGrid()[x][y].getLabel().equals("EnemySoldier")) {
            return 1;
        } else if (tutorial.getGrid()[x][y].getLabel().equals("EnemyCastle")) {
            return 2;
        } else {
            return 0;
        }
    }


    //EFFECTS: Simple AI for the enemy unit to move towards a target x and y coordinate (castle)
    public void enemySoldierMove(Pieces p, Pieces[][] gameGrid) {
        int targetx = getTargetXCoord(p, gameGrid);
        int targety = getTargetYCoord(gameGrid);

        //Go through each piece on board to find enemy units

        System.out.println("Unit[" + p.getPosX() + "][" + p.getPosY() + "]");
        System.out.println();

        String attackdir = enemySoldierAttackWhere(p, gameGrid);
        enemySoldierAttack(p, gameGrid, attackdir);

        if (p.seeIfMoved()) {
            if (p.getPosX() - targetx != -1) {
                enemyMoveDown(gameGrid, p);
            } else if (p.getPosY() - targety != 0) {
                int dir = -(p.getPosY() - targety);
                enemyMoveRightLeft(gameGrid, p, dir);
            }
        }
    }

     */


    /*
    // EFFECTS: Loads save file
    public void loadSave(Player player) {
        if (player.getTutorial()) {
            System.out.println("---------------Level One----------------");
            LevelOne one = new LevelOne();
            buyPhase(player);
            prepPhase(player, one.getGrid(), one.getMap());
        } else {
            startGame(player);
        }
    }

    //EFFECTS: Shows user options for units they can buy
    public void buyPhase(Player player) {
        System.out.println("\nBuy Phase\n");
        String in = "";
        while (!in.equals("Done")) {
            buyOptions(player);
            System.out.println("\nTo buy or learn more about a unit, type their name");
            System.out.println("To finish buying, enter 'Done'");
            System.out.println("To save game, enter 'Save'\nTo quit game, enter 'Quit'\n");
            in = getUserInputString();
            if (in.equals("Soldier")) {
                buyMenu(player,in);
            } else if (in.equals("Archer")) {
                buyMenu(player, in);
            } else if (in.equals("Save")) {
                savePlayer();
            } else if (in.equals("Quit")) {
                askSave();
                System.exit(0);
            } else {
                if (!in.equals("Done")) {
                    System.out.println("Unknown Command Please Try Again");
                }
            }
        }
    }

    public void askSave() {
        System.out.println("Would you like to save the game? (Y/N)");

        String in = getUserInputString();
        if (in.equals("Y")) {
            savePlayer();
        }
    }

    //EFFECTS: Shows the units available for purchase
    public void buyOptions(Player player) {
        System.out.println("Coins:" + player.getMoney() + "\n");

        System.out.println("Soldier: 5 coins;  MAX 10 Units");
        System.out.println("Archer: 5 coins; MAX 5 Units");
    }

    // EFFECTS: Prints out currently owned units
    public void printCurrentUnits(Player player,String unit) {
        System.out.println("\nCurrent owned units");
        System.out.println("Soldiers: " + player.getSoldiers().size());
        System.out.println("Archers: " + player.getArchers().size());
        System.out.println(unit + ":");
        System.out.println("To learn more about the unit, enter 'info'");
        System.out.println("To buy the unit, enter 'buy'");
        System.out.println("To go back, enter 'return'");
    }

    //EFFECTS: Shows currently owned units and allows user to look up information or buy the unit
    public void buyMenu(Player player, String unit) {
        printCurrentUnits(player,unit);

        String in = getUserInputString();
        System.out.println();


        switch (in) {
            case "info": {
                unitInfo(unit);
                break;
            }
            case "buy": {
                player.buyUnits(unit);
                break;
            }
            case "return": {
                return;
            }
            default: {
                System.out.println("Unknown Command");
                buyMenu(player, unit);
                break;
            }
        }
    }

    // EFFECTS: Displays info about soldier unit
    public void printSoldierInfo() {
        System.out.println("Cost:");
        System.out.println(("5 Coins"));

        System.out.println("Moves:");
        System.out.println("Can move 1 space or attack once per turn\n");

        System.out.println("Attack:");
        System.out.println("Can deal 2 damage to any enemy in a 1 block radius\n");

        System.out.println("Health");
        System.out.println("3\n");

        System.out.println("Value:");
        System.out.println("Gives 3 gold when enemy soldier defeated\n");
    }

    // EFFECTS: Displays info about archer unit
    public void printArcherInfo() {
        System.out.println("Cost:");
        System.out.println(("5 Coins"));

        System.out.println("Moves:");
        System.out.println("Can move 1 space or attack once per turn\n");

        System.out.println("Attack:");
        System.out.println("Can deal 1 damage to any enemy in a straight line ahead up to 2 or 3 spaces away\n");

        System.out.println("Health");
        System.out.println("2\n");

        System.out.println("Value:");
        System.out.println("Gives 2 gold when enemy soldier defeated\n");
    }

    //EFFECT: Displays the unit's information
    public void unitInfo(String unit) {
        switch (unit) {
            case "Soldier":
                printSoldierInfo();
                break;
            case "Archer":
                printArcherInfo();
                break;
        }
    }

    // EFFECTS: Prints out starting text for tutorial
    public void printTutorialPrepPhase() {
        System.out.println("\nNow it's time for you to place your units onto the field!\n");
        System.out.print("You will only be able to place your units on your side of the field ");
        System.out.println("(below the dotted line)");

        System.out.print("As you progress through the game you will find better");
        System.out.println(" units that can start on the other side\n\n");
    }

    // EFFECTS: Prints out text for prepPhase
    public void printDefaultPrepPhase() {
        System.out.println("To place a unit, enter 'p'");
        System.out.println("To take a unit back from the board, enter 't'");
        System.out.println("To see the enemy's units, enter 'm'");
        System.out.println("To finish, enter 'Done'");
        System.out.println("To save, enter 'Save'");
        System.out.println("To exit game, enter 'Quit'");
    }

    //EFFECTS: Runs the preparation phase of the game
    public boolean prepPhase(Player player, Pieces[][] gameGrid, String[][] mapView) {
        levelMap = mapView;
        System.out.println("Prep Phase");
        if (!player.getTutorial()) {
            printTutorialPrepPhase();
        }

        player.setTutorial(true);
        return runPrepPhase(player, gameGrid,true, false);

    }

    // EFFECTS: Helper function to run prepPhase and battlePhase
    @SuppressWarnings("checkstyle:MethodLength")
    public boolean runPrepPhase(Player player, Pieces[][] gameGrid, boolean cont, boolean completed) {
        while (cont) {
            printDefaultPrepPhase();
            String command = getUserInputString();
            if (command.length() > 0) {
                if (command.equals("p")) {
                    System.out.println("\nYou can see the units you own below");
                    displayUnitsRemaining(player);
                    getUnitsForPlacing(player, gameGrid);
                } else if (command.equals("t")) {
                    getUnitsForTaking(player, gameGrid);
                } else if (command.equals("m")) {
                    drawGrid(gameGrid);
                } else if (command.equals("Done")) {
                    completed = battlePhase(player, gameGrid);
                    cont = false;
                } else if (command.equals("Save")) {
                    savePlayer();
                } else if (command.equals("Quit")) {
                    savePlayer();
                    System.exit(0);
                } else {
                    System.out.println("Unknown Command");
                }
            }
        }
        return completed;
    }

    //EFFECTS: Get the unit that the player wants to place onto the field
    public void getUnitsForPlacing(Player player, Pieces[][] gameGrid) {
        System.out.println("To place a unit, type the unit name into the console");
        System.out.println("To go back to the previous menu, type 'Done'");
        String unitType = getUserInputString();

        if (unitType.length() > 0) {
            if (commands.contains(unitType)) {
                getNewCoords(player, gameGrid, unitType);
            } else {
                if (!unitType.equals("Done")) {
                    System.out.println("Sorry, I don't understand that command");
                    getUnitsForPlacing(player, gameGrid);
                }
            }

        }
    }

    //EFFECTS: Gets the unit that the player wants to take back
    public void getUnitsForTaking(Player player, Pieces[][] gameGrid) {
        System.out.println("Please enter the x coordinate(row) of the unit");
        int xcoord = getUserUnitCoord();
        System.out.println("Please enter the y coordinate(col) of the unit");
        int ycoord = getUserUnitCoord();

        if (gameGrid[xcoord][ycoord].seeIfEnemy()) {
            System.out.println("Can't take that unit!");
        } else {
            if (gameGrid[xcoord][ycoord].getLabel().equals("Castle")) {
                System.out.println("Can't move your castle!");
            } else {
                takeUnit(player, xcoord, ycoord, gameGrid);
            }
        }
    }

    //EFFECTS: Takes unit off the board, add it to player's cache of units and replaces location with a 'space'
    public void takeUnit(Player player, int xcoord, int ycoord, Pieces[][] gameGrid) {
        String unitType = gameGrid[xcoord][ycoord].getLabel();

        switch (unitType) {
            case "AllySoldier": {
                Soldier current = new Soldier(false, 0, 0);
                player.getSoldiers().add(current);

                Space fill = new Space();
                gameGrid[xcoord][ycoord] = fill;
                //Needs to be commented out for test to work since cannot simulate user input
                //prepPhase(player, gameGrid, levelMap);
                break;
            }
            case "AllyArcher": {
                Archer current = new Archer(false, 0, 0);
                player.getArchers().add(current);

                Space fill = new Space();
                gameGrid[xcoord][ycoord] = fill;
                //prepPhase(player, gameGrid, levelMap);
                break;
            }
        }
    }

    //EFFECTS: Get x and y coords for new unit location and calls placeUnit
    public void getNewCoords(Player player, Pieces[][] gameGrid, String unitType) {
        if (hasUnits(unitType, player)) {
            System.out.println("Input the x coordinate (row) that you would like to place your unit");
            int xcoord = getUserUnitCoord();
            if (xcoord < 0 || xcoord >= gameGrid.length || xcoord < gameGrid.length / 2) {
                System.out.println("Invalid X coordinate");
                getUnitsForPlacing(player,gameGrid);
                return;
            }
            System.out.println("Input the y coordinate (column) that you would like to place your unit");
            int ycoord = getUserUnitCoord();
            if (ycoord < 0 || ycoord >= gameGrid[0].length) {
                System.out.println("Y coord out of bounds");
                getUnitsForPlacing(player,gameGrid);
                return;
            }
            placeUnit(player,gameGrid,unitType,xcoord,ycoord);
        }
    }

    //MODIFIES: gameGrid, current unit
    //EFFECTS: Places soldier unit onto game board
    public void placeSoldier(Player player, Pieces[][] gameGrid, String unitType, int xcoord, int ycoord) {
        Soldier current = player.getSoldiers().get(0);
        current.setPosX(xcoord);
        current.setPosY(ycoord);


        if (!gameGrid[xcoord][ycoord].getLabel().equals("Space")) {
            System.out.println("Invalid coordinate location, please re-enter new x and y coordinates");
            current.setPosX(0);
            current.setPosY(0);
            getNewCoords(player, gameGrid, unitType);
        } else {
            gameGrid[xcoord][ycoord] = current;
            player.getSoldiers().remove(0);
            drawGrid(gameGrid);
            System.out.println();
            displayUnitsRemaining(player);
            //Needs to be commented out for test to work since cannot simulate user input
            //prepPhase(player, gameGrid, levelMap);
        }
    }

    //MODIFIES: gameGrid, current unit
    //EFFECTS: Places archer unit onto game board
    public void placeArcher(Player player, Pieces[][] gameGrid, String unitType, int xcoord, int ycoord) {
        Archer current = player.getArchers().get(0);
        current.setPosX(xcoord);
        current.setPosY(ycoord);

        if (!gameGrid[xcoord][ycoord].getLabel().equals("Space")) {
            System.out.println("Invalid coordinate location, please re-enter new x and y coordinates");
            current.setPosX(0);
            current.setPosY(0);
            getNewCoords(player, gameGrid, unitType);
        } else {
            gameGrid[xcoord][ycoord] = current;
            player.getArchers().remove(0);

            drawGrid(gameGrid);
            System.out.println();
            displayUnitsRemaining(player);
            //Needs to be commented out for test to work since cannot simulate user input
            //prepPhase(player, gameGrid, levelMap);
        }
    }

    //EFFECTS: Calls helper functions to place unit on board
    public void placeUnit(Player player, Pieces[][] gameGrid, String unitType, int xcoord, int ycoord) {
        switch (unitType) {
            case "Soldier": {
                placeSoldier(player,gameGrid,unitType,xcoord,ycoord);
                break;
            }
            case "Archer": {
                placeArcher(player,gameGrid,unitType,xcoord,ycoord);
                break;
            }
        }
    }

    // EFFECTS: Get x coord of enemy or ally castle
    public int getCastleX(boolean enemy, Pieces[][] gameGrid) {
        int xval = 0;

        for (int i = 0; i < gameGrid.length; i++) {
            for (int j = 0; j < gameGrid[0].length; j++) {
                if (enemy) {
                    if (gameGrid[i][j].getLabel().equals("EnemyCastle")) {
                        xval = i;
                        break;
                    }
                } else {
                    if (gameGrid[i][j].getLabel().equals("Castle")) {
                        xval = i;
                        break;
                    }
                }
            }
        }
        return xval;
    }

    // EFFECTS: Gets y coord of enemy or ally castle
    public int getCastleY(boolean enemy, Pieces[][] gameGrid) {
        int yval = 0;

        for (Pieces[] pieces : gameGrid) {
            for (int j = 0; j < gameGrid[0].length; j++) {
                if (enemy) {
                    if (pieces[j].getLabel().equals("EnemyCastle")) {
                        yval = j;
                        break;
                    }
                } else {
                    if (pieces[j].getLabel().equals("Castle")) {
                        yval = j;
                        break;
                    }
                }
            }
        }
        return yval;
    }

    //EFFECTS: Cycles through each of player's units and runs a battle sequence for each one, ends game once enemy castle dies
    public boolean battlePhase(Player player,Pieces[][] gameGrid) {
        int enemyx = getCastleX(true, gameGrid);
        int enemyy = getCastleY(true,gameGrid);
        int mycastlex = getCastleX(false, gameGrid);
        int mycastley = getCastleY(false, gameGrid);
        int friendlyunits;
        while (gameGrid[enemyx][enemyy].seeIfEnemy() && !gameGrid[mycastlex][mycastley].seeIfEnemy()) {
            friendlyunits = playerTurn(gameGrid);
            if (friendlyunits != 0) {
                enemyTurn(gameGrid);
                setMoveToFalse(gameGrid);
            } else {
                gameGrid[mycastlex][mycastley].setEnemy(true);
                break;
            }
        }
        return endBattle(gameGrid, player, enemyx, enemyy);
    }

    // EFFECTS: Helper function to run end of battle sequence
    public boolean endBattle(Pieces[][] gameGrid, Player player, int enemyx, int enemyy) {
        if (!gameGrid[enemyx][enemyy].seeIfEnemy() || gameGrid[enemyx][enemyy].getHealth() == 0) {
            giveUnitsBack(player, gameGrid);
            System.out.println("You Win! Thanks for playing the tutorial!");
            return true;
        } else {
            System.out.println("You Lost!");
            return false;
        }
    }

    // EFFECTS: Helper function to run player turn
    public int playerTurn(Pieces[][] gameGrid) {
        int friendlyunits = 0;
        for (Pieces[] row : gameGrid) {
            //Find friendly unit starting from top to bottom
            for (Pieces p : row) {
                if (!p.seeIfEnemy()) {
                    //Check to see if unit is friendly castle,if not proceed to move or attack
                    if (!p.getLabel().equals("Castle")) {
                        if (p.seeIfMoved()) {
                            System.out.println("\nUnit[" + p.getPosX() + "][" + p.getPosY() + "]");
                            friendlyunits++;
                            getCommand(gameGrid, p);
                        }

                    }
                }
            }
        }
        return friendlyunits;
    }

    // MODIFIES: player
    // EFFECTS: Gives player their units back after battle
    public void giveUnitsBack(Player player, Pieces[][] gameGrid) {
        for (Pieces[] pieces : gameGrid) {
            for (int j = 0; j < gameGrid[0].length; j++) {
                if (!pieces[j].seeIfEnemy()) {
                    if (pieces[j].getLabel().equals("AllySoldier")) {
                        player.getSoldiers().add((Soldier) pieces[j]);
                    } else if (pieces[j].getLabel().equals("AllyArcher")) {
                        player.getArchers().add((Archer) pieces[j]);
                    }
                }
            }
        }
    }

    //MODIFIES: Piece p
    //EFFECTS: Sets ally unit pieces movement boolean back to false for next turn
    public void setMoveToFalse(Pieces[][] gameGrid) {
        for (Pieces[] row : gameGrid) {
            for (Pieces p : row) {
                if (!p.seeIfEnemy()) {
                    if (!p.getLabel().equals("Castle")) {
                        p.setMoved(false);
                    }
                } else if (p.seeIfEnemy()) {
                    if (!p.getLabel().equals("Space") || !p.getLabel().equals("EnemyCastle")) {
                        p.setMoved(false);
                    }
                }
            }
        }
    }

    //EFFECTS: Prints out available commands
    public void printMoveCommandList() {
        System.out.println("To move unit, type 'm'");
        System.out.println("To try to attack with this unit, type 'a'");
        System.out.println("To see stats and abilities of this unit, type 's'");
        System.out.println("To skip this unit, type 'p'");
    }

    //EFFECTS: Gets player command for Piece p and calls attack function if necessary
    @SuppressWarnings({"checkstyle:MethodLength", "checkstyle:SuppressWarnings"})
    public void getCommand(Pieces[][] gameGrid, Pieces p) {
        if (!p.getLabel().equals("Spy")) {
            printMoveCommandList();
            String in = getUserInputString();

            switch (in) {
                case "m": {
                    canMove(gameGrid, p);
                    break;
                }
                case "a": {
                    if (p.getLabel().equals("AllySoldier")) {
                        canAttackSoldier(gameGrid, p);
                    } else if (p.getLabel().equals("AllyArcher")) {
                        canAttackArcher(gameGrid, p);
                    }
                    break;
                }
                case "p": {
                    return;
                }
                case "s": {
                    System.out.println("Enter x coordinate of unit you wish to see");
                    int x = getUserUnitCoord();
                    System.out.println("Enter y coordinate of unit you wish to see");
                    int y = getUserUnitCoord();

                    getStats(gameGrid, x, y);
                    getCommand(gameGrid,p);
                    break;
                }
                default: {
                    System.out.println("Unknown command, please enter again");
                    getCommand(gameGrid,p);
                    break;
                }
            }
        }
    }

    //EFFECTS: Displays the stats of selected unit
    public void getStats(Pieces[][] gameGrid, int x, int y) {
        if (x < gameGrid.length && x >= 0) {
            if (y < gameGrid[0].length && y >= 0) {
                Pieces p = gameGrid[x][y];
                System.out.println("Unit Name: " + p.getLabel());
                System.out.println("Unit Health: " + p.getHealth());
                System.out.println("Unit Damage: " + p.getDamage());
            }
        }
    }

    // EFFECTS: Checks if unit can move up
    public boolean canMoveUp(Pieces[][] gameGrid, Pieces p) {
        if (p.getPosX() - 1 >= 0) {
            return gameGrid[p.getPosX() - 1][p.getPosY()].getLabel().equals("Space");
        }
        return false;
    }

    // EFFECTS: Checks if unit can move down
    public boolean canMoveDown(Pieces[][] gameGrid, Pieces p) {
        if (p.getPosX() + 1 < gameGrid.length) {
            return gameGrid[p.getPosX() + 1][p.getPosY()].getLabel().equals("Space");
        }
        return false;
    }

    // EFFECTS: Checks if unit can move left
    public boolean canMoveLeft(Pieces[][] gameGrid, Pieces p) {
        if (p.getPosY() - 1 >= 0) {
            return gameGrid[p.getPosX()][p.getPosY() - 1].getLabel().equals("Space");
        }
        return false;
    }

    // EFFECTS: Checks if unit can move right
    public boolean canMoveRight(Pieces[][] gameGrid, Pieces p) {
        if (p.getPosY() + 1 < gameGrid[0].length) {
            return gameGrid[p.getPosX()][p.getPosY() + 1].getLabel().equals("Space");
        }
        return false;
    }

    //EFFECTS: Checks if ally unit can move to a specific spot on grid and calls the moveUnit function if possible
    public void canMove(Pieces[][] gameGrid, Pieces p) {
        boolean up = canMoveUp(gameGrid, p);
        boolean down = canMoveDown(gameGrid, p);
        boolean left = canMoveLeft(gameGrid, p);
        boolean right = canMoveRight(gameGrid, p);
        System.out.println("enter 'u','d','l','r' to move your unit up, down, left or right");
        String in = getUserInputString();
        if (in.equals("u") && up) {
            moveUnit("x", -1, gameGrid, p);
        } else if (in.equals("d") && down) {
            moveUnit("x", 1, gameGrid, p);
        } else if (in.equals("l") && left) {
            moveUnit("y", -1, gameGrid, p);
        } else if (in.equals("r") && right) {
            moveUnit("y", 1, gameGrid, p);
        } else {
            System.out.println("Invalid Command");
            canMove(gameGrid,p);
        }
    }
*/

    /*
    //MODIFIES: gameGrid
    //EFFECTS: moves selected unit to new location
    public void moveUnit(String direction, int distance, Pieces[][] gameGrid, Pieces p) {
        if (direction.equals("x")) {
            gameGrid[p.getPosX() + distance][p.getPosY()] = p;

            //Fill in p's old position with an empty space
            Space fill = new Space();
            gameGrid[p.getPosX()][p.getPosY()] = fill;

            //Set p's coordinates to new location
            p.setPosX(p.getPosX() + distance);
            //p.setPosY(p.getPosY());

            //Set if p moved to true
            p.setMoved(true);

            drawGrid(gameGrid);
        } else if (direction.equals("y")) {
            gameGrid[p.getPosX()][p.getPosY() + distance] = p;

            //Fill in p's old position with an empty space
            Space fill = new Space();
            gameGrid[p.getPosX()][p.getPosY()] = fill;

            //Set p's coordinates to new location
            p.setPosY(p.getPosY() + distance);
            //p.setPosY(p.getPosX());

            //Set if p moved to true
            p.setMoved(true);

            drawGrid(gameGrid);
        }
    }

    // EFFECTS: checks that archer can attack 2 or 3 spaces away and runs attack function
    public void canAttackArcher(Pieces[][] gameGrid, Pieces p) {
        boolean canattackthree = false;
        boolean canattacktwo = false;
        if (p.getPosX() - 3 >= 0) {
            canattackthree = true;
        }
        if (p.getPosX() - 2 >= 0) {
            canattacktwo = true;
        }
        System.out.println("Type '2' to attack two spaces away\nType '3' to attack three spaces away");
        String in = getUserInputString();

        if (in.equals("3")) {
            if (canattackthree) {
                checkAttackArcher(gameGrid, p, 3);
            }
        } else if (in.equals("2")) {
            if (canattacktwo) {
                checkAttackArcher(gameGrid, p, 2);
            }
        } else {
            System.out.println("Can't attack that!");
        }
    }

    // EFFECTS: check that archer can attack the selected distance
    public void checkAttackArcher(Pieces[][] gameGrid, Pieces p, int distance) {
        if (gameGrid[p.getPosX() - distance][p.getPosY()].seeIfEnemy()) {
            if (!gameGrid[p.getPosX() - distance][p.getPosY()].getLabel().equals("Space")) {
                attackUnitArcher(gameGrid, p, distance);
            } else {
                System.out.println("Can't attack an empty space");

                getCommand(gameGrid, p);
            }
        }
    }

    // EFFECTS: Helper function to check if unit can attack straight
    public boolean checkStraight(Pieces p) {
        return p.getPosX() - 1 >= 0;
    }

    // EFFECTS: Helper function to check if unit can attack left
    public boolean checkLeft(Pieces p) {
        return checkStraight(p) && p.getPosY() - 1 >= 0;
    }

    // EFFECTS: Helper function to check if unit can attack right
    public boolean checkRight(Pieces[][] gameGrid, Pieces p) {
        return checkStraight(p) && p.getPosY() + 1 < gameGrid[0].length;
    }

    //EFFECTS: Checks if unit can attack the space that the user selects and run attackUnitSoldier if possible
    public void canAttackSoldier(Pieces[][] gameGrid, Pieces p) {
        boolean spacestraight = checkStraight(p);
        boolean spaceleft = checkLeft(p);
        boolean spaceright = checkRight(gameGrid, p);

        System.out.println("Type 'l' to attack diagonally left, 'r' to attack diagonally right, 's' to attack straight ahead");
        String in = getUserInputString();
        if (in.equals("s")) {
            if (spacestraight) {
                tryAttackStraight(gameGrid, p);
            }
        } else if (in.equals("l")) {
            if (spaceleft) {
                tryAttackLeft(gameGrid, p);
            }
        } else if (in.equals("r")) {
            if (spaceright) {
                tryAttackRight(gameGrid, p);
            }
        } else {
            System.out.println("Invalid Command");
            canAttackSoldier(gameGrid, p);
        }
    }

    // EFFECTS: try to attack unit straight ahead
    public void tryAttackStraight(Pieces[][] gameGrid, Pieces p) {
        if (gameGrid[p.getPosX() - 1][p.getPosY()].seeIfEnemy()) {
            if (!gameGrid[p.getPosX() - 1][p.getPosY()].getLabel().equals("Space")) {
                attackUnitSoldier(gameGrid, p, "s");
            } else {
                System.out.println("Can't attack an empty space");
                getCommand(gameGrid, p);
            }
        }
    }

    // EFFECTS: try to attack unit to the left
    public void tryAttackLeft(Pieces[][] gameGrid, Pieces p) {
        if (gameGrid[p.getPosX() - 1][p.getPosY() - 1].seeIfEnemy()) {
            if (!gameGrid[p.getPosX() - 1][p.getPosY() - 1].getLabel().equals("Space")) {
                attackUnitSoldier(gameGrid, p, "l");
            } else {
                System.out.println("Can't attack an empty space");
                getCommand(gameGrid, p);
            }
        }
    }

    // EFFECTS: try to attack unit to the right
    public void tryAttackRight(Pieces[][] gameGrid, Pieces p) {
        if (gameGrid[p.getPosX() - 1][p.getPosY() + 1].seeIfEnemy()) {
            if (!gameGrid[p.getPosX() - 1][p.getPosY() + 1].getLabel().equals("Space")) {
                attackUnitSoldier(gameGrid, p, "r");
            } else {
                System.out.println("Can't attack an empty space");
                getCommand(gameGrid, p);
            }
        }
    }

    // EFFECTS: attacks enemy with ally archer unit
    public void attackUnitArcher(Pieces[][] gameGrid, Pieces p, int distance) {
        int damage = p.getDamage();
        int newhealth = gameGrid[p.getPosX() - distance][p.getPosY()].getHealth() - damage;

        if (newhealth > 0) {
            gameGrid[p.getPosX() - distance][p.getPosY()].setHealth(newhealth);
        } else {
            Space fill = new Space();
            gameGrid[p.getPosX() - distance][p.getPosY()] = fill;
        }

        drawGrid(gameGrid);
        System.out.println("Attacked " + distance + " units away!");
    }

    // EFFECTS: Runs soldier attack function based on user input
    public void attackUnitSoldier(Pieces[][] gameGrid, Pieces p, String direction) {
        System.out.println(p.getPosX());
        System.out.println(p.getPosY());
        int damage = p.getDamage();
        switch (direction) {
            case "s": {
                soldierAttack(gameGrid, p, -1, 0, damage);
                break;
            }
            case "l": {
                soldierAttack(gameGrid, p, -1, -1, damage);
                break;
            }
            case "r": {
                soldierAttack(gameGrid, p, -1, 1, damage);
                break;
            }
        }
    }

    //MODIFIES: gameGrid, p
    //EFFECTS: attacks selected unit and makes appropriate changes to health values.
    public void soldierAttack(Pieces[][] gameGrid, Pieces p, int xval, int yval, int damage) {
        int newhealth = gameGrid[p.getPosX() + xval][p.getPosY() + yval].getHealth() - damage;
        if (newhealth > 0) {
            gameGrid[p.getPosX() + xval][p.getPosY() + yval].setHealth(newhealth);
        } else {
            Pieces temp = gameGrid[p.getPosX() + xval][p.getPosY() + yval];
            player.setMoney(player.getMoney() + temp.getReward());
            fillSpace(gameGrid, p, xval, yval);
        }
        drawGrid(gameGrid);
        System.out.print("Attacked enemy unit[");
        System.out.print(p.getPosX() + xval);
        System.out.print("][");
        System.out.print(p.getPosY() + yval);
        System.out.print("]");
    }

    // MODIFIES: gameGrid
    // EFFECTS: helper function to fill in space on gameGrid
    public void fillSpace(Pieces[][] gameGrid, Pieces p, int xval, int yval) {
        Space fill = new Space();
        gameGrid[p.getPosX() + xval][p.getPosY() + yval] = p;

        gameGrid[p.getPosX()][p.getPosY()] = fill;

        p.setPosX(p.getPosX() + xval);
        p.setPosY(p.getPosY() + yval);
    }

    // EFFECTS: finds every enemy unit and runs their turn
    public void enemyTurn(Pieces[][] gameGrid) {
        for (int i = gameGrid.length - 1; i >= 0; i--) {
            for (int j = gameGrid[0].length - 1; j >= 0; j--) {
                Pieces p = gameGrid[i][j];
                //Check if piece is enemy
                if (p.seeIfEnemy() && !p.getLabel().equals("EnemyCastle")) {
                    //check if piece has already moved
                    if (p.seeIfMoved()) {
                        if (p.getLabel().equals("EnemySoldier")) {
                            System.out.println("Is EnemySoldier Unit!");
                            enemySoldierMove(p, gameGrid);
                        }
                    }
                }
            }
        }
    }

    // EFFECTS: gets the target x coordinate of enemy unit
    public int getTargetXCoord(Pieces p, Pieces[][] gameGrid) {
        int targetx = 0;
        for (Pieces[] r : gameGrid) {
            for (Pieces c : r) {
                //get x and y pos of player castle
                if (c.getLabel().equals("Castle")) {
                    targetx = c.getPosX();

                    if (p.getLabel().equals("EnemyArcher")) {
                        targetx -= 3;
                    }
                }
            }
        }

        System.out.println("Target X: " + targetx);
        return targetx;
    }

    // EFFECTS: gets the target y coordinate for enemy units
    public int getTargetYCoord(Pieces[][] gameGrid) {
        int targety = 0;
        for (Pieces[] r : gameGrid) {
            for (Pieces c : r) {
                //get x and y pos of player castle
                if (c.getLabel().equals("Castle")) {
                    targety = c.getPosY();
                }
            }
        }

        System.out.println("Target Y: " + targety);
        return targety;
    }

    // EFFECTS: checks possible attack locations for enemy soldier unit
    public String enemySoldierAttackWhere(Pieces p, Pieces[][] gameGrid) {
        //Check for unit to attack in front
        int targethealth = 0;
        String attackdir = "";

        System.out.println("Checking Unit[" + (p.getPosX() + 1) + "][" + p.getPosY() + "]");
        if (!gameGrid[p.getPosX() + 1][p.getPosY()].seeIfEnemy()) {
            targethealth = gameGrid[p.getPosX() + 1][p.getPosY()].getHealth();
            attackdir = "s";
        }

        System.out.println("Checking Unit[" + (p.getPosX() + 1) + "][" + (p.getPosY() - 1) + "]");
        if (!gameGrid[p.getPosX() + 1][p.getPosY() - 1].seeIfEnemy()) {
            if (gameGrid[p.getPosX() + 1][p.getPosY() - 1].getHealth() < targethealth || targethealth == 0) {
                targethealth = gameGrid[p.getPosX() + 1][p.getPosY() - 1].getHealth();
                attackdir = "l";
            }
        }

        System.out.println("Checking Unit[" + (p.getPosX() + 1) + "][" + (p.getPosY() + 1) + "]");
        if (!gameGrid[p.getPosX() + 1][p.getPosY() + 1].seeIfEnemy()) {
            if (gameGrid[p.getPosX() + 1][p.getPosY() + 1].getHealth() < targethealth || targethealth == 0) {
                attackdir = "r";
            }
        }
        System.out.println(attackdir);
        return attackdir;
    }

    // EFFECTS: Prints attack direction and runs attack function
    public void enemySoldierAttack(Pieces p, Pieces[][] gameGrid, String attackdir) {
        int ydir = 0;
        switch (attackdir) {
            case "l":
                ydir = -1;
                System.out.println("Attacked left!");
                break;
            case "r":
                ydir = 1;
                System.out.println("Attacked right!");
                break;
            case "s":
                System.out.println("Attacked straight ahead!");
                break;
        }

        if (attackdir.equals("s") || attackdir.equals("l") || attackdir.equals("r")) {
            runAttack(gameGrid, p, ydir);
        }
    }

    // MODIFIES: gameGrid, p
    // EFFECTS: Attacks selected unit
    public void runAttack(Pieces[][] gameGrid, Pieces p, int ydir) {
        Pieces attacked = gameGrid[p.getPosX() + 1][p.getPosY() + ydir];
        int attackedhealth = attacked.getHealth();
        if (attackedhealth - p.getDamage() <= 0) {
            gameGrid[p.getPosX() + 1][p.getPosY() + ydir] = p;
            Space fill = new Space();
            gameGrid[p.getPosX()][p.getPosY()] = fill;
            p.setPosX(p.getPosX() + 1);
            p.setPosY(p.getPosY() + ydir);
        } else {
            gameGrid[p.getPosX() + 1][p.getPosY() + ydir].setHealth(attackedhealth - p.getDamage());
        }
        p.setMoved(true);
        drawGrid(gameGrid);
    }

    //EFFECTS: Simple AI for the enemy unit to move towards a target x and y coordinate (castle)
    public void enemySoldierMove(Pieces p, Pieces[][] gameGrid) {
        int targetx = getTargetXCoord(p, gameGrid);
        int targety = getTargetYCoord(gameGrid);

        //Go through each piece on board to find enemy units

        System.out.println("Unit[" + p.getPosX() + "][" + p.getPosY() + "]");
        System.out.println();

        String attackdir = enemySoldierAttackWhere(p, gameGrid);
        enemySoldierAttack(p, gameGrid, attackdir);

        if (p.seeIfMoved()) {
            if (p.getPosX() - targetx != -1) {
                enemyMoveDown(gameGrid, p);
            } else if (p.getPosY() - targety != 0) {
                int dir = -(p.getPosY() - targety);
                enemyMoveRightLeft(gameGrid, p, dir);
            }
        }
    }

    // MODIFIES: gameGrid, p
    // EFFECTS: moves enemy soldier unit down
    public void enemyMoveDown(Pieces[][] gameGrid, Pieces p) {
        if (gameGrid[p.getPosX() + 1][p.getPosY()].getLabel().equals("Space")) {
            gameGrid[p.getPosX() + 1][p.getPosY()] = p;
            Space fill = new Space();

            gameGrid[p.getPosX()][p.getPosY()] = fill;

            p.setPosX(p.getPosX() + 1);

            p.setMoved(true);
            drawGrid(gameGrid);
        }
    }

    // MODIFIES: gameGrid, p
    // EFFECTS: moves enemy soldier unit right or left
    public void enemyMoveRightLeft(Pieces[][] gameGrid, Pieces p, int dir) {
        if (gameGrid[p.getPosX()][p.getPosY() + dir].getLabel().equals("Space")) {
            gameGrid[p.getPosX()][p.getPosY() + dir] = p;
            Space fill = new Space();

            gameGrid[p.getPosX()][p.getPosY()] = fill;

            p.setPosY(p.getPosY() + dir);

            p.setMoved(true);
            drawGrid(gameGrid);
        }
    }



    //EFFECTS: Draws out the pre map in a nice format
    public void drawGrid(Pieces[][] gameGrid) {
        int rows = 0;
        for (Pieces[] row : gameGrid) {
            System.out.print(rows + "  ");
            rows++;
            for (Pieces p : row) {
                System.out.print(p.getDisplayName() + "   ");
            }
            System.out.println();
        }
    }

     */

    //EFFECTS: Read user input
    private String getUserInputString() {
        String str = "";
        if (input.hasNext()) {
            str = input.nextLine();
        }
        return str;
    }



    /*
    //REQUIRES: User input is an integer
    //EFFECTS: Converts user input string into int
    private int getUserUnitCoord() {
        String coord = "";
        if (input.hasNext()) {
            coord = input.nextLine();
        }
        return Integer.parseInt(coord);
    }

    //EFFECTS: Shows the units that the player has remaining
    public void displayUnitsRemaining(Player player) {
        System.out.println("Military Units\n");
        System.out.print("Soldiers: " + player.getSoldiers().size() + "\n");
        System.out.println("Archers: " + player.getArchers().size() + "\n");
    }

    // EFFECTS: saves the player information to a file
    private void savePlayer() {
        try {
            jsonWriter.open();
            jsonWriter.write(player);
            jsonWriter.close();
            System.out.println("Saved Game!");
        } catch (FileNotFoundException e) {
            System.out.println("Unable to write to file");
        }
    }

    // MODIFIES: player
    // EFFECTS: loads in saved player file
    private void loadPlayer() {
        try {
            player = jsonReader.read();
            System.out.println("Loaded saved player");
        } catch (IOException e) {
            System.out.println("Unable to read from file");
        }
    }

     */





}

