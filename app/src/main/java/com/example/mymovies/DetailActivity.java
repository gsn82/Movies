package com.example.mymovies;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mymovies.adapters.ReviewAdapter;
import com.example.mymovies.adapters.TrailerAdapter;
import com.example.mymovies.data.FavouriteMovie;
import com.example.mymovies.data.MainViewModel;
import com.example.mymovies.data.Movie;
import com.example.mymovies.data.Review;
import com.example.mymovies.data.Trailer;
import com.example.mymovies.utils.JSONUtils;
import com.example.mymovies.utils.NetworkUtils;
import com.squareup.picasso.Picasso;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Locale;

public class DetailActivity extends AppCompatActivity {

    // описываем переменные
    private ImageView imageViewBigPoster;
    private ImageView imageViewAddToFavourite;
    private TextView textViewTitle;
    private TextView textViewOriginalTitle;
    private TextView textViewRating;
    private TextView textViewReleaseDate;
    private TextView textViewOverview;

    private ScrollView scrollViewInfo;

    private int id;
    private Movie movie;
    private FavouriteMovie favouriteMovie;
    private MainViewModel viewModel;

    private RecyclerView recyclerViewTrailer;
    private TrailerAdapter trailerAdapter;

    private RecyclerView recyclerViewReviews;
    private ReviewAdapter reviewAdapter;

    // язык
    private static String lang;

    // пере определяем меню
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    // отрабатываем нажатие ка меню
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case R.id.itemMain:
                Intent intent = new Intent(this, MainActivity.class);
                startActivity(intent);
                break;
            case R.id.itemFavourite:
                Intent intentToFavourite = new Intent(this, FavouriteActivity.class);
                startActivity(intentToFavourite);
                break;
        }
        return super.onOptionsItemSelected(item);
    }/**/


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        // опеределяет локальный язык на устройсте
        lang = Locale.getDefault().getLanguage();

        // указываем ссылки на элементы формы
        imageViewBigPoster = findViewById(R.id.imageViewBigPoster);
        imageViewAddToFavourite = findViewById(R.id.imageViewAddToFavourite);
        textViewTitle = findViewById(R.id.textViewTitle);
        textViewOriginalTitle = findViewById(R.id.textViewOriginalTitle);
        textViewRating = findViewById(R.id.textViewRating);
        textViewReleaseDate = findViewById(R.id.textViewReleaseDate);
        textViewOverview = findViewById(R.id.textViewOverview);

        scrollViewInfo = findViewById(R.id.scrollViewInfo);
        // получаем интент
        Intent intent = getIntent();
        if (intent != null && intent.hasExtra("id")) {
            id = intent.getIntExtra("id", -1);
        } else {
            // закрывам данную активность , и будет открываться та активность . которая его вызывала
            finish();
        }
        // выводим инф про фильму
        viewModel = ViewModelProviders.of(this).get(MainViewModel.class);
        movie = viewModel.getMovieById(id);
        Picasso.get().load(movie.getBigPosterPath()).into(imageViewBigPoster);
        textViewTitle.setText(movie.getTitle());
        textViewOriginalTitle.setText(movie.getOriginalTitle());
        textViewOverview.setText(movie.getOverview());
        textViewReleaseDate.setText(movie.getReleaseDate());
        textViewRating.setText(Double.toString(movie.getVoteAverage()));
        // существуетли этот фильм в избранном
        setFavourite();


        // ролики
        recyclerViewTrailer = findViewById(R.id.recyclerViewTrailers);
        trailerAdapter = new TrailerAdapter();
        recyclerViewTrailer.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewTrailer.setAdapter(trailerAdapter);
        JSONObject jsonObjectTrailers = NetworkUtils.getJSONForVideos(movie.getId(), lang);
        ArrayList<Trailer> trailers = JSONUtils.getTrailersFromJSON(jsonObjectTrailers);
        trailerAdapter.setTrailers(trailers);

        trailerAdapter.setOnTrailerClickListener(new TrailerAdapter.OnTrailerClickListener() {
            @Override
            public void onTrailerClick(String url) {
                //это у нас не явный интент
                // просматриваем видео в ютубе
                //
                Intent intent1ToTrailer = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                startActivity(intent1ToTrailer);
            }
        });

        //  Отзывы
        recyclerViewReviews = findViewById(R.id.recyclerViewReviews);
        reviewAdapter = new ReviewAdapter();
        recyclerViewReviews.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewReviews.setAdapter(reviewAdapter);
        JSONObject jsonObjectReviews = NetworkUtils.getJSONForReviews(movie.getId(), lang);
        ArrayList<Review> reviews = JSONUtils.getReviewsFromJSON(jsonObjectReviews);
        //  Toast.makeText(this, reviews.get(0).getAuthor(), Toast.LENGTH_SHORT).show();
        reviewAdapter.setReviews(reviews);

        // устанавливаем позицию. что бы в  scrollViewInfo  инфомация была сверху
        scrollViewInfo.smoothScrollTo(0, 0);/**/
    }


    public void onClickChangeFavourite(View view) {
        if (favouriteMovie == null) {
            // добавляем фильм в избранные
            viewModel.insertFavouriteMovie(new FavouriteMovie(movie));
            Toast.makeText(this, R.string.add_to_favourite, Toast.LENGTH_SHORT).show();
        } else {
            // убираем фильм из избранных
            viewModel.deleteFavouriteMovie(favouriteMovie);
            Toast.makeText(this, R.string.remove_from_favourite, Toast.LENGTH_SHORT).show();
        }
        setFavourite();
    }

    // существуетли этот фильм в избранном
    private void setFavourite() {
        favouriteMovie = viewModel.getFavouriteMovieById(id);
        if (favouriteMovie == null) {
            imageViewAddToFavourite.setImageResource(R.drawable.favourite_add_to);
        } else {
            imageViewAddToFavourite.setImageResource(R.drawable.favourite_remove);
        }
    }
}
