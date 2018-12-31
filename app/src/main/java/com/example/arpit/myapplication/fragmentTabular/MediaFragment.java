package com.example.arpit.myapplication.fragmentTabular;


import android.Manifest;
import android.annotation.SuppressLint;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.example.arpit.myapplication.R;
import com.example.arpit.myapplication.mediaPodcast.CustomAdapter;
import com.example.arpit.myapplication.mediaPodcast.CustomMediaAdapter;
import com.example.arpit.myapplication.mediaPodcast.SongDetails;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class MediaFragment extends Fragment {

    ListView fragmentSongsListView;
    RecyclerView fragmnetRecyclerView;
    public MediaFragment() {
        // Required empty public constructor
    }


    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_media, container, false);
//        fragmentSongsListView = rootView.findViewById(R.id.fragmentSongsList);
        fragmnetRecyclerView = rootView.findViewById(R.id.fragmentRecyclerView);
        runtimePermission();
        return rootView;
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    public void runtimePermission(){
        Dexter.withActivity(getActivity()).withPermissions(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .withListener(new MultiplePermissionsListener() {
                    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
                    @Override public void onPermissionsChecked(MultiplePermissionsReport report) {
                        getSongsDetails();
                    }
                    @Override public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {/* ... */}
                }).check();
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public void getSongsDetails(){
        final ArrayList<SongDetails> audioList = new ArrayList<>();
        final ArrayList<String> locationList = new ArrayList<>();
//        final ArrayList<String> audioList = new ArrayList<>();

        final String[] proj = { MediaStore.Audio.Media._ID,MediaStore.Audio.Media.DISPLAY_NAME,MediaStore.Audio.Media.DATA,
                MediaStore.Audio.Media.ARTIST, MediaStore.Audio.Media.ALBUM_ID};// Can include more data for more details and check it.

        try {
            @SuppressLint("Recycle") Cursor audioCursor = getActivity().getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, proj, null, null, null);
            if (audioCursor != null) {
                if (audioCursor.moveToFirst()) {
                    do {
                        int audioLocation = audioCursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA);
                        int audioName = audioCursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DISPLAY_NAME);
                        int audioArtist = audioCursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST);
                        Long audioAlbumArt = audioCursor.getLong(audioCursor.getColumnIndexOrThrow(MediaStore.Audio.Albums.ALBUM_ID));
                        String location = audioCursor.getString(audioLocation);
                        String name = audioCursor.getString(audioName);
                        String artist = audioCursor.getString(audioArtist);
                        audioList.add(new SongDetails(name, artist, location, audioAlbumArt));
                        locationList.add(location);
                    } while (audioCursor.moveToNext());
                }
            }
        }
        catch (NullPointerException e){
            Log.e("Null Pointer Exception", e.getMessage());
        }

//        CustomAdapter adapter = new CustomAdapter(getActivity(),R.layout.list_view_items,audioList);
//        fragmentSongsListView.setAdapter(adapter);

        CustomMediaFragmentAdapter mediaAdapter = new CustomMediaFragmentAdapter(getActivity(),audioList);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getContext(),LinearLayoutManager.VERTICAL,false);
        fragmnetRecyclerView.setLayoutManager(mLayoutManager);
        fragmnetRecyclerView.setItemAnimator(new DefaultItemAnimator());
        fragmnetRecyclerView.setAdapter(mediaAdapter);

//        fragmnetRecyclerView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//
////               String songName = songsList.getItemAtPosition(position).toString();
//                String songName = audioList.get(position).getSongName();
//                String location = audioList.get(position).getLocation();
//                String artist = audioList.get(position).getArtist();
//                Long album = audioList.get(position).getAlbumArtId();
//                startActivity(new Intent(getActivity(),AnotherMedia.class)
//                        .putExtra("location",location).putExtra("songsName",songName)
//                        .putExtra("pos",position).putExtra("artist",artist)
//                        .putExtra("album",album).putExtra("loc",locationList)
//                        .putExtra("audio",audioList));
//            }
//        });

    }

}
