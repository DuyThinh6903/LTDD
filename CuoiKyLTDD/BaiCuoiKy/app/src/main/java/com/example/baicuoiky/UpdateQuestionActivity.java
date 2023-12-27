package com.example.baicuoiky;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;

import com.example.baicuoiky.Models.CategoryModel;
import com.example.baicuoiky.Models.QuestionModel;
import com.example.baicuoiky.databinding.ActivityUpdateQuestionBinding;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class UpdateQuestionActivity extends AppCompatActivity {

    ActivityUpdateQuestionBinding binding;
    String categoryName,id, correctAns, optionA, optionB, optionC, optionD;
    ArrayList<QuestionModel> list;
    FirebaseDatabase database;

    RadioButton radioButton;
    int correct = -1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityUpdateQuestionBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        categoryName = getIntent().getStringExtra("category");
        id = getIntent().getStringExtra("id");

        database = FirebaseDatabase.getInstance();

        database.getReference().child("Sets").child(categoryName).child("questions")
                .child(id).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        binding.updateQuestion.setText(snapshot.child("question").getValue().toString());

                        optionA = snapshot.child("optionA").getValue().toString();
                        optionB = snapshot.child("optionB").getValue().toString();
                        optionC = snapshot.child("optionC").getValue().toString();
                        optionD = snapshot.child("optionD").getValue().toString();

                        correctAns = snapshot.child("correctAnsw").getValue().toString();
                        if(correctAns.equals(optionA)) {
                            radioButton = (RadioButton) binding.optionContainer.getChildAt(0);
                            radioButton.setChecked(true);
                        }
                        else if(correctAns.equals(optionB)) {
                            radioButton = (RadioButton) binding.optionContainer.getChildAt(1);
                            radioButton.setChecked(true);
                        }
                        else if(correctAns.equals(optionC)) {
                            radioButton = (RadioButton) binding.optionContainer.getChildAt(2);
                            radioButton.setChecked(true);
                        }
                        else {
                            radioButton = (RadioButton) binding.optionContainer.getChildAt(3);
                            radioButton.setChecked(true);
                        }

                        ((EditText)binding.answerContainer.getChildAt( 0)).setText(optionA);
                        ((EditText)binding.answerContainer.getChildAt( 1)).setText(optionB);
                        ((EditText)binding.answerContainer.getChildAt( 2)).setText(optionC);
                        ((EditText)binding.answerContainer.getChildAt( 3)).setText(optionD);

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

        binding.btnUpdateQuestion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                database.getReference().child("Sets").child(categoryName).child("questions").child(id)
                        .child("question").setValue(binding.updateQuestion.getText().toString());
                database.getReference().child("Sets").child(categoryName).child("questions").child(id)
                        .child("optionA").setValue(((EditText)binding.answerContainer.getChildAt( 0)).getText().toString());
                database.getReference().child("Sets").child(categoryName).child("questions").child(id)
                        .child("optionB").setValue(((EditText)binding.answerContainer.getChildAt( 1)).getText().toString());
                database.getReference().child("Sets").child(categoryName).child("questions").child(id)
                        .child("optionC").setValue(((EditText)binding.answerContainer.getChildAt( 2)).getText().toString());
                database.getReference().child("Sets").child(categoryName).child("questions").child(id)
                        .child("optionD").setValue(((EditText)binding.answerContainer.getChildAt( 3)).getText().toString());

                for (int i =0; i<4; i++){
                    RadioButton radioButton = (RadioButton) binding.optionContainer.getChildAt(i);
                    if (radioButton.isChecked()){
                        correct = i;
                        break;
                    }
                }

                database.getReference().child("Sets").child(categoryName).child("questions").child(id)
                        .child("correctAnsw").setValue(((EditText)binding.answerContainer.getChildAt( correct)).getText().toString());
                Toast.makeText( UpdateQuestionActivity.this, "Đã cập nhật câu hỏi", Toast.LENGTH_SHORT).show();
            }
        });
    }
}