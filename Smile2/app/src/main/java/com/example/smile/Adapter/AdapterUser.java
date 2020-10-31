package com.example.smile.Adapter;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.smile.Fragments.ProfileFragment;
import com.example.smile.MainActivity;
import com.example.smile.Models.User;
import com.example.smile.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class AdapterUser extends RecyclerView.Adapter<AdapterUser.ViewHolder> {

    private Context mContext;
    private List<User> mUsers;
    private boolean isfragment;

    private FirebaseUser firebaseUser;

    public AdapterUser(Context mContext, List<User> mUsers, boolean isfragment) {
        this.mContext = mContext;
        this.mUsers = mUsers;
        this.isfragment = isfragment;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
       View view = LayoutInflater.from(mContext).inflate(R.layout.user_item, viewGroup, false);
       return new AdapterUser.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder viewHolder, int i) {
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        final User user = mUsers.get(i);
        viewHolder.btn_follow.setVisibility(View.VISIBLE);
        viewHolder.username.setText(user.getUsername());
        viewHolder.status.setText(user.getStatus());
        Glide.with(mContext).load(user.getImageurl()).into(viewHolder.avatarIv);
        isFollowing(user.getUid(), viewHolder.btn_follow);
        if (user.getUid().equals(firebaseUser.getUid())){
            viewHolder.btn_follow.setVisibility(View.GONE);
        }
        viewHolder.itemView.setOnClickListener(view -> {
            if (isfragment) {
                SharedPreferences.Editor editor = mContext.getSharedPreferences("PREFS", Context.MODE_PRIVATE).edit();
                editor.putString("profileid", user.getUid());
                editor.apply();
                ((FragmentActivity) mContext).getSupportFragmentManager().beginTransaction().replace(R.id.content,
                        new ProfileFragment()).commit();
            }else {
                Intent intent = new Intent(mContext, MainActivity.class);
                intent.putExtra("publisherUid", user.getUid());
                mContext.startActivity(intent);
            }
        });
        viewHolder.btn_follow.setOnClickListener(view -> {
            if (viewHolder.btn_follow.getText().toString().equals("follow")){
                FirebaseDatabase.getInstance().getReference().child("Follow").child(firebaseUser.getUid())
                        .child("following").child(user.getUid()).setValue(true);
                FirebaseDatabase.getInstance().getReference().child("Follow").child(user.getUid())
                        .child("followers").child(firebaseUser.getUid()).setValue(true);
                addNotifications(user.getUid());
            }else {
                FirebaseDatabase.getInstance().getReference().child("Follow").child(firebaseUser.getUid())
                        .child("following").child(user.getUid()).removeValue();
                FirebaseDatabase.getInstance().getReference().child("Follow").child(user.getUid())
                        .child("followers").child(firebaseUser.getUid()).removeValue();
            }
        });
    }

    private void addNotifications(String userid) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Notifications").child(userid);
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("userid", firebaseUser.getUid());
        hashMap.put("text", "started following you");
        hashMap.put("postUid", "");
        hashMap.put("ispost", false);
        reference.push().setValue(hashMap);
    }

    @Override
    public int getItemCount() {
        return mUsers.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        public TextView username;
        public TextView status;
        public CircleImageView avatarIv;
        public Button btn_follow;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            username = itemView.findViewById(R.id.usernameTv);
            status = itemView.findViewById(R.id.statusTv);
            avatarIv = itemView.findViewById(R.id.avatarIv);
            btn_follow = itemView.findViewById(R.id.btn_follow);
        }
    }
    private void isFollowing(final String userid, final Button button){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference()
                .child("Follow").child(firebaseUser.getUid()).child("following");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.child(userid).exists()){
                    button.setText("following");
                }else {
                    button.setText("follow");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
