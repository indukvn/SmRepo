package com.example.smile.Adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.smile.DashboardActivity;
import com.example.smile.MainActivity;
import com.example.smile.Models.Comment;
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

import java.util.List;

public class AdapterComment extends RecyclerView.Adapter<AdapterComment.ViewHolder>{
    private Context mContext;
    private List<Comment> mComment;
    private String postUid;
    private FirebaseUser firebaseUser;

    public AdapterComment(Context mContext, List<Comment> mComment, String postUid) {
        this.mContext = mContext;
        this.mComment = mComment;
        this.postUid = postUid;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.comment_item, viewGroup, false);
        return new AdapterComment.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        Comment comment = mComment.get(i);
        viewHolder.comment.setText(comment.getComment());
        getUserInfo(viewHolder.avatarIv, viewHolder.usernameTv, comment.getPublisher());
        viewHolder.comment.setOnClickListener(view -> {
            Intent intent = new Intent(mContext, MainActivity.class);
            intent.putExtra("publisherid", comment.getPublisher());
            mContext.startActivity(intent);
        });
        viewHolder.avatarIv.setOnClickListener(view -> {
            Intent intent = new Intent(mContext, DashboardActivity.class);
            intent.putExtra("publisherid", comment.getPublisher());
            mContext.startActivity(intent);
        });

        viewHolder.itemView.setOnLongClickListener(view -> {
            if (comment.getPublisher().equals(firebaseUser.getUid())){
                AlertDialog alertDialog = new AlertDialog.Builder(mContext).create();
                alertDialog.setTitle("Do you want to delete?");
                alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "No",
                        (dialogInterface, i1) -> dialogInterface.dismiss());
                alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Yes",
                        (dialogInterface, i1) -> {
                            FirebaseDatabase.getInstance().getReference("Comments")
                                    .child(postUid).child(comment.getCommentid())
                                    .removeValue().addOnCompleteListener(task -> {
                                        if (task.isSuccessful()){
                                            Toast.makeText(mContext, "Deleted!", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                            dialogInterface.dismiss();
                        });
                alertDialog.show();
            }
            return true;
        });

    }

    @Override
    public int getItemCount() {
        return mComment.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        public ImageView avatarIv;
        public TextView usernameTv, comment;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            avatarIv = itemView.findViewById(R.id.avatarIv);
            usernameTv = itemView.findViewById(R.id.usernameTv);
            comment = itemView.findViewById(R.id.comment);
        }
    }
    private void getUserInfo(ImageView imageView, TextView usernameTv, String publisherid){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Users").child(publisherid);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                Glide.with(mContext).load(user.getImageurl()).into(imageView);
                usernameTv.setText(user.getUsername());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
