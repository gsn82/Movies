package com.example.mymovies.utils;


import com.example.mymovies.data.Movie;
import com.example.mymovies.data.Review;
import com.example.mymovies.data.Trailer;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class JSONUtils {

    private static final String KEY_RESULTS = "results";
    // Для отзывов
    private static final String KEY_AUTHOR = "author";
    private static final String KEY_CONTENT = "content";
    // Для видео
    private static final String KEY_KEY_OF_VIDEO = "key";
    private static final String KEY_NAME = "name";
    private static final String BASE_YOUTUBE_URL = "https://www.youtube.com/watch?v=";

    // вся информация о фильме
    private static final String KEY_VOTE_COUNT = "vote_count";
    private static final String KEY_ID = "id";
    private static final String KEY_TITLE = "title";
    private static final String KEY_ORIGINAL_TITLE = "original_title";
    private static final String KEY_OVERVIEW = "overview";
    private static final String KEY_POSTER_PATH = "poster_path";
    private static final String KEY_BACKDROP_PATH = "backdrop_path";
    private static final String KEY_VOTE_AVERAGE = "vote_average";
    private static final String KEY_RELEASE_DATE = "release_date";
    // базовый путь к изображению
    public static final String BASE_POSTER_URL = "https://image.tmdb.org/t/p/";
    // размер для малньго изображения
    public static final String SMALL_POSTER_SIZE = "w185";
    //размер для большого изображения
    public static final String BIG_POSTER_SIZE = "w780";


    // получаем массив Review из jsonObject
    public static ArrayList<Review> getReviewsFromJSON(JSONObject jsonObject) {
        // описываем массив
        ArrayList<Review> result = new ArrayList<>();
        if (jsonObject == null) {
            return result;
        }
        try {
            //получаем массив json
            JSONArray jsonArray = jsonObject.getJSONArray(KEY_RESULTS);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject objectReview = jsonArray.getJSONObject(i);
                // получаем нужные характеристики фильма
                String author = objectReview.getString(KEY_AUTHOR);
                String content = objectReview.getString(KEY_CONTENT);

                // создаем Review
                Review review = new Review(author,content);
                // добавляем Review в массив
                result.add(review);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return result;
    }
    // получаем массив Trailer из jsonObject
    public static ArrayList<Trailer> getTrailersFromJSON(JSONObject jsonObject) {
        // описываем массив
        ArrayList<Trailer> result = new ArrayList<>();
        if (jsonObject == null) {
            return result;
        }
        try {
            //получаем массив json
            JSONArray jsonArray = jsonObject.getJSONArray(KEY_RESULTS);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject objectTrailer = jsonArray.getJSONObject(i);
                // получаем нужные характеристики фильма
                String key = BASE_YOUTUBE_URL +  objectTrailer.getString(KEY_KEY_OF_VIDEO);
                String name = objectTrailer.getString(KEY_NAME);

                // созадем trailer
                Trailer trailer = new Trailer(key,name);
                // добавляем trailer в массив
                result.add(trailer);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return result;
    }


    // получаем массив Movie из jsonObject
    public static ArrayList<Movie> getMoviesFromJSON(JSONObject jsonObject) {
        // описываем массив
        ArrayList<Movie> result = new ArrayList<>();
        if (jsonObject == null) {
            return result;
        }
        try {
            //получаем массив json
            //находим фрагмент по ключу KEY_RESULTS
            JSONArray jsonArray = jsonObject.getJSONArray(KEY_RESULTS);
            // теперь пробигаемся по массиву
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject objectMovie = jsonArray.getJSONObject(i);
                // получаем нужные характеристики фильма
                int id = objectMovie.getInt(KEY_ID);
                int voteCount = objectMovie.getInt(KEY_VOTE_COUNT);
                String title = objectMovie.getString(KEY_TITLE);
                String originalTitle = objectMovie.getString(KEY_ORIGINAL_TITLE);
                String overview = objectMovie.getString(KEY_OVERVIEW);
                String posterPath = BASE_POSTER_URL + SMALL_POSTER_SIZE + objectMovie.getString(KEY_POSTER_PATH);
                String bigPosterPath = BASE_POSTER_URL + BIG_POSTER_SIZE + objectMovie.getString(KEY_POSTER_PATH);
                String backdropPath = objectMovie.getString(KEY_BACKDROP_PATH);
                double voteAverage = objectMovie.getDouble(KEY_VOTE_AVERAGE);
                String releaseDate = objectMovie.getString(KEY_RELEASE_DATE);
                // создаем фильм
                Movie movie = new Movie(id, voteCount, title, originalTitle, overview, posterPath, bigPosterPath, backdropPath, voteAverage, releaseDate);
                // добавляем фильм в массив
                result.add(movie);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        // возвращаем массив фильмов
        return result;
    }

}
