package com.github.hasanyildiz.dbdiff.db;

import java.util.Locale;

/**
 * Bir Oracle veritabanına bağlanmak için gereken bilgiler.
 */
public final class ConnectionSettings {

    private final String host;
    private final String port;
    private final String serviceName;
    private final String username;
    private final String password;

    public ConnectionSettings(String host, String port, String serviceName, String username, String password) {
        this.host = host.trim();
        this.port = port.trim();
        this.serviceName = serviceName.trim();
        this.username = username.trim();
        this.password = password;
    }

    public String getJdbcUrl() {
        return String.format("jdbc:oracle:thin:@%s:%s:%s", host, port, serviceName);
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    /** Oracle'da şema adı, bağlanan kullanıcının büyük harfli adıdır. */
    public String getSchemaName() {
        return username.toUpperCase(Locale.ROOT);
    }
}
