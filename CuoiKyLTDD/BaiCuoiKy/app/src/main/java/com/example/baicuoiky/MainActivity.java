package com.example.baicuoiky;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.baicuoiky.Adapters.CategoryAdapter;
import com.example.baicuoiky.Models.CategoryModel;
import com.example.baicuoiky.databinding.ActivityMainBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.squareup.picasso.Picasso;
import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends AppCompatActivity {

    ActivityMainBinding binding;
    FirebaseDatabase database;
    FirebaseStorage storage;
    CircleImageView categoryImage, uCategoryImage;
    EditText inputCategoryName, updateName;
    Button uploadCategory, updateCategory;
    View fetchImage, updateFetchImage;
    Dialog dialog, dialog1;
    Uri imageUri,  imageUri2;
    int i = 0;
    ArrayList<CategoryModel> list;
    CategoryAdapter adapter;
    ProgressDialog progressDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        getSupportActionBar().hide();

        database = FirebaseDatabase.getInstance();
        storage = FirebaseStorage.getInstance();

        list = new ArrayList<>();

        dialog = new Dialog(MainActivity.this);
        dialog.setContentView(R.layout.add_category_dialog);

        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialog.setCancelable(true);
        }


            progressDialog = new ProgressDialog(this);
            progressDialog.setTitle("Uploading");
            progressDialog.setMessage("Vui lòng chờ");


            uploadCategory = dialog.findViewById(R.id.btnUpload);
            inputCategoryName = dialog.findViewById(R.id.inputCategoryName);
            categoryImage = dialog.findViewById(R.id.categoryImage);
            fetchImage = dialog.findViewById(R.id.fetchImage);

        GridLayoutManager layoutManager = new GridLayoutManager(this, 2);
        binding.recyCategory.setLayoutManager(layoutManager);

        adapter = new CategoryAdapter(this, list, new CategoryAdapter.DeleteListener() {
            @Override
            public void onClick(int position, String id) {
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setTitle("Xóa");
                builder.setMessage("Bạn có chắc chắn xóa?");
                builder.setPositiveButton("Có", (dialogInterface, i) -> {
                    database.getReference().child("categories")
                            .child(id).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {
                                    Toast.makeText(MainActivity.this, "Đã xóa", Toast.LENGTH_SHORT).show();
                                }
                            });
                });
                builder.setNegativeButton("Không", (dialogInterface, i) -> {
                    dialogInterface.dismiss();
                });

                AlertDialog alertDialog = builder.create();
                alertDialog.show();
            }
        }, new CategoryAdapter.UpdateListener() {
            @Override
            public void onClick(int position, String id) {
                dialog1 = new Dialog(MainActivity.this);
                dialog1.setContentView(R.layout.update_category_dialog);

                if (dialog1.getWindow() != null) {
                    dialog1.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                    dialog1.setCancelable(true);
                }
                dialog1.show();

                updateCategory = dialog1.findViewById(R.id.btnUpdate);
                updateName = dialog1.findViewById(R.id.categoryName_update);
                uCategoryImage = dialog1.findViewById(R.id.categoryImage_update);
                updateFetchImage = dialog1.findViewById(R.id.fetchImage_Update);

                String oldName = list.get(position).getCategoryName();
                updateName.setText(oldName);
                System.out.println("Hello1");
                System.out.println(updateName.getText().toString());
                String uName = updateName.getText().toString();
                uName.trim();

                String oldImage = list.get(position).getCategoryImage();

                Picasso.get().load(oldImage).into(uCategoryImage);

                updateCategory.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String name = updateName.getText().toString();
                        if (imageUri2 == null) {
                            updateData(id, uName);
                        }
                        else if (name.isEmpty()) {
                            updateName.setError("Vui lòng nhập tên");
                        }
                        else {
                            updateData(id, uName);
                        }

                    }
                });
                updateFetchImage.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent();
                        intent.setAction(Intent.ACTION_GET_CONTENT);
                        intent.setType("image/*");
                        startActivityForResult(intent, 2);
                    }
                });

            }
        });
        binding.recyCategory.setAdapter(adapter);

        database.getReference().child("categories").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    list.clear();
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()){

                        list.add(new CategoryModel(
                                dataSnapshot.child("categoryName").getValue().toString(),
                            dataSnapshot.child("categoryImage").getValue().toString(),
                            dataSnapshot.getKey(),
                            Integer.parseInt(dataSnapshot.child("setNum").getValue().toString())
                        ));
                    };

                    adapter.notifyDataSetChanged();

                }
                else{
                    Toast.makeText(MainActivity.this, "Không có danh mục nào!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

                Toast.makeText(MainActivity.this,error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });


        binding.addCategory.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View view){
                    dialog.show();
                }
            });
        fetchImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                startActivityForResult(intent, 1);
            }
        });
            uploadCategory.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String name = inputCategoryName.getText().toString();
                    if (imageUri == null) {
                        Toast.makeText(MainActivity.this,
                        "Vui lòng chọn ảnh", Toast.LENGTH_SHORT).show();
                    }
                    else if (name.isEmpty()) {
                        inputCategoryName.setError("Nhập tên danh mục");
                    }
                    else {
                        progressDialog.show();
                        uploadData();
                    }
                }
            });
    }

    private void uploadData() {
        final StorageReference reference = storage.getReference().child("category")
                .child(new Date().getTime() +"");

        reference.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                reference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {

                        CategoryModel categoryModel = new CategoryModel();
                        categoryModel.setCategoryName(inputCategoryName.getText().toString());
                        categoryModel.setSetNum(0);
                        categoryModel.setCategoryImage(uri.toString());

                        database.getReference().child("categories").push()
                                .setValue(categoryModel).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {
                                        Toast.makeText(MainActivity.this, "Đã thêm",Toast.LENGTH_SHORT).show();
                                        progressDialog.dismiss();
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(MainActivity.this, e.getMessage(),Toast.LENGTH_SHORT).show();
                                        progressDialog.dismiss();
                                    }
                                });
                    }
                });
            }
        });

    };

    private void updateData(String id, String uName) {
        if (imageUri2 != null){
            final StorageReference reference = storage.getReference().child("category")
                    .child(new Date().getTime() +"");
            reference.putFile(imageUri2).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    reference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            CategoryModel categoryModel = new CategoryModel();
                            categoryModel.setCategoryName(updateName.getText().toString());
                            categoryModel.setCategoryImage(uri.toString());

                            database.getReference().child("categories").child(id)
                                    .setValue(categoryModel);
                            Toast.makeText(MainActivity.this, "Đã cập nhật",Toast.LENGTH_SHORT).show();
                            progressDialog.dismiss();
                            imageUri2 = null;
                        }
                    });
                }
            });
        }else {

            database.getReference().child("categories").child(id).child("categoryName")
                    .setValue(updateName.getText().toString());
            Toast.makeText(MainActivity.this, "Đã cập nhật",Toast.LENGTH_SHORT).show();
            progressDialog.dismiss();
        }

    };



    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            if (data != null) {
                imageUri = data.getData();
                categoryImage.setImageURI(imageUri);
            }
        }

        else if (requestCode == 2) {
            if (data != null) {
                imageUri2 = data.getData();
                uCategoryImage.setImageURI(imageUri2);
            }
        }
    };
}