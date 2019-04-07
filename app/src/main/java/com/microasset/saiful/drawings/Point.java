package com.microasset.saiful.drawings;


/**
 * a simple class for 2D point
 */
public class Point {
    public float x;
    public float y;

    /**
     * default constructor
     */
    public Point() {
        this.x = 0;
        this.y = 0;
    }

    /**
     *
     * @param x x coordinate
     * @param y y coordinate
     */
    public Point(float x,float y) {
        this.x = x;
        this.y = y;
    }

    /**
     *
     * @param p second point
     * @return euclidian distance between this point and another point
     */
    public float distance(Point p) {
        return (float)Math.sqrt((p.x-this.x)*(p.x-this.x) + (p.y-this.y)*(p.y-this.y));
    }

    /**
     *
     * @param p second point
     * @return squared euclidian distance between this point and another point
     */
    public float squaredDistance(Point p) {
        return (p.x-this.x)*(p.x-this.x) + (p.y-this.y)*(p.y-this.y);
    }

}
