package com.example.kuba.wcorders.woocommerce.data;

/*
    Class representing Wordpress post ID (must be bigger or equal 0)
 */
public class PostId implements Comparable<PostId> {
    final public int id;

    public PostId( int id ) {
        if( id < 0 ) {
            throw new IllegalArgumentException("Post id must be greater than zero");
        }
        this.id = id;
    }

    public PostId(String id) {
        try{
            this.id = Integer.parseInt(id);
            if( this.id < 0 ) {
                throw new IllegalArgumentException("Post id must be greater than zero");
            }
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Wrong post id supplied");
        }
    }

    public String toString() {
        return "#" + String.valueOf(id);
    }

    public boolean equals( Object postId ) {
        if( postId == null || !(postId instanceof PostId) ) {
            return false;
        }
        return ((PostId)postId).id == id;
    }

    @Override
    public int compareTo(PostId postId) {
        if( equals(postId) ) return 0;

        return postId.id > id ? 1 : -1;
    }
}
