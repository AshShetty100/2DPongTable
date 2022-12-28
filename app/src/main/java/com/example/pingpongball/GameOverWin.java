package com.example.pingpongball;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class GameOverWin extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_over_win);
    }
    //this will open PongActivity class by click on startGame method
    public void startGame(View view) {
        Intent intent = new Intent(GameOverWin.this,PongActivity.class);
        //(intent):this helps to open another activity
        startActivity(intent);//this will create the activity

    }
    public void Exit(View view) {
        Intent intent = new Intent(GameOverWin.this,GameActivity.class);
        //(intent):this helps to open another activity
        startActivity(intent);//this will create the activity

    }
}