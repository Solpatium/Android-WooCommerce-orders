package com.example.kuba.wcorders.woocommerce.oauth;

import com.example.kuba.wcorders.woocommerce.connections.RequestQuery;

/*
    Interface used for authorizing any request
 */
public interface OAuth {
    void authorize(RequestQuery query);
}
