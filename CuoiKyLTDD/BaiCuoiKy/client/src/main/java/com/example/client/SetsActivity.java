package com.example.client;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.example.client.Adapters.GrideAdapter;
import com.example.client.databinding.ActivitySetsBinding;
import com.google.firebase.database.FirebaseDatabase;

public class SetsActivity extends AppCompatActivity {
    ActivitySetsBinding binding;
    FirebaseDatabase database;
    GrideAdapter adapter;
    String key;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySetsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());


        database = FirebaseDatabase.getInstance();
        key = getIntent().getStringExtra("key");

        adapter = new GrideAdapter(getIntent().getIntExtra("sets", 0), getIntent().getStringExtra( "category"));
        binding.gridView.setAdapter(adapter);
    }
}