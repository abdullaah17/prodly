package prodly.gui;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JTextArea;
import javax.swing.JScrollPane;
import javax.swing.JCheckBox;
import javax.swing.JProgressBar;
import javax.swing.SwingUtilities;
import javax.swing.BoxLayout;
import javax.swing.BorderFactory;

import java.awt.CardLayout;
import java.awt.BorderLayout;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.GridLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.Dimension;
import java.awt.Component;
import java.awt.Insets;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Scanner;
import java.util.List;
import java.util.Set;
import java.util.Map;
import java.util.HashMap;
import static javax.swing.WindowConstants.EXIT_ON_CLOSE;
private JLabel managerStatusLabel;
private JLabel managerCompletionLabel;
private JLabel managerEtaLabel;
private JLabel managerReadinessLabel;


public class Dashboard extends JFrame {

    /* ===== THEME ===== */
    private static final Color BG = new Color(13, 13, 17);
    private static final Color CARD = new Color(24, 24, 30);
    private static final Color CARD_ALT = new Color(32, 32, 40);
    private static final Color ACCENT = new Color(88, 166, 255);
    private static final Color TEXT = new Color(230, 230, 235);
    private static final Color MUTED = new Color(150, 150, 160);
    private static final Color GREEN = new Color(0, 200, 120);
    private static final Color YELLOW = new Color(240, 200, 0);
    private static final Color RED = new Color(220, 80, 80);

    private CardLayout layout;
    private JPanel root;

    private JComboBox<String> roleBox;
    private JTextArea planArea;

    /* Progress screen */
    private JPanel timelinePanel;
    private JLabel explanationTitle;
    private JTextArea explanationBody;
    private JProgressBar progressBar;
    private JLabel completionDateLabel;

    /* Data */
    private final List<String> skills = new ArrayList<>();
    private final Set<String> completed = new HashSet<>();

    private final Map<String, String> difficultyMap = new HashMap<>();
    private final Map<String, Integer> daysMap = new HashMap<>();

    private final File progressFile = new File("data/user/progress.txt");

    public Dashboard() {
        setTitle("Prodly");
        setSize(1150, 680);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        initDifficultyData();

        layout = new CardLayout();
        root = new JPanel(layout);
        root.setBackground(BG);
        setContentPane(root);

        root.add(homeScreen(), "home");
        root.add(planScreen(), "plan");
        root.add(progressScreen(), "progress");
        root.add(managerScreen(), "manager");

        layout.show(root, "home");
    }

    /* ================= SCREEN 1 ================= */
    private JPanel homeScreen() {
        JPanel p = new JPanel(new GridBagLayout());
        p.setBackground(BG);
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(16, 16, 16, 16);

        JLabel title = label("Prodly", 36);
        JLabel subtitle = muted("Smart onboarding. Faster productivity.");

        roleBox = new JComboBox<>(new String[]{
                "Backend Engineer", "Frontend Engineer", "ML Engineer"
        });
        roleBox.setPreferredSize(new Dimension(260, 40));

        JButton start = primary("Generate Learning Plan");
        start.addActionListener(e -> {
            loadLearningPath();
            layout.show(root, "plan");
        });

        c.gridy = 0; p.add(title, c);
        c.gridy = 1; p.add(subtitle, c);
        c.gridy = 2; p.add(roleBox, c);
        c.gridy = 3; p.add(start, c);

        return p;
    }

    /* ================= SCREEN 2 ================= */
    private JPanel planScreen() {
        JPanel p = page("Learning Plan");

        planArea = new JTextArea();
        planArea.setEditable(false);
        styleText(planArea);

        JButton progressBtn = primary("Track Progress →");
        progressBtn.addActionListener(e -> {
            buildTimeline();
            layout.show(root, "progress");
        });

        JButton back = ghost("← Back");
        back.addActionListener(e -> layout.show(root, "home"));

        p.add(new JScrollPane(planArea), BorderLayout.CENTER);
        p.add(bottom(back, progressBtn), BorderLayout.SOUTH);

        return p;
    }

