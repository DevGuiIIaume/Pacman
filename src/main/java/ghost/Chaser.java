package ghost;

import game.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import processing.core.PApplet;
import processing.core.PImage;

public class Chaser extends Ghost {

    /** Creates a new Chaser object
    @param x The x coordinate of the chaser
    @param y The y coordinate of the chaser
    @param cellID The cell ID corresponding to the chaser
    @param sprite The sprite of the chaser
    @param app Instance of the app
    */
    public Chaser(int x, int y, char cellID, PImage sprite, App app) {
        super(x, y, cellID, sprite, app);
        this.cornerX = 0;
        this.cornerY = 0;
    }

    /** Finds the coordinates for where the ghost will move towards
    @return Integer array containing the x and y coordinates
    @param app Instance of the app
    @param scatter The current scatter state
    */
    public int[] targetMove(App app, boolean scatter) {
        // Checks current scatter state, targets corner if true
        if (scatter) {
            return new int[] {0, 0};
        }
        // Targets the waka's position
        return new int[] {app.game.waka.getX(), app.game.waka.getY()};
    }
}
