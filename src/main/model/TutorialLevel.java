package model;

//Creates a tutorial level with enemy units and a grid
public class TutorialLevel {
    private static Soldier enemySoldier1 = new Soldier(true, 1,1);
    private static Soldier enemySoldier2 = new Soldier(true,1,2);

    private static Soldier testSoldier = new Soldier(true, 3,1);

    private static Castle enemyCastle = new Castle(true, 0, 2);
    private static Castle myCastle = new Castle(false, 7, 1);
    private static Space s = new Space();

    Pieces[][] gameGrid = {
            {s,s,enemyCastle,s},
            {s,enemySoldier1, enemySoldier2,s},
            {s,s,s,s},
            {s,testSoldier,s,s},
            {s,s,s,s},
            {s,s,s,s},
            {s,s,s,s},
            {s,myCastle,s,s}
    };

    String[][] mapView = {
            {"    ","      0       ","      1      ","        2       ","       3          "},
            {"  0 ","     Space    ","     Space   ","  EnemyCastle   ","     Space        "},
            {"  1 ","     Space    "," EnemySoldier","  EnemySoldier  ","     Space        "},
            {"  2 ","     Space    ","     Space   ","    Space       ","     Space        "},
            {"  3 ","     Space    "," testSoldier ","    Space       ","     Space        "},
            {"----","--------------","-------------","----------------","------------------"},
            {"  4 ","     Space    ","     Space   ","    Space       ","     Space        "},
            {"  5 ","     Space    ","     Space   ","    Space       ","     Space        "},
            {"  6 ","     Space    ","     Space   ","    Space       ","     Space        "},
            {"  7 ","     Space    ","    Castle   ","    Space       ","     Space        "}
    };

    // EFFECTS: creates a new tutoriallevel ovject
    public TutorialLevel() {
        /*
        System.out.println("Tutorial!");

        System.out.println("Here's the map of the battle field");

        for (String[] row : mapView) {
            for (String s : row) {
                System.out.print(s);
            }
            System.out.println();
        }

        System.out.println("To start off the game, we have given you 25 gold");
        System.out.println("Go buy some units at the store before the battle begins!");

         */
    }

    // EFFECTS: returns gameGrid
    public Pieces[][] getGrid() {
        return this.gameGrid;
    }

    // EFFECTS: returns mapView
    public String[][] getMap() {
        return this.mapView;
    }

    public int getEnemyCastleX() {
        return 0;
    }

    public int getEnemyCastleY() {
        return 2;
    }

    public int getAllyCastleX() {
        return 7;
    }

    public int getAllyCastleY() {
        return 1;
    }


}
