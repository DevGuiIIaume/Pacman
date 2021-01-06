package game;

import processing.core.PApplet;
import processing.core.PImage;
import java.util.HashMap;

import ghost.*;
import game.*;

public class Cell extends GameObject {

    /** Constructs the new cell object
    @param x The x coordinate of the object
    @param y The y coordinate of the object
    @param cellID The cell ID corresponding to the object
    @param sprite The sprite of the object
    */
    public Cell(int x, int y, char cellID, PImage sprite) {
        super(x, y, cellID, sprite);
    }
}
