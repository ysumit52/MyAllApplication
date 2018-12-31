package com.example.arpit.myapplication.mediaPodcast;

import android.content.ContentUris;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.arpit.myapplication.R;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;

public class CustomAdapter extends ArrayAdapter<SongDetails> {
    private Long imageDetails;
    private Uri imageUri;
    private Context context;
    InputStream inputStream;
    ArrayList<SongDetails> songList;
    int layoutId;
    private Fragment fragment;
    public CustomAdapter(@NonNull Context context, int resource, ArrayList<SongDetails> songDetails) {
        super(context, resource, songDetails);
        songList = songDetails;
        layoutId = resource;
        this.context = context;
    }


    @Override
    public int getCount() {
        return super.getCount();
    }

    RecyclerView.ViewHolder holder;
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        convertView = inflater.inflate(layoutId, null);
        TextView textView = convertView.findViewById(R.id.textView);
        textView.setText(songList.get(position).getSongName());

        ImageView imageView = convertView.findViewById(R.id.imageView);
        imageDetails = songList.get(position).getAlbumArtId();
        imageUri = Uri.parse("content://media/external/audio/albumart");
        Uri albumArtUri = ContentUris.withAppendedId(imageUri, imageDetails);
        BitmapFactory.Options bitmapOptions = new BitmapFactory.Options();
        bitmapOptions.inSampleSize = 2;
        try {
            inputStream = context.getContentResolver().openInputStream(albumArtUri);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        Bitmap scaledBitmap = BitmapFactory.decodeStream(inputStream, null, bitmapOptions);
        if (scaledBitmap!=null) imageView.setImageBitmap(scaledBitmap);
        else imageView.setImageResource(R.drawable.background);

        // imageView.setImageURI(albumArtUri);
        // if(imageView.getDrawable()==null) imageView.setImageResource(R.drawable.background);

        ImageView playView = convertView.findViewById(R.id.playImage);
        playView.setImageResource(R.drawable.play);
        return convertView;

    }
}
