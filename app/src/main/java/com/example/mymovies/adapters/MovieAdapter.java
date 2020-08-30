package com.example.mymovies.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import com.example.mymovies.R;
import com.example.mymovies.data.Movie;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.MovieViewHolder> {

    private List<Movie> movies;
    // создаем объект нашего типа
    private OnPosterClickListener onPosterClickListener;
    // создаем объект нашего типа
    private OnReachEndListener onReachEndListener;


    public MovieAdapter() {
        movies = new ArrayList<>();
    }

    // реализация слушаетля на нажатие на постер
    public interface OnPosterClickListener {
        void onPosterClick(int position);
    }

    // если если мы дошли до конца списка LIstener , то мы будем закачать следуюшую страницу фильмов
   public interface OnReachEndListener {
        void onReachEnd();
    }

    public void setOnPosterClickListener(OnPosterClickListener onPosterClickListener) {
        this.onPosterClickListener = onPosterClickListener;
    }

    public void setOnReachEndListener(OnReachEndListener onReachEndListener) {
        this.onReachEndListener = onReachEndListener;
    }

    @NonNull
    @Override
    public MovieViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.movie_item, viewGroup, false);
        return new MovieViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MovieViewHolder movieViewHolder, int i) {
        // если достигли конца страницы, будем потом подгружать страницы
        if (movies.size() >=10 && i > movies.size()-4 && onReachEndListener != null)  {
            onReachEndListener.onReachEnd();
        }
        Movie movie = movies.get(i);
        // спомощью picasso ,
        // мы указали какую картинку и куда загрузить
        Picasso.get().load(movie.getPosterPath()).into(movieViewHolder.imageViewSmallPoster);
    }

    @Override
    public int getItemCount() {
        return movies.size();
    }

    class MovieViewHolder extends RecyclerView.ViewHolder {

        private ImageView imageViewSmallPoster;

        public MovieViewHolder(@NonNull View itemView) {
            super(itemView);

            imageViewSmallPoster = itemView.findViewById(R.id.imageViewSmallPoster);

            // реализация , если на нажали на постер
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (onPosterClickListener != null) {
                        onPosterClickListener.onPosterClick(getAdapterPosition());
                    }
                }
            });
        }
    }

    public void clear(){
        this.movies.clear();
        notifyDataSetChanged();
    }

    public List<Movie> getMovies() {
        return movies;
    }

    public void addMovies(List<Movie> movies) {
        this.movies.addAll(movies);
        notifyDataSetChanged();
    }

    public void setMovies(List<Movie> movies) {
        this.movies = movies;
        notifyDataSetChanged();
    }
}
