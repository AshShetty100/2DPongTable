package com.example.pingpongball;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;

//this is PongActivity class(activity_pong) ,in which we extend AppcompatActivity class
public class PongActivity extends AppCompatActivity {

//Here we create the object of GameThread class
    private GameThread mGameThread;

    //here we need the onCreate method,which is the part of AppCompatActivity class
    //this will help to create the Activity(activity_pong)

    @Override //(Override): we just use same method,only value will be change
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState); //by super() ,we will call the method
        setContentView(R.layout.activity_pong);
        //by setContentView(),we will set the(activity_pong) layout on screen

//Here we create the object(table) of PongTable and we give the ID of XMl file by findViewById()
        final PongTable table = (PongTable) findViewById(R.id.pongTable);
        table.setScoreOpponent((TextView) findViewById(R.id.tvScoreOpponent));
        table.setScorePlayer((TextView) findViewById(R.id.tvScorePlayer));
        table.setStatus((TextView) findViewById(R.id.tvStatus));

//here we calling getGame() from PongTable class and initialize
        mGameThread = table.getGame();


    }
}