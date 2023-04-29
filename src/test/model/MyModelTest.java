
package model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ui.GameManager;

import static org.junit.jupiter.api.Assertions.*;

class TutorialLevelTest {
    GameManager game;
    Player player;
    TutorialLevel test;

    @BeforeEach
    public void runBefore() {
        game = new GameManager();
        player = new Player();
        test = new TutorialLevel();
    }

    //GameManager Class Tests

    //Buy unit and add into player inventory
    @Test
    public void buyUnit() {
        player.buyUnits("Soldier");
        assertEquals(20, player.getMoney());
        assertEquals(1, player.getSoldiers().size());

        player.buyUnits("Archer");
        assertEquals(15, player.getMoney());
        assertEquals(1, player.getArchers().size());
    }

    @Test
    public void buyUnitButMax() {
        player.setMoney(1000);
        for(int i = 0;i<10;i++) {
            player.buyUnits("Soldier");
        }

        player.buyUnits("Soldier");

        //Player should still only have 10 soldiers
        assertEquals(10,player.getSoldiers().size());
        assertEquals(950,player.getMoney());

        player.setMoney(1000);

        for(int i = 0;i<5;i++) {
            player.buyUnits("Archer");
        }

        player.buyUnits("Archer");

        //Player should still only have 5 archers
        assertEquals(5,player.getArchers().size());
        assertEquals(975,player.getMoney());
    }

    @Test
    public void buyUnitButNoMoney() {
        player.setMoney(10);

        player.buyUnits("Soldier");
        player.buyUnits("Soldier");
        player.buyUnits("Soldier");

        assertEquals(2,player.getSoldiers().size());
        assertEquals(0,player.getMoney());

        player.setMoney(10);

        player.buyUnits("Archer");
        player.buyUnits("Archer");
        player.buyUnits("Archer");

        assertEquals(2,player.getArchers().size());
        assertEquals(0,player.getMoney());
    }

    @Test
    public void placeUnits() {
        game.startGame(player);
        game.getPlayer().buyUnits("Archer");
        game.getPlayer().buyUnits("Soldier");

        assertTrue(game.canPlaceUnit(6,1));
        game.placeUnit(6,1,"Soldier");
        assertEquals("AllySoldier", game.getGameGrid()[6][1].getLabel());
        assertEquals(0, game.getPlayer().getSoldiers().size());

        assertTrue(game.isAllyUnit(6,1));

        assertFalse(game.canPlaceUnit(6,1));
        game.placeUnit(6,2,"Archer");
        assertEquals("AllyArcher", game.getGameGrid()[6][2].getLabel());
        assertEquals(0, game.getPlayer().getArchers().size());

        assertFalse(game.isAllyUnit(1,1));
    }

