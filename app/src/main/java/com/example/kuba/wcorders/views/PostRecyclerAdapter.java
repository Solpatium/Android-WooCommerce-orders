package com.example.kuba.wcorders.views;


import android.app.Activity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import com.example.kuba.wcorders.woocommerce.data.Post;
import com.example.kuba.wcorders.woocommerce.data.PostCollection;

/*
    RecyclerView for every type of post.
 */
public abstract class PostRecyclerAdapter<PostType extends Post>
        extends RecyclerView.Adapter<RecyclerListElement<PostType>> {
    protected PostCollection<PostType> posts;
    protected final Activity parentActivity;
    protected RecyclerView recyclerPostView;

    public PostRecyclerAdapter(RecyclerView recyclerPostView, PostCollection<PostType> posts, Activity activity) {
        this.parentActivity = activity;
        this.posts = posts;
        this.recyclerPostView = recyclerPostView;

        // Add dividers
        recyclerPostView.addItemDecoration( new DividerItemDecoration(parentActivity, LinearLayoutManager.VERTICAL));

        // Set itself as recycler adapter
        recyclerPostView.setAdapter(this);
    }

    public void update( PostCollection<PostType> posts ) {
        this.posts = posts;
        notifyDataSetChanged();
    }

    protected void setupElement( RecyclerListElement<PostType> element, int position, PostType post ) {
        element.setPost(post);
    }

    @Override
    public void onBindViewHolder(RecyclerListElement<PostType> holder, int position) {
        PostType post = posts.get(position);
        setupElement(holder, position, post);
    }

    @Override
    public int getItemCount() {
        return posts.size();
    }
}
