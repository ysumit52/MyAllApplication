package com.example.arpit.myapplication.mediaPodcast;

import android.media.MediaPlayer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.arpit.myapplication.R;

public class PolticalPlayer extends AppCompatActivity implements View.OnClickListener {

    Button play,pause,stop;
    MediaPlayer player;
    int currentPlayPostion;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_media_player);
        play = findViewById(R.id.play);
        pause = findViewById(R.id.pause);
        stop = findViewById(R.id.stop);

        play.setOnClickListener(this);
        pause.setOnClickListener(this);
        stop.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.play:
                if(player==null) {
                    player = MediaPlayer.create(getApplicationContext(), R.raw.formula_1);
                    player.start();
                }
                else if(!player.isPlaying()){
                    player.seekTo(currentPlayPostion);
                    player.start();
                }
                break;
            case R.id.pause:
                if (player!=null){
                    player.pause();
                    currentPlayPostion = player.getCurrentPosition();
                }
                break;
            case R.id.stop:
                if(player!=null){
                    player.stop();
                    player = null;
                }
                break;
        }
    }
}
