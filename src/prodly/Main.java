package prodly;

import javax.swing.SwingUtilities;
import prodly.gui.Dashboard;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new Dashboard().setVisible(true);
        });
    }
}