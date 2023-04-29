package ui;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;


//Class to draw and display game
public class GUI extends JFrame {

    public static final int WIDTH = 1200;
    public static final int HEIGHT = 1200;

    private JPanel startPanel = new JPanel();
    private JPanel titlePanel = new JPanel();

    //Game window container
    private JPanel gamePanel = new JPanel();

    private JLabel title = new JLabel();

    private JButton newGame;
    private JButton loadGame;
    private JButton startLevel;
    private JButton levelTwo;
    private JButton buyMenu;

    private JButton[][] tutorial;
    private JButton[][] curr;

    private JPanel gameBoard;

    private JPanel buyPanel = new JPanel(new GridLayout(3,1,0,-500));
    private JPanel buyPanelRight = new JPanel(new GridLayout(4,1));

    private JPanel battleButton = new JPanel();

    private JPopupMenu solinfo = new JPopupMenu();
    private JPopupMenu archinfo = new JPopupMenu();

    JToolBar units;

    private boolean holdingSoldier = false;
    private boolean holdingArcher = false;
    private boolean holdingUnitCommand = false;

    private int currButtonX;
    private int currButtonY;

    String gameText = "To place a unit, select the unit button and the board location";

    Font buttonFont = new Font("Sans Serif", Font.BOLD, 30);

    GameManager game = new GameManager();

    // EFFECTS: Constructs new GUI object
    public GUI() {
        super("WarFare");
        makeStartButtons();
        drawStartScreen();
        initializeGraphics();
    }

    // EFFECTS: Creates window for application
    private void initializeGraphics() {
        setLayout(new BorderLayout());
        setMinimumSize(new Dimension(WIDTH,HEIGHT));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        gamePanel.setBackground(Color.darkGray);
        getContentPane().add(gamePanel);
        pack();
        setVisible(true);
    }

    // EFFECTS: Creates buttons for creating a new game or loading save
    private void makeStartButtons() {
        newGame = new JButton("New Game");
        loadGame = new JButton("Load Game");

        newGame.addActionListener(e -> {
            gamePanel.setVisible(false);
            gamePanel.remove(titlePanel);
            gamePanel.remove(startPanel);
            game.startGame(game.player);
            drawMenuScreen();
        });

        loadGame.addActionListener(e -> {
            gamePanel.setVisible(false);
            gamePanel.remove(titlePanel);
            gamePanel.remove(startPanel);
            game.loadPlayer();
            game.startGame(game.player);
            drawMenuScreen();
        });

    }

    // EFFECTS: Displays starting screen
    private void drawStartScreen() {
        titlePanel = new JPanel();
        titlePanel.setBackground(Color.darkGray);

        title.setText("Warfare");
        title.setFont(new Font("Sans Serif", Font.BOLD, 60));
        title.setForeground(Color.WHITE);
        titlePanel.setLayout(new GridBagLayout());
        titlePanel.setPreferredSize(new Dimension(1500,500));
        titlePanel.add(title);
        gamePanel.add(titlePanel);

        GridLayout layout = new GridLayout(1,2);
        layout.setHgap(50);
        layout.setVgap(250);
        startPanel.setLayout(layout);

        setButtonStyle(newGame);
        setButtonStyle(loadGame);

        startPanel.add(newGame, BorderLayout.LINE_START);
        startPanel.add(loadGame, BorderLayout.LINE_END);
        startPanel.setPreferredSize(new Dimension(800,100));
        gamePanel.add(startPanel);
    }

    // EFFECTS: Displays menu screen
    private void drawMenuScreen() {
        gamePanel.setLayout(new BorderLayout());
        addLevelButton();
        addBuyMenuButton();
        gamePanel.setVisible(true);
    }

    // EFFECTS: Draws preparation phase of tutorial level
    private void drawGameLevel() {
        if (!game.passtut) {
            tutorial = new JButton[game.tutorial.getGrid().length][game.tutorial.getGrid()[0].length];
            gamePanel.removeAll();
            gamePanel.setLayout(new BorderLayout(100,100));
            drawTitle("Preparation Phase");
            drawGameBoard("tutorial", 1);
            drawUnitsToolBar();
            drawScrollPane();
            gamePanel.setVisible(true);
        }
    }

    // EFFECTS: Displays title of phase
    private void drawTitle(String title) {
        JPanel phasePanel = new JPanel();

        JLabel phaseTitle = new JLabel(title);
        phaseTitle.setFont(new Font("Sans Serif", Font.BOLD, 60));

        phasePanel.add(phaseTitle);
        gamePanel.add(phasePanel,BorderLayout.PAGE_START);
    }

    // EFFECTS: Draw actual game board for prep phase
    @SuppressWarnings({"checkstyle:MethodLength", "checkstyle:SuppressWarnings"})
    private void drawGameBoard(String level, int phase) {
        if (level.equals("tutorial")) {
            tutorial = new JButton[game.tutorial.getGrid().length][game.tutorial.getGrid()[0].length];

            //Prep phase
            if (phase == 1) {
                drawTutBoard(tutorial);
                curr = tutorial;
            } else {
                drawTutBattleBoardTitle();
                drawTutBattleBoard();
                drawTutBattleBoardRight();
            }
        }
    }

