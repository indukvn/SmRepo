package com.example.smile;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.smile.Adapter.NotificationAdapter;
import com.example.smile.Models.Notification;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class NotifiactionActivity extends AppCompatActivity {
    private ImageView close;
    private ImageView notification;
    private RecyclerView recyclerView;
    private NotificationAdapter notificationAdapter;
    private List<Notification> notificationList;
    StorageReference storageReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notifiaction);

        close = findViewById(R.id.close);
        notification = findViewById(R.id.notification);
        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(linearLayoutManager);
        notificationList = new ArrayList<>();
        notificationAdapter = new NotificationAdapter(getApplicationContext(), notificationList, true);
        recyclerView.setAdapter(notificationAdapter);

        readNotifications();

        storageReference = FirebaseStorage.getInstance().getReference("notification");
        close.setOnClickListener(view -> {
            startActivity(new Intent(NotifiactionActivity.this, DashboardActivity.class));
            finish();
        });
        notification.setOnClickListener(view -> {
            startActivity(new Intent(NotifiactionActivity.this, DashboardActivity.class));
        });
    }
    private void readNotifications() {
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        assert firebaseUser != null;
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Notifications").child(firebaseUser.getUid());
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                notificationList.clear();
                for (DataSnapshot snapshot :dataSnapshot.getChildren()){
                    Notification notification = snapshot.getValue(Notification.class);
                    notificationList.add(notification);
                }
                Collections.reverse(notificationList);
                notificationAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}