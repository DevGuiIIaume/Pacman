package ghost;


import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import processing.core.PApplet;
import processing.core.PImage;

import game.*;

public class Ignorant extends Ghost {

    /** Creates a new Ignorant object
    @param x The x coordinate of the ignorant
    @param y The y coordinate of the ignorant
    @param cellID The cell ID corresponding to the ignorant
    @param sprite The sprite of the ignorant
    @param app Instance of the app
    */
    public Ignorant(int x, int y, char cellID, PImage sprite, App app) {
        super(x, y, cellID, sprite, app);
        this.cornerX = 0;
        this.cornerY = 576;
    }

    /** Finds the coordinates for where the ghost will move towards
    @return Integer array containing the x and y coordinates
    @param app Instance of the app
    @param scatter The current scatter state
    */
    public int[] targetMove(App app, boolean scatter) {
        // Checks current scatter state, targets corner if true
        if (scatter) {
            return new int[] {0, 576};
        }
        // If the waka is further away than 8 units, target the waka
        boolean chase = false;
        if ((int) getDistance(this.x, app.game.waka.getX(), this.y, app.game.waka.getY()) > 128) {
            chase = true;
        }

        if (chase) {
            // Target the waka's current position
            return new int[] {app.game.waka.getX(), app.game.waka.getY()};
        } else {
            // If the waka is closer than 8 units, target the designated corner
            return new int[] {this.cornerX, this.cornerY};
        }
    }
}
