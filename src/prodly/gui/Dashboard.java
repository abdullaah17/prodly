package prodly.gui;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.SwingUtilities;

public class Dashboard extends JFrame {

    public Dashboard() {
        setTitle("Prodly");
        setSize(600, 400);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        add(new JLabel("Dashboard Loaded Successfully"));
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new Dashboard().setVisible(true);
        });
    }
}