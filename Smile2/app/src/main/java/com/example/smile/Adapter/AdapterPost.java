package com.example.smile.Adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.smile.CommentsActivity;
import com.example.smile.FollowersActivity;
import com.example.smile.Fragments.PostDetailFragment;
import com.example.smile.Fragments.ProfileFragment;
import com.example.smile.Models.Post;
import com.example.smile.Models.User;
import com.example.smile.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.List;

public class AdapterPost extends RecyclerView.Adapter<AdapterPost.ViewHolder>{

    public Context mContext;
    public List<Post> mPost;
    private FirebaseUser firebaseUser;
    private String postUid;
    private TextView comments;
    private ImageView imageView;
    private TextView likes;

    public AdapterPost(Context mContext, List<Post> mPost, String postUid) {
        this.mContext = mContext;
        this.mPost = mPost;
        this.postUid = postUid;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.post_item, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        Post post = mPost.get(i);
        Glide.with(mContext).load(post.getPostimage())
                .apply(new RequestOptions().placeholder(R.drawable.placeholder))
                .into(viewHolder.post_image);
        if (post.getDescription().equals("")){
            viewHolder.description.setVisibility(View.GONE);
        }else {
            viewHolder.description.setVisibility(View.VISIBLE);
            viewHolder.description.setText(post.getDescription());
        }
        publisherInfo(viewHolder.avatarIv, viewHolder.usernameTv, viewHolder.publisher, post.getPublisher());
        isLiked(post.getPostUid(), viewHolder.like);
        nrLikes(viewHolder.likes, post.getPostUid());
        getComments(post.getPostUid(), viewHolder.comments);
        isSaved(post.getPostUid(), viewHolder.save);

        viewHolder.avatarIv.setOnClickListener(view -> {
            SharedPreferences.Editor editor = mContext.getSharedPreferences("PREFS", Context.MODE_PRIVATE).edit();
            editor.putString("profileUid", post.getPublisher());
            editor.apply();
            ((FragmentActivity)mContext).getSupportFragmentManager().beginTransaction().replace(R.id.content,
                    new ProfileFragment()).commit();
        });
        viewHolder.usernameTv.setOnClickListener(view -> {
            SharedPreferences.Editor editor = mContext.getSharedPreferences("PREFS", Context.MODE_PRIVATE).edit();
            editor.putString("profileUid", post.getPublisher());
            editor.apply();
            ((FragmentActivity)mContext).getSupportFragmentManager().beginTransaction().replace(R.id.content,
                    new ProfileFragment()).commit();
        });
        viewHolder.publisher.setOnClickListener(view -> {
            SharedPreferences.Editor editor = mContext.getSharedPreferences("PREFS", Context.MODE_PRIVATE).edit();
            editor.putString("profileUid", post.getPublisher());
            editor.apply();
            ((FragmentActivity)mContext).getSupportFragmentManager().beginTransaction().replace(R.id.content,
                    new ProfileFragment()).commit();
        });
        viewHolder.post_image.setOnClickListener(view -> {
            SharedPreferences.Editor editor = mContext.getSharedPreferences("PREFS", Context.MODE_PRIVATE).edit();
            editor.putString("postUid", post.getPostUid());
            editor.apply();
            ((FragmentActivity)mContext).getSupportFragmentManager().beginTransaction().replace(R.id.content,
                    new PostDetailFragment()).commit();
        });

        viewHolder.save.setOnClickListener(view -> {
            if (viewHolder.save.getTag().equals("save")){
                FirebaseDatabase.getInstance().getReference().child("Saves").child(firebaseUser.getUid())
                .child(post.getPostUid()).setValue(true);
            }else {
                FirebaseDatabase.getInstance().getReference().child("Saves").child(firebaseUser.getUid())
                        .child(post.getPostUid()).removeValue();
            }
        });
        viewHolder.like.setOnClickListener(view -> {
            if (viewHolder.like.getTag().equals("like")){
                FirebaseDatabase.getInstance().getReference().child("Likes").child(post.getPostUid())
                        .child(firebaseUser.getUid()).setValue(true);
                addNotifications(post.getPublisher(), post.getPostUid());
            }else {
                FirebaseDatabase.getInstance().getReference().child("Likes").child(post.getPostUid())
                        .child(firebaseUser.getUid()).removeValue();
            }
        });
        viewHolder.comment.setOnClickListener(view -> {
            Intent intent = new Intent(mContext, CommentsActivity.class);
            intent.putExtra("postUid", post.getPostUid());
            intent.putExtra("publisher", post.getPublisher());
            mContext.startActivity(intent);
        });
        viewHolder.comments.setOnClickListener(view -> {
            Intent intent = new Intent(mContext, CommentsActivity.class);
            intent.putExtra("postUid", post.getPostUid());
            intent.putExtra("publisher", post.getPublisher());
            mContext.startActivity(intent);
        });
        viewHolder.likes.setOnClickListener(view -> {
            Intent intent = new Intent(mContext, FollowersActivity.class);
            intent.putExtra("Uid", post.getPostUid());
            intent.putExtra("title", "likes");
            mContext.startActivity(intent);
        });
        viewHolder.more.setOnClickListener(view -> {
            PopupMenu popupMenu = new PopupMenu(mContext, view);
            popupMenu.setOnMenuItemClickListener(menuItem -> {
                switch (menuItem.getItemId()){
                    case R.id.edit:
                        editPost(post.getPostUid());
                        return true;
                    case R.id.delete:
                        FirebaseDatabase.getInstance().getReference("Posts")
                                .child(post.getPostUid()).removeValue()
                                .addOnCompleteListener(task -> {
                                    if (task.isSuccessful()){
                                        Toast.makeText(mContext, "Deleted!", Toast.LENGTH_SHORT).show();
                                    }
                                });
                        return true;
                    case R.id.report:
                        Toast.makeText(mContext, "Report clicked!", Toast.LENGTH_SHORT).show();
                        return true;
                    default:
                        return false;
                }
            });
            popupMenu.inflate(R.menu.post_menu);
            if (!post.getPublisher().equals(firebaseUser.getUid())){
                popupMenu.getMenu().findItem(R.id.edit).setVisible(false);
                popupMenu.getMenu().findItem(R.id.delete).setVisible(false);
            }
            popupMenu.show();
        });
    }

