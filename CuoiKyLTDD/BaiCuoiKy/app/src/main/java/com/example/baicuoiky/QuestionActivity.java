package com.example.baicuoiky;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.example.baicuoiky.Adapters.QuestionsAdapter;
import com.example.baicuoiky.Models.QuestionModel;
import com.example.baicuoiky.databinding.ActivityQuestionBinding;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class QuestionActivity extends AppCompatActivity {

    ActivityQuestionBinding binding;
    FirebaseDatabase database;
    ArrayList<QuestionModel> list;
    QuestionsAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityQuestionBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        getSupportActionBar().hide();

        database = FirebaseDatabase.getInstance();
        list = new ArrayList<>();

        int setNum = getIntent().getIntExtra( "setNum", 0);
        String categoryName = getIntent().getStringExtra("categoryName");

        LinearLayoutManager LayoutManager = new LinearLayoutManager(this);
        binding.recyQuestions.setLayoutManager(LayoutManager);

        adapter = new QuestionsAdapter(this, list, categoryName, new QuestionsAdapter.DeleteListener() {
            @Override
            public void onLongClick(int position, String id) {
                AlertDialog.Builder builder = new AlertDialog.Builder(QuestionActivity.this);
                builder.setTitle("Xóa");
                builder.setMessage("Bạn có chắc chắn xóa?");
                builder.setPositiveButton("Có", (dialogInterface, i) -> {
                    database.getReference().child("Sets").child(categoryName).child("questions")
                            .child(id).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {
                                    Toast.makeText(QuestionActivity.this, "Đã xóa", Toast.LENGTH_SHORT).show();
                                }
                            });
                });
                builder.setNegativeButton("Không", (dialogInterface, i) -> {
                    dialogInterface.dismiss();
                });

                AlertDialog alertDialog = builder.create();
                alertDialog.show();
            }
        }, new QuestionsAdapter.Updateistener() {
            @Override
            public void onClick(int position, String id) {
                Intent intent = new Intent(QuestionActivity.this, UpdateQuestionActivity.class);
                intent.putExtra( "category", categoryName);
                intent.putExtra( "id", id);
                startActivity(intent);
            }
        });

        binding.recyQuestions.setAdapter(adapter);

        database.getReference().child("Sets").child(categoryName).child("questions")
                .orderByChild("setNum").equalTo(setNum)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()){
                            list.clear();
                            for (DataSnapshot dataSnapshot:snapshot.getChildren()){

                                QuestionModel model = dataSnapshot.getValue(QuestionModel.class);
                                model.setKey(dataSnapshot.getKey());
                                list.add(model);

                            }

                            adapter.notifyDataSetChanged();


                        }
                        else {

                            Toast.makeText(QuestionActivity.this, "Không có câu hỏi nào", Toast.LENGTH_SHORT).show();

                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

        binding.addQuestions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(QuestionActivity.this, AddQuestionActivity.class);
                intent.putExtra( "category", categoryName);
                intent.putExtra( "setNum", setNum);
                startActivity(intent);
            }
        });
    }
}