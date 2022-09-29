package com.example.ecommerce.adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.media.Image;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.ecommerce.R;
import com.example.ecommerce.databinding.CartitemBinding;
import com.example.ecommerce.databinding.QuantitydilogBinding;
import com.example.ecommerce.models.Product;
import com.hishd.tinycart.model.Cart;
import com.hishd.tinycart.util.TinyCartHelper;


import java.util.ArrayList;

public class cartAdapter extends RecyclerView.Adapter<cartAdapter.cartViewHolder> {


    ;
    Context context;
    ArrayList<Product> products;
    CartListener cartListener;
    Cart cart;




    public  interface CartListener{
        public  void onQuantityChanged();
    }

    public cartAdapter(Context context, ArrayList<Product> products, CartListener cartListener) {
        this.context = context;
        this.products = products;
        this.cartListener= cartListener;
        cart = TinyCartHelper.getCart();
    }

    @NonNull
    @Override
    public cartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new cartViewHolder(LayoutInflater.from(context).inflate(R.layout.cartitem, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull cartViewHolder holder, int position) {
        Product model = products.get(position);


        Glide.with(context)
                .load(model.getImage())
                .into(holder.binding.image);
        holder.binding.price.setText("PKR" + model.getPrice());
        holder.binding.name.setText(model.getName());
        holder.binding.quantity.setText(String.valueOf(model.getQuantity()));
        holder.itemView.setOnClickListener(v -> {
            QuantitydilogBinding binding = QuantitydilogBinding.inflate(LayoutInflater.from(context));
            binding.productName.setText(model.getName());
            binding.productStock.setText("Stock" + model.getStock());
            binding.quantity.setText(String.valueOf(model.getQuantity()));

            int Stock = (int) model.getStock();
            binding.plusBtn.setOnClickListener(v14 -> {
                int quantity = model.getQuantity();
                quantity++;

                if (quantity > model.getStock()) {
                    Toast.makeText(context, "Stock available is : " + Stock, Toast.LENGTH_SHORT).show();
                    return;
                } else {
                    model.setQuantity(quantity);
                    binding.quantity.setText(String.valueOf(quantity));
                    notifyDataSetChanged();
                    cart.updateItem(model,model.getQuantity());
                    cartListener.onQuantityChanged();

                }


            });

            binding.minusBtn.setOnClickListener(v14 -> {
                int quantity = model.getQuantity();
                if (quantity > 1) {
                    quantity--;
                    model.setQuantity(quantity);
                    binding.quantity.setText(String.valueOf(quantity));
                    notifyDataSetChanged();
                    cart.updateItem(model,model.getQuantity());
                    cartListener.onQuantityChanged();

                } else {
                    Toast.makeText(context, "Quantity should be above 1 ", Toast.LENGTH_SHORT).show();
                }


            });




            AlertDialog dialog = new AlertDialog.Builder(context)
                    .setView(binding.getRoot())
                    .create();

            dialog.show();
            binding.saveBtn.setOnClickListener(v1 -> {
                dialog.dismiss();
                notifyDataSetChanged();
                cart.updateItem(model,model.getQuantity());
                cartListener.onQuantityChanged();


            });

            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.R.color.transparent));


        });


    }

    @Override
    public int getItemCount() {
        return products.size();
    }

    public class cartViewHolder extends RecyclerView.ViewHolder {
        CartitemBinding binding;

        public cartViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = CartitemBinding.bind(itemView);


        }
    }
}
