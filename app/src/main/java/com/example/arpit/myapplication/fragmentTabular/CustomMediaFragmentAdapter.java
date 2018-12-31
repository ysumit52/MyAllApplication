package com.example.arpit.myapplication.fragmentTabular;

import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.arpit.myapplication.R;
import com.example.arpit.myapplication.mediaPodcast.AnotherMedia;
import com.example.arpit.myapplication.mediaPodcast.SongDetails;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;

public class CustomMediaFragmentAdapter extends RecyclerView.Adapter<CustomMediaFragmentAdapter.MyViewHolder> {


    private Context context;
    private ArrayList<SongDetails> songList;
    public CustomMediaFragmentAdapter(@NonNull Context context,  ArrayList<SongDetails> songDetails) {
        songList = songDetails;
        this.context = context;
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        TextView songText;
        ImageView songImage;
        private MyViewHolder(@NonNull View itemView) {
            super(itemView);
            songText = itemView.findViewById(R.id.FragmentCardTextView);
            songImage = itemView.findViewById(R.id.FragmentCardImageView);
        }
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.cardview_fragment_horizontal, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder myViewHolder, final int position) {
        InputStream inputStream = null;
        myViewHolder.songText.setText(songList.get(position).getSongName());
        Long imageDetails = songList.get(position).getAlbumArtId();
        Uri imageUri = Uri.parse("content://media/external/audio/albumart");
        Uri albumArtUri = ContentUris.withAppendedId(imageUri, imageDetails);
        BitmapFactory.Options bitmapOptions = new BitmapFactory.Options();
        bitmapOptions.inSampleSize = 2;
        try {
            inputStream = context.getContentResolver().openInputStream(albumArtUri);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        Bitmap scaledBitmap = BitmapFactory.decodeStream(inputStream, null, bitmapOptions);
        if (scaledBitmap!=null) myViewHolder.songImage.setImageBitmap(scaledBitmap);
        else myViewHolder.songImage.setImageResource(R.drawable.background);

        myViewHolder.songImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                medaiFilesFinder(position);
            }
        });

        myViewHolder.songText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                medaiFilesFinder(position);
            }
        });

    }

    public void medaiFilesFinder(int position){
        Toast.makeText(context,songList.get(position).getSongName(),Toast.LENGTH_SHORT).show();
        String songName = songList.get(position).getSongName();
        String location = songList.get(position).getLocation();
        ArrayList<String> loc = new ArrayList<>();
        for (int i=0;i<songList.size();i++){
            loc.add(songList.get(i).getLocation());
        }
        String artist = songList.get(position).getArtist();
        Long album = songList.get(position).getAlbumArtId();
        context.startActivity(new Intent(context,AnotherMedia.class)
                .putExtra("location",location).putExtra("songsName",songName)
                .putExtra("pos",position).putExtra("artist",artist)
                .putExtra("album",album).putExtra("loc",loc)
                .putExtra("audio",songList).putExtra("thread",false));
    }



    @Override
    public int getItemCount() {
        return songList.size();
    }


}
