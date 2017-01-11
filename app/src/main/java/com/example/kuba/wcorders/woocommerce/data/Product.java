package com.example.kuba.wcorders.woocommerce.data;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.math.BigDecimal;
import java.util.Vector;

public class Product extends Post {
    public class MetaElement {
        public final String label, key;

        public MetaElement(String label, String key) {
            this.label = label;
            this.key = key;
        }

        public String toSting() {
            return label+ ": " + key;
        }
    }

    public final String name;
    public final BigDecimal price;
    private final Vector<MetaElement> meta;

    public Product(JSONObject product) throws JSONException {
        super(new PostId(product.getInt("id")));
        name = product.getString("name");
        price = new BigDecimal(product.getLong("price"));

        // Meta
        JSONArray metaElements = product.getJSONArray("meta");
        meta = new Vector<>(metaElements.length());
        JSONObject jsonObject;
        for(int i=0; i<metaElements.length(); i++) {
            jsonObject = (JSONObject)metaElements.get(i);
            meta.add(new MetaElement( jsonObject.getString("label"), jsonObject.getString("key")));
        }
    }

    public Vector<MetaElement> getMeta() {
        return new Vector<>(meta);
    }
}
