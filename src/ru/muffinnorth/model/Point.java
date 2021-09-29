package ru.muffinnorth.model;

public class Point {
    private double x;

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    private double y;

    public Point(double x, double y) {
        this.x = x;
        this.y = y;
    }

    @Override
    public String toString() {
        return "Point{" +
                "x=" + x +
                ", y=" + y +
                '}';
    }
}
