package com.noobcoder.chickenfront.forms;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.RoundRectangle2D;
import java.net.http.HttpResponse;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import org.json.JSONArray;
import org.json.JSONObject;
import com.noobcoder.chickenfront.util.HttpClientUtil;

public class AdminDashboardForm extends JFrame {
    private JPanel sidebarPanel;
    private JPanel mainPanel;
    private JPanel contentPanel;
    private JPanel headerPanel;
    private boolean sidebarVisible = true;
    private javax.swing.Timer slideTimer;
    private int sidebarTargetWidth = 250;
    private int sidebarCollapsedWidth = 50;
    private int sidebarCurrentWidth = 250;
    private JTable flightTable;
    private DefaultTableModel tableModel;
    private JLabel messageLabel;
    private JLabel sidebarTitleLabel;

    // Custom Colors (matching AirlineReservationDashboard)
    private final Color PRIMARY_BLUE = new Color(41, 128, 185);
    private final Color SECONDARY_BLUE = new Color(52, 152, 219);
    private final Color LIGHT_BLUE = new Color(174, 214, 241);
    private final Color DARK_BLUE = new Color(23, 32, 42);
    private final Color BACKGROUND_COLOR = new Color(248, 249, 250);
    private final Color WHITE = Color.WHITE;
    private final Color HOVER_COLOR = new Color(233, 247, 254);

    // Static flag to persist login session
    private static boolean isLoggedIn = false;

    public AdminDashboardForm() {
        initializeFrame();
        createComponents();
        setupLayout();
        loadFlights();
        setVisible(true);
    }

    private void initializeFrame() {
        setTitle("AMS - Admin Dashboard");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setMinimumSize(new Dimension(1000, 600));
        getContentPane().setBackground(BACKGROUND_COLOR);
    }

    private void createComponents() {
        setLayout(new BorderLayout());
        createSidebar();
        createMainContent();
    }

    private void createSidebar() {
        sidebarPanel = new JPanel() {
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

                RoundRectangle2D roundedRect = new RoundRectangle2D.Float(
                        0, 0, getWidth(), getHeight(), 20, 20
                );
                g2d.fill(roundedRect);
                g2d.dispose();
            }
        };
        sidebarPanel.setPreferredSize(new Dimension(sidebarTargetWidth, 0));
        sidebarPanel.setLayout(new BorderLayout());
        sidebarPanel.setBorder(new EmptyBorder(0, 0, 0, 0));

        headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);
        headerPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

        sidebarTitleLabel = new JLabel("AMS");
        sidebarTitleLabel.setFont(new Font("Segoe UI", Font.BOLD, 22));
        sidebarTitleLabel.setForeground(WHITE);
        sidebarTitleLabel.setHorizontalAlignment(SwingConstants.LEFT);
        sidebarTitleLabel.setPreferredSize(new Dimension(60, 40));

        JButton hamburgerBtn = createHamburgerButton();
        hamburgerBtn.setPreferredSize(new Dimension(40, 40));
        hamburgerBtn.setMaximumSize(new Dimension(40, 40));

        headerPanel.add(sidebarTitleLabel, BorderLayout.WEST);
        headerPanel.add(hamburgerBtn, BorderLayout.EAST);

        JPanel navPanel = createNavigationPanel();
        JPanel bottomPanel = createBottomPanel();

        sidebarPanel.add(headerPanel, BorderLayout.NORTH);
        sidebarPanel.add(navPanel, BorderLayout.CENTER);
        sidebarPanel.add(bottomPanel, BorderLayout.SOUTH);
    }

    private void updateHamburgerMargin() {
        int leftPad = sidebarVisible ? 10 : 14;
        headerPanel.setBorder(new EmptyBorder(10, leftPad, 10, 10));
        headerPanel.revalidate();
        headerPanel.repaint();
    }

    private JButton createHamburgerButton() {
        CustomStyledButton btn = new CustomStyledButton("", 30, PRIMARY_BLUE, WHITE, 2);
        btn.setPreferredSize(new Dimension(40, 40));
        btn.setMaximumSize(new Dimension(40, 40));
        btn.setFont(new Font("Arial", Font.BOLD, 20));
        btn.setForeground(WHITE);

        try {
            ImageIcon icon = new ImageIcon(getClass().getResource("/icons/hamburger.png"));
            btn.setIcon(new ImageIcon(icon.getImage().getScaledInstance(28, 28, Image.SCALE_SMOOTH)));
        } catch (Exception e) {
            System.out.println("Failed to load hamburger.png: " + e.getMessage());
            btn.setText("☰");
        }

        btn.addActionListener(e -> toggleSidebar());
        return btn;
    }

    private JPanel createNavigationPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setOpaque(false);
        panel.setBorder(new EmptyBorder(30, 0, 0, 0));

        String[] buttonTexts = {
                "Add Flight",
                "Modify Flight",
                "Delete Flight",
                "Customer Dashboard"
        };

        for (String text : buttonTexts) {
            JButton btn = createNavButton(text);
            panel.add(btn);
            panel.add(Box.createVerticalStrut(25));
        }
        panel.remove(panel.getComponentCount() - 1);
        return panel;
    }

    private JButton createNavButton(String text) {
        CustomStyledButton btn = new CustomStyledButton(text, 30, PRIMARY_BLUE, WHITE, 2);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btn.setAlignmentX(Component.CENTER_ALIGNMENT);
        btn.setMaximumSize(new Dimension(200, 45));

        btn.addActionListener(e -> {
            switch (text) {
                case "Add Flight" -> addOrModifyFlight(false);
                case "Modify Flight" -> addOrModifyFlight(true);
                case "Delete Flight" -> deleteFlight();
                case "Customer Dashboard" -> {
                    dispose();
                    new AirlineReservationDashboard().setVisible(true);
                }
            }
        });

        return btn;
    }

    private JPanel createBottomPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        panel.setOpaque(false);

        CustomStyledButton logoutBtn = new CustomStyledButton("Logout", 30, PRIMARY_BLUE, WHITE, 2);
        logoutBtn.setFont(new Font("Segoe UI", Font.BOLD, 14));

        logoutBtn.addActionListener(e -> {
            isLoggedIn = false;
            HttpClientUtil.clearAuthCredentials();
            dispose();
            new AdminLoginForm().setVisible(true);
        });

        panel.add(logoutBtn);
        return panel;
    }

    private void createMainContent() {
        mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(BACKGROUND_COLOR);
        mainPanel.setBorder(new EmptyBorder(30, 30, 30, 30));

        JLabel headerLabel = new JLabel("Admin Dashboard");
        headerLabel.setFont(new Font("Segoe UI", Font.BOLD, 32));
        headerLabel.setForeground(DARK_BLUE);
        headerLabel.setBorder(new EmptyBorder(0, 0, 30, 0));

        JPanel tablePanel = createTablePanel();

        mainPanel.add(headerLabel, BorderLayout.NORTH);
        mainPanel.add(tablePanel, BorderLayout.CENTER);

        messageLabel = new JLabel(" ", SwingConstants.CENTER);
        messageLabel.setForeground(DARK_BLUE);
        mainPanel.add(messageLabel, BorderLayout.SOUTH);

        contentPanel = new JPanel(new BorderLayout());
        contentPanel.add(mainPanel, BorderLayout.CENTER);
    }

    private JPanel createTablePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(230, 230, 230), 1),
                new EmptyBorder(20, 20, 20, 20)
        ));

        JLabel tableTitle = new JLabel("Flight Schedule");
        tableTitle.setFont(new Font("Segoe UI", Font.BOLD, 20));
        tableTitle.setForeground(DARK_BLUE);
        tableTitle.setBorder(new EmptyBorder(0, 0, 20, 0));

        String[] columns = {"Flight Number", "Origin", "Destination", "Departure", "Arrival", "Price", "Total Seats", "Available Seats", "Booked Seats"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        flightTable = new JTable(tableModel);
        styleTable(flightTable);

        JScrollPane scrollPane = new JScrollPane(flightTable);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getViewport().setBackground(WHITE);

        panel.add(tableTitle, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    private void styleTable(JTable table) {
        table.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        table.setRowHeight(45);
        table.setShowGrid(false);
        table.setIntercellSpacing(new Dimension(0, 1));
        table.setSelectionBackground(new Color(233, 247, 254));
        table.setSelectionForeground(DARK_BLUE);

        JTableHeader header = table.getTableHeader();
        header.setFont(new Font("Segoe UI", Font.BOLD, 14));
        header.setBackground(PRIMARY_BLUE);
        header.setForeground(WHITE);
        header.setPreferredSize(new Dimension(0, 50));
        header.setBorder(BorderFactory.createEmptyBorder());

        DefaultTableCellRenderer renderer = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                                                           boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

                if (!isSelected) {
                    if (row % 2 == 0) {
                        c.setBackground(WHITE);
                    } else {
                        c.setBackground(new Color(245, 245, 245));
                    }
                }

                setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(230, 230, 230)));
                return c;
            }
        };

        for (int i = 0; i < table.getColumnCount(); i++) {
            table.getColumnModel().getColumn(i).setCellRenderer(renderer);
        }
    }

    private void loadFlights() {
        try {
            // Debug: Check authentication state
            System.out.println("isLoggedIn: " + isLoggedIn);
            System.out.println("authHeader: " + (HttpClientUtil.isAuthHeaderSet() ? "Set" : "Null"));

            if (!isLoggedIn) {
                messageLabel.setText("Not logged in. Redirecting to login...");
                System.out.println("Redirecting due to isLoggedIn = false");
                dispose();
                new AdminLoginForm().setVisible(true);
                return;
            }

            HttpResponse<String> res = HttpClientUtil.sendGetRequest("/admin/flights/summary");
            System.out.println("API Response Status: " + res.statusCode());
            System.out.println("API Response Body: " + res.body());

            if (res.statusCode() == 200) {
                tableModel.setRowCount(0);
                JSONArray flights;
                try {
                    flights = new JSONArray(res.body());
                } catch (Exception ex) {
                    flights = new JSONObject(res.body()).getJSONArray("content");
                }
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

                for (int i = 0; i < flights.length(); i++) {
                    JSONObject f = flights.getJSONObject(i);
                    LocalDateTime departure = LocalDateTime.parse(f.getString("departureTime"));
                    LocalDateTime arrival = LocalDateTime.parse(f.getString("arrivalTime"));

                    tableModel.addRow(new Object[]{
                            f.getString("flightNumber"),
                            f.getString("origin"),
                            f.getString("destination"),
                            departure.format(formatter),
                            arrival.format(formatter),
                            f.optDouble("price", 0.0),
                            f.optInt("totalSeats", 0),
                            f.optInt("availableSeats", 0),
                            f.optInt("bookedSeats", 0)
                    });
                }
                messageLabel.setText("Flights loaded: " + flights.length());
            } else if (res.statusCode() == 401) {
                messageLabel.setText("Unauthorized: Please log in again.");
                System.out.println("Unauthorized: API returned 401");
                isLoggedIn = false;
                HttpClientUtil.clearAuthCredentials();
                dispose();
                new AdminLoginForm().setVisible(true);
            } else {
                messageLabel.setText("Failed to load flights: HTTP " + res.statusCode());
                System.out.println("Failed to load flights: HTTP " + res.statusCode());
            }
        } catch (Exception e) {
            e.printStackTrace();
            messageLabel.setText("Error loading flights: " + e.getMessage());
            System.out.println("Exception in loadFlights: " + e.getMessage());
        }
    }

    private void addOrModifyFlight(boolean isModify) {
        int row = isModify ? flightTable.getSelectedRow() : -1;
        if (isModify && row == -1) {
            JOptionPane.showMessageDialog(this, "Select a flight to modify.", "Warning", JOptionPane.WARNING_MESSAGE);
            messageLabel.setText("Select a flight to modify.");
            return;
        }

        JTextField flightNumberField = new JTextField(isModify ? tableModel.getValueAt(row, 0).toString() : "", 10);
        JTextField originField = new JTextField(isModify ? tableModel.getValueAt(row, 1).toString() : "", 10);
        JTextField destinationField = new JTextField(isModify ? tableModel.getValueAt(row, 2).toString() : "", 10);
        JTextField departureField = new JTextField(isModify ? tableModel.getValueAt(row, 3).toString() : "", 10);
        JTextField arrivalField = new JTextField(isModify ? tableModel.getValueAt(row, 4).toString() : "", 10);
        JTextField priceField = new JTextField(isModify ? tableModel.getValueAt(row, 5).toString() : "", 10);

        JPanel panel = new JPanel(new GridLayout(6, 2, 10, 10));
        panel.add(new JLabel("Flight #:")); panel.add(flightNumberField);
        panel.add(new JLabel("Origin:")); panel.add(originField);
        panel.add(new JLabel("Destination:")); panel.add(destinationField);
        panel.add(new JLabel("Departure (YYYY-MM-DD HH:MM):")); panel.add(departureField);
        panel.add(new JLabel("Arrival (YYYY-MM-DD HH:MM):")); panel.add(arrivalField);
        panel.add(new JLabel("Price ($):")); panel.add(priceField);

        int result = JOptionPane.showConfirmDialog(this, panel, (isModify ? "Modify" : "Add") + " Flight", JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION) {
            try {
                String flightNumber = flightNumberField.getText().trim();
                String origin = originField.getText().trim();
                String destination = destinationField.getText().trim();
                String departureStr = departureField.getText().trim();
                String arrivalStr = arrivalField.getText().trim();
                String priceStr = priceField.getText().trim();

                if (flightNumber.isEmpty() || origin.isEmpty() || destination.isEmpty() || 
                    departureStr.isEmpty() || arrivalStr.isEmpty() || priceStr.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "All fields are required.", "Validation Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                double price;
                try {
                    price = Double.parseDouble(priceStr);
                    if (price < 0) {
                        JOptionPane.showMessageDialog(this, "Price must be a non-negative number.", "Validation Error", JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                } catch (NumberFormatException nfe) {
                    JOptionPane.showMessageDialog(this, "Invalid price format. Please enter a valid number.", "Validation Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                // Convert input format (YYYY-MM-DD HH:MM) to API format (YYYY-MM-DDTHH:MM:SS)
                DateTimeFormatter inputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
                DateTimeFormatter apiFormatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;
                LocalDateTime departure;
                LocalDateTime arrival;
                try {
                    departure = LocalDateTime.parse(departureStr, inputFormatter);
                } catch (DateTimeParseException e) {
                    JOptionPane.showMessageDialog(this, "Departure time must match YYYY-MM-DD HH:MM.", "Validation Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                try {
                    arrival = LocalDateTime.parse(arrivalStr, inputFormatter);
                } catch (DateTimeParseException e) {
                    JOptionPane.showMessageDialog(this, "Arrival time must match YYYY-MM-DD HH:MM.", "Validation Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                if (departure.isAfter(arrival) || departure.isEqual(arrival)) {
                    JOptionPane.showMessageDialog(this, "Departure time must be before arrival time.", "Validation Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                JSONObject json = new JSONObject()
                        .put("flightNumber", flightNumber)
                        .put("origin", origin)
                        .put("destination", destination)
                        .put("departureTime", departure.format(apiFormatter))
                        .put("arrivalTime", arrival.format(apiFormatter))
                        .put("price", price);

                if (isModify) {
                    HttpResponse<String> getResp = HttpClientUtil.sendGetRequest("/admin/flights/" + flightNumber);
                    if (getResp.statusCode() == 200) {
                        JSONObject existing = new JSONObject(getResp.body());
                        json.put("totalSeats", existing.optInt("totalSeats", 100));
                        json.put("availableSeats", existing.optInt("availableSeats", 100));
                    } else {
                        json.put("totalSeats", 100);
                        json.put("availableSeats", 100);
                    }

                    HttpResponse<String> putResp = HttpClientUtil.sendPutRequest("/admin/flights/" + flightNumber, json.toString());
                    if (putResp.statusCode() == 200) {
                        JOptionPane.showMessageDialog(this, "Flight modified successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
                        messageLabel.setText("Modified successfully.");
                    } else {
                        JOptionPane.showMessageDialog(this, "Modify failed: HTTP " + putResp.statusCode() + "\n" + putResp.body(), "Error", JOptionPane.ERROR_MESSAGE);
                        messageLabel.setText("Modify failed: HTTP " + putResp.statusCode());
                    }
                } else {
                    json.put("totalSeats", 100);
                    json.put("availableSeats", 100);
                    HttpResponse<String> postResp = HttpClientUtil.sendPostRequest("/admin/flights", json.toString());
                    if (postResp.statusCode() == 200) {
                        JOptionPane.showMessageDialog(this, "Flight added successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
                        messageLabel.setText("Added successfully.");
                    } else {
                        JOptionPane.showMessageDialog(this, "Add failed: HTTP " + postResp.statusCode() + "\n" + postResp.body(), "Error", JOptionPane.ERROR_MESSAGE);
                        messageLabel.setText("Add failed: HTTP " + postResp.statusCode());
                    }
                }
                loadFlights(); // Auto-update table
            } catch (Exception e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(this, "Error processing flight: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                messageLabel.setText("Error processing flight: " + e.getMessage());
            }
        }
    }

    private void deleteFlight() {
        int row = flightTable.getSelectedRow();
        if (row == -1) {
            messageLabel.setText("Select a flight to delete.");
            return;
        }
        String fn = tableModel.getValueAt(row, 0).toString();
        int confirm = JOptionPane.showConfirmDialog(this, "Delete flight " + fn + "?", "Confirm", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                HttpResponse<String> res = HttpClientUtil.sendDeleteRequest("/admin/flights/" + fn);
                messageLabel.setText(res.statusCode() == 200 ? "Deleted successfully." : "Delete failed: HTTP " + res.statusCode());
                loadFlights(); // Auto-update table
            } catch (Exception e) {
                e.printStackTrace();
                messageLabel.setText("Delete error: " + e.getMessage());
            }
        }
    }

    private void toggleSidebar() {
        if (slideTimer != null && slideTimer.isRunning()) {
            slideTimer.stop();
        }

        int startWidth = sidebarCurrentWidth;
        int endWidth = sidebarVisible ? sidebarCollapsedWidth : sidebarTargetWidth;
        sidebarVisible = !sidebarVisible;

        updateHamburgerMargin();

        slideTimer = new javax.swing.Timer(10, new ActionListener() {
            int step = 0;
            final int totalSteps = 20;

            @Override
            public void actionPerformed(ActionEvent e) {
                step++;
                double progress = (double) step / totalSteps;
                progress = 1 - Math.pow(1 - progress, 3);

                sidebarCurrentWidth = (int) (startWidth + (endWidth - startWidth) * progress);
                sidebarPanel.setPreferredSize(new Dimension(sidebarCurrentWidth, 0));

                for (Component comp : sidebarPanel.getComponents()) {
                    if (comp != null && comp != sidebarPanel.getComponent(0)) {
                        comp.setVisible(sidebarCurrentWidth > sidebarCollapsedWidth + 10);
                    }
                }
                if (sidebarTitleLabel != null) {
                    sidebarTitleLabel.setVisible(sidebarCurrentWidth > sidebarCollapsedWidth + 10);
                }

                revalidate();
                repaint();

                if (step >= totalSteps) {
                    slideTimer.stop();
                }
            }
        });

        slideTimer.start();
    }

    private void setupLayout() {
        add(sidebarPanel, BorderLayout.WEST);
        add(contentPanel, BorderLayout.CENTER);
    }

    public static void setLoggedIn(boolean status) {
        isLoggedIn = status;
    }

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
        } catch (Exception e) {
            // Use default look and feel
        }

        SwingUtilities.invokeLater(() -> new AdminDashboardForm().setVisible(true));
    }
}