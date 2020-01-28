package com.example.user.notesaltarix;

import android.net.Uri;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.bumptech.glide.Glide;
import com.github.chrisbanes.photoview.PhotoView;

/**
 * Activity для зуминга и скролинга изображения из Activity заметки
 * @author Николай Шлянкин
 * @version 1.0
 */
public class ImageViewerActivity extends AppCompatActivity {

    private String uriString = "";

    private PhotoView photoView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_view);
        uriString = getIntent().getStringExtra("uri");
        Uri uri = Uri.parse(uriString);
        photoView = (PhotoView) findViewById(R.id.photoView);
        Glide.with(ImageViewerActivity.this)
                .load(uri.toString())
                .into(photoView);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_image_viewer_activity, menu);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.closeImage:
                onBackPressed();
                return true;
            case android.R.id.home:
                onBackPressed();
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
