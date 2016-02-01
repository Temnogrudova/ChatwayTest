package com.temnogrudova.chatwaytest;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;


public class MainActivity extends AppCompatActivity implements SivAdapter.onGridClickListener,
        PictureFragment.onPictureClickListener {
    Context context;
    GridFragment screenGrid;
    PictureFragment screenPicture;
    FragmentManager fragmentManager;
    View isTabletView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        context = this;
        isTabletView = findViewById(R.id.isTableView);
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(
                        Intent.ACTION_PICK,
                        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                intent.setType("image/*");
                startActivityForResult(
                        Intent.createChooser(intent, "Select File"),
                        Constants.SELECT_FILE);
            }
        });
        fragmentManager =  getSupportFragmentManager();
        screenGrid = new GridFragment();
        screenPicture = new PictureFragment();
    }
    @Override
    public void onStart(){
        super.onStart();
        setGridFragment();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        // getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void onSelectFromGalleryResult(Intent data){
        Uri selectedImageUri = data.getData();
        Bundle bundle = new Bundle();
        bundle.putString(Constants.LINK, selectedImageUri.toString());
        screenGrid = new GridFragment();
        screenGrid.setArguments(bundle);
        setGridFragment();
        saveImages(selectedImageUri.toString());

    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == Constants.SELECT_FILE) {
                onSelectFromGalleryResult(data);
            }
        }
    }

    @Override
    public void onItemClick(int pos) {
        Bundle bundle = new Bundle();
        bundle.putInt(Constants.POS, pos);
        screenPicture = new PictureFragment();
        screenPicture.setArguments(bundle);
        String tag = (String) isTabletView.getTag();
        if (tag.equals(Constants.DEVICE)) {
            changeFragment(R.id.content_frame, screenPicture, R.anim.from_left, R.anim.to_right );
        }
        else {
            changeFragment(R.id.content_pic, screenPicture, R.anim.from_left, R.anim.to_right );
        }
    }

    @Override
    public void onPictureClick() {
        String tag = (String) isTabletView.getTag();
        if (tag.equals(Constants.DEVICE)) {
            changeFragment(R.id.content_frame, screenGrid, R.anim.from_right, R.anim.to_left );
        }
    }



    public void saveImages(String selectedImageUri){
        SharedPreferences shre = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor edit=shre.edit();
        int newSize = shre.getInt(Constants.SIZE_DATA, 0) + 1;
        edit.putInt(Constants.SIZE_DATA, newSize);
        edit.putString(Constants.IMAGE_DATA + (newSize - 1), selectedImageUri);
        edit.commit();
    }

    public void setGridFragment(){
        fragmentManager.beginTransaction().replace(R.id.content_frame, screenGrid).addToBackStack("").commit();
    }

    public void changeFragment(int Container, Fragment FragTo, int enter_anim,int exit_anim){
        android.support.v4.app.FragmentTransaction fts = fragmentManager.beginTransaction();
        fts.setCustomAnimations(enter_anim, exit_anim);
        fts.replace(Container, FragTo, Constants.FRAG_TAG).addToBackStack("");
        fts.commit();
    }
}
