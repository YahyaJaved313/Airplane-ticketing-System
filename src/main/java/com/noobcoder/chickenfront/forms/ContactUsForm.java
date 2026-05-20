package com.noobcoder.chickenfront.forms;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class ContactUsForm extends BaseCustomerFrame {
    private JTextField nameField;
    private JTextField emailField;
    private JTextArea messageField;
    private JLabel confirmationLabel;
    private JButton submitButton;

    public ContactUsForm() {
        super("Contact Us");
        createContactContent();
        setVisible(true);
    }

    private void createContactContent() {
        // Title
        JLabel titleLabel = new JLabel("Contact Support & Help Center");
        titleLabel.setForeground(DARK_BLUE);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 32));
        titleLabel.setBorder(new EmptyBorder(0, 0, 20, 0));

        // Split Panel: Left (Form), Right (FAQs & Hotlines)
        JPanel splitPanel = new JPanel(new GridBagLayout());
        splitPanel.setOpaque(false);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weighty = 1.0;

        // --- Left Panel (Contact Form Card) ---
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

        JLabel cardTitle = new JLabel("Send Us a Message");
        cardTitle.setFont(new Font("Segoe UI", Font.BOLD, 18));
        cardTitle.setForeground(DARK_BLUE);
        cardTitle.setAlignmentX(Component.LEFT_ALIGNMENT);

        JPanel inputFields = new JPanel(new GridLayout(3, 1, 10, 15));
        inputFields.setOpaque(false);
        inputFields.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Name
        JPanel namePanel = new JPanel(new BorderLayout(5, 5));
        namePanel.setOpaque(false);
        JLabel nameLabel = new JLabel("Full Name:");
        nameLabel.setForeground(DARK_BLUE);
        nameLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        nameField = new JTextField();
        nameField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        nameField.setPreferredSize(new Dimension(0, 35));
        namePanel.add(nameLabel, BorderLayout.NORTH);
        namePanel.add(nameField, BorderLayout.CENTER);

        // Email
        JPanel emailPanel = new JPanel(new BorderLayout(5, 5));
        emailPanel.setOpaque(false);
        JLabel emailLabel = new JLabel("Email Address:");
        emailLabel.setForeground(DARK_BLUE);
        emailLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        emailField = new JTextField();
        emailField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        emailField.setPreferredSize(new Dimension(0, 35));
        emailPanel.add(emailLabel, BorderLayout.NORTH);
        emailPanel.add(emailField, BorderLayout.CENTER);

        // Message
        JPanel msgPanel = new JPanel(new BorderLayout(5, 5));
        msgPanel.setOpaque(false);
        JLabel messageLabel = new JLabel("Your Message:");
        messageLabel.setForeground(DARK_BLUE);
        messageLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        messageField = new JTextArea(4, 15);
        messageField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        messageField.setLineWrap(true);
        messageField.setWrapStyleWord(true);
        JScrollPane scrollMsg = new JScrollPane(messageField);
        scrollMsg.setPreferredSize(new Dimension(0, 100));
        msgPanel.add(messageLabel, BorderLayout.NORTH);
        msgPanel.add(scrollMsg, BorderLayout.CENTER);

        inputFields.add(namePanel);
        inputFields.add(emailPanel);

        submitButton = new CustomStyledButton("Submit Message", 30, PRIMARY_BLUE, WHITE, 2);
        submitButton.setFont(new Font("Segoe UI", Font.BOLD, 14));
        submitButton.setMaximumSize(new Dimension(370, 45));
        submitButton.setAlignmentX(Component.LEFT_ALIGNMENT);
        submitButton.addActionListener(e -> submit());

        confirmationLabel = new JLabel("", SwingConstants.LEFT);
        confirmationLabel.setForeground(DARK_BLUE);
        confirmationLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        confirmationLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        formCard.add(cardTitle);
        formCard.add(Box.createVerticalStrut(20));
        formCard.add(inputFields);
        formCard.add(Box.createVerticalStrut(15));
        formCard.add(msgPanel);
        formCard.add(Box.createVerticalStrut(25));
        formCard.add(submitButton);
        formCard.add(Box.createVerticalStrut(15));
        formCard.add(confirmationLabel);

        leftPanel.add(formCard, BorderLayout.NORTH);

        // --- Right Panel (FAQ & Hotline Card) ---
        JPanel rightPanel = new JPanel();
        rightPanel.setLayout(new BoxLayout(rightPanel, BoxLayout.Y_AXIS));
        rightPanel.setBackground(WHITE);
        rightPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(230, 230, 230), 1),
                BorderFactory.createEmptyBorder(25, 25, 25, 25)
        ));

        JLabel faqTitle = new JLabel("Frequently Asked Questions (FAQ)");
        faqTitle.setFont(new Font("Segoe UI", Font.BOLD, 20));
        faqTitle.setForeground(PRIMARY_BLUE);
        faqTitle.setAlignmentX(Component.LEFT_ALIGNMENT);

        rightPanel.add(faqTitle);
        rightPanel.add(Box.createVerticalStrut(20));

        // Add 3 elegant FAQs
        addFAQItem(rightPanel, "How do I request a booking cancellation?", 
                "You can cancel tickets directly with our system administrators or call our hotline with your Booking ID. Your refund is automatically processed back to the payment method.");
        addFAQItem(rightPanel, "What is the baggage limit for flights?", 
                "Every customer ticket includes 1 cabin bag (max 8kg) and 1 checked bag (max 23kg) at no extra cost. Excess luggage can be booked during check-in.");
        addFAQItem(rightPanel, "How early should I arrive at the gate?", 
                "We recommend arriving at the airport 2 hours prior to scheduled domestic departures and 3 hours for international flights. Boarding gates close 20 minutes before departure.");

        rightPanel.add(Box.createVerticalGlue());

        // Hotline Directory Section
        JPanel hotlinePanel = new JPanel(new GridLayout(3, 1, 5, 5));
        hotlinePanel.setOpaque(false);
        hotlinePanel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(230, 230, 230), 1),
                "Customer Support Directory",
                0, 0,
                new Font("Segoe UI", Font.BOLD, 14),
                PRIMARY_BLUE
        ));
        hotlinePanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel phoneSupport = new JLabel(" 📞 Toll-Free Support: +1-800-FLY-CHICKEN (24/7 Active)");
        phoneSupport.setFont(new Font("Segoe UI", Font.BOLD, 13));
        phoneSupport.setForeground(DARK_BLUE);

        JLabel emailSupport = new JLabel(" ✉ Email Center: support@chickenair.com (Response within 2h)");
        emailSupport.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        emailSupport.setForeground(DARK_BLUE);

        JLabel headquarter = new JLabel(" 📍 Office: Terminal 3, JFK International Airport, New York");
        headquarter.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        headquarter.setForeground(DARK_BLUE);

        hotlinePanel.add(phoneSupport);
        hotlinePanel.add(emailSupport);
        hotlinePanel.add(headquarter);

        rightPanel.add(hotlinePanel);

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

    private void addFAQItem(JPanel panel, String question, String answer) {
        JLabel qLabel = new JLabel("Q: " + question);
        qLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        qLabel.setForeground(DARK_BLUE);
        qLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JTextArea aArea = new JTextArea(answer);
        aArea.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        aArea.setForeground(new Color(110, 110, 110));
        aArea.setLineWrap(true);
        aArea.setWrapStyleWord(true);
        aArea.setEditable(false);
        aArea.setOpaque(false);
        aArea.setAlignmentX(Component.LEFT_ALIGNMENT);
        aArea.setMaximumSize(new Dimension(800, 60));

        panel.add(qLabel);
        panel.add(Box.createVerticalStrut(4));
        panel.add(aArea);
        panel.add(Box.createVerticalStrut(15));
    }

    private void submit() {
        String name = nameField.getText().trim();
        String email = emailField.getText().trim();
        String message = messageField.getText().trim();

        if (name.isEmpty() || email.isEmpty() || message.isEmpty()) {
            confirmationLabel.setText("Please fill in all fields.");
            confirmationLabel.setForeground(Color.RED);
            return;
        }

        confirmationLabel.setText("Support ticket submitted! We will contact you soon.");
        confirmationLabel.setForeground(new Color(46, 204, 113));
        nameField.setText("");
        emailField.setText("");
        messageField.setText("");
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new ContactUsForm().setVisible(true));
    }
}