package com.example.kuba.wcorders.woocommerce;

import android.content.ComponentName;
import android.content.ServiceConnection;
import android.os.IBinder;

/*
    Class needed to connect to service.
 */
public class WoocommerceConnection implements ServiceConnection {
    private Woocommerce wc;

    @Override
    public void onServiceConnected(ComponentName className, IBinder service) {
        // We've bound to LocalService, cast the IBinder and get LocalService instance
        wc = ((Woocommerce.WCBinder) service).getService();
    }

    @Override
    public void onServiceDisconnected(ComponentName componentName) {
        wc = null;
    }

    public boolean isConnected() {
        return wc != null;
    }

    public Woocommerce getWoocommerce() {
        return wc;
    }


}
