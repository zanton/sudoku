package com.ant.sudoku;

public class Coordinate {
	public float x;
    public float y;

    public Coordinate() {
    }
    
    public Coordinate(float newX, float newY) {
        x = newX;
        y = newY;
    }
    
    public Coordinate(Coordinate c) {
        x = c.x;
        y = c.y;
    }

    public boolean equals(Coordinate other) {
        if (x == other.x && y == other.y) {
            return true;
        }
        return false;
    }
    
    public double distance(Coordinate c) {
    	return Math.sqrt(Math.pow(x-c.x, 2) + Math.pow(y-c.y, 2));
    }
    
    public void transition(float dx, float dy) {
    	x += dx;
    	y += dy;
    }
}