    /* ================= SCREEN 3 ================= */
    private JPanel progressScreen() {
        JPanel p = page("Progress Dashboard");

        timelinePanel = new JPanel();
        timelinePanel.setLayout(new BoxLayout(timelinePanel, BoxLayout.Y_AXIS));
        timelinePanel.setBackground(CARD_ALT);

        JScrollPane timelineScroll = new JScrollPane(timelinePanel);
        timelineScroll.setBorder(null);

        progressBar = new JProgressBar(0, 100);
        progressBar.setStringPainted(true);

        completionDateLabel = new JLabel("Estimated completion: —");
        completionDateLabel.setForeground(ACCENT);

        explanationTitle = label("Select a skill", 16);
        explanationBody = new JTextArea();
        explanationBody.setEditable(false);
        styleText(explanationBody);

        JPanel explanationCard = card();
        explanationCard.setLayout(new BorderLayout());
        explanationCard.add(explanationTitle, BorderLayout.NORTH);
        explanationCard.add(explanationBody, BorderLayout.CENTER);

        JButton managerBtn = primary("Manager View");
        managerBtn.addActionListener(e -> layout.show(root, "manager"));

        JButton back = ghost("← Back");
        back.addActionListener(e -> layout.show(root, "plan"));

        JPanel top = new JPanel(new BorderLayout());
        top.setBackground(BG);
        top.add(progressBar, BorderLayout.CENTER);
        top.add(completionDateLabel, BorderLayout.EAST);

        JPanel center = new JPanel(new GridLayout(1, 2, 16, 16));
        center.setBackground(BG);
        center.add(timelineScroll);
        center.add(explanationCard);

        p.add(top, BorderLayout.NORTH);
        p.add(center, BorderLayout.CENTER);
        p.add(bottom(back, managerBtn), BorderLayout.SOUTH);

        return p;
    }

    /* ================= SCREEN 4 ================= */
    private JPanel managerScreen() {
    JPanel p = page("Manager Overview");

    JPanel grid = new JPanel(new GridLayout(4, 1, 12, 12));
    grid.setBackground(BG);

    managerCompletionLabel = managerCard("Completion", "—");
    managerEtaLabel = managerCard("Estimated Finish", "—");
    managerStatusLabel = managerCard("Status", "—");
    managerReadinessLabel = managerCard("Readiness Score", "—");

    grid.add(managerCompletionLabel);
    grid.add(managerEtaLabel);
    grid.add(managerStatusLabel);
    grid.add(managerReadinessLabel);

    JButton back = ghost("← Back");
    back.addActionListener(e -> layout.show(root, "progress"));

    p.add(grid, BorderLayout.CENTER);
    p.add(back, BorderLayout.SOUTH);

    return p;
}
private void updateManagerOverview() {
    int total = skills.size();
    int done = completed.size();
    int percent = total == 0 ? 0 : (int) ((done * 100.0) / total);

    managerCompletionLabel.setText(percent + "% (" + done + "/" + total + ")");
    managerEtaLabel.setText(estimateCompletionDate());

    String status;
    if (percent >= 70) status = "🟢 On Track";
    else if (percent >= 40) status = "🟡 At Risk";
    else status = "🔴 Delayed";

    managerStatusLabel.setText(status);

    int readiness = percent - ((total - done) * 2);
    managerReadinessLabel.setText(Math.max(readiness, 0) + "/100");
}


    /* ================= LOGIC ================= */
    private void loadLearningPath() {
        skills.clear();
        completed.clear();
        planArea.setText("");
        loadProgressFromDisk();

        try (Scanner sc = new Scanner(new File("data/output/learning_path.txt"))) {
            int i = 1;
            while (sc.hasNextLine()) {
                String s = sc.nextLine();
                skills.add(s);
                planArea.append(i++ + ". " + s + "\n\n");
            }
        } catch (IOException e) {
            planArea.setText("Failed to load learning path.");
        }
    }

    private void buildTimeline() {
        timelinePanel.removeAll();

        for (String skill : skills) {
            JCheckBox cb = new JCheckBox(skill + " " + badge(skill));
            cb.setBackground(CARD_ALT);
            cb.setForeground(TEXT);
            cb.setSelected(completed.contains(skill));

            cb.addActionListener(e -> {
                if (cb.isSelected()) completed.add(skill);
                else completed.remove(skill);
                saveProgressToDisk();
                updateProgress();
                showExplanation(skill);
            });

            timelinePanel.add(cb);
        }

        updateProgress();
        timelinePanel.revalidate();
        timelinePanel.repaint();
    }

