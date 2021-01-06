package game;

import java.util.Arrays;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;
import java.io.File;
import java.io.FileNotFoundException;

import java.io.FileReader;
import java.io.IOException;
import java.util.Iterator;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import processing.core.PApplet;
import processing.core.PImage;

import ghost.*;
import game.*;
import waka.*;

public class Game {

    /** Pairs all the cell IDs to a specific path, corresponding to the cell sprite
    */
    public HashMap<Character, String> cellResources;
    /** Stores a 2d matrix of game objects
    */
    public ArrayList<ArrayList<GameObject>> gameGrid;
    /** Stores the instance of the app
    */
    public App app;

    /** Stores the map filename
    */
    public String filename;
    /** Stores the number of lives remaining
    */
    public int lives;
    /** Stores the speed of the entities
    */
    public int speed;
    /** Stores the length of the modes specified
    */
    public ArrayList<Integer> modeLengths;
    /** Stores the current index of the mode lengths
    */
    public int modeCursor;

    /** Stores whether the ghosts are currently scattering
    */
    public boolean scatterState;
    /** Stores whether the ghosts are currently frightened and invisible
    */
    public boolean sodaCanEffect;
    /** Stores whether the ghosts are currently frightened
    */
    public boolean frightened;
    /** Stores the length of the frightned mode
    */
    public int frightenedLength;
    /** Stores the elapsed time since eating the superfruit
    */
    public int frightenedTime;

    /** Stores the waka instance
    */
    public Waka waka;
    /** Stores a list of all the ghosts
    */
    public ArrayList<Ghost> ghosts;
    /** Stores a list of all the walls
    */
    public ArrayList<GameObject> walls;
    /** Stores the number of fruit remaining
    */
    public int fruits;

    /** Stores whether the game is currently resetting
    */
    public boolean resetGame;

    /** Constructs a new game object
    @param app Instance of the app
    */
    public Game(App app) {
        this.app = app;
        this.cellResources = new HashMap<Character, String>();
        this.gameGrid = new ArrayList<ArrayList<GameObject>>();
        this.walls = new ArrayList<GameObject>();
        this.ghosts = new ArrayList<Ghost>();
        this.modeLengths = new ArrayList<Integer>();
        this.waka = null;
        this.frightenedLength = 7;
        this.filename = null;
        this.scatterState = true;
        this.modeCursor = 0;
        loadResources();
    }

    /** Loads the filepaths into a hashmap, pairing them to the corresponding cell ID
    */
    public void loadResources() {
        this.cellResources.put('0', "src/main/resources/empty.png");
        this.cellResources.put('1', "src/main/resources/horizontal.png");
        this.cellResources.put('2', "src/main/resources/vertical.png");
        this.cellResources.put('3', "src/main/resources/upLeft.png");
        this.cellResources.put('4', "src/main/resources/upRight.png");
        this.cellResources.put('5', "src/main/resources/downLeft.png");
        this.cellResources.put('6', "src/main/resources/downRight.png");
        this.cellResources.put('7', "src/main/resources/fruit.png");
        this.cellResources.put('8', "src/main/resources/superFruit.png");
        this.cellResources.put('p', "src/main/resources/playerClosed.png");
        this.cellResources.put('g', "src/main/resources/ghost.png");
        this.cellResources.put('a', "src/main/resources/ambusher.png");
        this.cellResources.put('c', "src/main/resources/chaser.png");
        this.cellResources.put('i', "src/main/resources/ignorant.png");
        this.cellResources.put('w', "src/main/resources/whim.png");
        this.cellResources.put('s', "src/main/resources/sodaCan.png");
    }


    /** Parses the JSON file and sets the corresponding attributes
    @param app The instance of the App
    */
    public void parseJSON(App app) {
        // Create a JSONParser object
        JSONParser jsonParser = new JSONParser();
        try {
            // Parse the config file
            FileReader fReader = new FileReader("config.json");
            JSONObject jsonObject = (JSONObject) jsonParser.parse(fReader);

            // Store the values into instance attributes
            this.filename = jsonObject.get("map").toString();
            this.lives = Integer.parseInt(jsonObject.get("lives").toString());
            this.speed = Integer.parseInt(jsonObject.get("speed").toString());
            String frightenedLength = jsonObject.get("frightenedLength").toString();
            this.frightenedLength = Integer.parseInt(frightenedLength);

            // Iterate through the list of mode lengths
            JSONArray jsonArray = (JSONArray) jsonObject.get("modeLengths");
            for (Object obj : jsonArray) {
                // Store the modes within an integer array list
                int mode = Integer.parseInt(obj.toString());
                this.modeLengths.add(mode);
            }
        } catch (FileNotFoundException fnfe) {
            fnfe.printStackTrace();
            return;
        } catch (IOException ioe) {
            ioe.printStackTrace();
            return;
        } catch (ParseException pe) {
            pe.printStackTrace();
            return;
        }
    }

