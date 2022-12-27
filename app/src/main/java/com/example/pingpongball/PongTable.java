package com.example.pingpongball;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;

import java.util.Random;

//Here we added SurfaceView class so it can render animation loop and time too
public class PongTable extends SurfaceView implements SurfaceHolder.Callback {

    private GameThread mGame;
    private TextView mStatus;
    private TextView mScorePlayer;
    private TextView mScoreOpponent;


    //created the private members
    // we created objects of the classes here
    // need here to draw in this class
    private Player mPlayer;
    private Player mOpponent;
    private Ball mBall;
    private Paint mNetPaint;
    private Paint mTableBoundsPaint;

    //created the private members
    // we created objects of the classes here
    // need here to draw in this class
    private int mTableWidth;
    private int mTableHeight;

    private Context mContext;

    SurfaceHolder mHolder;

    // we initialize the speed of racket and ball in this variable
    public static float PHY_RACQUET_SPEED = 15.0f;
    public static float PHY_BALL_SPEED = 15.0f;

    private float mAiMoveProbability;

    private boolean moving = false;
    private float mLastTouchY;

    //PongTable method
    // initialize PongTable and setup and draw all things like player , center line,rectangle ,PongTable etc
    public void initPongTable(Context ctx, AttributeSet attr){

        mContext = ctx;
        mHolder = getHolder();
        mHolder.addCallback(this);

        //Game Thread and Game Loop initialize
        mGame = new GameThread(this.getContext(),mHolder,this,
                new Handler(Looper.myLooper()){

            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                mStatus.setVisibility(msg.getData().getInt("visibility"));
                mStatus.setText(msg.getData().getString("text"));
            }
        },
                new Handler(Looper.myLooper()){

            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                mScorePlayer.setText(msg.getData().getString("player"));
                mScoreOpponent.setText(msg.getData().getString("opponent"));
            }
        });


        TypedArray a = ctx.obtainStyledAttributes(attr,R.styleable.PongTable);

        //we initialized the dimensions of racket height and width ,ball radius
        int racketHeight = a.getInteger(R.styleable.PongTable_racketHeight,340);
        int racketWidth = a.getInteger(R.styleable.PongTable_racketWidth,100);
        int ballRadius= a.getInteger(R.styleable.PongTable_ballRadius,25);

        // Set Player
        Paint playerPaint = new Paint();
        playerPaint.setAntiAlias(true);
        playerPaint.setColor(ContextCompat.getColor(mContext,R.color.player_color));
        mPlayer = new Player(racketWidth,racketHeight,playerPaint);

        // Set Opponent
        Paint opponentPaint = new Paint();
        opponentPaint.setAntiAlias(true);
        opponentPaint.setColor(ContextCompat.getColor(mContext,R.color.opponent_color));
        mOpponent = new Player(racketWidth,racketHeight,opponentPaint);

        // Set Ball
        Paint ballPaint = new Paint();
        ballPaint.setAntiAlias(true);
        ballPaint.setColor(ContextCompat.getColor(mContext,R.color.ball_color));
        mBall = new Ball(ballRadius,ballPaint);

        // Draw Middle lines
        mNetPaint = new Paint();
        mNetPaint.setAntiAlias(true);
        mNetPaint.setColor(Color.WHITE);
        mNetPaint.setAlpha(80);
        mNetPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        mNetPaint.setStrokeWidth(10.f);
        mNetPaint.setPathEffect(new DashPathEffect(new float[]{5,5},0));

        // Draw Bounds
        mTableBoundsPaint = new Paint();
        mTableBoundsPaint.setAntiAlias(true);
        mTableBoundsPaint.setColor(ContextCompat.getColor(mContext,R.color.table_color));
        mTableBoundsPaint.setStyle(Paint.Style.STROKE);
        mTableBoundsPaint.setStrokeWidth(15.f);

        mAiMoveProbability = 0.8f;

    }
    public void mscore() {
        if (getPlayer().PlayerScore == 5) {
            mGame.setState(GameThread.STATE_GameOverWin);
            return;
        } else if (getmOpponent().OpponentScore == 5) {
            mGame.setState(GameThread.STATE_GameOverLoss);
            return;
        }
    }

    public void WinJump(){
        Intent intent = new Intent(mContext, GameOverWin.class);
        mContext.startActivity(intent);
    }

    public void LossJump(){
        Intent intent = new Intent(mContext, GameOverLoss.class);
        mContext.startActivity(intent);
    }


    //we use this method to draw all things with the help of canvas
    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);

        //with the help of canvas ,we will draw the table color,rectangle bounds,
        canvas.drawColor(ContextCompat.getColor(mContext,R.color.table_color));
        canvas.drawRect(0,0,mTableWidth,mTableHeight,mTableBoundsPaint);

        //this help to draw the middle line (net of the table) by divide the width of table
        int middle = mTableWidth/2;
        canvas.drawLine(middle,1,middle,mTableHeight-1,mNetPaint);


        mGame.setScoreText(String.valueOf(mPlayer.PlayerScore),String.valueOf(mOpponent.OpponentScore));


     //this will draw our both players and ball by the help of canvas
        mPlayer.draw(canvas);
        mOpponent.draw(canvas);
        mBall.draw(canvas);
        mscore();

    }

    //constructor (parameterized) which we will use,when we need
    //we also initialize PongTable method in this constructor
    public PongTable(Context context, AttributeSet attrs) {
        super(context, attrs);

        initPongTable(context,attrs);

    }


    @Override
    public void surfaceCreated(SurfaceHolder surfaceHolder) {


        mGame.setRunning(true);
        mGame.start();



    }

    @Override
    public void surfaceChanged(SurfaceHolder surfaceHolder, int format, int width, int height) {

        mTableWidth = width;
        mTableHeight = height;

        mGame.setUpNewRound();



    }

    @Override
    public void surfaceDestroyed(SurfaceHolder surfaceHolder) {

        boolean retry = true;
        mGame.setRunning(false);
        while (retry){
            try {

                mGame.join();
                retry = false;

            }catch (InterruptedException e){
                e.printStackTrace();
            }
        }


    }

    //Here it detect the AI racket its position by ball y coordinate
    //if ball's y coordinate is less then AI top bound then it move up
    //else if ball's y coordinate is more then AI bottom bound then it move down
    private void doAI(){

        if (mOpponent.bounds.top > mBall.cy){
            movePlayer(mOpponent,
                    mOpponent.bounds.left,
                    mOpponent.bounds.top - PHY_RACQUET_SPEED);
        }else if (mOpponent.bounds.top + mOpponent.getRacquetHeight() < mBall.cy){
            movePlayer(mOpponent,
                    mOpponent.bounds.left,
                    mOpponent.bounds.top + PHY_RACQUET_SPEED);
        }

    }








    public void update(Canvas canvas){

       //here we check all types of collisions
        //1)if ball will hit on player racket,it will detect collision
        //2)if ball will hit on opponent-player racket,it will detect collision
        //3)if ball will hit on top or bottom wall,it will detect collision and change the velocity(reflect the ball)
        //4)if ball will hit on left wall,it will detect collision and show the thread of game lose
        //5)if ball will hit on right wall,it will detect collision and ahow the thread of game win
        if (checkCollisionPlayer(mPlayer,mBall)){
            handleCollision(mPlayer,mBall);
        }else if (checkCollisionPlayer(mOpponent,mBall)){
            handleCollision(mOpponent,mBall);
        }else if (checkCollisionWithTopOrBottomWall()){
            mBall.velocity_y = -mBall.velocity_y;
        }else if (checkCollisionWithLeftWall()){
            mGame.setState(GameThread.STATE_LOSE);
            return;
        }else if (checkCollisionWithRightWall()){
            mGame.setState(GameThread.STATE_WIN);
            return;
        }



        if (new Random(System.currentTimeMillis()).nextFloat() < mAiMoveProbability)
            doAI();
      //this will move the ball in the canvas by calling moveBall method
        mBall.moveBall(canvas);

    }

  //if the ball get hit on all side of the racket then ,we will detect collision
    //all side means (top,left,right,bottom) of the racket
    private boolean checkCollisionPlayer(Player player,Ball ball){

        return player.bounds.intersects(
                ball.cx - ball.getRadius(),
                ball.cy - ball.getRadius(),
                ball.cx + ball.getRadius(),
                ball.cy + ball.getRadius()
        );

    }

    //if the ball hit on top edge or bottom edge then,it will detect the collision
    private boolean checkCollisionWithTopOrBottomWall(){
        //if the ball's y coordinate is less the ball radius
        //then it detect collision at left wall
        return ((mBall.cy <= mBall.getRadius()) || (mBall.cy + mBall.getRadius() >= mTableHeight -1));
    }

    //if the ball's x coordinate is less the ball radius
    //then it detect collision at left wall
    private boolean checkCollisionWithLeftWall(){
        return mBall.cx <= mBall.getRadius();
    }


    private boolean checkCollisionWithRightWall(){
        return mBall.cx + mBall.getRadius() >= mTableWidth -1;
    }


    private void handleCollision(Player player,Ball ball){
        //here ball's x coordinate will reflect back ,when it will hit both the rackets
        //And slow-slow,we are increasing the velocity of the ball
        ball.velocity_x = -ball.velocity_x * 1.05f;

        if (player == mPlayer){

            ball.cx = mPlayer.bounds.right + ball.getRadius();

        }else if (player == mOpponent){

            ball.cx = mOpponent.bounds.left - ball.getRadius();

         //if we increase the speed of ball ,then we have to increase racket speed too
         // racket speed is need for AI racket movement
            PHY_RACQUET_SPEED = PHY_RACQUET_SPEED * 1.05f;
        }
    }



    @Override
    public boolean onTouchEvent(MotionEvent event) {


        if (!mGame.SensorsOn()){
            switch (event.getAction()){

                case MotionEvent.ACTION_DOWN:
                    if (mGame.isBetweenRounds()){
                        mGame.setState(GameThread.STATE_RUNNING);
                    }else {
                        if (isTouchOnRacket(event,mPlayer)){
                            moving = true;

                            mLastTouchY = event.getY();
                        }
                    }
                    break;

                case MotionEvent.ACTION_MOVE:
                    if (moving){

                        float y = event.getY();

                        float dy = y - mLastTouchY;

                        mLastTouchY = y;

                        movePlayerRacquet(dy,mPlayer);
                    }
                    break;

                case MotionEvent.ACTION_UP:
                    moving = false;
                    break;

            }
        }else {
            if (event.getAction() == MotionEvent.ACTION_DOWN){
                if (mGame.isBetweenRounds()){
                    mGame.setState(GameThread.STATE_RUNNING);
                }
            }
        }

        return true;

    }


    private boolean isTouchOnRacket(MotionEvent event,Player mPlayer){
        return mPlayer.bounds.contains(event.getX(),event.getY());
    }


    public GameThread getGame(){
        return mGame;
    }



    //by this method it actually help to move the player racket
    public void movePlayerRacquet(float dy,Player player){

        synchronized (mHolder){

            movePlayer(player,player.bounds.left,player.bounds.top + dy);
        }
    }

  //this will help to move both player and opponent racket
    public synchronized void movePlayer(Player player,float left,float top)
    {

        if (left<2){
            left = 2;

        }else if (left + player.getRacquetWidth() >= mTableWidth -2){
            left = mTableWidth - player.getRacquetWidth() - 2;
        }
        if (top <0){
            top = 0;
        }else if (top + player.getRacquetHeight()>= mTableHeight){
            top = mTableHeight - player.getRacquetHeight() - 1;
        }

        player.bounds.offsetTo(left,top);
    }

    //we will call this method,if we want reset  player rackets and ball
    //basically we will use this method for new round
    public void setupTable(){

        placeBall();
        placePlayers();
    }


   //this will set the player and opponent player in original position
    private void placePlayers(){
        mPlayer.bounds.offsetTo(2,(mTableHeight-mPlayer.getRacquetHeight())/2);
        mOpponent.bounds.offsetTo(mTableWidth-mOpponent.getRacquetWidth()-2,
                (mTableHeight - mOpponent.getRacquetHeight())/2);
    }

  //this will place the ball in original position that is center of the table
    private void placeBall(){
        mBall.cx = mTableWidth/2;
        mBall.cy = mTableHeight/2;
        mBall.velocity_y = (mBall.velocity_y / Math.abs(mBall.velocity_y) * PHY_BALL_SPEED);
        mBall.velocity_x = (mBall.velocity_x / Math.abs(mBall.velocity_x) * PHY_BALL_SPEED);
    }



    public Player getPlayer(){return mPlayer;}
    public Player getmOpponent(){return mOpponent;}
    public Ball getBall(){return mBall;}


    public void setScorePlayer(TextView view){mScorePlayer = view;}
    public void setScoreOpponent(TextView view){mScoreOpponent = view;}
    public void setStatus(TextView view){mStatus = view;}



}
