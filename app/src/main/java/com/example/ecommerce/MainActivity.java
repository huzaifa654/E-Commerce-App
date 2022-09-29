package com.example.ecommerce;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.ecommerce.adapters.CategoryAdapter;
import com.example.ecommerce.adapters.productAdapter;
import com.example.ecommerce.databinding.ActivityMainBinding;
import com.example.ecommerce.models.Categories;
import com.example.ecommerce.models.Product;
import com.example.ecommerce.utilites.Contants;
import com.mancj.materialsearchbar.MaterialSearchBar;

import org.imaginativeworld.whynotimagecarousel.model.CarouselItem;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    ActivityMainBinding binding;
    CategoryAdapter categoryAdapter;
    ArrayList<Categories> categories;


    productAdapter productAdapter;
    ArrayList<Product> products;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        getSupportActionBar().hide();
        binding.searchBar.setOnSearchActionListener(new MaterialSearchBar.OnSearchActionListener() {
            @Override
            public void onSearchStateChanged(boolean enabled) {

            }

            @Override
            public void onSearchConfirmed(CharSequence text) {
                Intent intent= new Intent(MainActivity.this, searchActivity.class);
                intent.putExtra("query", text.toString());
                startActivity(intent);


            }

            @Override
            public void onButtonClicked(int buttonCode) {

            }
        });

        initCatogories();
        initProducts();
        initSlider();
        getCatogories();
        getRecentProducts();

    }

    void getCatogories(){
        RequestQueue queue = Volley.newRequestQueue(this);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, Contants.GET_CATEGORIES_URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject mainObj= new JSONObject(response);
                    if (mainObj.getString("status").equals("success")){
                        JSONArray catogoriesArray= mainObj.getJSONArray("categories");
                        for (int i=0; i<catogoriesArray.length();i++){
                            JSONObject object=catogoriesArray.getJSONObject(i);
                            Categories catagory=new Categories(
                                    object.getString("name"),
                                    Contants.CATEGORIES_IMAGE_URL + object.getString("icon"),
                                    object.getString("color"),
                                    object.getString("brief"),
                                    object.getInt("id")
                            );
                            categories.add(catagory);

                        }
                        categoryAdapter.notifyDataSetChanged();

                    }else {
                        //donothing
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        queue.add(stringRequest);


    }
 void getRecentProducts(){
     RequestQueue queue = Volley.newRequestQueue(this);
     String URL=Contants.GET_PRODUCTS_URL + "?count=8";
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

 void getRecentoffers(){
     RequestQueue queue = Volley.newRequestQueue(this);
     StringRequest request= new StringRequest(Request.Method.GET,Contants.GET_OFFERS_URL,response -> {
         try {
             JSONObject object=new JSONObject(response);

             if (object.getString("status").equals("success")){
                 JSONArray offerArrray= object.getJSONArray("news_infos");
                 for (int i =0; i< offerArrray.length();i++){

                     JSONObject childObj=offerArrray.getJSONObject(i);
                     binding.carousel.addData(
                             new CarouselItem(
                                Contants.NEWS_IMAGE_URL  +  childObj.getString("image"),
                                     childObj.getString("title")
                             )


                     );
                 }


             }
         } catch (JSONException e) {
             e.printStackTrace();
         }

     },error -> {});
     queue.add(request);

 }


    private void initSlider() {
        getRecentoffers();

    }

    void initCatogories() {
        categories = new ArrayList<>();

        categoryAdapter = new CategoryAdapter(this, categories);
        GridLayoutManager layoutManager = new GridLayoutManager(this, 4);
        binding.categoriesList.setLayoutManager(layoutManager);
        binding.categoriesList.setAdapter(categoryAdapter);

    }

    void initProducts() {
        products = new ArrayList<>();
        productAdapter = new productAdapter(this, products);
        GridLayoutManager layoutManager = new GridLayoutManager(this, 2);
        binding.productRecyclerView.setLayoutManager(layoutManager);
        binding.productRecyclerView.setAdapter(productAdapter);

    }

}