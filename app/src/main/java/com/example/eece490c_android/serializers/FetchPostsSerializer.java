package com.example.eece490c_android.serializers;

import java.util.ArrayList;

public class FetchPostsSerializer {
    private String username;
    private ArrayList<PostSerializer> posts;

    public FetchPostsSerializer(String username) {
        this.username = username;
        this.posts = new ArrayList<PostSerializer>();
    }

    public String getUsername() {
        return username;
    }

    public ArrayList<PostSerializer> getPosts() {
        return posts;
    }
}
