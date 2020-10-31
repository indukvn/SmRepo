package com.example.smile.Adapter;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.smile.Fragments.PostDetailFragment;
import com.example.smile.Fragments.ProfileFragment;
import com.example.smile.Models.Notification;
import com.example.smile.Models.Post;
import com.example.smile.Models.User;
import com.example.smile.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.ViewHolder>{

    private Context mContext;
    private List<Notification> mNotification;
    private boolean isfragment;

    public NotificationAdapter(Context mContext, List<Notification> mNotification, boolean isfragment) {
        this.mContext = mContext;
        this.mNotification = mNotification;
        this.isfragment = isfragment;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.notification_item, viewGroup, false);
        return new NotificationAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        Notification notification = mNotification.get(i);
        viewHolder.text.setText(notification.getText());
        getUserInfo(viewHolder.avatarIv, viewHolder.usernameTv, notification.getUserUid());

        if (notification.isIspost()){
            viewHolder.post_image.setVisibility(View.VISIBLE);
            getPostImage(viewHolder.post_image, notification.getPostUid());
        }else {
            viewHolder.post_image.setVisibility(View.GONE);
        }
        viewHolder.itemView.setOnClickListener(view -> {
            if (notification.isIspost()){
                SharedPreferences.Editor editor = mContext.getSharedPreferences("PREFS", Context.MODE_PRIVATE).edit();
                editor.putString("postUid", notification.getPostUid());
                editor.apply();
                ((FragmentActivity)mContext).getSupportFragmentManager().beginTransaction().replace(R.id.content,
                        new PostDetailFragment()).commit();
            }else {
                SharedPreferences.Editor editor = mContext.getSharedPreferences("PREFS", Context.MODE_PRIVATE).edit();
                editor.putString("profileUid", notification.getUserUid());
                editor.apply();
                ((FragmentActivity)mContext).getSupportFragmentManager().beginTransaction().replace(R.id.content,
                        new ProfileFragment()).commit();
            }
        });
    }

    @Override
    public int getItemCount() {
        return mNotification.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        public ImageView avatarIv, post_image;
        public TextView usernameTv, text;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            avatarIv = itemView.findViewById(R.id.avatarIv);
            post_image = itemView.findViewById(R.id.post_image);
            usernameTv = itemView.findViewById(R.id.usernameTv);
            text = itemView.findViewById(R.id.comment);
        }
    }
    private void getUserInfo(ImageView imageView, TextView username, String publisherid){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").child(publisherid);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                assert user != null;
                Glide.with(mContext).load(user.getImageurl()).into(imageView);
                username.setText(user.getUsername());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
    private void getPostImage(ImageView imageView, String postUid){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Posts").child(postUid);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Post post = dataSnapshot.getValue(Post.class);
                assert post != null;
                Glide.with(mContext).load(post.getPostimage()).into(imageView);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

}
