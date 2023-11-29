package com.example.screamybird;

public class Terrain {
    private float xPos;
    private float yPos;

    public Terrain (float xPos, float yPos) {
        this.xPos = xPos;
        this.yPos= yPos;
    }

    public float getxPos () {
        return xPos;
    }

    public float getyPos () {
        return yPos;
    }

    public void setxPos (float newX) {
        this.xPos = newX;
    }
}
