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

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Scanner;
import java.util.List;
import java.util.Set;

public class Dashboard extends JFrame {

    /* ===== THEME ===== */
    private static final Color BG = new Color(13, 13, 17);
    private static final Color CARD = new Color(24, 24, 30);
    private static final Color CARD_ALT = new Color(32, 32, 40);
    private static final Color ACCENT = new Color(88, 166, 255);
    private static final Color SUCCESS = new Color(0, 200, 120);
    private static final Color TEXT = new Color(230, 230, 235);
    private static final Color MUTED = new Color(150, 150, 160);

    private CardLayout layout;
    private JPanel root;

    private JComboBox<String> roleBox;
    private JTextArea planArea;

    /* Progress screen */
    private JPanel timelinePanel;
    private JLabel explanationTitle;
    private JTextArea explanationBody;
    private JProgressBar progressBar;

    /* Data */
    private final List<String> skills = new ArrayList<>();
    private final Set<String> completed = new HashSet<>();

    public Dashboard() {
        setTitle("Prodly");
        setSize(1100, 650);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

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

        JPanel center = new JPanel(new GridLayout(1, 2, 16, 16));
        center.setBackground(BG);
        center.add(timelineScroll);
        center.add(explanationCard);

        p.add(center, BorderLayout.CENTER);
        p.add(progressBar, BorderLayout.NORTH);
        p.add(bottom(back, managerBtn), BorderLayout.SOUTH);

        return p;
    }

    /* ================= SCREEN 4 ================= */
    private JPanel managerScreen() {
        JPanel p = page("Manager Overview");

        JTextArea report = new JTextArea();
        report.setEditable(false);
        styleText(report);

        JButton back = ghost("← Back");
        back.addActionListener(e -> layout.show(root, "progress"));

        updateManagerReport(report);

        p.add(new JScrollPane(report), BorderLayout.CENTER);
        p.add(back, BorderLayout.SOUTH);

        return p;
    }

    /* ================= LOGIC ================= */
    private void loadLearningPath() {
        skills.clear();
        completed.clear();
        planArea.setText("");

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
            JCheckBox cb = new JCheckBox(skill);
            cb.setBackground(CARD_ALT);
            cb.setForeground(TEXT);

            cb.addActionListener(e -> {
                if (cb.isSelected()) completed.add(skill);
                else completed.remove(skill);
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
    }

    private void showExplanation(String skill) {
        explanationTitle.setText(skill);
        explanationBody.setText(
                "What: " + skill + "\n\n" +
                "Why: Required before advanced topics.\n\n" +
                "Impact: Improves onboarding speed."
        );
    }

    private void updateManagerReport(JTextArea r) {
        r.setText(
                "Completed: " + completed.size() + "\n" +
                "Remaining: " + (skills.size() - completed.size()) + "\n" +
                "Completion: " +
                (skills.isEmpty() ? 0 :
                (completed.size() * 100 / skills.size())) + "%"
        );
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