    // EFFECTS: Helper function to draw tutorial game board
    private void drawTutBoard(JButton[][] tutorial) {
        int gridx = game.tutorial.getGrid().length;
        int gridy = game.tutorial.getGrid()[0].length;
        String name;

        gameBoard = new JPanel(new GridLayout(gridx, gridy,0,0));
        gameBoard.setBackground(Color.darkGray);
        gameBoard.setBorder(new EmptyBorder(0,250,0,50));

        for (int i = 0; i < game.tutorial.getGrid().length; i++) {
            for (int j = 0; j < game.tutorial.getGrid()[0].length; j++) {
                name = game.getUnitName(i, j);
                JButton boardButton = new JButton(name);
                setBoardButtonStyle(boardButton);
                setBoardButtonFunc(boardButton);

                tutorial[i][j] = boardButton;

            }
        }
        addGameBoardButton(tutorial);
        gamePanel.add(gameBoard, BorderLayout.CENTER);
    }

    // EFFECTS: draw tutorial game board for battle phase
    private void drawTutBattleBoard() {
        gamePanel.setBorder(new EmptyBorder(50,0,0,270));
        int gridx = curr.length;
        int gridy = curr[0].length;

        gameBoard = new JPanel(new GridLayout(gridx, gridy,0,0));
        gameBoard.setBorder(new EmptyBorder(0,250,0,50));
        gameBoard.setBackground(Color.darkGray);

        //Remove all action listeners on button
        for (JButton[] jbuttons : curr) {
            for (int j = 0; j < curr[0].length; j++) {
                ActionListener al = jbuttons[j].getActionListeners()[0];
                jbuttons[j].removeActionListener(al);

                if (jbuttons[j].getText().equals("AllySoldier") || jbuttons[j].getText().equals("AllyArcher")) {
                    addBattleButtonAllyFunc(jbuttons[j]);
                }
            }
        }

        addGameBoardButton(curr);
        gamePanel.add(gameBoard,BorderLayout.CENTER);

    }

    // EFFECTS: Draw right side of battle phase window
    private void drawTutBattleBoardRight() {
        battleButton.setBackground(Color.darkGray);
        battleButton.setPreferredSize(new Dimension(500,100));
        battleButton.setBorder(new EmptyBorder(0,100,0,100));

        JLabel money = new JLabel();
        int currmoney = game.player.getMoney();
        money.setBorder(new EmptyBorder(50,0,100,0));

        money.setText("Current Money: " + currmoney);
        money.setFont(new Font("Sans serif", Font.BOLD, 24));
        money.setForeground(Color.WHITE);

        JButton endTurn = new JButton("End Turn");
        endTurn.setBackground(new Color(250,19,2));
        endTurn.setForeground(Color.WHITE);
        endTurn.setPreferredSize(new Dimension(500,100));

        addEndTurnFunc(endTurn);

        battleButton.add(money);
        battleButton.add(endTurn);

        gamePanel.add(battleButton, BorderLayout.LINE_END);
        gamePanel.setVisible(true);
    }

