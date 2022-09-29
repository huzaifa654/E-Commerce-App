package com.example.ecommerce;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.GridLayoutManager;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.ecommerce.adapters.cartAdapter;
import com.example.ecommerce.databinding.ActivityCheckoutBinding;
import com.example.ecommerce.models.Product;
import com.example.ecommerce.utilites.Contants;
import com.hishd.tinycart.model.Cart;
import com.hishd.tinycart.model.Item;
import com.hishd.tinycart.util.TinyCartHelper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class Checkout extends AppCompatActivity {
    ActivityCheckoutBinding binding;
    cartAdapter adapter;
    ArrayList<Product> products;
    double totalPrice = 0;
    final int tax = 11;
    ProgressDialog progressDialog;
    Cart cart;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCheckoutBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Processing...");

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        binding.processorder.setOnClickListener(v -> {
            processOrder();

        });

        products = new ArrayList<>();
        cart = TinyCartHelper.getCart();
        for (Map.Entry<Item, Integer> item : cart.getAllItemsWithQty().entrySet()) {
            Product product = (Product) item.getKey();
            int quantity = item.getValue();
            product.setQuantity(quantity);
            products.add(product);
        }

        adapter = new cartAdapter(this, products, new cartAdapter.CartListener() {
            @Override
            public void onQuantityChanged() {
                binding.subtotal.setText(String.format("PKR   %.2f ", cart.getTotalPrice()));
                totalPrice = (cart.getTotalPrice().doubleValue() * tax / 100) + cart.getTotalPrice().doubleValue();
                binding.total.setText("PKR" + totalPrice);


            }
        });
        GridLayoutManager layoutManager = new GridLayoutManager(this, 1);
        DividerItemDecoration itemDecoration = new DividerItemDecoration(this, layoutManager.getOrientation());
        binding.cartList.addItemDecoration(itemDecoration);
        binding.cartList.setLayoutManager(layoutManager);
        binding.cartList.setAdapter(adapter);
        binding.subtotal.setText(String.format("PKR   %.2f ", cart.getTotalPrice()));
        totalPrice = (cart.getTotalPrice().doubleValue() * tax / 100) + cart.getTotalPrice().doubleValue();
        binding.total.setText("PKR" + totalPrice);


    }

    void processOrder() {
        progressDialog.show();
        RequestQueue queue = Volley.newRequestQueue(this);
        JSONObject productOrder = new JSONObject();
        JSONObject dataOject = new JSONObject();
        try {
            productOrder.put("address", binding.addressBox.getText().toString());
            productOrder.put("buyer", binding.nameBox.getText().toString());
            productOrder.put("comment", binding.commentbox.getText().toString());
            productOrder.put("created_at", Calendar.getInstance().getTimeInMillis());
            productOrder.put("last_update", Calendar.getInstance().getTimeInMillis());
            productOrder.put("date_ship", Calendar.getInstance().getTimeInMillis());
            productOrder.put("email", binding.emailBox.getText().toString());
            productOrder.put("phone", binding.numberBox.getText().toString());
            productOrder.put("serial", "cab8c1a4e4421a3b");
            productOrder.put("shipping", "");
            productOrder.put("shipping_location", "");
            productOrder.put("shipping_rate", "0.0");
            productOrder.put("status", "WAITING");
            productOrder.put("tax", tax);
            productOrder.put("total_fees", totalPrice);


            JSONArray product_order_detail = new JSONArray();
            for (Map.Entry<Item, Integer> item : cart.getAllItemsWithQty().entrySet()) {
                Product product = (Product) item.getKey();
                int quantity = item.getValue();
                product.setQuantity(quantity);
                products.add(product);
                JSONObject productObject = new JSONObject();
                productObject.put("amount", quantity);
                productObject.put("price_item", product.getPrice());
                productObject.put("product_id", product.getId());
                productObject.put("product_name", product.getName());
                product_order_detail.put(productObject);

            }
            dataOject.put("product_order", productOrder);
            dataOject.put("product_order_detail", product_order_detail);
            Log.e("err", dataOject.toString());


        } catch (JSONException e) {
        }
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, Contants.POST_ORDER_URL, dataOject, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {

                    if (response.getString("status").equals("success")) {
                        Toast.makeText(Checkout.this, "Order Success", Toast.LENGTH_SHORT).show();
                        String orderNumber=response.getJSONObject("data").getString("code");
                        new AlertDialog.Builder(Checkout.this)
                                .setTitle("Order Successful")
                                .setMessage("Your Order Number is" + " "+ orderNumber)
                                .setPositiveButton("Pay Now", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        Intent intent = new Intent(Checkout.this,Payment.class);
                                        intent.putExtra("orderCode",orderNumber);
                                        startActivity(intent);


                                    }
                                }).show()
                        ;

                    } else {
                        Toast.makeText(Checkout.this, "Order Failed", Toast.LENGTH_SHORT).show();
                        new AlertDialog.Builder(Checkout.this)
                                .setTitle("Order Failed")
                                .setMessage("Something went wrong please try again.")
                                .setPositiveButton("Close", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {

                                    }
                                }).show()
                        ;


                    }
                    progressDialog.dismiss();
                    Log.e("res", response.toString());
                } catch (Exception e) {
                }


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                headers.put("Security", "secure_code");
                return headers;
            }
        };
        queue.add(request);
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return super.onSupportNavigateUp();
    }



}
