package game;

import processing.core.PImage;

public abstract class GameObject {

    /** Stores the object's x position
    */
    protected int x;
    /** Stores the object's y position
    */
    protected int y;
    /** Stores the object's cell ID
    */
    protected char cellID;
    /** Stores the object's sprite
    */
    protected PImage sprite;
    /** Stores whether the object is a ghost
    */
    public boolean isGhost;
    /** Stores whether the object is a fruit
    */
    public boolean isFruit;
    /** Stores whether the object is a superfruit
    */
    public boolean isSuperFruit;
    /** Stores whether the object is a soda can
    */
    public boolean isSodaCan;

    /** Creates a new game object
    @param x The x coordinate of the object
    @param y The y coordinate of the object
    @param cellID The cell ID corresponding to the object
    @param sprite The sprite of the object
    */
    public GameObject(int x, int y, char cellID, PImage sprite) {
        this.x = x;
        this.y = y;
        this.cellID = cellID;
        this.sprite = sprite;
        this.isGhost = false;
        this.isFruit = false;
        this.isSuperFruit = false;
        this.isSodaCan = false;
    }

    /** Gets the x position
    @return X position
    */
    public int getX() {
        return this.x;
    }

    /** Sets the x position
    @param x The x value
    */
    public void setX(int x) {
        this.x = x;
    }

    /** Sets the y position
    @param y The y value
    */
    public void setY(int y) {
        this.y = y;
    }

    /** Gets the y position
    @return Y position
    */
    public int getY() {
        return this.y;
    }

    /** Gets the sprite image
    @return The sprite image
    */
    public PImage getSprite() {
        return this.sprite;
    }

    /** Gets the ID of the cell
    @return The cell ID
    */
    public char getCellID() {
        return this.cellID;
    }

    /** Gets the sprite width
    @return The sprite width
    */
    public int getWidth() {
        return this.sprite.width;
    }

    /** Returns the sprite height
    @return The sprite height
    */
    public int getHeight() {
        return this.sprite.height;
    }

    /** Gets whether the game object is a fruit
    @return True if the game object is a fruit, else false
    */
    public boolean isFruit() {
        return this.isFruit;
    }

    /** Gets whether the game object is a super fruit
    @return True if the game object is a super fruit, else false
    */
    public boolean isSuperFruit() {
        return this.isSuperFruit;
    }

    /** Gets whether the game object is a soda can
    @return True if the game object is a soda can, else false
    */
    public boolean isSodaCan() {
        return this.isSodaCan;
    }

    /** Gets whether the game object is a ghost
    @return True if the game object is a ghost, else false
    */
    public boolean isGhost() {
        return this.isGhost;
    }

    /** Converts important attributes to a String
    @return A string containing the cell ID and x,y position
    */
    public String toString() {
        String cellID = "ID: " + String.valueOf(this.cellID);
        String position = "(X,Y): " + String.valueOf(this.x) + "," + String.valueOf(this.y);
        return cellID + ", " + position;
    }




}
