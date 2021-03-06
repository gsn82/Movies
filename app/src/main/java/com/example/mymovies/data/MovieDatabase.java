package com.example.mymovies.data;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

@Database(entities = {Movie.class,FavouriteMovie.class}, version = 2, exportSchema = false)
public abstract class MovieDatabase extends RoomDatabase {

    // имя базы данных
    private static final String DB_NAME = "movies.db";
    private static MovieDatabase database;
    //данную переменную используем , для синхранизации между потоками
    private static final Object LOCK = new Object();

    public static MovieDatabase getInstance(Context context) {
       // сихранизируемм потоки
        synchronized (LOCK) {
            if (database == null) {
                database = Room.databaseBuilder(context, MovieDatabase.class, DB_NAME).fallbackToDestructiveMigration().build();
            }
        }
        return database;
    }

    public abstract MovieDao movieDao();
}
