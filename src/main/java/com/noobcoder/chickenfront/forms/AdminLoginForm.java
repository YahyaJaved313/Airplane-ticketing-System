package com.noobcoder.chickenfront.forms;

import com.noobcoder.chickenfront.util.HttpClientUtil;

import javax.swing.*;
import java.awt.*;
import java.net.http.HttpResponse;

public class AdminLoginForm extends JFrame {
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JLabel messageLabel;
    private JButton loginButton;
    private JButton backButton;

    // Theme colors from LoginForm (matching AdminDashboardForm)
    private final Color PRIMARY_BLUE = new Color(41, 128, 185);
    private final Color DARK_BLUE = new Color(23, 32, 42);
    private final Color BACKGROUND_COLOR = new Color(248, 249, 250);
    private final Color WHITE = Color.WHITE;

    public AdminLoginForm() {
        setTitle("AMS - Admin Login");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setMinimumSize(new Dimension(1000, 600));
        getContentPane().setBackground(BACKGROUND_COLOR); // Updated to match LoginForm

        JPanel backgroundPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                GradientPaint gradient = new GradientPaint(
                        0, 0, PRIMARY_BLUE,
                        0, getHeight(), new Color(52, 73, 94)
                );
                g2d.setPaint(gradient);
                g2d.fillRect(0, 0, getWidth(), getHeight());
                g2d.dispose();
            }
        };
        backgroundPanel.setLayout(new GridBagLayout());
        setContentPane(backgroundPanel);

        JPanel mainPanel = new JPanel(new GridLayout(6, 1, 10, 10));
        mainPanel.setBackground(WHITE);
        mainPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(230, 230, 230), 1),
                BorderFactory.createEmptyBorder(30, 30, 30, 30)
        ));

        JLabel titleLabel = new JLabel("Admin Login", SwingConstants.CENTER);
        titleLabel.setForeground(DARK_BLUE); // Updated to match LoginForm
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 32)); // Updated to match LoginForm
        mainPanel.add(titleLabel);

        JPanel usernamePanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        usernamePanel.setOpaque(false);
        JLabel usernameLabel = new JLabel("Email:");
        usernameLabel.setForeground(DARK_BLUE); // Updated to match LoginForm
        usernameLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14)); // Updated to match LoginForm
        usernameField = new JTextField(15);
        usernameField.setFont(new Font("Segoe UI", Font.PLAIN, 14)); // Consistent font for input
        usernamePanel.add(usernameLabel);
        usernamePanel.add(usernameField);
        mainPanel.add(usernamePanel);

        JPanel passwordPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        passwordPanel.setOpaque(false);
        JLabel passwordLabel = new JLabel("Password:");
        passwordLabel.setForeground(DARK_BLUE); // Updated to match LoginForm
        passwordLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14)); // Updated to match LoginForm
        passwordField = new JPasswordField(15);
        passwordField.setFont(new Font("Segoe UI", Font.PLAIN, 14)); // Consistent font for input
        passwordPanel.add(passwordLabel);
        passwordPanel.add(passwordField);
        mainPanel.add(passwordPanel);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        buttonPanel.setOpaque(false);
        loginButton = new CustomStyledButton("Login", 30, PRIMARY_BLUE, WHITE, 2); // Updated to match LoginForm
        loginButton.setFont(new Font("Segoe UI", Font.BOLD, 14)); // Updated to match LoginForm
        loginButton.addActionListener(e -> login());
        backButton = new CustomStyledButton("Back to Login", 30, PRIMARY_BLUE, WHITE, 2); // Updated to match LoginForm
        backButton.setFont(new Font("Segoe UI", Font.BOLD, 14)); // Updated to match LoginForm
        backButton.addActionListener(e -> goToLogin());
        buttonPanel.add(loginButton);
        buttonPanel.add(backButton);
        mainPanel.add(buttonPanel);

        messageLabel = new JLabel("", SwingConstants.CENTER);
        messageLabel.setForeground(DARK_BLUE); // Updated to match LoginForm
        messageLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14)); // Updated to match LoginForm
        mainPanel.add(messageLabel);

        backgroundPanel.add(mainPanel);
    }

    private void login() {
        String email = usernameField.getText();
        String password = new String(passwordField.getPassword());
        if (email.isEmpty() || password.isEmpty()) {
            messageLabel.setText("Please enter email and password.");
            return;
        }

        try {
            HttpClientUtil.setAuthCredentials(email, password);
            HttpResponse<String> response = HttpClientUtil.sendGetRequest("/admin/flights");
            if (response.statusCode() == 200) {
                messageLabel.setText("Admin login successful!");
                AdminDashboardForm.setLoggedIn(true);
                dispose();
                new AdminDashboardForm().setVisible(true);
            } else {
                messageLabel.setText("Admin login failed: Invalid credentials or insufficient permissions.");
                HttpClientUtil.clearAuthCredentials();
            }
        } catch (Exception e) {
            messageLabel.setText("Error: " + e.getMessage());
            HttpClientUtil.clearAuthCredentials();
        }
    }

    private void goToLogin() {
        dispose();
        new LoginForm().setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new AdminLoginForm().setVisible(true));
    }
}