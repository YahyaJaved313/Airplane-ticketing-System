package com.noobcoder.chickenfront.forms;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class UserProfileForm extends BaseCustomerFrame {
    private JTextField nameField;
    private JTextField emailField;
    private JLabel messageLabel;
    private JButton saveButton;

    public UserProfileForm() {
        super("User Profile");
        createUserProfileContent();
        setVisible(true);
    }

    private void createUserProfileContent() {
        // Header
        JLabel titleLabel = new JLabel("User Profile & Membership Dashboard");
        titleLabel.setForeground(DARK_BLUE);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 32));
        titleLabel.setBorder(new EmptyBorder(0, 0, 20, 0));

        // Split Panel: Left (Form Card), Right (Loyalty Dashboard)
        JPanel splitPanel = new JPanel(new GridBagLayout());
        splitPanel.setOpaque(false);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weighty = 1.0;

        // --- Left Panel (Editable Form) ---
        JPanel leftPanel = new JPanel(new BorderLayout());
        leftPanel.setOpaque(false);
        leftPanel.setPreferredSize(new Dimension(420, 0)); // Fixed width to prevent stretching

        JPanel formCard = new JPanel();
        formCard.setLayout(new BoxLayout(formCard, BoxLayout.Y_AXIS));
        formCard.setBackground(WHITE);
        formCard.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(230, 230, 230), 1),
                BorderFactory.createEmptyBorder(25, 25, 25, 25)
        ));

        JLabel formTitle = new JLabel("Account Details");
        formTitle.setFont(new Font("Segoe UI", Font.BOLD, 18));
        formTitle.setForeground(DARK_BLUE);
        formTitle.setAlignmentX(Component.LEFT_ALIGNMENT);

        JPanel fieldsPanel = new JPanel(new GridLayout(2, 2, 15, 15));
        fieldsPanel.setOpaque(false);
        fieldsPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel nameLabel = new JLabel("Full Name:");
        nameLabel.setForeground(DARK_BLUE);
        nameLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        nameField = new JTextField("Valued Passenger");
        nameField.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        JLabel emailLabel = new JLabel("Email Address:");
        emailLabel.setForeground(DARK_BLUE);
        emailLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        emailField = new JTextField("customer@chickenair.com");
        emailField.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        fieldsPanel.add(nameLabel);
        fieldsPanel.add(nameField);
        fieldsPanel.add(emailLabel);
        fieldsPanel.add(emailField);

        saveButton = new CustomStyledButton("Save Changes", 30, PRIMARY_BLUE, WHITE, 2);
        saveButton.setFont(new Font("Segoe UI", Font.BOLD, 14));
        saveButton.setMaximumSize(new Dimension(370, 45));
        saveButton.setAlignmentX(Component.LEFT_ALIGNMENT);
        saveButton.addActionListener(e -> saveProfile());

        messageLabel = new JLabel("", SwingConstants.LEFT);
        messageLabel.setForeground(DARK_BLUE);
        messageLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        messageLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        formCard.add(formTitle);
        formCard.add(Box.createVerticalStrut(20));
        formCard.add(fieldsPanel);
        formCard.add(Box.createVerticalStrut(25));
        formCard.add(saveButton);
        formCard.add(Box.createVerticalStrut(15));
        formCard.add(messageLabel);

        leftPanel.add(formCard, BorderLayout.NORTH);

        // --- Right Panel (Loyalty Club Dashboard) ---
        JPanel rightPanel = new JPanel();
        rightPanel.setLayout(new BoxLayout(rightPanel, BoxLayout.Y_AXIS));
        rightPanel.setBackground(WHITE);
        rightPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(230, 230, 230), 1),
                BorderFactory.createEmptyBorder(25, 25, 25, 25)
        ));

        // Tier Card Header
        JPanel tierHeader = new JPanel(new BorderLayout(10, 0));
        tierHeader.setOpaque(false);
        tierHeader.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel clubTitle = new JLabel("Chicken Club Frequent Flyer");
        clubTitle.setFont(new Font("Segoe UI", Font.BOLD, 22));
        clubTitle.setForeground(PRIMARY_BLUE);

        JLabel tierBadge = new JLabel("  GOLD ELITE  ");
        tierBadge.setOpaque(true);
        tierBadge.setBackground(new Color(212, 175, 55)); // Gold Color
        tierBadge.setForeground(WHITE);
        tierBadge.setFont(new Font("Segoe UI", Font.BOLD, 12));
        tierBadge.setBorder(BorderFactory.createLineBorder(new Color(184, 134, 11), 1));

        tierHeader.add(clubTitle, BorderLayout.WEST);
        tierHeader.add(tierBadge, BorderLayout.EAST);

        rightPanel.add(tierHeader);
        rightPanel.add(Box.createVerticalStrut(25));

        // Milage Progress Section
        JLabel progressTitle = new JLabel("Miles Flown (Tier Qualification)");
        progressTitle.setFont(new Font("Segoe UI", Font.BOLD, 14));
        progressTitle.setForeground(DARK_BLUE);
        progressTitle.setAlignmentX(Component.LEFT_ALIGNMENT);

        JProgressBar progressBar = new JProgressBar(0, 20000);
        progressBar.setValue(12450);
        progressBar.setStringPainted(true);
        progressBar.setString("12,450 / 20,000 Miles");
        progressBar.setFont(new Font("Segoe UI", Font.BOLD, 12));
        progressBar.setForeground(new Color(212, 175, 55));
        progressBar.setBackground(new Color(240, 240, 240));
        progressBar.setPreferredSize(new Dimension(500, 30));
        progressBar.setMaximumSize(new Dimension(800, 30));
        progressBar.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel nextTierLabel = new JLabel("Next Reward Tier: PLATINUM ELITE (7,550 miles remaining)");
        nextTierLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        nextTierLabel.setForeground(new Color(120, 120, 120));
        nextTierLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        rightPanel.add(progressTitle);
        rightPanel.add(Box.createVerticalStrut(10));
        rightPanel.add(progressBar);
        rightPanel.add(Box.createVerticalStrut(5));
        rightPanel.add(nextTierLabel);
        rightPanel.add(Box.createVerticalStrut(25));

        // Active Perks Card
        JPanel perksCard = new JPanel();
        perksCard.setLayout(new BoxLayout(perksCard, BoxLayout.Y_AXIS));
        perksCard.setOpaque(false);
        perksCard.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(230, 230, 230), 1),
                "Your Active Gold Elite Perks",
                0, 0,
                new Font("Segoe UI", Font.BOLD, 14),
                new Color(212, 175, 55)
        ));
        perksCard.setAlignmentX(Component.LEFT_ALIGNMENT);

        addPerk(perksCard, " ✈  Complimentary Airport Lounge Access worldwide");
        addPerk(perksCard, " ✈  Priority Boarding (Group A) & Fast-Track Security lane");
        addPerk(perksCard, " ✈  +50% Bonus Miles accumulation on all bookings");
        addPerk(perksCard, " ✈  Extra 23kg Checked Bag allowance for free");

        rightPanel.add(perksCard);
        rightPanel.add(Box.createVerticalGlue());

        // GridBagConstraints Setup
        gbc.gridx = 0;
        gbc.weightx = 0.35;
        splitPanel.add(leftPanel, gbc);

        gbc.gridx = 1;
        gbc.weightx = 0.65;
        gbc.insets = new Insets(0, 30, 0, 0);
        splitPanel.add(rightPanel, gbc);

        mainPanel.add(titleLabel, BorderLayout.NORTH);
        mainPanel.add(splitPanel, BorderLayout.CENTER);
    }

    private void addPerk(JPanel panel, String perkText) {
        JLabel perkLabel = new JLabel(perkText);
        perkLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
        perkLabel.setForeground(DARK_BLUE);
        perkLabel.setBorder(new EmptyBorder(5, 10, 5, 5));
        perkLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(perkLabel);
    }

    private void saveProfile() {
        String name = nameField.getText().trim();
        String email = emailField.getText().trim();

        if (name.isEmpty() || email.isEmpty()) {
            messageLabel.setText("Fields cannot be empty.");
            messageLabel.setForeground(Color.RED);
            return;
        }

        messageLabel.setText("Account updated successfully!");
        messageLabel.setForeground(new Color(46, 204, 113));
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new UserProfileForm().setVisible(true));
    }
}