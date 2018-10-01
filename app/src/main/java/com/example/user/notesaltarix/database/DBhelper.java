package com.example.user.notesaltarix.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.user.notesaltarix.adapters.Note;

import java.util.ArrayList;
import java.util.List;

/**
 * Класс для работы с SQLite базой данных
 * @author Николай Шлянкин
 * @version 1.0
 */
public class DBhelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "notesManager";
    private static final String TABLE_NOTES = "notes";
    private static final String TITTLE = "tittle";
    private static final String KEY_ID = "id";
    private static final String DATA = "data";
    private static final String URI = "uri";
    private static final String IMPORTANT = "important";

    /**
     * Конструктор класса
     * @param context context, из которого вызывается класс*/
    public DBhelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_NOTES_TABLE = "CREATE TABLE " + TABLE_NOTES + "("
                + KEY_ID + " INTEGER PRIMARY KEY," + TITTLE + " TEXT,"
                + DATA + " TEXT," + URI + " TEXT," + IMPORTANT + " INTEGER" + ")";
        db.execSQL(CREATE_NOTES_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NOTES);

        onCreate(db);
    }

    /**Добавляет заметку в БД
     *@param note добавляемая заметка
     */
    public void addNote(Note note) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(TITTLE, note.getTittle());
        cv.put(DATA, note.getData());
        cv.put(URI, note.getUri());
        cv.put(IMPORTANT, note.getImportant());
        db.insert(TABLE_NOTES, null, cv);
        db.close();
    }

    /**
     * Получить заметку
     * @param id уникальный номер возвращаемой заметки
     */
    public Note getNote(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_NOTES, new String[] { KEY_ID,
                        TITTLE, DATA, URI, IMPORTANT }, KEY_ID + "=?",
                new String[] { String.valueOf(id) }, null, null, null, null);

        if (cursor != null){
            cursor.moveToFirst();
        }
        return new Note(cursor.getString(1), cursor.getString(2), cursor.getString(3), cursor.getInt(0), cursor.getInt(4));
    }

    /**
     * Получить все заметки из базы данных
     * @param notesList структура, в которую записываются заметки
     */
    public void getAllNotes(List <Note> notesList) {
        notesList.clear();
        String selectQuery = "SELECT  * FROM " + TABLE_NOTES;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            do {
                Note note = new Note(cursor.getString(1), cursor.getString(2), cursor.getString(3), cursor.getInt(0), cursor.getInt(4));
                notesList.add(note);
            } while (cursor.moveToNext());
        }
    }

    /**
     * Обновляет заметку в БД
     * @param updNote обновленный класс заметки
     * @param id уникальный номер обновляемой заметки
     */
    public void updateNote(Note updNote, int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(TITTLE, updNote.getTittle());
        cv.put(DATA, updNote.getData());
        cv.put(URI, updNote.getUri());
        cv.put(IMPORTANT, updNote.getImportant());
        db.update(TABLE_NOTES, cv, KEY_ID + " = ?", new String[] { String.valueOf(id) });
        db.close();
    }

    /**
     * Удалить заметку
     * @param id уникальный номер удаляемой заметки
     */
    public void deleteNote(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_NOTES, KEY_ID + " = ?", new String[] { String.valueOf(id) });
        db.close();
    }

    /**
     * Удалить базу данных
     */
    public void deleteAll() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_NOTES, null, null);
        db.close();
    }

    /**
     * @return количество заметок в БД
     */
    public int getNotesCount() {
        String countQuery = "SELECT  * FROM " + TABLE_NOTES;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        cursor.close();

        return cursor.getCount();
    }
}
