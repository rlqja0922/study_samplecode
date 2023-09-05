package com.rebornsoft.naversearch;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

public class BlogAdapter extends RecyclerView.Adapter<BlogAdapter.ViewHolder> {
    ArrayList<Blog> bloglist = new ArrayList<>();
    public static Context context;

    public BlogAdapter(ArrayList<Blog> bloglist, Context context) {
        this.bloglist = bloglist;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View itemView = inflater.inflate(R.layout.blog_item, parent, false);

        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Blog blog = bloglist.get(position);
        holder.setItem(blog);

    }

    @Override
    public int getItemCount() {
        return bloglist.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        TextView tv_title, tv_description, tv_bloggername, tv_postdate;

        //생성자를 만든다.
        //여기서 itemView는 화면에 표시되는 각각의 person_item.xml 뷰이다
        public ViewHolder(@NonNull View itemView) {
            super(itemView);


            tv_title =itemView.findViewById(R.id.tv_title);
            tv_description =itemView.findViewById(R.id.tv_description);
            tv_bloggername =itemView.findViewById(R.id.tv_bloggername);
            tv_postdate =itemView.findViewById(R.id.tv_postdate);
        }

        public void setItem(Blog item) {
            tv_title.setText(item.getTv_title());
            tv_description.setText(item.getTv_description());
            tv_bloggername.setText(item.getTv_bloggername());
            tv_postdate.setText(item.getTv_postdate());


//            tvName.setText(item.getName());
//            tvPhoneNum.setText(item.getPhoneNum());
//            imageView.setImageResource(item.getResId());
        }
    }
}