    private void updateProgress() {
        int percent = skills.isEmpty()
                ? 0
                : (int) ((completed.size() * 100.0) / skills.size());
        progressBar.setValue(percent);
        completionDateLabel.setText("Estimated completion: " + estimateCompletionDate());
    }

    private void showExplanation(String skill) {
    String difficulty = difficultyMap.getOrDefault(skill, "Beginner");
    int days = daysMap.getOrDefault(skill, 2);

    explanationTitle.setText(skill + " " + badge(skill));
    explanationBody.setText(
            "Difficulty: " + difficulty + "\n\n" +
            "Why it matters:\nThis skill is required for your role.\n\n" +
            "Estimated time: " + days + " days"
    );
}


    /* ================= PERSISTENCE ================= */
    private void saveProgressToDisk() {
        try {
            progressFile.getParentFile().mkdirs();
            FileWriter fw = new FileWriter(progressFile);
            for (String s : completed) fw.write(s + "\n");
            fw.close();
        } catch (IOException ignored) {}
    }

    private void loadProgressFromDisk() {
        if (!progressFile.exists()) return;
        try (Scanner sc = new Scanner(progressFile)) {
            while (sc.hasNextLine()) completed.add(sc.nextLine());
        } catch (IOException ignored) {}
    }

private String estimateCompletionDate() {
    int remainingDays = 0;

    for (String s : skills) {
        if (!completed.contains(s)) {
            remainingDays += daysMap.getOrDefault(s, 2); // ✅ SAFE
        }
    }

    LocalDate date = LocalDate.now().plusDays(remainingDays);
    return date.format(DateTimeFormatter.ofPattern("dd MMM yyyy"));
}


    /* ================= MANAGER ================= */
    private void updateManagerReport(JTextArea r) {
        r.setText(
                "Completed: " + completed.size() + "\n" +
                "Remaining: " + (skills.size() - completed.size()) + "\n" +
                "Estimated finish: " + estimateCompletionDate()
        );
    }

    /* ================= DATA ================= */
    private void initDifficultyData() {
        difficultyMap.put("Programming Basics", "Beginner");
        difficultyMap.put("OOP", "Beginner");
        difficultyMap.put("Data Structures", "Intermediate");
        difficultyMap.put("Algorithms", "Advanced");
        difficultyMap.put("System Design", "Advanced");

        daysMap.put("Programming Basics", 2);
        daysMap.put("OOP", 3);
        daysMap.put("Data Structures", 5);
        daysMap.put("Algorithms", 6);
        daysMap.put("System Design", 5);
    }

    private String badge(String skill) {
    String d = difficultyMap.getOrDefault(skill, "Beginner");

    if (d.equals("Beginner")) return "🟢";
    if (d.equals("Intermediate")) return "🟡";
    return "🔴";
}


    /* ================= UI HELPERS ================= */
    private JPanel page(String title) {
        JPanel p = new JPanel(new BorderLayout(16, 16));
        p.setBackground(BG);
        p.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        p.add(label(title, 22), BorderLayout.NORTH);
        return p;
    }

    private JPanel bottom(JButton left, JButton right) {
        JPanel p = new JPanel(new BorderLayout());
        p.setBackground(BG);
        p.add(left, BorderLayout.WEST);
        p.add(right, BorderLayout.EAST);
        return p;
    }

    private JPanel card() {
        JPanel p = new JPanel();
        p.setBackground(CARD);
        p.setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));
        return p;
    }

    private JLabel label(String t, int s) {
        JLabel l = new JLabel(t);
        l.setFont(new Font("Segoe UI Semibold", Font.PLAIN, s));
        l.setForeground(TEXT);
        return l;
    }

    private JLabel muted(String t) {
        JLabel l = new JLabel(t);
        l.setForeground(MUTED);
        return l;
    }

    private void styleText(JTextArea a) {
        a.setBackground(CARD);
        a.setForeground(ACCENT);
        a.setFont(new Font("Consolas", Font.PLAIN, 14));
        a.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));
    }

    private JButton primary(String t) {
        JButton b = new JButton(t);
        b.setBackground(ACCENT);
        b.setForeground(Color.BLACK);
        b.setFocusPainted(false);
        return b;
    }

    private JButton ghost(String t) {
        JButton b = new JButton(t);
        b.setForeground(TEXT);
        b.setBackground(BG);
        b.setFocusPainted(false);
        return b;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new Dashboard().setVisible(true));
    }
}
