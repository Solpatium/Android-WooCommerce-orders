package com.example.kuba.wcorders.woocommerce;

import com.android.volley.Response;
import com.example.kuba.wcorders.woocommerce.data.Order;
import com.example.kuba.wcorders.woocommerce.data.PostCollection;
import com.example.kuba.wcorders.woocommerce.data.PostId;

/*
    Interface API of our service.
 */
public interface WoocommerceAPI {
    PostCollection<Order> getOrders();
    Order getOrder(PostId id);
    void getNewOrders(Response.Listener<PostCollection> onSuccess, Response.ErrorListener onFail);
}