    /** Loads the map into a 2d matrix of GameObjects.
    Iterates through the map who's path given in the config file.
    Each game object has its coordiantes incremented by an offset of 16 pixels.
    The cell sprite path is fetched from the hash map using the cell ID as a key.
    @return A 2d matrix of GameObjects
    @param app The instance of the App
    */
    public ArrayList<ArrayList<GameObject>> loadGame(App app) {
        // Null check
        if (this.filename == null || this.filename.equals("")) {
            return null;
        }
        // Create 2d game matrix of game objects
        ArrayList<ArrayList<GameObject>> gameMatrix = new ArrayList<ArrayList<GameObject>>();

        try {
            // Read into the file
            File file = new File(this.filename);
            Scanner scan = new Scanner(file);

            int y = 0;
            while (scan.hasNextLine()) {
                // Store the current line as a String
                String line = scan.nextLine();
                // Create an array for the current row
                ArrayList<GameObject> currentRow = new ArrayList<GameObject>();
                int x = 0;
                // Iterate through the line
                for (char cellID : line.toCharArray()) {

                    // Find the corresponding cellSpritePath from the hashmap
                    String cellSpritePath = cellResources.get(cellID);
                    // Load the sprite
                    PImage cellSprite = app.loadImage(cellSpritePath);
                    // Construct the cell object
                    Cell cell = new Cell(x, y, cellID, cellSprite);

                    // If the cell is a player
                    if (cellID == 'p') {
                        // Create a waka object
                        Waka waka = new Waka(x, y, cellID, cellSprite, app);
                        // Assign it to the game
                        waka.app = app;
                        this.waka = waka;
                    } else if ("aciw".contains(Character.toString(cellID))) {
                        // If the cell ID corresponds to a ghost
                        // Create a subtype corresponding to the cell ID
                        Ghost ghost = null;
                        if (cellID == 'a') {
                            ghost = new Ambusher(x, y, cellID, cellSprite, app);
                        } else if (cellID == 'c') {
                            ghost = new Chaser(x, y, cellID, cellSprite, app);
                        } else if (cellID == 'i') {
                            ghost = new Ignorant(x, y, cellID, cellSprite, app);
                        } else if (cellID == 'w') {
                            ghost = new Whim(x, y, cellID, cellSprite, app);
                        }
                        ghost.isGhost = true;
                        // Add the ghost to the list of ghosts
                        this.ghosts.add(ghost);
                    } else if (cellID == 's') {
                        cell.isSodaCan = true;
                    } else {
                        int value = Character.getNumericValue(cellID);
                        // If the cell is a wall
                        if (value >= 1 && value <= 6) {
                            this.walls.add(cell);
                        } else if (value == 7) {
                            // If the cell is a fruit
                            cell.isFruit = true;
                            this.fruits += 1;
                        } else if (value == 8) {
                            // If the cell is a superfruit
                            cell.isSuperFruit = true;
                        }
                    }
                    // Add the object to the current row
                    currentRow.add(cell);
                    x += 16;
                }
                // Add the row to the 2d matrix of game objects
                gameMatrix.add(currentRow);
                y += 16;
            }
            this.gameGrid = gameMatrix;
            return gameMatrix;
        } catch (FileNotFoundException fnfe) {
            fnfe.printStackTrace();
            return null;
        }
    }

    /** Checks for whether the game has reached a win or loss condition
    @return Returns true if the game needs to be reset
    @param app An instance of the app
    */
    public boolean checkWinOrLose(App app) {
        if (this.fruits == 0) {
            // Win the game if the number of fruits equals 0
            drawWin(app);
            return true;
        } else if (this.lives == 0) {
            // Lose the game if the lives have all been lost
            drawLose(app);
            return true;
        }
        return false;
    }

    /** Draws the winning screen if all the fruits have been consumed
    @param app Instance of the app
    */
    public void drawWin(App app) {
        app.background(0, 0, 0);
        app.text("YOU WIN", 160, 260);
    }

    /** Draws the losing screen if no lives remain
    @param app Instance of the app
    */
    public void drawLose(App app) {
        app.background(0, 0, 0);
        app.text("GAME OVER", 160, 260);
    }

    /** Draws the gamegrid to the screen
    */
    public void draw() {
        // Iterates through the game matrix and draws each gameobject to the screen
        for (ArrayList<GameObject> row : this.gameGrid) {
            for (GameObject obj : row) {
                // Null check
                if (obj == null) {
                    continue;
                }
                if (!("aciwgp".contains(Character.toString(obj.getCellID())))) {
                    app.image(obj.getSprite(), obj.getX(), obj.getY());
                }
            }
        }
    }
}
