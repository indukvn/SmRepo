package com.example.smile.Adapter;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.smile.Fragments.PostDetailFragment;
import com.example.smile.Models.Post;
import com.example.smile.R;

import java.util.List;

public class MyFotoAdapter extends RecyclerView.Adapter<MyFotoAdapter.ViewHolder> {
    private Context context;
    private List<Post> mPosts;

    public MyFotoAdapter(Context context, List<Post> mPosts) {
        this.context = context;
        this.mPosts = mPosts;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(context).inflate(R.layout.fotos_item, viewGroup, false);
        return new MyFotoAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        Post post = mPosts.get(i);
        Glide.with(context).load(post.getPostimage()).into(viewHolder.post_image);
        viewHolder.post_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences.Editor editor = context.getSharedPreferences("PREFS", Context.MODE_PRIVATE).edit();
                editor.putString("postUid", post.getPostUid());
                editor.apply();
                ((FragmentActivity)context).getSupportFragmentManager().beginTransaction().replace(R.id.content,
                        new PostDetailFragment()).commit();
            }
        });
    }

    @Override
    public int getItemCount() {
        return mPosts.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        public ImageView post_image;
        public ViewHolder(@Nullable View itemView){
            super(itemView);
            post_image = itemView.findViewById(R.id.post_image);
        }
    }
}
