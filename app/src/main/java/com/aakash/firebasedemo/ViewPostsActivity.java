package com.aakash.firebasedemo;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class ViewPostsActivity extends AppCompatActivity {

    private ListView lvPosts;
    private ArrayList<String> alPosts;
    private ArrayAdapter adPosts;
    private String sender;
    private ImageView imgPost;
    private TextView tvDesc;
    private ArrayList<DataSnapshot> dataSnapshots;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_posts);

        imgPost = findViewById(R.id.imgPost);
        tvDesc = findViewById(R.id.tvPostDesc);

        lvPosts = findViewById(R.id.lvPosts);
        alPosts = new ArrayList ();
        adPosts = new ArrayAdapter(this, android.R.layout.simple_list_item_1, alPosts);

        lvPosts.setAdapter(adPosts);
        dataSnapshots = new ArrayList<>();

        String user = FirebaseAuth.getInstance().getCurrentUser().getUid();

        FirebaseDatabase.getInstance().getReference().child("users")
                .child(user)
                .child("received_posts")
                .addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                        dataSnapshots.add(snapshot);
                        alPosts.add(snapshot.child("fromWhom").getValue().toString());
                        adPosts.notifyDataSetChanged();
                    }

                    @Override
                    public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

                    }

                    @Override
                    public void onChildRemoved(@NonNull DataSnapshot snapshot) {
                        int i =0;
                        for (DataSnapshot ds : dataSnapshots){
                            if (ds.getKey().equals(snapshot.getKey())){
                                dataSnapshots.remove(i);
                                alPosts.remove(i);
                            }
                            i++;
                        }
                        adPosts.notifyDataSetChanged();
                        imgPost.setImageResource(R.drawable.placeholder);
                        tvDesc.setText("");
                    }

                    @Override
                    public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

        lvPosts.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                ProgressBar pbP = findViewById(R.id.pbP);
                pbP.animate().start();
                pbP.setVisibility(View.VISIBLE);
                DataSnapshot dataSnapshot = dataSnapshots.get(i);
                String downloadLink = dataSnapshot.child("imageLink").getValue().toString();
                Picasso.get().load(downloadLink).into(imgPost, new Callback() {
                    @Override
                    public void onSuccess() {
                        pbP.setVisibility(View.GONE);
                    }

                    @Override
                    public void onError(Exception e) {

                    }
                });
                tvDesc.setText(dataSnapshot.child("des").getValue().toString());
            }
        });

        lvPosts.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int index, long l) {
                AlertDialog.Builder builder;

                builder = new AlertDialog.Builder(ViewPostsActivity.this);


                builder.setTitle("Delete Entry")
                        .setMessage("Are you sure to delete?")
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                FirebaseDatabase.getInstance().getReference().child("users")
                                        .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                        .child("received_posts")
                                        .child(dataSnapshots.get(index).getKey())
                                        .removeValue();
                            }
                        })
                        .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                            }
                        })
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .show();
                return false;
            }
        });
    }
}