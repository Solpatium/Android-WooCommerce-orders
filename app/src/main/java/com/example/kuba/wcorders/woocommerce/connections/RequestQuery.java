package com.example.kuba.wcorders.woocommerce.connections;

import android.content.res.Resources;
import android.support.annotation.NonNull;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;


import static com.example.kuba.wcorders.woocommerce.connections.RequestQuery.RequestType.GET;

/*
    Builder used for generating request URL.
 */
public class RequestQuery {
    public enum RequestType {
        GET, POST;
    }
    private String endpoint;
    private Map<String, String> arguments = new HashMap<>();
    private final String standardEndpoint = "/wp-json/wc/v1/";
    public final RequestType type;
    public final String targetUrl;


    public RequestQuery(@NonNull String targetUrl, @NonNull String endpoint ) {
        this(targetUrl, endpoint, GET);
    }

    public RequestQuery(@NonNull String targetUrl, @NonNull String endpoint, @NonNull  RequestType type) {
        this.targetUrl = targetUrl;
        this.endpoint = endpoint;
        this.type = type;
    }

    public RequestQuery add(@NonNull String name, @NonNull String value) {
        if( arguments.containsKey(name) ) {
            throw new IllegalArgumentException("Argument already exists");
        }

        arguments.put(name, value);

        return this;
    }

    public String get(String name) {
        if( !arguments.containsKey(name) ) throw new Resources.NotFoundException("Such key doesn't exist");
        return arguments.get(name);
    }

    private String getStringArguments() throws UnsupportedEncodingException {
        StringBuilder builder = new StringBuilder();
        for (Map.Entry<?,?> entry : arguments.entrySet()) {
            if (builder.length() > 0) {
                builder.append("&");
            }
            builder.append(String.format("%s=%s",
                    URLEncoder.encode(entry.getKey().toString(), "UTF-8"),
                    URLEncoder.encode(entry.getValue().toString(), "UTF-8")
            ));
        }
        return builder.toString();
    }

    public void sortByKey() {
        arguments = new TreeMap<String,String>(arguments);
    }

    public Map<String,String> getArguments() {
        return new TreeMap<>(arguments);
    }

    public String getBareUrl() {
        return targetUrl+standardEndpoint+endpoint;
    }

    public String getUrlWithArguments() {
        try {
            return getBareUrl() + "?" + getStringArguments();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            throw new RuntimeException();
        }
    }

}
