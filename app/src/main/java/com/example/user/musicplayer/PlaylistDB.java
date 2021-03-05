package com.example.user.musicplayer;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

public class PlaylistDB extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "plalistManager";
    private static final String TABLE_LIST = "LIST";
    private static final String TITLE = "TITLE";
    private static final String ARTIST = "ARTIST";
    private static final String ALBUM = "ALBUM";
    private static final String GENRE = "GENRE";
    private static final String PATH = "PATH";
    private static final  String ART = "ART";

    public PlaylistDB(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String sql = "CREATE TABLE " + TABLE_LIST + "("
                + TITLE + " TEXT,"
                + ARTIST + " TEXT,"
                + ALBUM + " TEXT,"
                + GENRE + " TEXT,"
                + PATH + " TEXT,"
                + ART + " BLOB"
                + ")";
        db.execSQL(sql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        //db.execSQL("DROP TABLE "+TABLE_LIST);
    }

    public void addTrack(Tracks tracks) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(TITLE, tracks.getTitle());
        values.put(ARTIST, tracks.getArtist());
        values.put(ALBUM, tracks.getAlbum());
        values.put(GENRE, tracks.getGenre());
        values.put(PATH, tracks.getPath());
        values.put(ART, tracks.getArt());

        db.insert(TABLE_LIST, null, values);
        db.close();
    }

    public List<Tracks> getAllTracks() {
        List<Tracks> tracksList = new ArrayList<>();
        String selectQuery = "SELECT  * FROM " + TABLE_LIST;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                Tracks tracks = new Tracks();

                tracks.setTitle(cursor.getString(0));
                tracks.setArtist(cursor.getString(1));
                tracks.setAlbum(cursor.getString(2));
                tracks.setGenre(cursor.getString(3));
                tracks.setPath(cursor.getString(4));
                tracks.setArt(cursor.getBlob(5));
                tracksList.add(tracks);
            } while (cursor.moveToNext());
        }
        return tracksList;
    }

    public void deleteTrack(Tracks tracks) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_LIST, TITLE + " =?", new String[]{tracks.getTitle()});
        db.close();
    }
}
