package com.example.mymovies.data;

import android.app.Application;
import android.os.AsyncTask;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.List;
import java.util.concurrent.ExecutionException;

public class MainViewModel extends AndroidViewModel {

    private static MovieDatabase database;
    // список всех фильмов
    private LiveData<List<Movie>> movies;
    // список всех ваворитных фильмов
    private LiveData<List<FavouriteMovie>> favouriteMovies;


    public MainViewModel(@NonNull Application application) {
        super(application);
        database = MovieDatabase.getInstance(getApplication());
        movies = database.movieDao().getAllMovies();
        favouriteMovies = database.movieDao().getAllFavouriteMovies();

    }

    /// возвращаем фильм по id
    public Movie getMovieById(int id) {
        try {
            // запускаем новый поток , и запрашиваем фильм по id
            return new GetMovieTask().execute(id).get();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return null;
    }


    /// возвращаем фильм по id
    public FavouriteMovie getFavouriteMovieById(int id) {
        try {
            // запускаем новый поток , и запрашиваем фильм по id
            return new GetFavouriteMovieTask().execute(id).get();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return null;
    }

    public LiveData<List<Movie>> getMovies() {
        return movies;
    }

    public LiveData<List<FavouriteMovie>> getFavouriteMovies() {
        return favouriteMovies;
    }

    // для добавления фильма
    public void insertMovie(Movie movie) {
        new InsertTask().execute(movie);
    }

    // для добавления фильма
    public void insertFavouriteMovie(FavouriteMovie favouriteMovie) {
        new InsertFavouriteTask().execute(favouriteMovie);
    }

    // для удаления одного фильма
    public void deleteMovie(Movie movie) {
        new DeleteTask().execute(movie);
    }

    // для удаления одного фильма
    public void deleteFavouriteMovie(FavouriteMovie favouriteMovie) {
        new DeleteFavouriteTask().execute(favouriteMovie);
    }

    // для удаления всей базы фильмов
    public void deleteAllMovies() {
        new DeleteMoviesTask().execute();
    }

    // !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!! т.к мы не можем делать в основном потоке , приходится создавать в другие  потоки
    // для удаления одного фильма
    private static class DeleteTask extends AsyncTask<Movie, Void, Void> {
        @Override
        protected Void doInBackground(Movie... movies) {
            if (movies != null && movies.length > 0) {
                database.movieDao().deleteMovie(movies[0]);
            }
            return null;
        }
    }

    // для удаления одного фильма
    private static class DeleteFavouriteTask extends AsyncTask<FavouriteMovie, Void, Void> {
        @Override
        protected Void doInBackground(FavouriteMovie... favouriteMovie) {
            database.movieDao().deleteFavouriteMovie(favouriteMovie[0]);

            return null;
        }
    }

    // для добавления фильма
    private static class InsertTask extends AsyncTask<Movie, Void, Void> {
        @Override
        protected Void doInBackground(Movie... movies) {
            if (movies != null && movies.length > 0) {
                database.movieDao().insertMovie(movies[0]);
            }
            return null;
        }
    }

    // для добавления фильма
    private static class InsertFavouriteTask extends AsyncTask<FavouriteMovie, Void, Void> {
        @Override
        protected Void doInBackground(FavouriteMovie... favouriteMovie) {
            if (favouriteMovie != null && favouriteMovie.length > 0) {
                database.movieDao().insertFavouriteMovie(favouriteMovie[0]);
            }
            return null;
        }
    }

    // для удаления всей базы фильмов
    private static class DeleteMoviesTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... integers) {
            database.movieDao().deleteAllMovies();
            return null;
        }
    }

    // для поиска фильма по id
    private static class GetMovieTask extends AsyncTask<Integer, Void, Movie> {

        @Override
        protected Movie doInBackground(Integer... integers) {
            if (integers != null && integers.length > 0) {
                /// возвращаем фильм по id
                return database.movieDao().getMovieById(integers[0]);
            }
            return null;
        }
    }

    // для поиска фильма по id
    private static class GetFavouriteMovieTask extends AsyncTask<Integer, Void, FavouriteMovie> {

        @Override
        protected FavouriteMovie doInBackground(Integer... integers) {
            if (integers != null && integers.length > 0) {
                /// возвращаем фильм по id
                return database.movieDao().getFavouriteMovieById(integers[0]);
            }
            return null;
        }
    }
}
