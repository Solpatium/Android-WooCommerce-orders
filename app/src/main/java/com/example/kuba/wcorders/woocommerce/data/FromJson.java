package com.example.kuba.wcorders.woocommerce.data;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/*
    Just an abstract class for mapFromJson.
 */
public abstract class FromJson {
    static Map<String,String> mapFromJson(JSONObject object) {
        Iterator<String> nameItr = object.keys();
        Map<String, String> outMap = new HashMap<String, String>();
        while(nameItr.hasNext()) {
            String name = nameItr.next();
            try {
                outMap.put(name, object.getString(name));
            } catch (JSONException e) {
                // Do nothing
            }
        }
        return outMap;
    }
}
