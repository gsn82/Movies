package com.example.mymovies;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.mymovies.data.FavouriteMovie;
import com.example.mymovies.data.MainViewModel;
import com.example.mymovies.data.Movie;

import java.util.ArrayList;
import java.util.List;

public class FavouriteActivity extends AppCompatActivity {

    private RecyclerView recyclerViewFavouriteMovies;
    private MovieAdapter movieAdapter;
    // используем для списка любимых фильмов
    private MainViewModel viewModel;


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
        setContentView(R.layout.activity_favourite);

        recyclerViewFavouriteMovies = findViewById(R.id.recyclerViewFavouriteMovies);
        //ввиде сетке
        recyclerViewFavouriteMovies.setLayoutManager(new GridLayoutManager(this, 2));

        movieAdapter = new MovieAdapter();
        recyclerViewFavouriteMovies.setAdapter(movieAdapter);

        viewModel = ViewModelProviders.of(this).get(MainViewModel.class);
        // получаем список избранных фильмов
        LiveData<List<FavouriteMovie>> favouriteMovies = viewModel.getFavouriteMovies();

        favouriteMovies.observe(this, new Observer<List<FavouriteMovie>>() {
            @Override
            public void onChanged(List<FavouriteMovie> favouriteMovies) {
                List<Movie> movies = new ArrayList<>();
                if (favouriteMovies != null) {
                    movies.addAll(favouriteMovies);
                    movieAdapter.setMovies(movies);
                }
            }
        });

        movieAdapter.setOnPosterClickListener(new MovieAdapter.OnPosterClickListener() {
            @Override
            public void onPosterClick(int position) {

               //position - номер фильма , который нажали
               Movie movie = movieAdapter.getMovies().get(position);
          //      Toast.makeText(FavouriteActivity.this, ""+position+ "   " + movie.getId(), Toast.LENGTH_SHORT).show();
                //открываем новую активити , с полным описанием фильма
               Intent intent = new Intent(FavouriteActivity.this, DetailActivity.class);
               int id =  movie.getId();
              intent.putExtra("id",  id);
                startActivity(intent);
            }
        });

    }
}
