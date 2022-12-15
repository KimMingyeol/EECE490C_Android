package com.example.eece490c_android.serializers;

public class LogInSerializer {
    private String username;
    private String password;
    private String token;

    public LogInSerializer(String username, String password) {
        this.username = username;
        this.password = password;
        this.token = null;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getToken() {
        return token;
    }
}
