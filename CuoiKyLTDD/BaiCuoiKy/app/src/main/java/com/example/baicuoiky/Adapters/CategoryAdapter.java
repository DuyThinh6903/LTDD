package com.example.baicuoiky.Adapters;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.baicuoiky.MainActivity;
import com.example.baicuoiky.Models.CategoryModel;
import com.example.baicuoiky.R;
import com.example.baicuoiky.SetsActivity;
import com.example.baicuoiky.databinding.ItemCategoryBinding;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.viewHolder> {

    Context context;
    ArrayList<CategoryModel>list;
    FirebaseDatabase database;

    DeleteListener listener;

    UpdateListener uListener;

    public CategoryAdapter(Context context, ArrayList<CategoryModel> list, DeleteListener listener, UpdateListener uListener) {
        this.context = context;
        this.list = list;
        this.listener = listener;
        this.uListener = uListener;
    }

    @NonNull
    @Override
    public viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(context).inflate(R.layout.item_category,parent,false);
        return new viewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull viewHolder holder, int position) {

        CategoryModel model = list.get(position);

        holder.binding.categoryName.setText(model.getCategoryName());

        Picasso.get()
                .load(model.getCategoryImage())
                .placeholder(R.drawable.upload)
                .into(holder.binding.categoryImages);

        holder.binding.deleteCategory.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                String id = list.get(holder.getAdapterPosition()).getKey();
                listener.onClick(holder.getAdapterPosition(),id);
            }
        });

        holder.binding.editCategory.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                String id = list.get(holder.getAdapterPosition()).getKey();
                uListener.onClick(holder.getAdapterPosition(),id);
            }
        });

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(context, SetsActivity.class);
                intent.putExtra("category",model.getCategoryName());
                intent.putExtra("sets",model.getSetNum());
                intent.putExtra("key",model.getKey());

                context.startActivity(intent);

            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    //them
    public class viewHolder extends RecyclerView.ViewHolder {

        ItemCategoryBinding binding;
        public viewHolder(@NonNull View itemView) {
            super(itemView);

            binding = ItemCategoryBinding.bind(itemView);

        }
    }

    public interface DeleteListener{
        public void onClick(int position, String id);
    }

    public interface UpdateListener{
        public void onClick(int position, String id);
    }
}