    //EFFECTS: Adds functionality to end turn button
    private void addEndTurnFunc(JButton endTurn) {
        endTurn.addActionListener(e -> {
            redrawBattleBoard();
            for (int i = curr.length - 1; i >= 0; i--) {
                for (int j = curr[0].length - 1; j >= 0; j--) {
                    String name = game.gameGrid[i][j].getLabel();
                    if (name.equals("EnemySoldier") || name.equals("EnemyArcher")) {
                        int decision = game.chooseEnemyMove(i,j);
                        decisionCases(decision, i, j);
                    }

                    redrawBattleBoard();
                }
            }
            try {
                checkEnemyCastleDefeated();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
            checkAllyCastleDefeated();
            setAllyMovedFalse();
        });
    }

    // EFFECTS: Runs enemy actions
    private void decisionCases(int decision, int i, int j) {
        if (decision == 1) {
            drawStraightAttack(i, j);
        } else if (decision == 2) {
            drawLeftAttack(i, j);
        } else if (decision == 3) {
            drawRightAttack(i, j);
        } else if (decision == 4) {
            drawMoveStraight(i, j);
        } else if (decision == 5) {
            drawMoveLeft(i, j);
        } else if (decision == 6) {
            drawMoveRight(i, j);
        }
    }

    // EFFECTS: Helper function to redraw battle game board
    private void redrawBattleBoard() {
        holdingUnitCommand = false;
        gameBoard.removeAll();
        battleButton.removeAll();
        gamePanel.setVisible(false);
        gamePanel.remove(gameBoard);
        gamePanel.remove(battleButton);

        resetGameBoard();
        drawTutBattleBoardRight();
    }

    // EFFECTS: Helper function for enemy action
    private void drawStraightAttack(int i, int j) {
        int[] newLocation = game.attackStraightSoldier(game.gameGrid[i][j]);
        drawEnemyUpdate(i, j, newLocation);
    }

    // EFFECTS: Helper function for enemy action
    private void drawLeftAttack(int i, int j) {
        int[] newLocation = game.attackLeftSoldier(game.gameGrid[i][j]);
        drawEnemyUpdate(i, j, newLocation);
    }

    // EFFECTS: Helper function for enemy action
    private void drawRightAttack(int i, int j) {
        int[] newLocation = game.attackRightSoldier(game.gameGrid[i][j]);
        drawEnemyUpdate(i, j, newLocation);
    }

    // EFFECTS: Helper function for enemy action
    private void drawMoveStraight(int i, int j) {
        game.moveStraight(game.gameGrid[i][j]);
        JButton fill = new JButton();
        curr[i + 1][j] = curr[i][j];
        curr[i][j] = fill;
    }

    // EFFECTS: Helper function for enemy action
    private void drawMoveLeft(int i, int j) {
        game.moveLeft(game.gameGrid[i][j]);
        JButton fill = new JButton();
        curr[i][j - 1] = curr[i][j];
        curr[i][j] = fill;
    }

    // EFFECTS: Helper function for enemy action
    private void drawMoveRight(int i, int j) {
        game.moveRight(game.gameGrid[i][j]);
        JButton fill = new JButton();
        curr[i][j + 1] = curr[i][j];
        curr[i][j] = fill;
    }

    // EFFECTS: Helper function to redraw board after enemy move
    private void drawEnemyUpdate(int i, int j, int[] newLocation) {
        if (newLocation[0] == -1) {
            JButton fill = new JButton();
            curr[i][j] = fill;
        } else if (newLocation[0] != i) {
            JButton fill = new JButton();
            curr[newLocation[0]][newLocation[1]] = curr[i][j];

            curr[i][j] = fill;
        }
    }

    //EFFECTS: Set all ally pieces to be able to move
    private void setAllyMovedFalse() {
        for (int i = 0; i < curr.length; i++) {
            for (int j = 0; j < curr[0].length; j++) {
                if (!game.gameGrid[i][j].seeIfEnemy() && !game.gameGrid[i][j].getLabel().equals("Castle")) {
                    game.gameGrid[i][j].setMoved(false);
                }
            }
        }
    }

    // EFFECTS: Draw title for battle phase
    private void drawTutBattleBoardTitle() {
        JPanel title = new JPanel();
        title.setBackground(Color.DARK_GRAY);
        title.setPreferredSize(new Dimension(1000,100));
        title.setBorder(new EmptyBorder(0,150,0,0));

        JLabel titleText = new JLabel();
        titleText.setText("Battle Phase");
        titleText.setFont(new Font("Sans serif", Font.BOLD, 60));
        titleText.setForeground(Color.WHITE);
        title.add(titleText);
        gamePanel.add(title, BorderLayout.PAGE_START);
    }

    // EFFECTS: Visually reset the game board to default
    private void resetGameBoard() {
        gameBoard.removeAll();
        for (JButton[] jbuttons : curr) {
            for (int j = 0; j < curr[0].length; j++) {
                jbuttons[j].setBackground(Color.WHITE);
                jbuttons[j].setForeground(Color.BLACK);

                if (jbuttons[j].getActionListeners().length != 0) {
                    ActionListener al = jbuttons[j].getActionListeners()[0];
                    jbuttons[j].removeActionListener(al);
                }

                if (jbuttons[j].getText().equals("AllySoldier") || jbuttons[j].getText().equals("AllyArcher")) {
                    addBattleButtonAllyFunc(jbuttons[j]);
                }
            }
        }

        addGameBoardButton(curr);
        gamePanel.add(gameBoard, BorderLayout.CENTER);
        gamePanel.setVisible(true);
    }

    // EFFECTS: Add button functionality to ally buttons
    @SuppressWarnings({"checkstyle:MethodLength", "checkstyle:SuppressWarnings"})
    private void addBattleButtonAllyFunc(JButton button) {
        button.addActionListener(e -> {
            if (!holdingUnitCommand) {
                button.setBackground(new Color(46, 126, 255));
                button.setForeground(Color.WHITE);

                holdingUnitCommand = true;

                int x = getButtonX(e, curr);
                int y = getButtonY(e, curr);

                currButtonX = x;
                currButtonY = y;

                if (game.gameGrid[currButtonX][currButtonY].seeIfMoved()) {
                    if (button.getText().equals("AllySoldier")) {
                        //Check for enemy and movement options
                        if (x - 1 >= 0) {
                            if (y - 1 >= 0) {
                                if (game.isLegalSpace(x - 1, y - 1)) {
                                    curr[x - 1][y - 1].setBackground(new Color(201, 50, 50));
                                    curr[x - 1][y - 1].setForeground(Color.WHITE);

                                    addEnemyButtonFunc(curr[x - 1][y - 1]);
                                }

                                if (game.isSpace(x, y - 1)) {
                                    curr[x][y - 1].setBackground(new Color(207, 207, 207));
                                    addMoveButtonFunc(curr[x][y - 1]);
                                }

                            }

                            if (y + 1 < curr[0].length) {
                                if (game.isLegalSpace(x - 1, y + 1)) {
                                    curr[x - 1][y + 1].setBackground(new Color(201, 50, 50));
                                    curr[x - 1][y + 1].setForeground(Color.WHITE);

                                    addEnemyButtonFunc(curr[x - 1][y + 1]);
                                }

                                if (game.isSpace(x, y + 1)) {
                                    curr[x][y + 1].setBackground(new Color(207, 207, 207));
                                    addMoveButtonFunc(curr[x][y + 1]);
                                }
                            }

                            if (game.isSpace(x - 1, y)) {
                                curr[x - 1][y].setBackground(new Color(207, 207, 207));
                                addMoveButtonFunc(curr[x - 1][y]);
                            }

                            if (game.isLegalSpace(x - 1, y)) {

                                //remove actionlistener if exists
                                if (curr[x - 1][y].getActionListeners().length != 0) {
                                    ActionListener al = curr[x - 1][y].getActionListeners()[0];
                                    curr[x - 1][y].removeActionListener(al);
                                }


                                curr[x - 1][y].setBackground(new Color(201, 50, 50));
                                curr[x - 1][y].setForeground(Color.WHITE);

                                addEnemyButtonFunc(curr[x - 1][y]);
                            }

                        }

                        if (x + 1 < curr.length) {
                            if (game.isSpace(x + 1, y)) {
                                curr[x + 1][y].setBackground(new Color(207, 207, 207));
                                addMoveButtonFunc(curr[x + 1][y]);
                            }
                        }

                    } else if (button.getText().equals("AllyArcher")) {
                        getMovementOptions(x,y);
                        getAttackOptions(x,y);
                    }
                }
            } else {
                holdingUnitCommand = false;
                gameBoard.removeAll();
                gamePanel.setVisible(false);
                gamePanel.remove(gameBoard);
                resetGameBoard();
            }
        });
    }

    // EFFECTS: Get possible movement options for player
    @SuppressWarnings({"checkstyle:MethodLength", "checkstyle:SuppressWarnings"})
    private void getMovementOptions(int x, int y) {
        if (y - 1 >= 0) {
            if (game.isSpace(x, y - 1)) {
                curr[x][y - 1].setBackground(new Color(207, 207, 207));
                addMoveButtonFunc(curr[x][y - 1]);
            }
        }
        if (y + 1 < curr[0].length) {
            if (game.isSpace(x, y + 1)) {
                curr[x][y + 1].setBackground(new Color(207, 207, 207));
                addMoveButtonFunc(curr[x][y + 1]);
            }
        }
        if (x + 1 < curr.length) {
            if (game.isSpace(x + 1, y)) {
                curr[x + 1][y].setBackground(new Color(207, 207, 207));
                addMoveButtonFunc(curr[x + 1][y]);
            }
        }
        if (x - 1 >= 0) {
            if (game.isSpace(x - 1, y)) {
                curr[x - 1][y].setBackground(new Color(207, 207, 207));
                addMoveButtonFunc(curr[x - 1][y]);
            }
        }
    }

    // EFFECTS: Get possible attack options for player
    private void getAttackOptions(int x, int y) {
        //1 square ahead
        if (x - 1 >= 0) {
            showAttackSpace(x - 1,y);
        }
        //2 squares ahead
        if (x - 2 >= 0) {
            showAttackSpace(x - 2, y);
        }
        //3 squares ahead
        if (x - 3 >= 0) {
            showAttackSpace(x - 3, y);
        }
    }

    // EFFECTS: Displays which units can be attacked
    private void showAttackSpace(int x, int y) {
        if (game.isLegalSpace(x,y)) {
            //remove actionlistener if exists
            if (curr[x][y].getActionListeners().length != 0) {
                ActionListener al = curr[x][y].getActionListeners()[0];
                curr[x][y].removeActionListener(al);
            }

            curr[x][y].setBackground(new Color(201, 50, 50));
            curr[x][y].setForeground(Color.WHITE);

            addEnemyButtonFunc(curr[x][y]);
        }
    }

    // EFFECTS: Adds button functionality to enemy buttons on player turn
    @SuppressWarnings({"checkstyle:MethodLength", "checkstyle:SuppressWarnings"})
    private void addEnemyButtonFunc(JButton button) {
        button.addActionListener(e -> {
            if (holdingUnitCommand) {
                int targetX = getButtonX(e, curr);
                int targetY = getButtonY(e, curr);
                if (curr[currButtonX][currButtonY].getText().equals("AllySoldier")) {
                    int[] result = game.attackUnitSoldier(currButtonX, currButtonY, targetX, targetY);
                    if (result[0] == 0) {
                        curr[targetX][targetY] = curr[currButtonX][currButtonY];
                        JButton fill = new JButton();
                        curr[currButtonX][currButtonY] = fill;
                    } else if (result[1] == 0) {
                        curr[currButtonX][currButtonY] = curr[targetX][targetY];
                        JButton fill = new JButton();
                        curr[targetX][targetY] = fill;
                    }
                } else if (curr[currButtonX][currButtonY].getText().equals("AllyArcher")) {
                    int[] result = game.attackUnitArcher(currButtonX, currButtonY, targetX, targetY);
                    //Enemy target died
                    if (result[0] == 1) {
                        JButton fill = new JButton();
                        curr[getButtonX(e, curr)][getButtonY(e, curr)] = fill;
                    }
                    //Player archer died
                    if (result[1] == 1) {
                        JButton fill = new JButton();
                        curr[currButtonX][currButtonY] = fill;
                    }
                }
                redrawBattleBoard();
                try {
                    checkEnemyCastleDefeated();
                    checkAllyCastleDefeated();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        });
    }

    public void checkAllyCastleDefeated() {
        if (game.checkAllyCastle()) {
            System.out.println("You Lost!");

            JLabel label = new JLabel("You Lost!");
            label.setForeground(Color.WHITE);
            label.setFont(new Font("Sans serif",Font.BOLD, 64));

            JPanel loss = new JPanel();
            loss.setBackground(Color.darkGray);
            JButton home = new JButton("Go Back Home");
            setButtonStyle(home);


            loss.add(label);
            loss.add(home);

            gamePanel.setVisible(false);
            gamePanel.remove(gameBoard);
            gamePanel.add(loss, BorderLayout.CENTER);
            gamePanel.setVisible(true);

            home.addActionListener(e -> {
                gamePanel.setVisible(false);
                gamePanel.removeAll();
                drawMenuScreen();
                gamePanel.setVisible(true);
            });
        }
    }

    //EFFECTS: Check if enemy castle has been defeated
    public void checkEnemyCastleDefeated() throws IOException {
        if (game.checkEnemyCastle()) {
            JPanel victory = new JPanel();
            victory.setBackground(Color.darkGray);
            victory.setPreferredSize(new Dimension(500,500));
            BufferedImage image = ImageIO.read(new File("data/victory.jpg"));
            JLabel label = new JLabel(new ImageIcon(image));
            victory.add(label);

            JButton home = new JButton("Go Back Home");
            setButtonStyle(home);
            victory.add(home);
            gamePanel.setVisible(false);
            gamePanel.remove(gameBoard);
            gamePanel.add(victory, BorderLayout.CENTER);
            gamePanel.setVisible(true);

            home.addActionListener(e -> {
                game.player.setTutorial(true);
                gamePanel.setVisible(false);
                gamePanel.removeAll();
                drawMenuScreen();
                gamePanel.setVisible(true);
            });

        }
    }

    // EFFECTS: Add move functionality to buttons on player turn
    private void addMoveButtonFunc(JButton button) {
        button.addActionListener(e -> {
            System.out.println("New x " + getButtonX(e, curr));
            System.out.println("New y " + getButtonY(e, curr));


            game.moveUnit(currButtonX,currButtonY,getButtonX(e, curr), getButtonY(e, curr));
            curr[getButtonX(e, curr)][getButtonY(e, curr)] = curr[currButtonX][currButtonY];

            JButton fill = new JButton();
            curr[currButtonX][currButtonY] = fill;
            System.out.println("Current pressed x " + currButtonX);
            System.out.println("current pressed y " + currButtonY);

            holdingUnitCommand = false;

            gameBoard.removeAll();
            gamePanel.setVisible(false);
            gamePanel.remove(gameBoard);
            resetGameBoard();
        });
    }

    // EFFECTS: Draw tool bar for prep phase
    @SuppressWarnings({"checkstyle:MethodLength", "checkstyle:SuppressWarnings"})
    private void drawUnitsToolBar() {
        units = new JToolBar("Units", JToolBar.VERTICAL);
        units.setBackground(Color.darkGray);

        units.setFloatable(false);
        JButton soldier = new JButton("Soldiers x" + game.player.getSoldiers().size());
        JButton archer = new JButton("Archers x" + game.player.getArchers().size());

        JButton finishPlacing = new JButton("Finish Prep");

        setFinishPrepButtonFunc(finishPlacing);
        setSoldierButtonFunc(soldier);
        setArcherButtonFunc(archer);

        setButtonStyle(soldier);
        setButtonStyle(archer);

        if (holdingSoldier) {
            soldier.setBackground(new Color(73,163,184));
            soldier.setForeground(Color.WHITE);
        }

        if (holdingArcher) {
            archer.setBackground(new Color(73,163,184));
            archer.setForeground(Color.WHITE);
        }

        units.add(soldier);
        units.addSeparator();
        units.add(archer);
        units.add(finishPlacing);

        units.setBorder(new EmptyBorder(0,0,0,150));

        gamePanel.add(units, BorderLayout.LINE_END);
    }

    // EFFECTS: Draw text windo
    private void drawScrollPane() {
        JTextArea message = new JTextArea();
        message.setEditable(false);
        message.setText(gameText);
        message.setFont(new Font("Sans serif",Font.BOLD,35));
        message.setPreferredSize(new Dimension(1500,250));

        JScrollPane gameTextField = new JScrollPane(message);
        gameTextField.setPreferredSize(new Dimension(1800,250));
        gameTextField.setBorder(BorderFactory.createLineBorder(Color.BLACK));

        gamePanel.add(gameTextField, BorderLayout.PAGE_END);
    }

    // EFFECTS: Helper function to redraw buy menu
    private void drawBuyMenu() {
        gamePanel.setLayout(new BorderLayout());
        drawBuyMenuRight();
        drawBuyMenuLeft();
        gamePanel.setVisible(true);
    }

    // EFFECTS: Draw right side of buy window
    @SuppressWarnings({"checkstyle:MethodLength", "checkstyle:SuppressWarnings"})
    private void drawBuyMenuRight() {
        JLabel money = new JLabel();
        money.setText("Coins: " + game.player.getMoney());
        money.setBorder(new EmptyBorder(0,150,0,150));

        JLabel currSolUnits  = new JLabel();
        currSolUnits.setText("Soldier Owned: " + game.player.getSoldiers().size());
        currSolUnits.setBorder(new EmptyBorder(0,150,0,150));

        JLabel currArchUnits = new JLabel();
        currArchUnits.setText("Archers Owned: " + game.player.getArchers().size());
        currArchUnits.setBorder(new EmptyBorder(0,150,0,150));

        JButton back = new JButton("Back");
        setButtonStyle(back);
        back.addActionListener(e -> {
            gamePanel.setVisible(false);
            gamePanel.removeAll();
            buyPanel.removeAll();
            buyPanelRight.removeAll();
            drawMenuScreen();
            gamePanel.setVisible(true);
        });

        setTextStyle(money);
        setTextStyle(currSolUnits);
        setTextStyle(currArchUnits);

        buyPanelRight.add(money);
        buyPanelRight.add(currSolUnits);
        buyPanelRight.add(currArchUnits);
        buyPanelRight.add(back);

        buyPanel.add(buyPanelRight,BorderLayout.LINE_END);
        gamePanel.add(buyPanel,BorderLayout.LINE_END);
    }

    // EFFECTS: Draw left side of buy window
    @SuppressWarnings({"checkstyle:MethodLength", "checkstyle:SuppressWarnings"})
    private void drawBuyMenuLeft() {
        JPanel units = new JPanel();
        units.setLayout(new GridLayout(3,2));

        JButton buySoldier = new JButton("Buy Soldier: 5 Coins");
        buySoldier.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                solinfo = buildSoldierPopup();
                drawPopup(e, solinfo);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                solinfo.setVisible(false);
            }

        });

        buySoldier.addActionListener(e -> {
            gamePanel.setVisible(false);
            buyPanel.remove(buyPanelRight);
            buyPanelRight.removeAll();
            game.player.buySoldier();
            drawBuyMenuRight();
            gamePanel.setVisible(true);
            System.out.println(game.player.getSoldiers().size());
        });
        JButton buyArcher = new JButton("Buy Archer: 5 Coins");

        buyArcher.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                archinfo = buildArcherPopup();
                drawPopup(e, archinfo);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                archinfo.setVisible(false);
            }
        });

        buyArcher.addActionListener(e -> {
            gamePanel.setVisible(false);
            buyPanel.remove(buyPanelRight);
            buyPanelRight.removeAll();
            game.player.buyArcher();
            drawBuyMenuRight();
            gamePanel.setVisible(true);
            System.out.println(game.player.getArchers().size());
        });
        setButtonStyle(buySoldier);
        setButtonStyle(buyArcher);

        JButton buyScout = new JButton("Buy Scout: 7 Coins");
        JButton buySpy = new JButton("Buy Spy: 10 Coins");
        JButton buyKnight = new JButton("Buy Spy: 15 Coins");
        JButton buyJuggernaut = new JButton("Buy Juggernaut: 20 coins");
        setButtonNAStyle(buyScout);
        setButtonNAStyle(buySpy);
        setButtonNAStyle(buyKnight);
        setButtonNAStyle(buyJuggernaut);
        units.add(buySoldier);
        units.add(buyArcher);
        units.add(buyScout);
        units.add(buySpy);
        units.add(buyKnight);
        units.add(buyJuggernaut);
        units.setBorder(BorderFactory.createLineBorder(Color.BLACK));

        gamePanel.add(units, BorderLayout.CENTER);
    }

    // EFFECTS: Draw popup
    private void drawPopup(MouseEvent e, JPopupMenu popup) {
        popup.show(e.getComponent(),e.getX(),e.getY());
    }

    // EFFECTS: Create popup for soldier unit
    @SuppressWarnings({"checkstyle:MethodLength", "checkstyle:SuppressWarnings"})
    private JPopupMenu buildSoldierPopup() {
        JPopupMenu spopup = new JPopupMenu();
        JTextArea info = new JTextArea(13, 25);
        info.setText("Soldier: \n\n"
                +
                "Moves:\n"
                +
                "Can move 1 space or attack once per turn\n"
                +
                "Attack:\n"
                +
                "Can deal 2 damage to any enemy in a 1 block radius\n"
                +
                "Health:\n "
                +
                "3 hp\n"
                +
                "Value:\n"
                +
                "Gives 3 gold when enemy soldier defeated\n\n"
                +
                "MAXIMUM 10 UNITS OWNED");
        info.setFont(buttonFont);
        info.setLineWrap(true);
        info.setWrapStyleWord(true);
        spopup.add(info);
        spopup.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        return spopup;
    }

    // EFFECTS: Create popup for archer unit
    @SuppressWarnings({"checkstyle:MethodLength", "checkstyle:SuppressWarnings"})
    private JPopupMenu buildArcherPopup() {
        JPopupMenu spopup = new JPopupMenu();
        JTextArea info = new JTextArea(13, 25);
        info.setText("Archer: \n\n"
                +
                "Moves:\n"
                +
                "Can move 1 space or attack once per turn\n"
                +
                "Attack:\n"
                +
                "Can deal 1 damage to any enemy in a straight line ahead up to 2 or 3 spaces away\n"
                +
                "Health:\n "
                +
                "2 hp\n"
                +
                "Value:\n"
                +
                "Gives 2 gold when enemy soldier defeated\n\n"
                +
                "MAXIMUM 5 UNITS OWNED");
        info.setFont(buttonFont);
        info.setLineWrap(true);
        info.setWrapStyleWord(true);
        spopup.add(info);
        spopup.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        return spopup;
    }

    // EFFECTS: Add level buttons to start page
    @SuppressWarnings({"checkstyle:MethodLength", "checkstyle:SuppressWarnings"})
    private void addLevelButton() {
        JPanel levels = new JPanel();
        levels.setBackground(Color.darkGray);
        levels.setPreferredSize(new Dimension(500, 500));
        startLevel = new JButton("Play Level 1");
        setButtonStyle(startLevel);
        startLevel.setPreferredSize(new Dimension(300,75));

        startLevel.addActionListener(e -> {
            game.startGame(game.player);
            gamePanel.setVisible(false);
            drawGameLevel();
        });
        levels.add(startLevel);
        levelTwo = new JButton("Play Level 2");
        setButtonStyle(levelTwo);
        levelTwo.setPreferredSize(new Dimension(300,75));
        if (game.player.getTutorial()) {
            levelTwo.addActionListener(e -> System.out.println("Level two coming soon"));
        } else {
            levelTwo.setForeground(Color.LIGHT_GRAY);
            levelTwo.setBackground(Color.WHITE);

            levelTwo.setFont(buttonFont);
        }
        levels.add(levelTwo);
        levels.setBorder(new EmptyBorder(500,75,0,0));
        gamePanel.add(levels, BorderLayout.CENTER);
    }

    // EFFECTS: Add button to go to buy menu
    @SuppressWarnings({"checkstyle:MethodLength", "checkstyle:SuppressWarnings"})
    private void addBuyMenuButton() {
        JPanel buyButtonPanel = new JPanel();
        buyButtonPanel.setBackground(Color.darkGray);
        buyButtonPanel.setPreferredSize(new Dimension(400,500));
        buyButtonPanel.setBorder(new EmptyBorder(450,0,100,150));

        buyMenu = new JButton("Buy Units");
        buyMenu.addActionListener(e -> {
            gamePanel.setVisible(false);
            gamePanel.remove(startLevel);
            gamePanel.remove(buyMenu);
            gamePanel.removeAll();
            drawBuyMenu();
        });

        setButtonStyle(buyMenu);

        buyButtonPanel.add(buyMenu);

        JButton quit = new JButton("Quit");
        quit.addActionListener(e -> {
            game.printEventLog();
            System.exit(0);
        });

        setButtonStyle(quit);
        buyButtonPanel.add(quit);

        JButton saveAndQuit = new JButton("Save and Quit");
        saveAndQuit.addActionListener(e -> {
            game.printEventLog();
            game.save();
            System.exit(0);
        });

        setButtonStyle(saveAndQuit);
        buyButtonPanel.add(saveAndQuit);
        gamePanel.add(buyButtonPanel, BorderLayout.LINE_END);
    }

    // EFFECTS: Inputs jbuttons from gameboard into 2d array
    private void addGameBoardButton(JButton[][] map) {
        for (JButton[] jbuttons : map) {
            for (int j = 0; j < map[0].length; j++) {
                gameBoard.add(jbuttons[j]);
            }
        }
    }

    // EFFECTS: Set style for buttons on board
    private void setBoardButtonStyle(JButton button) {
        button.setForeground(Color.BLACK);
        button.setBackground(Color.WHITE);

        button.setPreferredSize(new Dimension(50,50));

    }

    // EFFECTS: Set style for generic buttons
    private void setButtonStyle(JButton button) {
        button.setForeground(Color.BLACK);
        button.setBackground(Color.WHITE);

        button.setFont(buttonFont);
    }

    // EFFECTS: Set style for not available buttons
    private void setButtonNAStyle(JButton button) {
        button.setForeground(Color.LIGHT_GRAY);
        button.setBackground(Color.WHITE);

        button.setFont(buttonFont);
    }

    // EFFECTS: Add functionality to placing soldier button
    private void setSoldierButtonFunc(JButton button) {
        button.addActionListener(e -> {
            if (game.hasUnits("Soldier", game.player)) {
                if (!holdingSoldier) {
                    holdingSoldier = true;
                    holdingArcher = false;
                } else {
                    holdingSoldier = false;
                }

                gamePanel.remove(units);
                units.removeAll();
                drawUnitsToolBar();
                gamePanel.revalidate();
                gamePanel.repaint();
            }
        });
    }

    // EFFECTS: Add functionality to placing archer button
    private void setArcherButtonFunc(JButton button) {
        button.addActionListener(e -> {
            if (game.hasUnits("Archer", game.player)) {
                if (!holdingArcher) {
                    holdingArcher = true;
                    holdingSoldier = false;
                } else {
                    holdingArcher = false;
                }

                gamePanel.remove(units);
                units.removeAll();
                drawUnitsToolBar();
                gamePanel.revalidate();
                gamePanel.repaint();
            }
        });
    }

    // EFFECTS: Add functionality to board after selecting unit
    private void setBoardButtonFunc(JButton boardButton) {
        boardButton.addActionListener(e -> {
            if (holdingArcher | holdingSoldier) {
                placeUnit(e, tutorial);
            } else {
                takeUnit(e, tutorial);
            }
        });
    }

    // EFFECTS: Add functionality to finish prep button
    private void setFinishPrepButtonFunc(JButton finishButton) {
        finishButton.setBackground(new Color(24,214,49));
        finishButton.setFont(new Font("Sans serif", Font.BOLD, 24));
        finishButton.setForeground(Color.WHITE);

        finishButton.addActionListener(e -> {
            gamePanel.removeAll();
            gamePanel.setVisible(false);
            drawGameBoard("tutorial", 2);
        });
    }

    // EFFECTS: Set default text style
    private void setTextStyle(JLabel text) {
        text.setFont(buttonFont);
    }

    // EFFECTS: Get x coordinate of event e
    // RETURNS: i
    private int getButtonX(ActionEvent e, JButton[][] map) {
        for (int i = 0; i < map.length; i++) {
            for (int j = 0; j < map[0].length; j++) {
                if (map[i][j] == e.getSource()) {
                    return i;
                }
            }
        }
        return 0;
    }

    // EFFECTS: Get y coordinate of event e
    // RETURNS: i
    private int getButtonY(ActionEvent e, JButton[][] map) {
        for (JButton[] jbuttons : map) {
            for (int j = 0; j < map[0].length; j++) {
                if (jbuttons[j] == e.getSource()) {
                    return j;
                }
            }
        }
        return 0;
    }

    // EFFECTS: Place unit on board
    private void placeUnit(ActionEvent e, JButton[][] map) {
        int x = getButtonX(e, map);
        int y = getButtonY(e, map);
        JButton newButton = new JButton();
        setBoardButtonStyle(newButton);

        if (game.canPlaceUnit(x, y)) {
            setBoardButtonFunc(newButton);
            if (holdingArcher) {
                newButton.setText("AllyArcher");
                game.placeUnit(x, y, "Archer");
            } else if (holdingSoldier) {
                newButton.setText("AllySoldier");
                game.placeUnit(x, y, "Soldier");
            }

            map[x][y] = newButton;
            redrawGameBoard(map);
            updateGamePanel();
        }
    }

    // EFFECTS: Take unit off of board
    private void takeUnit(ActionEvent e, JButton[][] map) {
        int x = getButtonX(e,map);
        int y = getButtonY(e, map);

        if (game.isAllyUnit(x,y)) {
            JButton fillButton = new JButton();
            setBoardButtonStyle(fillButton);
            setBoardButtonFunc(fillButton);

            map[x][y] = fillButton;
            game.takeUnit(x,y);

            redrawGameBoard(map);

            updateGamePanel();
        }
    }

    // EFFECTS: Helper function to redraw game board
    private void redrawGameBoard(JButton[][] map) {
        gameBoard.removeAll();
        addGameBoardButton(map);
        gamePanel.remove(units);
        holdingArcher = false;
        holdingSoldier = false;
        drawUnitsToolBar();
    }

    // EFFECTS: Helper function to update game panel
    private void updateGamePanel() {
        gamePanel.revalidate();
        gamePanel.repaint();
    }


}
