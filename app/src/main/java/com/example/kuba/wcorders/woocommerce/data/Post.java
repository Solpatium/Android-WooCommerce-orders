package com.example.kuba.wcorders.woocommerce.data;

/*
    Data class modeling post from WooCommerce.
 */
public abstract class Post extends FromJson implements Comparable<Post> {
    public final PostId id;

    public Post(PostId id) {
        this.id = id;
    }

    public boolean equals( Object o ) {
        if( o == null || !(o instanceof Post) ) {
            return false;
        }
        return id.equals(((Post) o).id);
    }

    public int compareTo( Post post ) {
        if( equals(post) ) return 0;

        return id.compareTo(post.id);
    }
}
