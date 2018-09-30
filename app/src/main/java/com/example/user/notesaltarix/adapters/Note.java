package com.example.user.notesaltarix.adapters;

import android.net.Uri;

public class Note {
    private int id;
    private String uri;
    private String tittle;
    private String data;
    private int important;

    public Note() {
        uri = "";
        tittle = "";
        data = "";
    }

    public Note(Note note) {
        this.uri = note.uri;
        this.id = note.id;
        this.data = note.data;
        this.tittle = note.tittle;
        this.important = note.important;
    }

    public Note(String tittle, String data, String uri, int important) {
        this.uri = uri;
        this.tittle = tittle;
        this.data = data;
        this.id = -1;
        this.important = important;
    }

    public Note(String tittle, String data, String uri, int id, int important) {
        this.uri = uri;
        this.tittle = tittle;
        this.data = data;
        this.id = id;
        this.important = important;
    }

    public int getImportant() { return important; }

    public String getData() {
        return data;
    }

    public String getTittle() {
        return tittle;
    }

    public String getUri() {
        return uri;
    }

    public void setData(String data) {
        this.data = data;
    }

    public void setTittle(String tittle) {
        this.tittle = tittle;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public int getId() { return id; }
}
