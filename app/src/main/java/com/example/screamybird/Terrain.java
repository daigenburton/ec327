package com.example.screamybird;

public class Terrain {
    private float x;
    private float y;
    private float height;

    public Terrain (float xPos, float yPos, float height) {
        this.x = xPos;
        this.y= yPos;
        this.height=height;
    }

    public float getX () {
        return x;
    }

    public float getY () {
        return y;
    }

    public float getHeight () { return height; }

    public void setX (float newX) {
        this.x = newX;
    }
}