    @Test
    public void takeUnit() {
        game.startGame(player);
        game.getPlayer().buyUnits("Soldier");
        game.placeUnit(6,1,"Soldier");
        game.takeUnit(6,1);
        assertEquals("Space", game.getGameGrid()[6][1].getLabel());
        assertEquals(1, game.getPlayer().getSoldiers().size());
    }
/*
    //Place selected unit into grid
    @Test
    public void putUnitIntoGrid() {
        Soldier placedSoldier = new Soldier(false,6,2);

        player.getSoldiers().add(placedSoldier);

        game.placeUnit(player, test.gameGrid,"Soldier",6,2);

        assertEquals(placedSoldier, test.gameGrid[6][2]);
    }

    //Take selected unit out of grid
    @Test
    public void takeUnitFromGrid() {
        Soldier placedSoldier = new Soldier(false,6,2);
        player.getSoldiers().add(placedSoldier);

        game.placeUnit(player, test.gameGrid,"Soldier",6,2);
        game.takeUnit(player,6,2, test.gameGrid);

        assertEquals("Space", test.gameGrid[6][2].getLabel());
    }

    //Move friendly unit
    @Test
    public void moveUnits() {
        Soldier placedSoldier = new Soldier(false,6,2);
        player.getSoldiers().add(placedSoldier);

        game.placeUnit(player, test.gameGrid,"Soldier",6,2);

        game.moveUnit("x",-1, test.gameGrid, test.gameGrid[6][2]);

        assertEquals(placedSoldier, test.gameGrid[5][2]);
        assertFalse(test.gameGrid[5][2].seeIfMoved());

        Archer placedarcher = new Archer(false,6,0);
        player.getArchers().add(placedarcher);

        game.placeUnit(player, test.gameGrid,"Archer",6,0);

        game.moveUnit("x",-1, test.gameGrid, test.gameGrid[6][0]);

        assertEquals(placedarcher, test.gameGrid[5][0]);
        assertFalse(test.gameGrid[5][0].seeIfMoved());
    }

    //Attack enemy unit but don't kill
    @Test
    public void attackUnits() {
        Soldier enemySoldier1 = new Soldier(true, 1,1);
        Soldier enemySoldier2 = new Soldier(true,1,2);
        Castle enemyCastle = new Castle(true, 0, 2);
        Castle myCastle = new Castle(false, 7, 1);
        Space s = new Space();

        Soldier testSoldier = new Soldier(false,2,1);

        Pieces[][] testGrid = {
                {s,s,enemyCastle,s},
                {s,enemySoldier1, enemySoldier2,s},
                {s,testSoldier,s,s},
                {s,s,s,s},
                {s,s,s,s},
                {s,s,s,s},
                {s,s,s,s},
                {s,myCastle,s,s}
        };

        game.attackUnitSoldier(testGrid, testGrid[2][1],"s");

        assertEquals(1,testGrid[1][1].getHealth());
    }

    //Attack friendly unit and kill
    @Test
    public void attackUnitAndKill() {
        Soldier enemySoldier1 = new Soldier(true, 1,1);
        Soldier enemySoldier2 = new Soldier(true,1,2);
        Castle enemyCastle = new Castle(true, 0, 2);
        Castle myCastle = new Castle(false, 7, 1);
        Space s = new Space();

        Soldier testSoldier = new Soldier(false,2,1);

        Pieces[][] testGrid = {
                {s,s,enemyCastle,s},
                {s,enemySoldier1, enemySoldier2,s},
                {s,testSoldier,s,s},
                {s,s,s,s},
                {s,s,s,s},
                {s,s,s,s},
                {s,s,s,s},
                {s,myCastle,s,s}
        };

        game.attackUnitSoldier(testGrid, testGrid[2][1],"s");
        game.attackUnitSoldier(testGrid, testGrid[2][1],"s");

        assertEquals("AllySoldier",testGrid[1][1].getLabel());

    }

    //Check that enemy has moved down the grid
    @Test
    public void enemyPath() {
        game.enemyTurn(test.gameGrid);

        assertEquals("EnemySoldier",test.gameGrid[2][1].getLabel());
        assertEquals("EnemySoldier",test.gameGrid[2][2].getLabel());
    }

    //Return false if player has no units
    @Test
    public void hasNoUnits() {
        assertFalse(game.hasUnits("Soldier",player));
        assertFalse(game.hasUnits("Archer",player));
    }

    @Test
    public void hasUnitsLeft() {
        player.buyUnits("Soldier");
        player.buyUnits("Archer");

        assertTrue(game.hasUnits("Soldier",player));
        assertTrue(game.hasUnits("Archer",player));
    }

    @Test
    public void getGameGrid() {
        Soldier enemySoldier1 = new Soldier(true, 1,1);
        Soldier enemySoldier2 = new Soldier(true,1,2);
        Castle enemyCastle = new Castle(true, 0, 2);
        Castle myCastle = new Castle(false, 7, 1);
        Space s = new Space();

        Pieces[][] testGrid = {
                {s,s,enemyCastle,s},
                {s,enemySoldier1, enemySoldier2,s},
                {s,s,s,s},
                {s,s,s,s},
                {s,s,s,s},
                {s,s,s,s},
                {s,s,s,s},
                {s,myCastle,s,s}
        };

        for(int i=0;i<testGrid.length;i++) {
            for(int j=0;j<testGrid[0].length;j++) {
                assertEquals(testGrid[i][j].getLabel(),test.getGrid()[i][j].getLabel());
            }
        }
    }

    @Test
    public void getMapView() {
        String[][] testView = {
                {"    ","      0       ","      1      ","        2       ","       3          "},
                {"  0 ","     Space    ","     Space   ","  EnemyCastle   ","     Space        "},
                {"  1 ","     Space    "," EnemySoldier","  EnemySoldier  ","     Space        "},
                {"  2 ","     Space    ","     Space   ","    Space       ","     Space        "},
                {"  3 ","     Space    ","     Space   ","    Space       ","     Space        "},
                {"----","--------------","-------------","----------------","------------------"},
                {"  4 ","     Space    ","     Space   ","    Space       ","     Space        "},
                {"  5 ","     Space    ","     Space   ","    Space       ","     Space        "},
                {"  6 ","     Space    ","     Space   ","    Space       ","     Space        "},
                {"  7 ","     Space    ","    Castle   ","    Space       ","     Space        "}
        };

        for(int i=0;i<testView.length;i++) {
            for(int j = 0;j<testView[0].length;j++) {
                assertEquals(testView[i][j],test.getMap()[i][j]);
            }
        }
    }
    */



