package com.example.pingpongball;

import android.graphics.Canvas;
import android.graphics.Paint;

public class Ball {

    //here we declared the variables of coordinates x and y of the ball
    //variables of velocity x and y of the ball
    //public variables
    public float cx;
    public float cy;
    public float velocity_x;
    public float velocity_y;

    //we need radius and paint to draw the ball ,so we declared that variables
    //private variables
    private int radius;
    private Paint paint;

    //we create a constructor(parameterized) to get the value of radius and paint
    public Ball(int radius,Paint paint) {
        this.radius = radius;
        this.paint = paint;
        this.velocity_x = PongTable.PHY_BALL_SPEED;
        this.velocity_y = PongTable.PHY_BALL_SPEED;

    }

    //this will draw the ball by canvas (class)
    //we will use this draw method in PongTable class
    public void draw(Canvas canvas){
        canvas.drawCircle(cx,cy,radius,paint);
    }

    //for the movement of the ball by ball coordinate and velocity
    //basically we are adding velocity to coordinate ,so it can move to the next coordinate
    //It is working on physics of ball move
    public void moveBall(Canvas canvas){
        cx+=velocity_x;
        cy+=velocity_y;

        //collision detect by condition statement
        //if the ball hit on top edge then,it will stop there and re-position the ball
        if(cy<radius){
            cy=radius;
        }
        //if the ball hit on bottom edge then,it will stop there and re-position the ball
        else if (cy+radius>= canvas.getHeight()){
            cy= canvas.getHeight()-radius-1;
        }
    }

    //this variable is private in this class,so to get value in other class,so we made this method
    public int getRadius() {
        return radius;
    }

}
