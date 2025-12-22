package gui;

import javax.swing.*;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Component;
import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.List;

public class Dashboard extends JFrame {

    // ===== THEME =====
    private static final Color BG_MAIN = new Color(12, 12, 16);
    private static final Color BG_CARD = new Color(22, 22, 28);
    private static final Color BG_INPUT = new Color(35, 35, 45);
    private static final Color ACCENT = new Color(0, 200, 255);
    private static final Color TEXT = new Color(230, 230, 230);
    private static final Color MUTED = new Color(160, 160, 160);

    private JComboBox<String> roleDropdown;
    private JButton runBtn;
    private JLabel statusLabel;
    private JLabel nextSkillLabel;
    private JPanel skillsPanel;

    public Dashboard() {
        setTitle("Prodly AI – Smart Onboarding");
        setSize(900, 600);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel root = new JPanel(new BorderLayout(15, 15));
        root.setBackground(BG_MAIN);
        root.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        setContentPane(root);

        /* ===== HEADER ===== */
        JLabel title = new JLabel("Prodly Onboarding Engine");
        title.setForeground(TEXT);
        title.setFont(new Font("Segoe UI Semibold", Font.PLAIN, 26));

        JLabel subtitle = new JLabel("Role-based learning • Progress-aware • AI-assisted");
        subtitle.setForeground(MUTED);

        JPanel header = new JPanel(new GridLayout(2, 1));
        header.setBackground(BG_MAIN);
        header.add(title);
        header.add(subtitle);

        root.add(header, BorderLayout.NORTH);

        /* ===== CENTER ===== */
        JPanel center = new JPanel(new GridLayout(1, 2, 15, 15));
        center.setBackground(BG_MAIN);

        // LEFT CARD
        JPanel leftCard = card();
        leftCard.setLayout(new BorderLayout(10, 10));

        roleDropdown = new JComboBox<>(new String[]{"Backend", "Frontend", "ML"});
        styleInput(roleDropdown);

        runBtn = new JButton("Generate Learning Path");
        styleButton(runBtn);

        skillsPanel = new JPanel();
        skillsPanel.setBackground(BG_CARD);
        skillsPanel.setLayout(new BoxLayout(skillsPanel, BoxLayout.Y_AXIS));

        leftCard.add(roleDropdown, BorderLayout.NORTH);
        leftCard.add(runBtn, BorderLayout.CENTER);
        leftCard.add(new JScrollPane(skillsPanel), BorderLayout.SOUTH);

        // RIGHT CARD
        JPanel rightCard = card();
        rightCard.setLayout(new BorderLayout(10, 10));

        nextSkillLabel = new JLabel("Next Skill: —");
        nextSkillLabel.setForeground(ACCENT);
        nextSkillLabel.setFont(new Font("Segoe UI Semibold", Font.PLAIN, 18));

        statusLabel = new JLabel("Idle");
        statusLabel.setForeground(MUTED);

        rightCard.add(nextSkillLabel, BorderLayout.NORTH);
        rightCard.add(statusLabel, BorderLayout.SOUTH);

        center.add(leftCard);
        center.add(rightCard);

        root.add(center, BorderLayout.CENTER);

        runBtn.addActionListener(e -> runEngine());

        loadExistingCompletion();
    }

    /* ================= ENGINE ================= */
    private void runEngine() {
        runBtn.setEnabled(false);
        runBtn.setText("Generating...");
        statusLabel.setText("Running backend engine…");

        new Thread(() -> {
            try {
                new File("data/input").mkdirs();
                new File("data/output").mkdirs();

                try (FileWriter fw = new FileWriter("data/input/role.txt")) {
                    fw.write(roleDropdown.getSelectedItem().toString());
                }

                Process p = new ProcessBuilder(
                        "cmd", "/c", "cpp_core\\engine.exe"
                ).redirectErrorStream(true).start();

                p.waitFor();
                Thread.sleep(200);

                loadLearningPath();
                loadNextSkill();

                SwingUtilities.invokeLater(() ->
                        statusLabel.setText("Learning path updated")
                );

            } catch (Exception ex) {
                SwingUtilities.invokeLater(() ->
                        statusLabel.setText("Error running engine")
                );
            } finally {
                SwingUtilities.invokeLater(() -> {
                    runBtn.setEnabled(true);
                    runBtn.setText("Generate Learning Path");
                });
            }
        }).start();
    }

    /* ================= LEARNING PATH ================= */
    private void loadLearningPath() throws Exception {
        skillsPanel.removeAll();

        File pathFile = new File("data/output/learning_path.txt");
        if (!pathFile.exists()) return;

        Scanner sc = new Scanner(pathFile);
        List<String> completed = loadCompleted();

        while (sc.hasNextLine()) {
            String skill = sc.nextLine().trim();
            JCheckBox cb = new JCheckBox(skill);
            cb.setBackground(BG_CARD);
            cb.setForeground(TEXT);
            cb.setSelected(completed.contains(skill));

            cb.addActionListener(e -> saveCompleted());
            skillsPanel.add(cb);
        }
        sc.close();

        SwingUtilities.invokeLater(() -> {
            skillsPanel.revalidate();
            skillsPanel.repaint();
        });
    }

    /* ================= NEXT SKILL ================= */
    private void loadNextSkill() {
        File f = new File("data/output/next_skills.txt");
        if (!f.exists()) return;

        try (Scanner sc = new Scanner(f)) {
            if (sc.hasNextLine()) {
                SwingUtilities.invokeLater(() ->
                        nextSkillLabel.setText(sc.nextLine())
                );
            }
        } catch (Exception ignored) {}
    }

    /* ================= COMPLETION ================= */
    private void saveCompleted() {
        try (FileWriter fw = new FileWriter("data/input/completed_skills.txt")) {
            for (Component c : skillsPanel.getComponents()) {
                JCheckBox cb = (JCheckBox) c;
                if (cb.isSelected()) {
                    fw.write(cb.getText() + "\n");
                }
            }
        } catch (Exception ignored) {}
    }

    private List<String> loadCompleted() {
        List<String> list = new ArrayList<>();
        File f = new File("data/input/completed_skills.txt");
        if (!f.exists()) return list;

        try (Scanner sc = new Scanner(f)) {
            while (sc.hasNextLine()) list.add(sc.nextLine().trim());
        } catch (Exception ignored) {}
        return list;
    }

    private void loadExistingCompletion() {
        try {
            loadLearningPath();
            loadNextSkill();
        } catch (Exception ignored) {}
    }

    /* ================= UI HELPERS ================= */
    private JPanel card() {
        JPanel p = new JPanel();
        p.setBackground(BG_CARD);
        p.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        return p;
    }

    private void styleInput(JComponent c) {
        c.setBackground(BG_INPUT);
        c.setForeground(TEXT);
        c.setFont(new Font("Segoe UI", Font.PLAIN, 14));
    }

    private void styleButton(JButton b) {
        b.setBackground(ACCENT);
        b.setForeground(Color.BLACK);
        b.setFont(new Font("Segoe UI Semibold", Font.PLAIN, 14));
        b.setFocusPainted(false);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new Dashboard().setVisible(true));
    }
}
