package com.example.mymovies;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.Loader;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.net.Network;
import android.os.Bundle;

import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mymovies.data.MainViewModel;
import com.example.mymovies.data.Movie;
import com.example.mymovies.utils.JSONUtils;
import com.example.mymovies.utils.NetworkUtils;

import org.json.JSONObject;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<JSONObject> {

    private RecyclerView recyclerViewPosters;
    private MovieAdapter movieAdapter;
    private Switch switchSort;

    private TextView textViewTopRated;
    private TextView textViewPopularity;

    private MainViewModel viewModel;
    // уникальный идентификар загрущика
    private static final int LOADER_ID = 133;
    // данная переменная будет отвечать, за номер страниы которую загрузили
    private static int page = 1;

    //признак загрузки данных
    private static boolean isLoading = false;

    // менеджер загрузок
    private LoaderManager loaderManager;
    // метод сортировки
    private static int methodOfSort;

    // язык
    private static String lang;

    private ProgressBar progressBarLoading;

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

    private int getColumnCount() {
        DisplayMetrics displayMetrics = new DisplayMetrics();

        // определяем характеристики экрана
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        // ширина в пиксилях в dp
        //  количество пикселей / на плотност
        int width = (int) (displayMetrics.widthPixels / displayMetrics.density);

        return width / 185 > 2 ? width / 185 : 2;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // опеределяет локальный язык на устройсте
        lang = Locale.getDefault().getLanguage();

        viewModel = ViewModelProviders.of(this).get(MainViewModel.class);
        // здесь используется патрм синголтон
        // он отвечает за все загрузки
        loaderManager = LoaderManager.getInstance(this);

        progressBarLoading = findViewById(R.id.progressBarLoading);

        switchSort = findViewById(R.id.switchSort);
        textViewTopRated = findViewById(R.id.textViewTopRated);
        textViewPopularity = findViewById(R.id.textViewPopularity);

        recyclerViewPosters = findViewById(R.id.recyclerViewPosters);
        // распологаем сеткой в 2 колонки
        recyclerViewPosters.setLayoutManager(new GridLayoutManager(this, getColumnCount()));
        movieAdapter = new MovieAdapter();
        recyclerViewPosters.setAdapter(movieAdapter);

        switchSort.setChecked(true);

        switchSort.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                // если переключили вид сортировки , то будем считывать с 1 страницы
                page = 1;
                serMethodOfSort(isChecked);
            }
        });

        switchSort.setChecked(false);

        // нажимаем на пост
        movieAdapter.setOnPosterClickListener(new MovieAdapter.OnPosterClickListener() {
            @Override
            public void onPosterClick(int position) {
                Movie movie = movieAdapter.getMovies().get(position);
                Intent intent = new Intent(MainActivity.this, DetailActivity.class);
                intent.putExtra("id", movie.getId());
                startActivity(intent);
            }
        });
        // данный метод сработает , если мы долистали конца списка, то мы загружаем следующую страниу
        movieAdapter.setOnReachEndListener(new MovieAdapter.OnReachEndListener() {
            @Override
            public void onReachEnd() {
                // если загрузка не началась , то мы его запускаем
                if (!isLoading) {
                    // methodOfSort метод сортировки
                    // page номер страницы
                    downloadData(methodOfSort, page);
                }
            }
        });

        LiveData<List<Movie>> moviesFromLiveData = viewModel.getMovies();
        moviesFromLiveData.observe(this, new Observer<List<Movie>>() {
            @Override
            public void onChanged(List<Movie> movies) {
                // загружаем данные с баззы
                if (page == 1) {
                    movieAdapter.setMovies(movies);
                }
            }
        });
    }

    public void onClickSetPopularity(View view) {
        serMethodOfSort(false);
        switchSort.setChecked(false);
    }

    public void onClickSetTopRated(View view) {
        serMethodOfSort(true);
        switchSort.setChecked(true);
    }

    private void serMethodOfSort(boolean isTopRated) {
        if (isTopRated) {
            // устанавливаем цвета
            textViewTopRated.setTextColor(getResources().getColor(R.color.colorAccent));
            textViewPopularity.setTextColor(getResources().getColor(R.color.white_color));
            methodOfSort = NetworkUtils.TOP_RATED;
        } else {
            textViewPopularity.setTextColor(getResources().getColor(R.color.colorAccent));
            textViewTopRated.setTextColor(getResources().getColor(R.color.white_color));
            methodOfSort = NetworkUtils.POPULARITY;
        }
        // запускаем первую загрузку
        downloadData(methodOfSort, page);
    }


    private void downloadData(int methodOfSort, int page) {
        // фомируем url
        URL url = NetworkUtils.buildURL(methodOfSort, page,lang);
        //сохраняем данные в bundle
        Bundle bundle = new Bundle();
        bundle.putString("url", url.toString());
        // запускаем загрущик
        loaderManager.restartLoader(LOADER_ID, bundle, this);
    }


    @NonNull
    @Override
    public Loader<JSONObject> onCreateLoader(int id, @Nullable Bundle bundle) {
        NetworkUtils.JSONLoader jsonLoader = new NetworkUtils.JSONLoader(this, bundle);
        // к jsonLoader добаялем слушателя
        jsonLoader.setOnStartLoadingListener(new NetworkUtils.JSONLoader.OnStartLoadingListener() {
            @Override
            public void onStartLoading() {
                progressBarLoading.setVisibility(View.VISIBLE);
                //указываем , что идет загрузка
                isLoading = true;
            }
        });
        // возращаем наш загрущик
        return jsonLoader;
    }

    @Override
    public void onLoadFinished(@NonNull Loader<JSONObject> loader, JSONObject jsonObject) {
        // получаем список фильмов
        ArrayList<Movie> movies = JSONUtils.getMoviesFromJSON(jsonObject);
        // добавляем данные
        if (movies != null && !movies.isEmpty()) {
            if (page == 1) {
                // очищаем список
                viewModel.deleteAllMovies();
                //очищаем адаптер
                movieAdapter.clear();
            }
            for (Movie movie : movies) {
                viewModel.insertMovie(movie);
            }
            // добавляем в базу сразу 20 фильмо в базу
            movieAdapter.addMovies(movies);
            //номер страницы увеличиваем на 1
            page++;

        }
        progressBarLoading.setVisibility(View.INVISIBLE);
        //загрузка завершина
        isLoading = false;
        // после загрузки фильмов и добавления их в базу данных,
        // мы должны удалить загрущик
        loaderManager.destroyLoader(LOADER_ID);
    }

    @Override
    public void onLoaderReset(@NonNull Loader<JSONObject> loader) {

    }
}