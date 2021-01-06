package game;

import ghost.*;
import game.*;

import processing.core.PApplet;
import processing.core.PImage;

public abstract class Entity extends GameObject {

    /** Stores an instance of the app
    */
    protected App app;
    /** Stores the speed of the entity
    */
    protected int speed;
    /** Stores whether the entity is currently alive
    */
    protected boolean alive;

    /** Stores the last valid move made by the entity
    */
    protected int lastMove;
    /** Stores the next queued move
    */
    protected int nextMove;

    /** Stores the initial X value to be used for later resetting the entity
    */
    protected int defaultX;
    /** Stores the initial Y value to be used for later resetting the entity
    */
    protected int defaultY;

    /** Stores the offset necessary to centre the entity on the screen
    */
    protected int magicOffset;

    /** The constructor for the Entity class
    @param x The x coordinate of the entity
    @param y The y coordinate of the entity
    @param cellID The cell ID corresponding to the entity
    @param sprite The sprite of the entity
    @param app Instance of the app
    */
    public Entity(int x, int y, char cellID, PImage sprite, App app) {
        super(x, y, cellID, sprite);
        this.app = app;
        this.speed = app.game.speed;
        this.magicOffset = 5;
        this.alive = true;
        this.defaultX = x;
        this.defaultY = y;
    }

    /** Checks for a collision event.
    Returns true if waka executes its queued move and its sprite overlaps with a wall sprite.
    @return Returns true if move causes a collision with a wall
    @param moveCode The given mode code
    @param app The instance of the App
    */
    public boolean checkCollision(int moveCode, App app) {

        // Calculate future position assuming move is valid
        int futureX = this.x;
        int futureY = this.y;

        // Change the position depending on the move
        if (moveCode == 38) {
            // Up
            futureY -= this.speed;
        } else if (moveCode == 37) {
            // Left
            futureX -= this.speed;
        } else if (moveCode == 40) {
            // Down
            futureY += this.speed;
        } else if (moveCode == 39) {
            // Right
            futureX += this.speed;
        } else {
            // Return false if the move is invalid
            return false;
        }

        boolean outOfBoundsX = futureX < 0 || futureX > 448;
        boolean outOfBoundsY = futureY < 0 || futureY > 576;
        if (outOfBoundsX || outOfBoundsY) {
            return true;
        }

        // Calculate vertices of the entity
        int entityLeft = futureX;
        int entityRight = futureX + 16;
        int entityTop = futureY;
        int entityBottom = futureY + 16;

        boolean collision = false;

        // Iterate through every wall in the game
        for (GameObject wall : app.game.walls) {

            int wallLeft = wall.getX();
            int wallRight = wall.getX() + wall.getWidth();
            int wallTop = wall.getY();
            int wallBottom = wall.getY() + wall.getHeight();

            // Treat the entity and wall as rectangular objects
            // If the corners of the entity are within the wall, trigger a collision event
            if (entityLeft < wallRight && entityRight > wallLeft && entityTop < wallBottom && entityBottom > wallTop) {
                collision = true;
                break;
            }
        }
        return collision;
    }

    /** Moves the entity given the move code.
    Updates the position of the waka based on the move code and current speed
    @return Returns true if the move was successfully actioned
    @param moveCode The given move
    @param app The instance of the App
    */
    public boolean move(int moveCode, App app) {
        // Return false if invalid move
        if (!(moveCode >= 37 && moveCode <= 40)) {
            return false;
        }
        // If the next queued move is able to be actioned, call move and reset the queued move
        if (this.nextMove != 0 && !checkCollision(this.nextMove, app)) {
            moveCode = this.nextMove;
            this.nextMove = 0;
        }

        // Ensure that the move does not cause collision
        if (checkCollision(moveCode, app)) {
            return false;
        }

        // Increment the position of the entity depending on the move
        // Set the last move
        if (moveCode == 38) {
            //Up
            this.y -= this.speed;
            this.lastMove = 38;
            return true;
        } else if (moveCode == 37) {
            //Left
            this.x -= this.speed;
            this.lastMove = 37;
            return true;
        } else if (moveCode == 40) {
            //Down
            this.y += this.speed;
            this.lastMove = 40;
            return true;
        } else {
            //Right, ie. moveCode == 39
            this.x += this.speed;
            this.lastMove = 39;
            return true;
        }
    }

    /** Resets the entity's position to its starting position
    */
    public void reset() {
        this.x = this.defaultX;
        this.y = this.defaultY;
    }

    /** Returns the initial x value
    @return The initial x value
    */
    public int getDefaultX() {
        return this.defaultX;
    }

    /** Returns the initial y value
    @return The initial y value
    */
    public int getDefaultY() {
        return this.defaultY;
    }

    /** Gets the last move
    @return The last move made by the entity
    */
    public int getLastMove() {
        return this.lastMove;
    }

    /** Sets the last move to the parameter
    @param move The move to be set
    */
    public void setLastMove(int move) {
        this.lastMove = move;
    }

    /** Gets whether the entity is alive or not
    @return Returns a boolean, true if alive
    */
    public boolean isAlive() {
        return this.alive;
    }

    /** Sets the ghost boolean alive to the parameter
    @param bool The boolean value to set alive to
    */
    public void setAlive(boolean bool) {
        this.alive = bool;
    }
}
