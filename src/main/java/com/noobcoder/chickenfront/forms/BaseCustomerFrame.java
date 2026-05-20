package com.noobcoder.chickenfront.forms;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.RoundRectangle2D;
import com.noobcoder.chickenfront.util.HttpClientUtil;

public abstract class BaseCustomerFrame extends JFrame {
    protected JPanel sidebarPanel;
    protected JPanel mainPanel; // Content area where subclasses add their elements
    protected JPanel contentPanel;
    protected JPanel headerPanel;
    private boolean sidebarVisible = true;
    private javax.swing.Timer slideTimer;
    private int sidebarTargetWidth = 250;
    private int sidebarCollapsedWidth = 50;
    private int sidebarCurrentWidth = 250;
    protected JLabel hmLabel;

    // Custom Colors matching AirlineReservationDashboard
    protected final Color PRIMARY_BLUE = new Color(41, 128, 185);
    protected final Color SECONDARY_BLUE = new Color(52, 152, 219);
    protected final Color LIGHT_BLUE = new Color(174, 214, 241);
    protected final Color DARK_BLUE = new Color(23, 32, 42);
    protected final Color BACKGROUND_COLOR = new Color(248, 249, 250);
    protected final Color WHITE = Color.WHITE;
    protected final Color HOVER_COLOR = new Color(233, 247, 254);

    public BaseCustomerFrame(String title) {
        initializeFrame(title);
        createBaseComponents();
        setupBaseLayout();
    }

    private void initializeFrame(String title) {
        setTitle("Airline Ticket Reservation System - " + title);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setMinimumSize(new Dimension(1100, 700));
        getContentPane().setBackground(BACKGROUND_COLOR);
    }

    private void createBaseComponents() {
        setLayout(new BorderLayout());
        createSidebar();
        createMainPanel();
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

        hmLabel = new JLabel("H&M");
        hmLabel.setFont(new Font("Segoe UI", Font.BOLD, 22));
        hmLabel.setForeground(WHITE);
        hmLabel.setHorizontalAlignment(SwingConstants.LEFT);
        hmLabel.setPreferredSize(new Dimension(60, 40));

        JButton hamburgerBtn = createHamburgerButton();
        hamburgerBtn.setPreferredSize(new Dimension(40, 40));
        hamburgerBtn.setMaximumSize(new Dimension(40, 40));

        headerPanel.add(hmLabel, BorderLayout.WEST);
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
                "Dashboard",
                "Search Flights",
                "Book Flight",
                "Flight Status",
                "User Profile",
                "Contact Us"
        };

        for (String text : buttonTexts) {
            JButton btn = createNavButton(text);
            panel.add(btn);
            panel.add(Box.createVerticalStrut(20));
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
            if (isCurrentForm(text)) {
                return;
            }
            dispose();
            switch (text) {
                case "Dashboard":
                    new AirlineReservationDashboard().setVisible(true);
                    break;
                case "Search Flights":
                    new FlightSearchForm().setVisible(true);
                    break;
                case "Book Flight":
                    new BookFlightForm().setVisible(true);
                    break;
                case "Flight Status":
                    new FlightStatusForm().setVisible(true);
                    break;
                case "User Profile":
                    new UserProfileForm().setVisible(true);
                    break;
                case "Contact Us":
                    new ContactUsForm().setVisible(true);
                    break;
            }
        });

        return btn;
    }

    private boolean isCurrentForm(String buttonText) {
        String className = this.getClass().getSimpleName();
        switch (buttonText) {
            case "Dashboard": return className.equals("AirlineReservationDashboard");
            case "Search Flights": return className.equals("FlightSearchForm");
            case "Book Flight": return className.equals("BookFlightForm");
            case "Flight Status": return className.equals("FlightStatusForm");
            case "User Profile": return className.equals("UserProfileForm");
            case "Contact Us": return className.equals("ContactUsForm");
        }
        return false;
    }

    private JPanel createBottomPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        panel.setOpaque(false);

        CustomStyledButton logoutBtn = new CustomStyledButton("Logout", 30, PRIMARY_BLUE, WHITE, 2);
        logoutBtn.setFont(new Font("Segoe UI", Font.BOLD, 14));

        logoutBtn.addActionListener(e -> {
            HttpClientUtil.clearAuthCredentials();
            dispose();
            new LoginForm().setVisible(true);
        });

        panel.add(logoutBtn);
        return panel;
    }

    private void createMainPanel() {
        mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(BACKGROUND_COLOR);
        mainPanel.setBorder(new EmptyBorder(30, 30, 30, 30));

        contentPanel = new JPanel(new BorderLayout());
        contentPanel.add(mainPanel, BorderLayout.CENTER);
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
                if (hmLabel != null) {
                    hmLabel.setVisible(sidebarCurrentWidth > sidebarCollapsedWidth + 10);
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

    private void setupBaseLayout() {
        add(sidebarPanel, BorderLayout.WEST);
        add(contentPanel, BorderLayout.CENTER);
    }

    protected void styleTable(JTable table) {
        table.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        table.setRowHeight(45);
        table.setShowGrid(false);
        table.setIntercellSpacing(new Dimension(0, 1));
        table.setSelectionBackground(new Color(233, 247, 254));
        table.setSelectionForeground(DARK_BLUE);

        javax.swing.table.JTableHeader header = table.getTableHeader();
        header.setFont(new Font("Segoe UI", Font.BOLD, 14));
        header.setBackground(PRIMARY_BLUE);
        header.setForeground(WHITE);
        header.setPreferredSize(new Dimension(0, 50));
        header.setBorder(BorderFactory.createEmptyBorder());

        javax.swing.table.DefaultTableCellRenderer renderer = new javax.swing.table.DefaultTableCellRenderer() {
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
}
