package com.example.kuba.wcorders.woocommerce.data;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.text.ParseException;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/*
    Pseudo collection used for passing posts around.
 */
public abstract class PostCollection<PostType extends Post>{
    private List<PostType> postList = new LinkedList<>();

    public PostCollection(JSONArray posts) throws JSONException, ParseException {
        JSONObject post;
        for(int i=0; i<posts.length(); i++ ) {
            post = posts.getJSONObject(i);
            postList.add(postFromJson(post));
        }
        Collections.sort(postList);
    }

    public PostCollection( PostCollection<PostType> postCollection ) {
        postList.addAll(postCollection.postList);
    }

    public int size() {
        return postList.size();
    }

    public PostType getById( PostId id ) {
        for( PostType post : postList) {
            if( post.id.equals(id) ) {
                return post;
            }
        }
        throw new IllegalArgumentException("Such post does not exist");
    }

    public PostType get( int index ) {
        return postList.get(index);
    }

    public String toString() {
        StringBuilder builder = new StringBuilder();
        for( PostType post  : postList) builder.append(post.toString()+"\n");
        return builder.toString();
    }

    protected abstract PostType postFromJson( JSONObject post ) throws JSONException, ParseException;

}
