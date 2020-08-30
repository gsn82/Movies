package com.example.mymovies.data;


import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface MovieDao {
    // выводим все фильмы
    @Query("SELECT * FROM movies")
    LiveData<List<Movie>> getAllMovies();

    @Query("SELECT * FROM favourite_movies")
    LiveData<List<FavouriteMovie>> getAllFavouriteMovies();

    // выводим один филь по id
    @Query("SELECT * FROM movies WHERE id == :movieId")
    Movie getMovieById(int movieId);

    // выводим один филь по id
    @Query("SELECT * FROM favourite_movies WHERE id == :movieId")
    FavouriteMovie getFavouriteMovieById(int movieId);

    // удяляем все фильмы
    @Query("DELETE FROM movies")
    void deleteAllMovies();



    @Insert
    void insertMovie(Movie movie);
    @Insert
    void insertFavouriteMovie(FavouriteMovie favouriteMovie);

    @Delete
    void deleteMovie(Movie movie);
    @Delete
    void deleteFavouriteMovie(FavouriteMovie favouriteMovie);
}
