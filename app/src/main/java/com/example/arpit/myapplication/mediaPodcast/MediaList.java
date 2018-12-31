package com.example.arpit.myapplication.mediaPodcast;

import android.Manifest;
import android.content.Intent;
import android.database.Cursor;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.example.arpit.myapplication.R;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.karumi.dexter.listener.single.PermissionListener;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class MediaList extends AppCompatActivity {

    String [] items;
    ListView songsListView;
    RecyclerView recyclerView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_media_list);
        initToolbar();
        songsListView = findViewById(R.id.songsList);
        recyclerView = findViewById(R.id.recyclerView);
        runtimePermission();
    }

    private void initToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setTitle(getString(R.string.mediaActivity));
        setSupportActionBar(toolbar);

    }

    public void runtimePermission(){
        Dexter.withActivity(this).withPermissions(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .withListener(new MultiplePermissionsListener() {
                        @Override public void onPermissionsChecked(MultiplePermissionsReport report) {
                            getSongsDetails();
                        }
                        @Override public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {/* ... */}
                    }).check();
    }

    public ArrayList<File> findSongs(File file){
        ArrayList<File> arrayList = new ArrayList<>();
        File [] files = file.listFiles();
        for (File singleFiles: files){
            if (singleFiles.isDirectory() && singleFiles.isHidden()){
                arrayList.addAll(findSongs(singleFiles));
            }
            else {
                if (singleFiles.getName().endsWith(".mp3") || singleFiles.getName().endsWith(".wav")){
                    arrayList.add(singleFiles);
                }
            }
        }
        return arrayList;
    }

    public void getSongs(){
        ArrayList<File> mySongs = findSongs(Environment.getExternalStorageDirectory());
        items= new String[mySongs.size()];

        for (int i =0;i<mySongs.size();i++){
            items[i] = mySongs.get(i).toString().replace(".mp3","").replace(".wav","");
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,android.R.layout.simple_list_item_1,items);
        songsListView.setAdapter(adapter);
    }

    public void getSongsDetails(){
        final ArrayList<SongDetails> audioList = new ArrayList<>();
        final ArrayList<String> locationList = new ArrayList<>();
//        final ArrayList<String> audioList = new ArrayList<>();

        final String[] proj = { MediaStore.Audio.Media._ID,MediaStore.Audio.Media.DISPLAY_NAME,MediaStore.Audio.Media.DATA,
                                    MediaStore.Audio.Media.ARTIST, MediaStore.Audio.Media.ALBUM_ID};// Can include more data for more details and check it.

        final Cursor audioCursor = getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, proj, null, null, null);

        if(audioCursor != null){
            if(audioCursor.moveToFirst()){
                do{
                    int audioLocation = audioCursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA);
                    int audioName = audioCursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DISPLAY_NAME);
                    int audioArtist = audioCursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST);
                    Long audioAlbumArt = audioCursor.getLong(audioCursor.getColumnIndexOrThrow(MediaStore.Audio.Albums.ALBUM_ID));
                    String location = audioCursor.getString(audioLocation);
                    String name = audioCursor.getString(audioName);
                    String artist = audioCursor.getString(audioArtist);
                    //String albumArt = audioCursor.getString(audioAlbumArt);
//                    audioList.add(audioCursor.getString(audioLocation));
                    audioList.add(new SongDetails(name,artist,location,audioAlbumArt));
                    locationList.add(location);
                }while(audioCursor.moveToNext());
            }
        }
        audioCursor.close();

//        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,android.R.layout.simple_list_item_1,android.R.id.text1, audioList);

        CustomAdapter adapter = new CustomAdapter(this,R.layout.list_view_items,audioList);
        songsListView.setAdapter(adapter);

        CustomMediaAdapter mediaAdapter = new CustomMediaAdapter(this,audioList);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext(),LinearLayoutManager.HORIZONTAL,false);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(mediaAdapter);

        songsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
           @Override
           public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

//               String songName = songsList.getItemAtPosition(position).toString();
               String songName = audioList.get(position).songName;
               String location = audioList.get(position).location;
               String artist = audioList.get(position).artist;
               Long album = audioList.get(position).albumArtId;
               startActivity(new Intent(getApplicationContext(),AnotherMedia.class)
                       .putExtra("location",location).putExtra("songsName",songName)
               .putExtra("pos",position).putExtra("artist",artist)
               .putExtra("album",album).putExtra("loc",locationList)
               .putExtra("audio",audioList));
           }
       });
    }

}
