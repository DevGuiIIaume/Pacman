package game;

import processing.core.PApplet;
import processing.core.PImage;
import processing.core.PFont;
import java.util.ArrayList;

import waka.*;
import game.*;
import ghost.*;

public class App extends PApplet {
    /** The width of game the window
    */
    public static final int WIDTH = 448;
    /** The height of game the window
    */
    public static final int HEIGHT = 576;
    /** The instance of the game, containing the game matrix and attributes for the state
    */
    public Game game;
    /** Stores the time to calculate when to change to scatter
    */
    public int time;
    /** Stores whether the game is currently in debug mode
    */
    public boolean debug;
    /** Stores the font needed to draw the win and lose screen
    */
    public PFont font;

    /** Constructs new App object
    */
    public App() {
        this.time = 0;
        this.debug = false;
        this.game = new Game(this);
    }

    /** Sets up the game instance and the necessary fonts
    */
    public void setup() {
        frameRate(60);
        this.game.parseJSON(this);
        this.game.loadGame(this);
        this.font = this.createFont("src/main/resources/PressStart2P-Regular.ttf", 16f);
        textFont(this.font);
    }

    /** Creates the game window with the specified dimensions
    */
    public void settings() {
        size(WIDTH, HEIGHT);
    }

    /** Ticks through the entities in the game
    */
    public void tickEntities() {
        for(Ghost ghost : this.game.ghosts) {
            ghost.tick(this);
        }
        this.game.waka.tick(this);
    }

    /** Draws the entities in the game to the screen
    */
    public void drawEntities() {
        for(Ghost ghost : this.game.ghosts) {
            ghost.draw(this);
        }
        this.game.waka.draw(this);
    }

    /** Draws all the game objects to the screen
    */
    public void draw() {
        background(0, 0, 0);
        // Resets the game state
        if (this.game.resetGame) {
            resetGame();
        }

        // Ticks through the entities
        tickEntities();

        // Checks for whether there is a win or lose condition
        if (this.game.checkWinOrLose(this)) {
            this.game.resetGame = true;
            return;
        }

        // Draws the game grid and the entities
        this.game.draw();

        // Draws the ghost and waka entities to the screen
        drawEntities();

        // Increments the time
        this.time += 1;
    }

    /** Checks for what the last key press was
    */
    public void keyPressed() {
        Waka waka = this.game.waka;
        // Starts debug mode if the key press was spacebar
        if (keyCode == 32) {
            this.debug = !this.debug;
        } else if (waka.checkCollision(keyCode, this)) {
            // If the move causes collision, queue it as the next move instead
            waka.nextMove = keyCode;
        } else if (keyCode >= 37 && keyCode <= 40) {
            // If the move doesn't cause collision, store is as the last lastMove
            // Reset the next move
            waka.lastMove = keyCode;
            waka.nextMove = 0;
        }
    }

    /** Resets the game state after 10 seconds
    */
    public void resetGame() {
        // Pause the game for 10 seconds
        this.delay(10000);
        // Reset the time and states
        this.time = 0;
        this.debug = false;
        // Create a new game instance
        this.game = new Game(this);
        setup();
    }

    public static void main(String[] args) {
        PApplet.main("game.App");
    }

}
