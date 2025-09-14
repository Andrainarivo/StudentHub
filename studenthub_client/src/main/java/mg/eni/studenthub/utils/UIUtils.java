package mg.eni.studenthub.utils;

import javax.swing.*;

public class UIUtils {
    public static void installSystemLookAndFeel() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {}
    }

    public static void installFlatLaf() {
        try {
            Class.forName("com.formdev.flatlaf.FlatLightLaf");
            com.formdev.flatlaf.FlatLightLaf.setup();
        } catch (Exception e) {
            installSystemLookAndFeel();
        }
    }

}
