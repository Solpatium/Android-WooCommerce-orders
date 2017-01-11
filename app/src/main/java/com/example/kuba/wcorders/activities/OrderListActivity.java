package com.example.kuba.wcorders.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;


import com.example.kuba.wcorders.fragments.OrderDetailFragment;
import com.example.kuba.wcorders.R;
import com.example.kuba.wcorders.WcOrders;
import com.example.kuba.wcorders.views.OrderRecyclerAdapter;
import com.example.kuba.wcorders.woocommerce.data.Order;

/*
    Class displaying list of orders.
    If device is wide enough OrderDetailFragment is displayed.
 */
public class OrderListActivity extends AppCompatActivity {

    private boolean isTwoPane;
    private RecyclerView orderListRecycler;
    private SwipeRefreshLayout swipeContainer;

    public void openOrderDetail(final Order order) {
        if (isTwoPane) {
            Bundle arguments = new Bundle();
            arguments.putInt("id", order.id.id );
            OrderDetailFragment fragment = new OrderDetailFragment();
            fragment.setArguments(arguments);
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.order_detail_container, fragment)
                    .commit();
        } else {
            Intent intent = new Intent(this, OrderDetailActivity.class);
            intent.putExtra("id", order.id.id );
            startActivity(intent);
        }
    }

    public boolean isTwoPane() {
        return isTwoPane;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if( !WcOrders.isAuthenticated() ) {
            Intent intent = new Intent(this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            this.startActivity(intent);
            finish();
        }

        setContentView(R.layout.activity_order_list);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle("Orders");

        setupRecyclerView();

        if (findViewById(R.id.order_detail_container) != null) {
            // The detail container view will be present only in the large-screen layouts
            isTwoPane = true;
        }

        setupRefresh();
    }


    private void setupRefresh() {
        swipeContainer = (SwipeRefreshLayout) findViewById(R.id.swipeContainer);
        // Setup refresh listener which triggers new data loading
        swipeContainer.setOnRefreshListener(()->refresh());
        // Configure the refreshing colors
        swipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);

    }

    private void refresh() {
        swipeContainer.setRefreshing(true);
        WcOrders.getWoocommerce().getNewOrders(
                (orders)->{
                    ((OrderRecyclerAdapter)orderListRecycler.getAdapter()).update(orders);
                    swipeContainer.setRefreshing(false);
                }
                , ((e)-> refreshFailed() ) );
    }

    private void refreshFailed() {
        swipeContainer.setRefreshing(false);
        AlertDialog.Builder dlgAlert  = new AlertDialog.Builder(this);

        dlgAlert.setMessage("Can't refresh");
        dlgAlert.setTitle("Failure");
        dlgAlert.setPositiveButton("OK", null);
        dlgAlert.create().show();
    }

    private void setupRecyclerView() {
        orderListRecycler = (RecyclerView) findViewById(R.id.order_list);

        if( WcOrders.isAuthenticated() ) {
            new OrderRecyclerAdapter(
                    orderListRecycler,
                    WcOrders.getWoocommerce().getOrders(),
                    this);
        }
    }
    
}
