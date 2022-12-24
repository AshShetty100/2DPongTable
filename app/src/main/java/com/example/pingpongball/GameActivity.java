package com.example.pingpongball;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

//this is a GameActivity class(activity_game) ,in which we extend AppcompatActivity class
public class GameActivity extends AppCompatActivity {

//here we need the onCreate method,which is the part of AppCompatActivity class
 //this will help to create the Activity(activity_game)

    @Override  //(Override): we just use same method,only value will be change
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState); //by super() ,we will call the method
        setContentView(R.layout.activity_game);
        //by setContentView(),we will set the(activity_game) layout on screen
    }

 //this will open PongActivity class by click on startGame method
    public void startGame(View view) {
        Intent intent = new Intent(GameActivity.this,PongActivity.class);
        //(intent):this helps to open another activity
        startActivity(intent);//this will create the activity

    }
}