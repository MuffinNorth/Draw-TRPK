package ru.muffinnorth.view;

import ru.muffinnorth.model.Circle;
import ru.muffinnorth.model.Point;

import javax.swing.*;
import java.awt.*;
import java.awt.print.PageFormat;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.text.DecimalFormat;
import java.util.ArrayList;

public class Canvas extends JPanel implements RadiusController, TranslationController, ScaleController, SquareController {
    private double scale;
    private double step;
    private final double dpi = 51;
    private double realXScale;
    private double realYScale;

    private int translationX = 1;
    private int translationY = 1;
    private double r = 1.0f;

    private double square = 0;

    private MainForm mf;

    public Canvas(MainForm mf){
        super();
        this.mf = mf;
        scale = 1;
    }

    public void addR(){
        r += 0.1;
        this.repaint();
    }

    private void drawAxis(Graphics2D g){
        if(mf.IsGridChecked()){
            g.setPaint(new Color(124,124,124,45));
            for (int i = this.getWidth()/2 + translationX; i > 0  ; i -= dpi) {
                drawXTicket(g, i);
            }
            for (int i = this.getWidth()/2 + translationX; i < this.getWidth() ; i += dpi) {
                drawXTicket(g, i);
            }
            for (int i = this.getHeight()/2 - translationY; i > 0 ; i -= dpi) {
                drawYTicket(g, i);
            }
            for (int i = this.getHeight()/2 - translationY; i < this.getHeight() ; i += dpi) {
                drawYTicket(g, i);
            }
        }
        g.setPaint(new Color(150,150,150,150));
        g.drawLine(0, this.getHeight()/2 - translationY, this.getWidth(), this.getHeight()/2 - translationY);
        g.drawLine(this.getWidth()/2 + translationX, 0, this.getWidth()/2 + translationX, this.getHeight());
    }

    private void drawXTicket(Graphics2D g, int i) {
        g.drawLine(i, 0, i,this.getHeight());
    }

    private void drawYTicket(Graphics2D g, int i) {
        g.drawLine(0, i, this.getWidth(),i);
    }

    private void drawCircle(Graphics2D g, Circle circle){
        int pixelPositionX = (int) (this.getWidth()/2 + circle.getPosition().getX()*realXScale + translationX);
        int pixelPositionY = (int) (this.getHeight()/2 - circle.getPosition().getY()*realYScale - translationY - r/2*realYScale);
        int pixelRadiusX = (int) (circle.getRadius()*realXScale) * 2;
        int pixelRadiusY = (int) (circle.getRadius()*realYScale) * 2;
        g.drawOval(pixelPositionX - pixelRadiusX/2, pixelPositionY - pixelRadiusY/2, pixelRadiusX, pixelRadiusY);
    }

    private void fillCircle(Graphics2D g, Circle circle){
        int pixelPositionX = (int) (this.getWidth()/2 + circle.getPosition().getX()*realXScale + translationX);
        int pixelPositionY = (int) (this.getHeight()/2 - circle.getPosition().getY()*realYScale - translationY - r/2*realYScale);
        int pixelRadiusX = (int) (circle.getRadius()*realXScale) * 2;
        int pixelRadiusY = (int) (circle.getRadius()*realYScale) * 2;
        g.fillOval(pixelPositionX - pixelRadiusX/2, pixelPositionY - pixelRadiusY/2, pixelRadiusX, pixelRadiusY);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        realXScale = scale * (dpi) ;
        realYScale = scale * (dpi) ;
        Graphics2D g2d = (Graphics2D) g;
        if(r != 0){
            g2d.setStroke(new BasicStroke(2));
            Circle one = new Circle(-r,0,r);
            Circle two = new Circle(r,0,r);
            Point zeroPoint = one.getIntercetions(two).get(0);
            if(Double.isNaN(zeroPoint.getX())){
                r = r + 0.001;
                one = new Circle(-r,0,r);
                two = new Circle(r,0,r);
                zeroPoint = one.getIntercetions(two).get(0);

            }
            Circle tree = new Circle(zeroPoint.getX(), zeroPoint.getY(), r);
            Point bottomLeftPoint;
            {
                ArrayList<Point> points = one.getIntercetions(tree);
                bottomLeftPoint = points.stream()
                        .filter(point -> point.getY() < 0)
                        .findFirst().get();
            }
            Circle four = new Circle(bottomLeftPoint.getX(), bottomLeftPoint.getY(), r);
            Point bottomRightPoint;
            {
                ArrayList<Point> points = two.getIntercetions(tree);
                bottomRightPoint = points.stream()
                        .filter(point -> point.getY() < 0)
                        .findFirst().get();
            }
            Circle five = new Circle(bottomRightPoint.getX(), bottomRightPoint.getY(), r);
            Point bottomPoint;
            {
                ArrayList<Point> points = four.getIntercetions(five);
                bottomPoint = points.stream()
                        .filter(point -> point.getY() < 0)
                        .findFirst().get();
            }
            Circle six = new Circle(bottomPoint.getX(), bottomPoint.getY(), r);
            g2d.setColor(Color.green);
            fillCircle(g2d, new Circle(zeroPoint.getX() , zeroPoint.getY()-r*0.6, r*0.6));
            g2d.setColor(Color.white);
            fillCircle(g2d, one);
            fillCircle(g2d, two);
            fillCircle(g2d, six);
            g2d.setColor(Color.black);
            drawCircle(g2d, one);
            drawCircle(g2d, two);
            drawCircle(g2d, six);
            double sTriangle = 0;
            {
                double x1,y1,x2,y2,x3,y3;
                x1 = -r;
                y1 = 0;
                x2 = r;
                y2 = 0;
                x3 = bottomPoint.getX();
                y3 = bottomPoint.getY();
                double a = Math.sqrt(Math.pow(x2-x1, 2) + Math.pow(y2-y1,2));
                double b = Math.sqrt(Math.pow(x3-x2, 2) + Math.pow(y3-y2,2));
                double c = Math.sqrt(Math.pow(x1-x3, 2) + Math.pow(y1-y3,2));
                double p = (a+b+c)/2;
                sTriangle = Math.sqrt(p*(p-a)*(p-b)*(p-c));
            }
            square = sTriangle - one.getSquere()/2;
            mf.getSquareField().setText(String.valueOf(square));
            DecimalFormat df = new DecimalFormat("##.##");
            g2d.drawString("Масштаб: 1см:" + df.format(1/ scale) + "см" , this.getWidth()-120, this.getHeight()-5);
        }
        g2d.setStroke(new BasicStroke(1));
        drawAxis(g2d);
        super.repaint();
    }

    @Override
    public void setRadius(double r) {
        scale = 1;
        scale = 1;
        this.r = r;
        repaint();
    }

    @Override
    public double getRadius() {
        return r;
    }

    @Override
    public void setTranslation(int x, int y) {
        translationY += y;
        translationX -= x;
        repaint();
    }

    @Override
    public void reset() {
        scale = 1;
        translationY = 0;
        translationX = 0;
    }

    @Override
    public void setScaleDiff(double diff) {
        if(1.0 / (scale-diff) > r*10.0 || scale < 0){
            scale = 1.0 / (r*10.0);
        }else{
            scale -= diff;
        }
    }

    @Override
    public double getSquare() {
        if(r == 0){
            return 0;
        }
        return square;
    }
}