    //Pieces Class Tests

    //Check unit values are coming through correctly
    @Test
    public void seeUnit() {
        Soldier newsoldier = new Soldier(false,5,2);

        assertFalse(newsoldier.seeIfEnemy());
        assertEquals(5,newsoldier.getPosX());
        assertEquals(2,newsoldier.getPosY());
        assertEquals(2,newsoldier.getDamage());
        assertEquals("AllySoldier", newsoldier.getLabel());
        assertEquals(3, newsoldier.getHealth());
        assertEquals(5,newsoldier.getCost());
    }

    @Test
    public void setSoldierHealth() {
        Soldier test = new Soldier(true, 0, 0);
        test.setHealth(5);

        assertEquals(5, test.getHealth());
    }

    @Test
    public void setSoldierEnemyBoolean() {
        Soldier test = new Soldier(true, 0, 0);
        test.setEnemy(false);
        assertFalse(test.seeIfEnemy());

        test.setEnemy(true);
        assertTrue(test.seeIfEnemy());
    }

    @Test
    public void setArcherEnemyBoolean() {
        Archer test = new Archer(true, 0, 0);
        test.setEnemy(false);

        assertFalse(test.seeIfEnemy());

        test.setEnemy(true);
        assertTrue(test.seeIfEnemy());
    }

    @Test
    public void enemyArcher() {
        Archer enemyarcher = new Archer(true,3,3);

        assertTrue(enemyarcher.seeIfEnemy());
        assertEquals(3,enemyarcher.getPosX());
        assertEquals(3,enemyarcher.getPosY());
        assertEquals(1,enemyarcher.getDamage());
        assertEquals("EnemyArcher", enemyarcher.getLabel());
        assertEquals(1, enemyarcher.getHealth());
        assertEquals(5,enemyarcher.getCost());
        assertTrue(enemyarcher.seeIfMoved());
        enemyarcher.setHealth(5);
        assertEquals(5,enemyarcher.getHealth());
    }


    @Test
    public void castleMethods() {
        Castle testcastle = new Castle(false,5,1);

        assertFalse(testcastle.seeIfEnemy());

        testcastle.setEnemy(true);
        assertTrue(testcastle.seeIfEnemy());

        testcastle.setEnemy(false);

        testcastle.setPosX(1);
        testcastle.setPosY(1);
        assertEquals(1,testcastle.getPosX());
        assertEquals(1,testcastle.getPosY());

        assertEquals(0,testcastle.getDamage());

        assertEquals("Castle", testcastle.getLabel());
        testcastle.setMoved(true);
        assertTrue(testcastle.seeIfMoved());

        testcastle.setHealth(15);
        assertEquals(15,testcastle.getHealth());

        assertEquals("Castle ",testcastle.toString());
    }

