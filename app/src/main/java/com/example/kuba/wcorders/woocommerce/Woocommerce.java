package com.example.kuba.wcorders.woocommerce;


import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.example.kuba.wcorders.woocommerce.connections.RequestQuery;
import com.example.kuba.wcorders.woocommerce.data.Order;
import com.example.kuba.wcorders.woocommerce.data.PostCollection;
import com.example.kuba.wcorders.woocommerce.data.PostId;
import com.example.kuba.wcorders.woocommerce.oauth.OAuth;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;

/*
    Implementation of WoocommerceAPI. Uses Volley for making requests.
 */
public class Woocommerce extends Service implements WoocommerceAPI {
    public class WCBinder extends Binder {
        public Woocommerce getService() {
            return Woocommerce.this;
        }
    }
    private final WCBinder binder = new WCBinder();
    private OAuth authentication = null;
    private PostCollection<Order> orders;
    private String url;
    private RequestQueue queue;

    public void authenticate(@NonNull String url, @NonNull OAuth auth, Response.Listener<PostCollection> onSuccess, Response.ErrorListener onFail ) {
        queue = Volley.newRequestQueue(this);
        this.url = url;
        authentication = auth;
        getNewOrders( onSuccess, onFail );
    }

    public boolean isAuthentictated() {
        return authentication != null;
    }

    @Override
    public PostCollection<Order> getOrders() {
        return new PostCollection<Order>(orders) {
            // This function will not be used
            @Override
            protected Order postFromJson(JSONObject post) throws JSONException, ParseException {
                return new Order(post);
            }
        };
    }

    @Override
    public Order getOrder(PostId id) {
        return orders.getById(id);
    }

    @Override
    public void getNewOrders(Response.Listener<PostCollection> onSuccess, Response.ErrorListener onFail) {
        getJsonArray( new RequestQuery(url,"orders").add("per_page", "100"), (r -> {
            try {
                orders = new PostCollection<Order>(r) {
                    @Override
                    protected Order postFromJson(JSONObject post) throws JSONException, ParseException {
                        return new Order(post);
                    }
                };
            } catch (Exception e) {
                e.printStackTrace();
                onFail.onErrorResponse(new VolleyError("Parsing failed"));
                return;
            }
            onSuccess.onResponse(orders); }
        ), onFail );
    }

    private void getJsonArray( RequestQuery query, Response.Listener<JSONArray> onSuccess, Response.ErrorListener onFail ) {
        authentication.authorize(query);
        String url = query.getUrlWithArguments();

        JsonArrayRequest request = new JsonArrayRequest( Request.Method.GET, url, null, onSuccess, onFail );
        queue.add(request);
    }


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

}
