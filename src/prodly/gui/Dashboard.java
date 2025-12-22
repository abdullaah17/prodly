package gui;

import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.util.Scanner;

public class Dashboard extends JFrame {

    public Dashboard() {
        setTitle("Prodly – Basic Dashboard");
        setSize(500, 350);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));

        // Top input
        JTextField roleInput = new JTextField("Backend");

        // Button
        JButton runBtn = new JButton("Generate Learning Path");

        // Output area
        JTextArea outputArea = new JTextArea();
        outputArea.setEditable(false);

        add(roleInput, BorderLayout.NORTH);
        add(runBtn, BorderLayout.CENTER);
        add(new JScrollPane(outputArea), BorderLayout.SOUTH);

        runBtn.addActionListener(e -> {
            try {
                // Ensure folders exist
                new File("data/input").mkdirs();
                new File("data/output").mkdirs();

                // Write role for C++ engine
                try (FileWriter fw = new FileWriter("data/input/role.txt")) {
                    fw.write(roleInput.getText().trim());
                }

                // Run C++ backend
                Process p = new ProcessBuilder("cmd", "/c", "cpp_core\\engine.exe")
                        .redirectErrorStream(true)
                        .start();

                p.waitFor();
                Thread.sleep(200); // allow file flush

                // Read output
                outputArea.setText("");
                File out = new File("data/output/learning_path.txt");
                if (!out.exists()) {
                    outputArea.setText("No output generated.");
                    return;
                }

                Scanner sc = new Scanner(out);
                while (sc.hasNextLine()) {
                    outputArea.append(sc.nextLine() + "\n");
                }
                sc.close();

            } catch (Exception ex) {
                outputArea.setText("Error: " + ex.getMessage());
            }
        });
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new Dashboard().setVisible(true);
        });
    }
}
