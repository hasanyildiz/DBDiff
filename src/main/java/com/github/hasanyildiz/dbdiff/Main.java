package com.github.hasanyildiz.dbdiff;

import java.awt.EventQueue;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import com.github.hasanyildiz.dbdiff.ui.MainFrame;

/**
 * Uygulamanın giriş noktası.
 */
public final class Main {

    private static final Logger LOGGER = Logger.getLogger(Main.class.getName());

    private Main() {
    }

    public static void main(String[] args) {
        useNimbusLookAndFeel();
        EventQueue.invokeLater(() -> new MainFrame().setVisible(true));
    }

    /** Nimbus görünümü yoksa varsayılan görünüm kullanılır. */
    private static void useNimbusLookAndFeel() {
        try {
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    return;
                }
            }
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException
                | UnsupportedLookAndFeelException ex) {
            LOGGER.log(Level.FINE, "Nimbus görünümü yüklenemedi", ex);
        }
    }
}
