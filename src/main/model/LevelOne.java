package model;

// Level One
public class LevelOne {
    private static Soldier enemySoldier1 = new Soldier(true, 2,2);
    private static Soldier enemySoldier2 = new Soldier(true,2,3);
    private static Soldier enemySoldier3 = new Soldier(true, 2, 1);
    private static Soldier enemySoldier4 = new Soldier(true, 2, 4);
    private static Soldier enemySoldier5 = new Soldier(true, 1, 2);
    private static Soldier enemySoldier6 = new Soldier(true, 1, 3);
    private static Castle enemyCastle = new Castle(true, 0, 2);
    private static Castle myCastle = new Castle(false, 7, 1);
    private static Space s = new Space();

    Pieces[][] gameGrid = {
            /*0*/ {s,s,s,enemyCastle,s,s},
            /*1*/ {s,s,enemySoldier5,enemySoldier6,s,s},
            /*2*/ {s,enemySoldier3,enemySoldier1,enemySoldier2,enemySoldier4,s},
            /*3*/ {s,s,s,s,s,s},
            /*4*/ {s,s,s,s,s,s},
            /*5*/ {s,s,s,s,s,s},
            /*6*/ {s,s,s,s,s,s},
            /*7*/ {s,s,s,s,s,s},
            /*8*/ {s,s,s,s,s,s},
            /*9*/ {s,s,s,myCastle,s,s}
    };

    @SuppressWarnings({"checkstyle:LineLength", "checkstyle:SuppressWarnings"})
    String[][] mapView = {
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

    // EFFECTS: returns gameGrid
    public Pieces[][] getGrid() {
        return this.gameGrid;
    }

    // EFFECTS: returns mapView
    public String[][] getMap() {
        return this.mapView;
    }
}
