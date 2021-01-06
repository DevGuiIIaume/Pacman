package waka;

import game.*;
import ghost.*;

import java.util.Arrays;
import java.util.ArrayList;
import java.util.HashMap;
import processing.core.PApplet;
import processing.core.PImage;


public class Waka extends Entity {

    /** Stores the sprite for the player facing up
    */
    protected PImage playerUp;
    /** Stores the sprite for the player facing down
    */
    protected PImage playerDown;
    /** Stores the sprite for the player facing left
    */
    protected PImage playerLeft;
    /** Stores the sprite for the player facing right
    */
    protected PImage playerRight;
    /** Stores the closed mouth sprite for the player
    */
    protected PImage playerClosed;
    /** Stores the sprite for the player's life
    */
    protected PImage playerLife;

    /** Creates a new waka object
    @param x The x coordinate of the waka
    @param y The y coordinate of the waka
    @param cellID The cell ID corresponding to the waka
    @param sprite The sprite of the waka
    @param app Instance of the app
    */
    public Waka(int x, int y, char cellID, PImage sprite, App app) {
        super(x, y, cellID, sprite, app);

        this.lastMove = 37;

        this.playerUp = this.app.loadImage("src/main/resources/playerUp.png");
        this.playerDown = this.app.loadImage("src/main/resources/playerDown.png");
        this.playerLeft = this.app.loadImage("src/main/resources/playerLeft.png");
        this.playerRight = this.app.loadImage("src/main/resources/playerRight.png");
        this.playerClosed = this.app.loadImage("src/main/resources/playerClosed.png");
        this.playerLife = this.app.loadImage("src/main/resources/playerRight.png");
        this.playerLife.resize(30, 30);
    }

    /** Checks for whether the waka is currently colliding with a ghost
    @return Returns true if the waka has collided with a ghost, otherwise false
    @param app Instance of the app
    */
    public boolean checkGhostCollision(App app) {
        // Iterate through the list of ghosts
        for (Ghost ghost : app.game.ghosts) {
            // Checks that the ghost is alive and is currently in the same cell as waka
            if (ghost.isAlive() && this.x / 16 == ghost.getX() / 16 && this.y / 16 == ghost.getY() / 16) {
                // Eats the ghost if it is in a frightened state
                if (app.game.frightened) {
                    ghost.setAlive(false);
                } else {
                    // Waka dies and the positions are reset
                    app.game.lives -= 1;
                    for (Ghost g : app.game.ghosts) {
                        g.reset();
                        g.setAlive(true);
                    }
                    // Waka is reset
                    this.reset();
                    this.lastMove = 37;
                    return true;
                }
            }
        }
        return false;
    }

    /** Checks for whether waka has collided with a fruit, superfruit or soda can
    @return Whether the waka has collided with a power up
    @param app Instance of the app
    */
    public boolean checkPowerUpCollision(App app) {
        // Gets the current game object from the game matrix
        ArrayList<GameObject> row = app.game.gameGrid.get(this.y/16);
        GameObject cell = row.get(this.x/16);
        // Check for whether the cell is a power up
        if (!(cell.isFruit() || cell.isSuperFruit() || cell.isSodaCan())) {
            return false;
        } else {
            if (cell.isFruit()) {
                // Decrement the number of fruits
                app.game.fruits -= 1;
            } else if (cell.isSuperFruit()) {
                // Set the ghosts to frightened and begin a timer
                app.game.frightened = true;
                app.game.frightenedTime = app.frameCount;
            } else if (cell.isSodaCan()) {
                // Set the ghosts to frightened and begin a timer
                // Set the ghosts to experience the soda can effect, turning them invisible
                app.game.frightened = true;
                app.game.frightenedTime = app.frameCount;
                app.game.sodaCanEffect = true;
            }
            // Replace the cell with an empty sprite
            PImage emptyImage = app.loadImage("src/main/resources/empty.png");
            Cell emptyCell = new Cell(this.y/16, this.x/16, '0', emptyImage);
            row.set(this.x/16, emptyCell);
            return true;
        }
    }

    /** Updates the sprite given the current direction
    @param wakaDirection The waka's current direction
    */
    public void updateSprite(int wakaDirection) {
            if (wakaDirection == 38) {
                // UP
                this.sprite = this.playerUp;
            } else if (wakaDirection == 37) {
                // LEFT
                this.sprite = this.playerLeft;
            } else if (wakaDirection == 40) {
                // DOWN
                this.sprite = this.playerDown;
            } else if (wakaDirection == 39) {
                // RIGHT
                this.sprite = this.playerRight;
            }
    }

    /** Ticks the waka, moving and checking for collision
    @param app Instance of the app
    */
    public void tick(App app) {
        if (move(this.lastMove, app)) {
            updateSprite(this.lastMove);
        }
        // Eats the fruit if present
        // Activates the superfruit/sodaCan if eaten
        checkPowerUpCollision(app);
        // Checks for whether waka is colliding with a ghost
        checkGhostCollision(app);
    }

    /** Draws the number of lives to the screen
    @param app Instance of the app
    */
    public void drawLives(App app) {
        int x = 20;
        int y = 540;
        for (int i = 0; i < app.game.lives; i++) {
            app.image(this.playerLife, x, y);
            x += 40;
        }
    }

    /** Draws the waka to the screen, alternating between closed and open sprite
    @param app Instance of the app
    */
    public void draw(App app) {
        // Offset the sprite to better centre it
        int xPos = this.x - this.magicOffset;
        int yPos = this.y - this.magicOffset;
        // Alternates between open and closed mouth at regular intervals
        if (app.frameCount % 8 >= 0 && app.frameCount % 16 <= 8) {
            app.image(this.playerClosed, xPos, yPos);
        } else {
            app.image(this.sprite, xPos, yPos);
        }
        // Draws the number of lives to the screen
        drawLives(app);
    }




}
