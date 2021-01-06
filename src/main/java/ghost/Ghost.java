package ghost;

import game.*;
import waka.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import processing.core.PApplet;
import processing.core.PImage;

public abstract class Ghost extends Entity {

    /** Stores the PImage of the frightened sprite
    */
    protected PImage frightenedSprite;

    /** Stores the x coordinate of the designated corner when scattering
    */
    protected int cornerX;
    /** Stores the y coordinate of the designated corner when scattering
    */
    protected int cornerY;

    /** The constructor for the Ghost class
    @param x The x coordinate of the ghost
    @param y The y coordinate of the ghost
    @param cellID The cell ID corresponding to the ghost
    @param sprite The sprite of the ghost
    @param app Instance of the app
    */
    public Ghost(int x, int y, char cellID, PImage sprite, App app) {
        super(x, y, cellID, sprite, app);
        this.frightenedSprite = app.loadImage("src/main/resources/frightened.png");
    }

    /** Ticks the ghost
    @param app Instance of the app
    */
    public void tick(App app) {
        // If the ghost has been eaten, reset the ghost
        if (!this.alive) {
            this.reset();
            return;
        } else if (app.game.frightened) {
            // Pick a random move
            this.nextMove = (int)(Math.random() * ((40 - 37) + 1)) + 37;
            // Ensure this move doesn't move the ghost backwards
            if (validMove(this.nextMove, this.lastMove) && !checkCollision(this.nextMove, app)) {
                move(this.nextMove, app);
            }
            // Reset the frightened state if the designated time has elapsed
            if ((app.frameCount - app.game.frightenedTime) / 60 > app.game.frightenedLength) {
                app.game.frightened = false;
                app.game.sodaCanEffect = false;
            }
            return;
        }
        // Decides the target coordinates depending on scatter state
        int[] targetCoordinates = targetMove(app, checkScatter());
        // Find the best move given the target coordinates
        ArrayList<Integer> moveCode = getBestMove(targetCoordinates[0], targetCoordinates[1], app);
        // Attempts to set the next move for the ghost
        for (int move : moveCode) {
            // Check that the ghost isn't moving backwards or colliding with a wall
            if (validMove(this.lastMove, move) && !checkCollision(move, app)) {
                this.nextMove = move;
                break;
            }
        }
        // Move the ghost
        if (!move(this.lastMove, app)) {
            for (int i : moveCode) {
                if (move(i, app)) {
                    // Loop will break as soon as a move has been executed
                    break;
                }
            }
        }
    }

    /** Checks for whether the ghost is currently in scatter mode
    @return True if the ghost is in scatter mode, otherwise false
    */
    public boolean checkScatter() {
        // Reset the cursor and scatter state
        int modeLengthSize = app.game.modeLengths.size();
        if (modeLengthSize == this.app.game.modeCursor || modeLengthSize == 1) {
            this.app.game.modeCursor = 0;
            this.app.game.scatterState = true;
            return true;
        }
        // If the time for the current mode has elapsed
        if (app.time / 60 >= app.game.modeLengths.get(this.app.game.modeCursor)) {
            app.time = 0;
            // Change the state of scatter
            app.game.scatterState = !app.game.scatterState;
            // Incremenet the cursor
            this.app.game.modeCursor += 1;
        }
        return app.game.scatterState;
    }

    /** Checks whether the given move does not move the ghost backwards
    @return Returns true if the move is valid
    @param lastMove the last move the ghost performed
    @param nextMove the queued next move
    */
    public boolean validMove(int lastMove, int nextMove) {
        // Checks that the move is within the specified bounds
        boolean invalidLastMove = !(lastMove >= 37 && lastMove <= 40);
        boolean invalidNextMove = !(nextMove >= 37 && nextMove <= 40);
        if (invalidLastMove || invalidNextMove) {
            return false;
        }
        // Checks the complement of each move
        // Returns false if the move turns the ghost in the opposite direction
        if (nextMove == 38 && lastMove == 40) {
            return false;
        } else if (nextMove == 40 && lastMove == 38) {
            return false;
        } else if (nextMove == 37 && lastMove == 39) {
            return false;
        } else if (nextMove == 39 && lastMove == 37) {
            return false;
        } else {
            return true;
        }
    }

    /** Gets the best move for a given direction
    @return Returns a list of moves ranked from best to worst
    @param targetX The x position of the target
    @param targetY The Y position of the target
    @param app The instance of the app
    */
    public ArrayList<Integer> getBestMove(int targetX, int targetY, App app) {

        ArrayList<Double> distances = new ArrayList<Double>();
        HashMap<Double, Integer> map = new HashMap<Double, Integer>();

        // Draws the debug line
        if (app.debug) {
            app.stroke(255, 255, 255);
            if (!checkScatter()) {
                targetX += magicOffset;
                targetY += magicOffset;
            }
            int x = this.x + this.magicOffset;
            int y = this.y + this.magicOffset;
            app.line((float) x, (float) y, (float) targetX, (float) targetY);
        }
        // Calculates the euclidean distance
        double moveW = getDistance(this.x, targetX, this.y - 1, targetY);
        double moveA = getDistance(this.x - 1, targetX, this.y, targetY);
        double moveS = getDistance(this.x, targetX, this.y + 1, targetY);
        double moveD = getDistance(this.x + 1, targetX, this.y, targetY);

        // Add each (move, distance) as a key, value pair
        // Add each distance to a list
        distances.add(moveW);
        map.put(moveW, 38);
        distances.add(moveA);
        map.put(moveA, 37);
        distances.add(moveS);
        map.put(moveS, 40);
        distances.add(moveD);
        map.put(moveD, 39);

        // Sort the list of distances from smallest to largest
        Collections.sort(distances);
        ArrayList<Integer> bestMove = new ArrayList<Integer>();

        // Create a list of moves ordered from best to worst
        // Where the best move has the smallest euclidean distance
        for (double d : distances) {
            bestMove.add(map.get(d));
        }

        return bestMove;
    }

    /** Gets the euclidean distance between two points
    @return Returns the euclidean distance
    @param ghostX Ghost's x position
    @param x Target x position
    @param ghostY Ghost's y position
    @param y Target y position
    */
    public double getDistance(int ghostX, int x, int ghostY, int y) {
        double xVal = Math.pow(ghostX - x, 2);
        double yVal = Math.pow(ghostY - y, 2);
        double distance = Math.pow(xVal + yVal, 0.5);
        return distance;
    }

    /** Draws the ghost depending on its state
    @param app Instance of the app
    */
    public void draw(App app) {
        if (!this.alive) {
            return;
        }
        boolean frightened = this.app.game.frightened;
        boolean sodaCanEffect = app.game.sodaCanEffect;

        if (!sodaCanEffect && frightened && app.frameCount % 8 >= 0 && app.frameCount % 30 <= 8) {
            // Do not draw the sprite if it under the soda can effect
            // Alternate between nothing and the frightened sprite to give the ghost a shimmering effect
            app.image(this.frightenedSprite, this.x - this.magicOffset, this.y - this.magicOffset);
        } else if (!(frightened || sodaCanEffect)) {
            // Do not draw the sprite if it under the soda can effect
            app.image(this.sprite, this.x - this.magicOffset, this.y - this.magicOffset);
        }
    }

    /** Finds the coordinates for where the ghost will move towards
    @return Integer array containing the x and y coordinates
    @param app Instance of the app
    @param scatter The current scatter state
    */
    public abstract int[] targetMove(App app, boolean scatter);





}
