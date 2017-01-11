package com.example.kuba.wcorders.woocommerce.data;

import org.json.JSONException;
import org.json.JSONObject;
import java.math.BigDecimal;
import java.util.Currency;

/*
    Class used form modeling ordered products.
 */
public class ProductOrdered extends Product {

    public final int quantity;
    public final Currency currency;
    public ProductOrdered(Currency currency, JSONObject product) throws JSONException {
        super(product);
        this.currency = currency;
        quantity = product.getInt("quantity");
    }

    public String getPrice() {
        return price.multiply(new BigDecimal(quantity)).toString()+currency.getSymbol();
    }
}
