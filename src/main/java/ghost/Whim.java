package ghost;

import java.util.List;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import processing.core.PApplet;
import processing.core.PImage;

import game.*;

import java.util.stream.*;

public class Whim extends Ghost {

    /** Creates a new Whim object
    @param x The x coordinate of the whim
    @param y The y coordinate of the whim
    @param cellID The cell ID corresponding to the whim
    @param sprite The sprite of the whim
    @param app Instance of the app
    */
    public Whim(int x, int y, char cellID, PImage sprite, App app) {
        super(x, y, cellID, sprite, app);
        this.cornerX = 448;
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
            return new int[] {448, 576};
        }
        // Gets the waka's direction and position
        int wakaDirection = app.game.waka.getLastMove();
        int wakaX = app.game.waka.getX();
        int wakaY = app.game.waka.getY();
        // Get the chaser object
        List<Ghost> chasers = app.game.ghosts.stream()
                                      .filter((g) -> g.getCellID() == 'c')
                                      .collect(Collectors.toList());
        if (chasers.size() == 0) {
            return new int[] {wakaX, wakaY};
        }
        Ghost chaser = chasers.get(0);
        // Get the chaser's position
        int ghostX = chaser.getX();
        int ghostY = chaser.getY();

        // Construct a vector given the current direction of the waka and the chaser
        // Double this vector
        if (wakaDirection == 38) {
            // UP
            wakaY -= 32;
            return new int[] {ghostX + 2*(wakaX - ghostX), ghostY + 2*(wakaY - ghostY)};
        } else if (wakaDirection == 37) {
            // LEFT
            wakaX -= 32;
            return new int[] {ghostX + 2*(wakaX - ghostX), ghostY + 2*(wakaY - ghostY)};
        } else if (wakaDirection == 40) {
            // DOWN
            wakaY += 32;
            return new int[] {ghostX + 2*(wakaX - ghostX), ghostY + 2*(wakaY - ghostY)};
        } else {
            // RIGHT
            wakaX += 32;
            return new int[] {ghostX + 2*(wakaX - ghostX), ghostY + 2*(wakaY - ghostY)};
        }
    }
}
