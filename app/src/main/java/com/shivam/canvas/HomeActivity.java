package com.shivam.canvas;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Map;

public class HomeActivity extends AppCompatActivity {
    FirebaseFirestore db;
    FloatingActionButton add;
    RecyclerView recyclerView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
         db = FirebaseFirestore.getInstance();
         add=findViewById(R.id.floatingActionButton);
         recyclerView=findViewById(R.id.recycler_view);
         recyclerView.setLayoutManager(new GridLayoutManager(this,2));
         add.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View view) {
                 Intent intent = new Intent(HomeActivity.this, MainActivity.class);
               //  intent.putExtra("mobile", mobile);
                 startActivity(intent);
             }
         });

    }
    void fetchImage()
    {
        db.collection("files")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            ArrayList<Map<String, Object>> imageuriList=new ArrayList<>();
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                imageuriList.add(document.getData());
                                Log.v("url",(String) document.getData().get("url"));
                            }
                            ImageAdapter imageAdapter=new ImageAdapter(imageuriList,HomeActivity.this);
                            recyclerView.setAdapter(imageAdapter);
                            recyclerView.setHasFixedSize(true);
                        } else {
                            // Log.w(TAG, "Error getting documents.", task.getException());
                        }
                    }
                });
    }

    @Override
    protected void onResume() {
        super.onResume();
        fetchImage();
    }
}