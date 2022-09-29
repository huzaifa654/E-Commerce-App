package com.example.ecommerce.adapters;

import android.content.Context;
import android.content.Intent;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.ecommerce.Catagoery;
import com.example.ecommerce.R;
import com.example.ecommerce.databinding.ItmeCatogoriesBinding;
import com.example.ecommerce.models.Categories;
import java.util.ArrayList;

//
public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder>{
    Context context;
    ArrayList<Categories> categories;

    public CategoryAdapter(Context context, ArrayList<Categories> categories) {
        this.context = context;
        this.categories = categories;
    }

    @NonNull
    @Override
    public CategoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new CategoryViewHolder(LayoutInflater.from(context).inflate(R.layout.itme_catogories, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull CategoryViewHolder holder, int position) {
        Categories category= categories.get(position);
        holder.binding.label.setText(Html.fromHtml(category.getName()));
        Glide.with(context)
                .load(category.getIcon())
                .into(holder.binding.productImage);
        holder.itemView.setOnClickListener(view -> {
            Intent intent= new Intent(context, Catagoery.class);
            intent.putExtra("catId",category.getId());
            intent.putExtra("catagoeryName",category.getName());
            context.startActivity(intent);


        });



    }

    @Override
    public int getItemCount() {
        return  categories.size();
    }

    public static class CategoryViewHolder extends RecyclerView.ViewHolder {
        ItmeCatogoriesBinding binding;
        public CategoryViewHolder(@NonNull View itemView) {
            super(itemView);
            binding=ItmeCatogoriesBinding.bind(itemView);


        }
    }
}
