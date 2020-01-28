package com.example.user.notesaltarix;

import android.content.Intent;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.example.user.notesaltarix.adapters.NoteRecyclerAdapter;
import com.example.user.notesaltarix.database.DBhelper;

import java.util.ArrayList;

/**
 * основное Activity, в котором представлены все заметки
 * @author Николай Шлянкин
 * @version 1.0
 */
public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    private FloatingActionButton addNote;
    private RecyclerView notesRecyclerView;
    private NoteRecyclerAdapter notesAdapter;
    private ArrayList notes;
    private DBhelper dBhelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        dBhelper = new DBhelper(this);

        addNote = (FloatingActionButton)findViewById(R.id.addNote);
        notesRecyclerView = (RecyclerView)findViewById(R.id.notesRecyclerView);
        LinearLayoutManager LLmanager = new LinearLayoutManager(this);
        notesRecyclerView.setLayoutManager(LLmanager);
        notesRecyclerView.setHasFixedSize(true);
        notesRecyclerView.setItemAnimator(new DefaultItemAnimator());
        notesRecyclerView.canScrollVertically(View.SCROLL_AXIS_VERTICAL);
        notes = new ArrayList<>();
        notesAdapter = new NoteRecyclerAdapter(notes, this);
        notesRecyclerView.setAdapter(notesAdapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main_activity, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.createNoteItem:
                onClick(addNote);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateNotesRecyclerView();
    }

    /**
     * Обновляет элементы в Recycler View
     */
    private void updateNotesRecyclerView() {
        dBhelper.getAllNotes(notes);
        notesAdapter.notifyDataSetChanged();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.addNote:
                Intent intent = new Intent(MainActivity.this, CreateNoteActivity.class);
                startActivity(intent);
                break;
        }
    }
}
