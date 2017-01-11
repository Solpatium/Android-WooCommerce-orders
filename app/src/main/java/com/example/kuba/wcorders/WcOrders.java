package com.example.kuba.wcorders;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import com.android.volley.Response;
import com.example.kuba.wcorders.woocommerce.Woocommerce;
import com.example.kuba.wcorders.woocommerce.WoocommerceAPI;
import com.example.kuba.wcorders.woocommerce.WoocommerceConnection;
import com.example.kuba.wcorders.woocommerce.data.PostCollection;
import com.example.kuba.wcorders.woocommerce.exceptions.NotAuthenticatedYetException;
import com.example.kuba.wcorders.woocommerce.oauth.OAuth;

/*
    Main activity that connects to service.
 */
public class WcOrders extends Application {
    private static WoocommerceConnection wcConnection;

    public void onCreate() {
        Intent serviceIntent = new Intent(this, Woocommerce.class );
        wcConnection = new WoocommerceConnection();
        startService(serviceIntent);
        bindService(serviceIntent, wcConnection, Context.BIND_ABOVE_CLIENT);
    }

    public static WoocommerceAPI getWoocommerce() {
        if( !wcConnection.isConnected() || !wcConnection.getWoocommerce().isAuthentictated() ) {
            throw new NotAuthenticatedYetException();
        }
        return wcConnection.getWoocommerce();
    }

    public static boolean isAuthenticated() {
        return wcConnection.isConnected() && wcConnection.getWoocommerce().isAuthentictated();
    }

    public static void authenticate(@NonNull String url, @NonNull OAuth auth, Response.Listener<PostCollection> onSuccess, Response.ErrorListener onFail ) {
        wcConnection.getWoocommerce().authenticate(url,auth,onSuccess,onFail);
    }


}