    @Test
    public void spaceMethods() {
        Space testspace = new Space();

        assertEquals("Space",testspace.getLabel());

        assertEquals(0,testspace.getHealth());
        assertEquals(0,testspace.getDamage());

        testspace.setPosX(0);
        testspace.setPosY(0);
        assertEquals(0,testspace.getPosX());
        assertEquals(0,testspace.getPosY());
        assertTrue(testspace.seeIfEnemy());

        testspace.setHealth(0);
        testspace.setMoved(false);

        testspace.setEnemy(false);
        assertFalse(testspace.seeIfEnemy());

        assertEquals("Space       ", testspace.toString());
    }

    // Player Methods

    @Test
    public void setTutorialBoolean() {
        player.setTutorial(true);
        assertTrue(player.getTutorial());

        player.setTutorial(false);
        assertFalse(player.getTutorial());
    }

    @Test
    public void setLevelOneBoolean() {
        player.setLevelOne(true);
        assertTrue(player.getLevelOne());

        player.setLevelOne(false);
        assertFalse(player.getLevelOne());
    }
}
/*
class LevelOneTest {
    GameManager game;
    Player player;
    LevelOne test;

    @BeforeEach
    public void runBefore() {
        game = new GameManager();
        player = new Player();
        test = new LevelOne();
    }





    @Test
    public void constructorTest() {
        Soldier enemySoldier1 = new Soldier(true, 2,2);
        Soldier enemySoldier2 = new Soldier(true,2,3);
        Soldier enemySoldier3 = new Soldier(true, 2, 1);
        Soldier enemySoldier4 = new Soldier(true, 2, 4);
        Soldier enemySoldier5 = new Soldier(true, 1, 2);
        Soldier enemySoldier6 = new Soldier(true, 1, 3);
        Castle enemyCastle = new Castle(true, 0, 2);
        Castle myCastle = new Castle(false, 7, 1);
        Space s = new Space();

        Pieces[][] testgrid = {
                {s, s, s, enemyCastle, s, s},
                {s, s, enemySoldier5, enemySoldier6, s, s},
                {s, enemySoldier3, enemySoldier1, enemySoldier2, enemySoldier4, s},
                {s, s, s, s, s, s},
                {s, s, s, s, s, s},
                {s, s, s, s, s, s},
                {s, s, s, s, s, s},
                {s, s, s, s, s, s},
                {s, s, s, s, s, s},
                {s, s, s, myCastle, s, s}
        };

        String[][] testmap = {
                {"    ","      0       ","      1      ","        2       ","       3          ","       4          ","       5          "},
                {"  0 ","     Space    ","     Space   ","     Space      ","   EnemyCastle    ","     Space        ","     Space        "},
                {"  1 ","     Space    ","     Space   ","  EnemySoldier  ","  EnemySoldier    ","     Space        ","     Space        "},
                {"  2 ","     Space    "," EnemySoldier","  EnemySoldier  ","  EnemySoldier    ","   EnemySoldier   ","     Space        "},
                {"  3 ","     Space    ","     Space   ","    Space       ","     Space        ","     Space        ","     Space        "},
                {"  4 ","     Space    ","     Space   ","    Space       ","     Space        ","     Space        ","     Space        "},
                {"----","--------------","-------------","----------------","------------------","------------------","------------------"},
                {"  5 ","     Space    ","     Space   ","    Space       ","     Space        ","     Space        ","     Space        "},
                {"  6 ","     Space    ","     Space   ","    Space       ","     Space        ","     Space        ","     Space        "},
                {"  7 ","     Space    ","     Space   ","    Space       ","     Space        ","     Space        ","     Space        "},
                {"  8 ","     Space    ","     Space   ","    Space       ","     Space        ","     Space        ","     Space        "},
                {"  9 ","     Space    ","     Space   ","    Space       ","     Castle       ","     Space        ","     Space        "}
        };

        for(int i =0; i< testgrid.length; i++) {
            for( int j = 0; j < testgrid[0].length; j++) {
                assertEquals(testgrid[i][j].getLabel(), test.getGrid()[i][j].getLabel());
            }
        }

        for(int a = 0; a < testmap.length; a++) {
            for(int b = 0; b < testmap[0].length; b++) {
                assertEquals(testmap[a][b], test.getMap()[a][b]);
            }
        }

    }


}

 */


