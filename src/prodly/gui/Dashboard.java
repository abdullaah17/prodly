package gui;

import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.util.*;

public class Dashboard extends JFrame {

    public Dashboard() {
        setTitle("Prodly – Role-Based Onboarding");
        setSize(800, 600);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        getContentPane().setBackground(new Color(18, 18, 18)); // dark background

        // Main panel with padding
        JPanel mainPanel = new JPanel();
        mainPanel.setBackground(new Color(28, 28, 28)); // slightly lighter than bg
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        mainPanel.setLayout(new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(15, 15, 15, 15);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Header
        JLabel header = new JLabel("Prodly – Role-Based Onboarding");
        header.setFont(new Font("Segoe UI", Font.PLAIN, 24));
        header.setForeground(new Color(220, 220, 220));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        mainPanel.add(header, gbc);

        // Role input
        JTextField roleInput = new JTextField("Backend");
        roleInput.setBackground(new Color(38, 38, 38));
        roleInput.setForeground(new Color(200, 200, 200));
        roleInput.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        roleInput.setCaretColor(Color.WHITE);
        roleInput.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(60, 60, 60)),
                BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        gbc.gridy = 1;
        gbc.gridwidth = 2;
        mainPanel.add(roleInput, gbc);

        // Generate button
        JButton generateBtn = new JButton("Generate Learning Path");
        generateBtn.setBackground(new Color(58, 58, 58));
        generateBtn.setForeground(new Color(220, 220, 220));
        generateBtn.setFocusPainted(false);
        generateBtn.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        generateBtn.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        generateBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        gbc.gridy = 2;
        gbc.gridwidth = 2;
        mainPanel.add(generateBtn, gbc);

        // Output area
        JTextArea outputArea = new JTextArea();
        outputArea.setEditable(false);
        outputArea.setBackground(new Color(38, 38, 38));
        outputArea.setForeground(new Color(200, 200, 200));
        outputArea.setFont(new Font("Consolas", Font.PLAIN, 14));
        outputArea.setLineWrap(true);
        outputArea.setWrapStyleWord(true);

        JScrollPane scrollPane = new JScrollPane(outputArea);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(60, 60, 60)));
        scrollPane.setViewportBorder(BorderFactory.createEmptyBorder());
        scrollPane.setBackground(new Color(38, 38, 38));

        gbc.gridy = 3;
        gbc.weightx = 1;
        gbc.weighty = 1;
        gbc.fill = GridBagConstraints.BOTH;
        mainPanel.add(scrollPane, gbc);

        add(mainPanel);

        // Action
        generateBtn.addActionListener(e -> {
            try {
                BufferedWriter writer = new BufferedWriter(
                        new FileWriter("data/input/role.txt"));
                writer.write(roleInput.getText().trim());
                writer.close();

                Runtime.getRuntime().exec("cmd /c cpp_core\\engine.exe");
                Thread.sleep(500);

                outputArea.setText("");
                Scanner sc = new Scanner(
                        new File("data/output/learning_path.txt"));
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
        SwingUtilities.invokeLater(() -> new Dashboard().setVisible(true));
    }
}
