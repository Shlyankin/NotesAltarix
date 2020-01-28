package com.example.user.notesaltarix;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.user.notesaltarix.adapters.Note;
import com.example.user.notesaltarix.database.DBhelper;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Activity, в котором отображена вся информация о выбранной записке
 * @author Николай Шлянкин
 * @version 1.0
 */
public class NoteActivity extends AppCompatActivity implements View.OnClickListener{

    private ImageView imageView;
    private TextView tittleView, dataView;
    private FloatingActionButton editNoteButton, deleteNoteButton;

    private DBhelper dBhelper;

    private Note currentNote;
    int id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note);

        dBhelper = new DBhelper(this);

        final Intent intent = getIntent();
        id =  intent.getIntExtra("id", -1);
        if(id != -1) {
            currentNote = dBhelper.getNote(id);
        } else {
            Toast.makeText(this, "Note not found. Try again later...", Toast.LENGTH_LONG);
            onBackPressed();
        }


        imageView = (ImageView) findViewById(R.id.imageView3);
        tittleView = (TextView) findViewById(R.id.tittleView);
        dataView = (TextView) findViewById(R.id.dataView);
        editNoteButton = (FloatingActionButton) findViewById(R.id.editNoteButton);
        deleteNoteButton = (FloatingActionButton) findViewById(R.id.deleteNoteButton);

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent imageIntent = new Intent(NoteActivity.this, ImageViewerActivity.class);
                imageIntent.putExtra("uri", currentNote.getUri());
                startActivity(imageIntent);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_note_activity, menu);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.editItem:
                onClick(editNoteButton);
                return true;
            case R.id.deleteItem:
                onClick(deleteNoteButton);
                return true;
            case R.id.shareItem:
                Intent shareIntent = new Intent(Intent.ACTION_SEND);

                shareIntent.putExtra(Intent.EXTRA_TITLE, currentNote.getTittle());
                shareIntent.putExtra(Intent.EXTRA_TEXT, currentNote.getTittle() + '\n' + currentNote.getData());
                shareIntent.putExtra(Intent.EXTRA_STREAM, Uri.parse(currentNote.getUri()));
                shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                shareIntent.setType("*/*");

                startActivity(Intent.createChooser(shareIntent, "Select app for sharing"));
                return true;
            case R.id.writeFileItem:
                writeFile(currentNote);
                return true;
            case android.R.id.home:
                onBackPressed();
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void writeFile(Note note) {
        try {
            String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());//получаем время
            String imageFileName = note.getTittle() + timeStamp;//состовляем имя файла
            //Создаём файл

            File file = File.createTempFile(
                    imageFileName,  /* prefix */
                    ".txt",         /* suffix */
                    getExternalCacheDir()      /* directory */
            );

            FileOutputStream fos = new FileOutputStream(file);
            OutputStreamWriter writer = new OutputStreamWriter(fos);
            String s = note.getTittle() + "\n" + note.getData();
            writer.write(s);
            writer.flush();
            writer.close();
            Toast.makeText(this, "File directory: " + file.getAbsolutePath(), Toast.LENGTH_SHORT).show();
        } catch (FileNotFoundException e) {
            Toast.makeText(this, "FileWrite Error", Toast.LENGTH_SHORT).show();
        }
        catch (IOException e) {
            Toast.makeText(this, "FileWrite Error", Toast.LENGTH_SHORT).show();
        }
    }

    private void updateViews() {
        tittleView.setText(currentNote.getTittle());
        dataView.setText(currentNote.getData());
        if(currentNote.getUri() != null && !currentNote.getUri().equals("")) {
            Glide.with(this)
                    .load(currentNote.getUri())
                    .into(imageView);
        }
    }

    @Override
    protected void onResume() {
        currentNote = dBhelper.getNote(id);
        updateViews();
        super.onResume();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.editNoteButton:
                Intent intentEditNote = new Intent(NoteActivity.this, EditNoteActivity.class);
                intentEditNote.putExtra("id", currentNote.getId());
                startActivity(intentEditNote);
                break;
            case R.id.deleteNoteButton:
                if(currentNote.getId() != -1) {
                    new AlertDialog.Builder(this)
                            .setTitle("Delete note?")
                            .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            dBhelper.deleteNote(currentNote.getId());
                                            onBackPressed();
                                        }
                                    })
                            .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {

                                }
                            }).show();
                } else {
                    Toast.makeText(this, "Note is not deleted. Try again later...", Toast.LENGTH_LONG).show();
                }
                break;
        }
    }
}
