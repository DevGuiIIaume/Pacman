package ghost;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import processing.core.PApplet;
import processing.core.PImage;

import game.*;

public class Ambusher extends Ghost {

    /** Creates a new Ambusher object
    @param x The x coordinate of the ambusher
    @param y The y coordinate of the ambusher
    @param cellID The cell ID corresponding to the ambusher
    @param sprite The sprite of the ambusher
    @param app Instance of the app
    */
    public Ambusher(int x, int y, char cellID, PImage sprite, App app) {
        super(x, y, cellID, sprite, app);
        this.cornerX = 448;
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
            return new int[] {448, 0};
        }

        // Gets the waka's direction and position
        int wakaX = app.game.waka.getX();
        int wakaY = app.game.waka.getY();
        int lastMove = app.game.waka.getLastMove();

        // Targts two units in front of waka given its current direction
        if (lastMove == 38) {
            // UP
            return new int[] {wakaX, wakaY - 64};
        } else if (lastMove == 37) {
            // LEFT
            return new int[] {wakaX - 64, wakaY};
        } else if (lastMove == 40) {
            // DOWN
            return new int[] {wakaX, wakaY + 64};
        } else if (lastMove == 39) {
            // RIGHT
            return new int[] {wakaX + 64, wakaY};
        } else {
            // If the last move isn't correctly defined
            // default to targetting the waka's current position
            return new int[] {wakaX, wakaY};
        }
    }
}
