package com.example.eece490c_android.serializers;

public class SignUpSerializer {
    private String username;
    private String password;

    public SignUpSerializer(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }
}
