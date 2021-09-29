package ru.muffinnorth.model;

import java.awt.*;
import java.util.ArrayList;

public class Circle {
    private Point position;
    private double radius;

    public Point getPosition() {
        return position;
    }

    public double getRadius() {
        return radius;
    }

    public Circle(double x, double y, double r){
        position = new Point(x, y);
        radius = r;
    }

    public double getSquere(){
        return Math.PI*Math.pow(this.radius, 2);
    }

    public ArrayList<Point> getIntercetions(Circle other){
        ArrayList<Point> points = new ArrayList<>();
        double d = Math.sqrt(Math.pow(other.getPosition().getX() - this.getPosition().getX(), 2) + Math.pow(other.getPosition().getY() - this.getPosition().getY(), 2));
        double a = (Math.pow(this.radius, 2) - Math.pow(other.radius, 2) + Math.pow(d, 2)) / (2*d);
        double h = Math.sqrt(Math.pow(this.radius,2) - Math.pow(a, 2));


        Point point2 = new Point(
                this.position.getX() + a*(other.getPosition().getX() - this.getPosition().getX()) / d,
                this.position.getY() + a*(other.getPosition().getY() - this.getPosition().getY()) / d
        );

        Point point3 = new Point(
                point2.getX() + h*(other.getPosition().getY() - this.getPosition().getY()) / d,
                point2.getY() - h*(other.getPosition().getX() - this.getPosition().getX()) / d
        );

        Point point4 = new Point(
                point2.getX() - h*(other.getPosition().getY() - this.getPosition().getY()) / d,
                point2.getY() + h*(other.getPosition().getX() - this.getPosition().getX()) / d
        );
        points.add(point3);
        points.add(point4);
        return points;
    }

}
