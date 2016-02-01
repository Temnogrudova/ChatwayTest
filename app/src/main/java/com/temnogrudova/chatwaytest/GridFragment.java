package com.temnogrudova.chatwaytest;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.content.CursorLoader;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Base64;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

/**
 * Created by 123 on 26.01.2016.
 */
public class GridFragment extends Fragment {
    private Context context;
    protected RecyclerView recyclerView;
    protected SivAdapter adapter;
    private ArrayList<String> images;
    private ArrayList<String> uris;

    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;

    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.screen_grid, container, false);
        openImages();
        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerViewPreviews);
        reloadRecyclerView();
        return view;
    }

    private void openImages() {
        SharedPreferences shre = PreferenceManager.getDefaultSharedPreferences(context);
        int size = shre.getInt(Constants.SIZE_DATA, 0);
        uris = new ArrayList<>();
        for (int i=0; i<size; i++) {
            String previouslyEncodedImage = shre.getString(Constants.IMAGE_DATA + i, "");
            if (!previouslyEncodedImage.equals("") ){
                uris.add(previouslyEncodedImage);
            }
            else
                break;
        }
        images = getImages(uris);
    }

    public ArrayList<String> getImages (ArrayList<String> uris){
        ArrayList<String> res;
        res = new ArrayList<String>();
        for (int i =0;i<uris.size();i++) {
            Uri selectedImageUri = Uri.parse(uris.get(i));
            String[] projection = {MediaStore.MediaColumns.DATA};
            Cursor cursor = context.getContentResolver().query(selectedImageUri, projection, null, null, null);
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);
            cursor.moveToFirst();
            String selectedImagePath = cursor.getString(column_index);
            res.add(selectedImagePath);
        }
        return res;
    }

    // reloads recyclerView with new options
    public void reloadRecyclerView() {
        // Recognition of what orientation is now and getting current screen width
        Display display = getActivity().getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);

        int imagePreviewSize; // for size of preview miniature
        if (this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            imagePreviewSize = size.x / Constants.COLUMNS_IN_PORTRAIT;
            recyclerView.setLayoutManager(new GridLayoutManager(context, Constants.COLUMNS_IN_PORTRAIT));
        } else {
            if (getActivity().findViewById(R.id.isTableView).getTag().equals(Constants.DEVICE)) {
                imagePreviewSize = size.x / Constants.COLUMNS_IN_LANDSCAPE;
            }else
            {
                imagePreviewSize = size.y / Constants.COLUMNS_IN_LANDSCAPE;
            }
            recyclerView.setLayoutManager(new GridLayoutManager(context, Constants.COLUMNS_IN_LANDSCAPE));
        }

        // Instantiation of new recyclerView adapter  //, manager
        adapter = new SivAdapter(context, images, imagePreviewSize);
        recyclerView.setAdapter(adapter);

    }
}
