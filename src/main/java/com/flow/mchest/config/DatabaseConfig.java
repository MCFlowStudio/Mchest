package com.flow.mchest.config;

public class DatabaseConfig {
    String host, port, username, password, databaseName;
    int poolSize;

    public DatabaseConfig(String host, String port, String username, String password, String databaseName, int poolSize) {
        this.host = host;
        this.port = port;
        this.username = username;
        this.password = password;
        this.databaseName = databaseName;
        this.poolSize = poolSize;
    }

    public String getHost() {
        return host;
    }

    public String getPort() {
        return port;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getDatabaseName() {
        return databaseName;
    }

    public int getPoolSize() {
        return poolSize;
    }
}