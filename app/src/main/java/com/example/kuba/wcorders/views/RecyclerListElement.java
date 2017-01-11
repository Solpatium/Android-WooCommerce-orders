package com.example.kuba.wcorders.views;

import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.example.kuba.wcorders.woocommerce.data.Post;

/*
    Element displayed by PostRecyclerView.
 */
public abstract class RecyclerListElement<PostType extends Post> extends RecyclerView.ViewHolder {
    public final View view;
    public RecyclerListElement(View itemView) {
        super(itemView);
        view = itemView;
    }

    public abstract void setPost( PostType post );
}
