package com.example.user.notesaltarix.adapters;

import android.net.Uri;

/**
 * Класс записка. Хранит основную информацию о записке.
 * @author Николай Шлянкин
 * @version 1.0
 */
public class Note {
    /**Уникальный номер записки*/
    private int id;
    /**URI изображения, если не инициилизировано, то пустая строка*/
    private String uri;
    /**Заголовок заметки*/
    private String tittle;
    /**Текст записки*/
    private String data;
    /**Степень важности записки*/
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

    public String getData() { return data; }

    public String getTittle() { return tittle; }

    public String getUri() { return uri; }

    public int getId() { return id; }

    public void setData(String data) { this.data = data; }

    public void setTittle(String tittle) { this.tittle = tittle; }

    public void setUri(String uri) { this.uri = uri; }

}
