package com.example.kuba.wcorders.woocommerce.data;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Currency;
import java.util.Date;
import java.util.Locale;
import java.util.Map;

/*
    Data class modeling order from WooCommerce.
 */
public class Order extends Post {

    public enum Status {
        completed,
        processing,
        pending,
        deleted,
        on_hold,
        cancelled,
        refunded,
        failed,
        other; // just in case, new states might be implemented by plugins

        public static Status fromString( final String state ) {
            try {
                return valueOf(state.replace("-","_")); // We need to replace - with _, enums in java doesn't allow "-"
            } catch( IllegalArgumentException e ) {
                return other;
            }
        }
    }

    public final String name, surname;
    public final Status status;
    public final Date created;
    public final Currency currency;
    public final BigDecimal total;
    public final String paymentMethod;
    public final PostCollection<ProductOrdered> products;
    public final Map<String,String> billing;
    public final Map<String,String> shipping;

    public Order( JSONObject order ) throws JSONException, ParseException {
        super(new PostId(order.getString("id")));
        JSONObject billing = order.getJSONObject("billing");
        JSONObject shipping = order.getJSONObject("shipping");
        name = billing.getString("first_name");
        surname = billing.getString("last_name");
        status = Status.fromString(order.getString("status"));
        DateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm", Locale.getDefault());
        created = format.parse(order.getString("date_created"));

        currency = Currency.getInstance(order.getString("currency"));
        total = new BigDecimal(order.getLong("total"));
        paymentMethod = order.getString("payment_method");

        this.billing = mapFromJson(billing);
        if( shipping.length() > 0 ) {
            this.shipping = mapFromJson(shipping);
        } else {
            this.shipping = this.billing;
        }

        JSONArray lineItems = order.getJSONArray("line_items");
        this.products = new PostCollection<ProductOrdered>(lineItems) {
            @Override
            protected ProductOrdered postFromJson(JSONObject post) throws JSONException, ParseException {
                return new ProductOrdered(currency, post);
            }
        };
    }

    public String getCustomerFullname() {
        if( name.isEmpty() && surname.isEmpty() ) {
            return "Unknown";
        }
        return name + " " + surname;
    }

    public String getTotal() { return total.toString()+currency.getSymbol(); }

    public String getIdString() {
        return id.toString();
    }

    public String toString() {
        return getIdString() + " by " + getCustomerFullname();
    }

    public boolean equals( Object order ) {
        if( order == null || !(order instanceof Order) ) {
            return false;
        }

        return ((Order) order).id.equals(id);
    }
}