    @Override
    public int getItemCount() {
        return mPost.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{

        public ImageView avatarIv, post_image, like, comment, save, more;
        public TextView usernameTv, likes, publisher, description, comments;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            avatarIv = itemView.findViewById(R.id.avatarIv);
            post_image = itemView.findViewById(R.id.post_image);
            like = itemView.findViewById(R.id.like);
            comment = itemView.findViewById(R.id.comment);
            save = itemView.findViewById(R.id.save);
            usernameTv = itemView.findViewById(R.id.usernameTv);
            likes = itemView.findViewById(R.id.likes);
            publisher = itemView.findViewById(R.id.publisher);
            description = itemView.findViewById(R.id.description);
            comments = itemView.findViewById(R.id.comments);
            more = itemView.findViewById(R.id.more);
        }
    }
    private void getComments(String postUid, TextView comments){
        this.postUid = postUid;
        this.comments = comments;
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Comments").child("postUid");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                comments.setText("View All "+dataSnapshot.getChildrenCount() + "Comments");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
    private void isLiked(String postUid, final ImageView imageView){
        this.postUid = postUid;
        this.imageView = imageView;
        final FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Likes")
                .child("postUid");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                assert firebaseUser != null;
                if (dataSnapshot.child(firebaseUser.getUid()).exists()){
                    imageView.setImageResource(R.drawable.ic_liked);
                    imageView.setTag("liked");
                }else {
                    imageView.setImageResource(R.drawable.ic_like);
                    imageView.setTag("like");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void addNotifications(String userid, String postUid){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Notifications").child(userid);
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("userid", firebaseUser.getUid());
        hashMap.put("text", "liked your post");
        hashMap.put("postUid", postUid);
        hashMap.put("ispost", true);
        reference.push().setValue(hashMap);
    }

    private void nrLikes(TextView likes, String postUid){
        this.likes = likes;
        this.postUid = postUid;
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Likes")
                .child("postUid");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                likes.setText(dataSnapshot.getChildrenCount()+" likes");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void publisherInfo(final ImageView avatarIv, final TextView usernameTv, final TextView publisher, final String Uid){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").child(Uid);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot datasnapshot) {
                User user = datasnapshot.getValue(User.class);
                assert user != null;
                Glide.with(mContext).load(user.getImageurl()).into(avatarIv);
                usernameTv.setText(user.getUsername());
                publisher.setText(user.getUsername());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
    private void isSaved(String postUid, ImageView imageView){
        this.postUid = postUid;
        this.imageView = imageView;
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        assert firebaseUser != null;
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Saves")
                .child(firebaseUser.getUid());
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.child("postUid").exists()){
                    imageView.setImageResource(R.drawable.ic_save_black);
                    imageView.setTag("saved");
                }else {
                    imageView.setImageResource(R.drawable.ic_save);
                    imageView.setTag("save");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
    private void editPost(String postUid){
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(mContext);
        alertDialog.setTitle("Edit Post");
        EditText editText = new EditText(mContext);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT
        );
        editText.setLayoutParams(lp);
        alertDialog.setView(editText);
        getText(postUid, editText);
        alertDialog.setPositiveButton("Edit",
                (dialogInterface, i) -> {
                    HashMap<String, Object> hashMap = new HashMap<>();
                    hashMap.put("description", editText.getText().toString());
                    FirebaseDatabase.getInstance().getReference("Posts")
                            .child(postUid).updateChildren(hashMap);
                });
        alertDialog.setNegativeButton("Cancel",
                (dialogInterface, i) -> dialogInterface.dismiss());
        alertDialog.show();
    }
    private void getText(String postUid, EditText editText){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Posts")
                .child(postUid);
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                editText.setText(dataSnapshot.getValue(Post.class).getDescription());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
