package com.rebornsoft.naversearch;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;

public class MovieAdapters extends RecyclerView.Adapter<MovieAdapters.ViewHolder> {

    ArrayList<Movie> movielist = new ArrayList<>();
    public static Context context;


    public MovieAdapters(ArrayList<Movie> movielist, Context context) {
        this.movielist = movielist;
        this.context = context;
    }

    @NonNull
    @Override
    public MovieAdapters.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View itemView = inflater.inflate(R.layout.movie_item, parent, false);

        return new ViewHolder(itemView);

    }

    @Override
    public void onBindViewHolder(@NonNull MovieAdapters.ViewHolder holder, int position) {
    Movie movie = movielist.get(position);
    holder.setItem(movie);

    }

    @Override
    public int getItemCount() {
        return movielist.size();
    }
    public void addItem(Movie item){
        movielist.add(item);
    }
     class ViewHolder extends RecyclerView.ViewHolder {

        TextView tv_title, tv_director, tv_actor;
        ImageView img_movie;


        //생성자를 만든다.
        //여기서 itemView는 화면에 표시되는 각각의 person_item.xml 뷰이다
        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            tv_title =itemView.findViewById(R.id.tv_title);
            tv_actor =itemView.findViewById(R.id.tv_actor);
            tv_director =itemView.findViewById(R.id.tv_director);
            img_movie =itemView.findViewById(R.id.img_movie);

        }

        public void setItem(Movie item) {
            tv_title.setText(item.getTitle());
            tv_actor.setText(item.getActor());
            tv_director.setText(item.getDirector());
            Glide.with(context).load(item.getImage()).into(img_movie);


//            tvName.setText(item.getName());
//            tvPhoneNum.setText(item.getPhoneNum());
//            imageView.setImageResource(item.getResId());
        }
    }
}
