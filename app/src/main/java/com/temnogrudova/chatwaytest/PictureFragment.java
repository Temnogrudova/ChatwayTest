package com.temnogrudova.chatwaytest;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.util.ArrayList;

/**
 * Created by 123 on 26.01.2016.
 */
public class PictureFragment extends Fragment {
    private Context context;
    public interface onPictureClickListener {
        void onPictureClick();
    }

    onPictureClickListener pictureClickListener;

    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
        try {
            pictureClickListener = (onPictureClickListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString());
        }

    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.screen_picture, container, false);
        final ImageView picture = (ImageView)view.findViewById(R.id.ivPicture);
        picture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (v == view.findViewById(R.id.ivPicture)) {
                    pictureClickListener.onPictureClick();
                }
            }
        });
        Bundle bundle = this.getArguments();
        String selectedImagePath = getImage(getUri(bundle.getInt(Constants.POS)));
        picture.setImageBitmap(BitmapFactory.decodeFile(selectedImagePath));
        return view;
    }

    private String getImage(String uri) {
        String res;
        Uri selectedImageUri = Uri.parse(uri);
        String[] projection = { MediaStore.MediaColumns.DATA };
        Cursor cursor = context.getContentResolver().query(selectedImageUri, projection, null, null, null);
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);
        cursor.moveToFirst();

        res = cursor.getString(column_index);
        return res;
    }

    public String getUri(int pos){
        String res;
        SharedPreferences shre = PreferenceManager.getDefaultSharedPreferences(context);
        int size = shre.getInt(Constants.SIZE_DATA, 0);
        ArrayList<String> uris = new ArrayList<>();
        for (int i=0;i<size;i++) {
            String previouslyEncodedImage = shre.getString(Constants.IMAGE_DATA + i, "");
            if (!previouslyEncodedImage.equals("") ){
                uris.add(previouslyEncodedImage);
            }
            else
                break;
        }
        res = uris.get(pos);
        return res;
    }

    @Override
    public void onResume() {
        super.onResume();
        getView().setFocusableInTouchMode(true);
        getView().requestFocus();
        getView().setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_UP && keyCode == KeyEvent.KEYCODE_BACK) {
                    pictureClickListener.onPictureClick();
                    return true;
                }
                return false;
            }
        });
    }
}
