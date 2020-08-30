package com.example.mymovies.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import com.example.mymovies.R;
import com.example.mymovies.data.Trailer;

import java.util.ArrayList;

public class TrailerAdapter extends RecyclerView.Adapter<TrailerAdapter.TrailerViewHolder>{

    // список роликов
    private ArrayList<Trailer> trailers;
    // слушатель на воспроизведения ролика
    private OnTrailerClickListener onTrailerClickListener;


    @NonNull
    @Override
    public TrailerViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.trailer_item,viewGroup,false);
        return new TrailerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TrailerViewHolder trailerViewHolder, int position) {
        Trailer trailer = trailers.get(position);

        trailerViewHolder.textNameOfVideo.setText(trailer.getName());
    }

    @Override
    public int getItemCount() {
        return trailers.size();
    }

    public void setTrailers(ArrayList<Trailer> trailers) {
        this.trailers = trailers;
        // указываем адапетру , что данные изменились
        notifyDataSetChanged();
    }

    public interface OnTrailerClickListener{
        void onTrailerClick(String url);
    }

    class TrailerViewHolder extends RecyclerView.ViewHolder{

         private TextView textNameOfVideo;

        public TrailerViewHolder(@NonNull View itemView) {
            super(itemView);

              textNameOfVideo = itemView.findViewById(R.id.textNameOfVideo);
              itemView.setOnClickListener(new View.OnClickListener() {
                  @Override
                  public void onClick(View view) {
                      if (onTrailerClickListener != null) {
                          // указываем полный путь к ролику
                          onTrailerClickListener.onTrailerClick(trailers.get(getAdapterPosition()).getKey());
                      }
                  }
              });
        }
    }

    public void setOnTrailerClickListener(OnTrailerClickListener onTrailerClickListener) {
        this.onTrailerClickListener = onTrailerClickListener;
    }
}
