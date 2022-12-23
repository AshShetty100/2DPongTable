package com.example.pingpongball;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;

public class Player {
   //we declared the members(variables and objects)
   //3 members are private and 2 members public
    private int racquetWidth;
    private int racquetHeight;
    public int score;
    private Paint paint;

    public RectF bounds;

    //we created the constructor(parameterized),so we can pass the value of this variable
    // from other class by object to this class
    //and that value will initialized by local var to instance var
    public Player(int racquetWidth, int racquetHeight, Paint paint) {
        this.racquetWidth = racquetWidth;
        this.racquetHeight = racquetHeight;
        this.paint = paint;
        score = 0;
        bounds = new RectF(0, 0, racquetWidth, racquetHeight);
    }

   //this will draw the player(racket) by canvas (class)
    //we will use this draw method in PongTable class
    public void draw(Canvas canvas) {
        canvas.drawRoundRect(bounds, 5, 5, paint);
    }


  //this variables is private in this class,so to get or access the value in other class,we made this method
    public int getRacquetWidth() {
        return racquetWidth;
    }

    public int getRacquetHeight() {
        return racquetHeight;
    }


}