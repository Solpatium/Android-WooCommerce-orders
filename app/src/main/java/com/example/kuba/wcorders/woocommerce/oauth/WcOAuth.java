package com.example.kuba.wcorders.woocommerce.oauth;

import android.support.annotation.NonNull;

import com.example.kuba.wcorders.woocommerce.connections.RequestQuery;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
import java.util.UUID;
import android.util.Base64;

import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

/*
    OAuth implementation. Authorizes Woocommerce requests.
    Uses one legged authentication.
 */
public class WcOAuth implements OAuth {

    private static final String HASH_ALGORITHM = "HMAC-SHA1";
    private static final String URL_ENCODING  = "UTF-8";
    private final String consumerKey;
    private final String consumerSecret;

    public WcOAuth( String consumerKey, String consumerSecret ) {
        this.consumerKey = consumerKey;
        this.consumerSecret = consumerSecret;
    }

    private String percentEncode(String string ) throws UnsupportedEncodingException {
        return URLEncoder.encode(string, URL_ENCODING);
    }

    @Override
    public void authorize(@NonNull RequestQuery query ) {
        Long tsLong = System.currentTimeMillis()/1000;
        String timestamp = tsLong.toString();

        UUID uuid = UUID.randomUUID();
        String nonce = uuid.toString();

        query.add("oauth_consumer_key", consumerKey);
        query.add("oauth_timestamp", timestamp);
        query.add("oauth_nonce", nonce);
        query.add("oauth_signature_method", HASH_ALGORITHM);
        query.sortByKey();
        query.add("oauth_signature", generateSignature(query));
        query.sortByKey();
    }

    private String getSecret() {
        return consumerSecret + '&';
    }

    private String generateSignature( RequestQuery query ) {
        try {
            /*
                I Collect parameters
             */
            Map<String,String> encodedArguments = new HashMap<>();

            for (Map.Entry<String, String> parameter : query.getArguments().entrySet()) {
                encodedArguments.put( percentEncode(parameter.getKey()), percentEncode(parameter.getValue()));
            }

            Map<String,String> sortedEncodedArguments = new TreeMap<>(encodedArguments);
            StringBuilder queryStringBuilder = new StringBuilder();
            for (Map.Entry<String, String> parameter : sortedEncodedArguments.entrySet()) {
                queryStringBuilder
                        .append(parameter.getKey())
                        .append('=')
                        .append(parameter.getValue())
                        .append('&');
            }
            String queryString = queryStringBuilder.toString().substring(0,queryStringBuilder.length()-1); // Remove last &

            /*
                II Create the signature base string
             */
            String signatureBase = query.type.toString() + "&" + percentEncode(query.getBareUrl()) + "&" + percentEncode(queryString);

            /*
                III Generate the signature
             */
            return SHA1(signatureBase, getSecret());
        }catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException();
        }
    }


    private static String SHA1(String baseString, String keyString) throws NoSuchAlgorithmException, UnsupportedEncodingException {
        byte[] keyBytes = keyString.getBytes();
        SecretKey secretKey = new SecretKeySpec(keyBytes, "HmacSHA1");

        Mac mac = Mac.getInstance("HmacSHA1");

        try {
            mac.init(secretKey);
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        }

        byte[] text = baseString.getBytes();

        return new String(Base64.encodeToString(mac.doFinal(text), Base64.DEFAULT)).trim();
    }


}
