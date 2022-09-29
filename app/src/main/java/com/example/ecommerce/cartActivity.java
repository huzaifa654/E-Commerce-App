package com.example.ecommerce;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.GridLayoutManager;

import com.example.ecommerce.adapters.cartAdapter;
import com.example.ecommerce.databinding.ActivityCartBinding;
import com.example.ecommerce.models.Product;
import com.hishd.tinycart.model.Cart;
import com.hishd.tinycart.model.Item;
import com.hishd.tinycart.util.TinyCartHelper;

import java.util.ArrayList;
import java.util.Map;

public class cartActivity extends AppCompatActivity {
    ActivityCartBinding binding;
    cartAdapter adapter;
    ArrayList<Product> products;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding= ActivityCartBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        binding.continueBtn.setOnClickListener(v -> {
            startActivity(new Intent(cartActivity.this,Checkout.class));

        });



        products= new ArrayList<>();
        Cart cart = TinyCartHelper.getCart();
        for(Map.Entry<Item,Integer>item: cart.getAllItemsWithQty().entrySet()){
            Product product=(Product)item.getKey();
            int quantity=item.getValue();
            product.setQuantity(quantity);
            products.add(product);
        }

        adapter= new cartAdapter(this, products, new cartAdapter.CartListener() {
            @Override
            public void onQuantityChanged() {
                binding.subtotal.setText(String.format("PKR   %.2f ",cart.getTotalPrice()));


            }
        });
        GridLayoutManager layoutManager = new GridLayoutManager(this,1);
        DividerItemDecoration itemDecoration= new DividerItemDecoration(this,layoutManager.getOrientation());
        binding.cartList.addItemDecoration(itemDecoration);
        binding.cartList.setLayoutManager(layoutManager);
        binding.cartList.setAdapter(adapter);
        binding.subtotal.setText(String.format("PKR   %.2f ",cart.getTotalPrice()));

    }


    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return super.onSupportNavigateUp();
    }
}