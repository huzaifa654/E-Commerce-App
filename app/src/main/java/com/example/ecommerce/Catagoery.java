package com.example.ecommerce;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;

import android.os.Bundle;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.ecommerce.adapters.productAdapter;
import com.example.ecommerce.databinding.ActivityCatagoeryBinding;
import com.example.ecommerce.models.Product;
import com.example.ecommerce.utilites.Contants;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class Catagoery extends AppCompatActivity {
    ActivityCatagoeryBinding binding;
    productAdapter productAdapter;
    ArrayList<Product> products;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=ActivityCatagoeryBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        products = new ArrayList<>();
        productAdapter = new productAdapter(this, products);
        int catId=getIntent().getIntExtra("catId",0);
        String catagoeryName=getIntent().getStringExtra("catagoeryName");
        getSupportActionBar().setTitle(catagoeryName);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        getProducts(catId);
        GridLayoutManager layoutManager = new GridLayoutManager(this, 2);
        binding.productRecyclerView.setLayoutManager(layoutManager);
        binding.productRecyclerView.setAdapter(productAdapter);



    }
    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return super.onSupportNavigateUp();
    }
    void getProducts(int catId){
        RequestQueue queue = Volley.newRequestQueue(this);
        String URL= Contants.GET_PRODUCTS_URL + "?category_id="+ catId;
        StringRequest request=new StringRequest(Request.Method.GET, URL, response -> {
            try {
                JSONObject object= new JSONObject(response);
                if (object.getString("status").equals("success")) {
                    JSONArray productsArray = object.getJSONArray("products");
                    for (int i = 0; i < productsArray.length(); i++) {
                        JSONObject chilObject = productsArray.getJSONObject(i);
                        Product product = new Product(
                                chilObject.getString("name"),
                                Contants.PRODUCTS_IMAGE_URL + chilObject.getString("image"),
                                chilObject.getString("status"),
                                chilObject.getDouble("price"),
                                chilObject.getDouble("price_discount"),
                                chilObject.getInt("stock"),
                                chilObject.getInt("id")


                        );
                        products.add(product);
                    }
                    productAdapter.notifyDataSetChanged();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }, error -> {

        });
        queue.add(request);

    }

}