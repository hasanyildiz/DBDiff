package com.github.hasanyildiz.dbdiff.ui;

import java.sql.SQLException;
import java.util.concurrent.ExecutionException;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import com.github.hasanyildiz.dbdiff.compare.DiffListener;
import com.github.hasanyildiz.dbdiff.compare.SchemaDiff;
import com.github.hasanyildiz.dbdiff.db.ConnectionSettings;
import com.github.hasanyildiz.dbdiff.db.DatabaseConnection;
import com.github.hasanyildiz.dbdiff.db.OracleSchemaReader;

/**
 * Karşılaştırmayı arka planda çalıştırır; durum mesajlarını, script satırlarını ve hataları Swing olay iş
 * parçacığında (EDT) {@link Callback}'e iletir.
 */
final class CompareWorker extends SwingWorker<Void, Void> {

    /** Tüm metotlar EDT üzerinde çağrılır. */
    interface Callback {

        void onStatus(String message);

        void onScriptLine(String line);

        void onError(String title, String message);

        /** Karşılaştırma başarılı ya da hatalı bittiğinde en son çağrılır. */
        void onFinished();
    }

    private static final String CONNECTION_FAILED_TITLE = "Bağlantı sağlanamadı";

    private final ConnectionSettings sourceSettings;
    private final ConnectionSettings targetSettings;
    private final Callback callback;

    CompareWorker(ConnectionSettings sourceSettings, ConnectionSettings targetSettings, Callback callback) {
        this.sourceSettings = sourceSettings;
        this.targetSettings = targetSettings;
        this.callback = callback;
    }

    @Override
    protected Void doInBackground() throws Exception {
        try (DatabaseConnection source = openConnection(sourceSettings, "Kaynak")) {
            postStatus("Kaynak veritabanı bağlantısı başarılı!");
            try (DatabaseConnection target = openConnection(targetSettings, "Hedef")) {
                postStatus("Hedef veritabanı bağlantısı başarılı!");
                new SchemaDiff(new OracleSchemaReader(source), new OracleSchemaReader(target))
                        .run(new DiffListener() {
                            @Override
                            public void onProgress(String message) {
                                postStatus(message);
                            }

                            @Override
                            public void onScriptLine(String line) {
                                SwingUtilities.invokeLater(() -> callback.onScriptLine(line));
                            }
                        });
                postStatus("İşlem tamamlandı!");
            }
        }
        return null;
    }

    private static DatabaseConnection openConnection(ConnectionSettings settings, String side)
            throws ConnectionFailedException {
        DatabaseConnection connection;
        try {
            connection = DatabaseConnection.open(settings);
        } catch (SQLException ex) {
            throw new ConnectionFailedException(ex.getLocalizedMessage(), ex);
        }
        boolean valid;
        try {
            valid = connection.testConnection();
        } catch (SQLException ex) {
            closeQuietly(connection);
            throw new ConnectionFailedException(ex.getLocalizedMessage(), ex);
        }
        if (!valid) {
            closeQuietly(connection);
            throw new ConnectionFailedException(side + " veritabanına bağlantı sağlanamadı", null);
        }
        return connection;
    }

    private static void closeQuietly(DatabaseConnection connection) {
        try {
            connection.close();
        } catch (SQLException ignored) {
            // Bağlantı zaten kullanılamıyor; asıl hata kullanıcıya gösteriliyor.
        }
    }

    private void postStatus(String message) {
        SwingUtilities.invokeLater(() -> callback.onStatus(message));
    }

    @Override
    protected void done() {
        try {
            get();
        } catch (ExecutionException ex) {
            Throwable cause = ex.getCause();
            String title = cause instanceof ConnectionFailedException ? CONNECTION_FAILED_TITLE : "Bir hata oluştu.";
            callback.onError(title, cause.getLocalizedMessage());
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
        } finally {
            callback.onFinished();
        }
    }

    /** Kaynak veya hedef veritabanına bağlanılamadığında fırlatılır. */
    private static final class ConnectionFailedException extends Exception {

        private static final long serialVersionUID = 1L;

        ConnectionFailedException(String message, Throwable cause) {
            super(message, cause);
        }
    }
}
