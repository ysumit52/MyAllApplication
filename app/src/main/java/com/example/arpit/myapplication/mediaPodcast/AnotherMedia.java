package com.example.arpit.myapplication.mediaPodcast;

import android.annotation.SuppressLint;
import android.app.Application;
import android.app.Notification;
import android.content.ContentUris;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;

import com.example.arpit.myapplication.R;
import com.mikhaellopez.circularimageview.CircularImageView;

import java.util.ArrayList;

import static com.example.arpit.myapplication.notificationBar.NotificationBarHeader.Channel_Id;


public class AnotherMedia extends AppCompatActivity implements View.OnClickListener {

    TextView songNameLabel, artistNameLabel;
    Button btnNext,btnPrevious,btnPlay;
    SeekBar seekBar;
    CircularImageView imageAlbum;
    String locationName, songDetailsName, artistName;
    int currentPlayPostion;
    Long imageArtAlbumId;
    static MediaPlayer myMediaPlayer;
    int currentPlayPosition;
    int position;
    Uri uri,mediaUri;
    int totalDuration;
    //ArrayList mySongs;
    ArrayList<String> mySongs;
    ArrayList<SongDetails> mySongs2;
    Thread updateSeekBar;
    Intent intent ;
    private NotificationManagerCompat notificationManager;
    @SuppressLint("NewApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_another_media);
        initToolbar();
        intent = getIntent();
        notificationManager = NotificationManagerCompat.from(this);
        imageAlbum = findViewById(R.id.imageAlbum);
        songNameLabel = findViewById(R.id.songName);
        artistNameLabel = findViewById(R.id.artistName);
        btnNext = findViewById(R.id.next);
        btnNext.setOnClickListener(this);
        btnPlay = findViewById(R.id.playNew);
        btnPlay.setOnClickListener(this);
        btnPrevious = findViewById(R.id.previous);
        btnPrevious.setOnClickListener(this);
        mySongs = new ArrayList<>();
        seekBar = findViewById(R.id.seekBar);

        if(myMediaPlayer !=null){
            myMediaPlayer.stop();
            myMediaPlayer.release();
        }

        updateSeekBar = new Thread() {
            @Override
            public void run() {
                   totalDuration = myMediaPlayer.getDuration();

                int currentPosition = 0;
                 while (currentPosition<totalDuration){
                     try {
                        sleep(500);
                        if (myMediaPlayer!=null){
                            
                            currentPosition = myMediaPlayer.getCurrentPosition();
                        }
                        else currentPosition = 0;
                         if (seekBar != null) {
                             seekBar.setProgress(currentPosition);
                         }

                     }
                     catch (InterruptedException e){
                         e.printStackTrace();
                         myMediaPlayer.stop();
                         myMediaPlayer.release();

                     }
                 }
            }
        };





        locationName = intent.getStringExtra("location");
        songDetailsName = intent.getStringExtra("songsName");
        artistName = intent.getStringExtra("artist");
        imageArtAlbumId = intent.getLongExtra("album",0);
        mySongs = intent.getStringArrayListExtra("loc");
        mySongs2 = (ArrayList) intent.getParcelableArrayListExtra("audio");
        Log.e("Details About :",mySongs2.toString());
        try{
            position = intent.getIntExtra("pos",0);
            //mySongs = (ArrayList) intent.getParcelableArrayListExtra("mySongs");
        }catch (NullPointerException e){
            e.printStackTrace();
        }


        songNameLabel.setText(songDetailsName);
        if(artistName!=null) artistNameLabel.setText(artistName);
        else artistNameLabel.setText(R.string.defaultArtistName);
        songNameLabel.setSelected(true);
        imageSetter(imageArtAlbumId);

       // imageUri = Uri.parse(imageArtAlbum);


        //uri = Uri.parse(mySongs.get(position).toString());
        uri = Uri.parse(locationName);
        myMediaPlayer = MediaPlayer.create(getApplicationContext(),uri);

//        myMediaPlayer = new MediaPlayer();
//        myMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
//
//        try {
//            myMediaPlayer.setDataSource(getApplicationContext(),uri);
//            myMediaPlayer.prepare();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
        myMediaPlayer.start();
        seekBar.setMax(myMediaPlayer.getDuration());
        updateSeekBar.start();
//        seekBar.getProgressDrawable().setColorFilter(getResources().getColor(R.color.colorPrimary),PorterDuff.Mode.MULTIPLY);
//        seekBar.getThumb().setColorFilter(getResources().getColor(R.color.colorPrimary),PorterDuff.Mode.SRC_IN);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            myMediaPlayer.seekTo(seekBar.getProgress());
            }
        });

        myMediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                playNext();
            }
        });

    }

    private void initToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setTitle(getString(R.string.playingSong));
        setSupportActionBar(toolbar);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.previous:
                playPrevious();
                break;
            case R.id.playNew:
                seekBar.setMax(myMediaPlayer.getDuration());
                if(myMediaPlayer.isPlaying()){
                    btnPlay.setBackgroundResource(R.drawable.play);
                    myMediaPlayer.pause();
                    currentPlayPostion = myMediaPlayer.getCurrentPosition();
                }
                else {
                    btnPlay.setBackgroundResource(R.drawable.pause);
                    myMediaPlayer.seekTo(currentPlayPostion);
                    myMediaPlayer.start();
                }
                break;
            case R.id.next:
                playNext();
                break;
        }
    }

    private void imageSetter(Long id){
        Uri imageUri = Uri.parse("content://media/external/audio/albumart");
        Uri albumArtUri = ContentUris.withAppendedId(imageUri, id);
        imageAlbum.setImageURI(albumArtUri);
        if(imageAlbum.getDrawable()==null) imageAlbum.setImageResource(R.drawable.album_art);
    }

    private void playNext(){
        myMediaPlayer.stop();
        myMediaPlayer.release();
        position =(position+1)%mySongs.size();
        mediaUri = Uri.parse(mySongs.get(position));
        myMediaPlayer = MediaPlayer.create(getApplicationContext(),mediaUri);
        songDetailsName = mySongs2.get(position).songName;
        songNameLabel.setText(songDetailsName);
        imageArtAlbumId = mySongs2.get(position).albumArtId;
        imageSetter(imageArtAlbumId);
        seekBar.setMax(myMediaPlayer.getDuration());
        int pos = myMediaPlayer.getCurrentPosition();
        seekBar.setProgress(pos);
        myMediaPlayer.start();
    }

    private void playPrevious(){
        myMediaPlayer.stop();
        myMediaPlayer.release();
        position =((position-1)<0)?(mySongs.size()-1):(position-1);
        mediaUri = Uri.parse(mySongs.get(position));
        myMediaPlayer = MediaPlayer.create(getApplicationContext(),mediaUri);
        songDetailsName = mySongs2.get(position).songName;
        songNameLabel.setText(songDetailsName);
        imageArtAlbumId = mySongs2.get(position).albumArtId;
        imageSetter(imageArtAlbumId);
        seekBar.setMax(myMediaPlayer.getDuration());
        int pos = myMediaPlayer.getCurrentPosition();
        seekBar.setProgress(pos);
        myMediaPlayer.start();

    }

    @Override
    public void onBackPressed() {
    Notification notification = new NotificationCompat.Builder(this,Channel_Id)
            .setSmallIcon(R.drawable.pause)
            .setContentTitle("My Application")
            .setContentText(songDetailsName)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setCategory(NotificationCompat.CATEGORY_MESSAGE)
            .build();

        notificationManager.notify(1, notification);
        super.onBackPressed();
    }
}
