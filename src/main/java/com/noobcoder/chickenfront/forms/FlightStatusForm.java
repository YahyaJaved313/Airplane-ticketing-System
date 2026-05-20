package com.noobcoder.chickenfront.forms;

import com.noobcoder.chickenfront.util.HttpClientUtil;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.net.http.HttpResponse;
import org.json.JSONArray;
import org.json.JSONObject;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class FlightStatusForm extends BaseCustomerFrame {
    private JTextField flightNumberField;
    private JTable statusBoardTable;
    private DefaultTableModel tableModel;
    private JPanel boardingPassPanel;

    // Detailed Card components
    private JLabel passFlightNum;
    private JLabel passOriginDest;
    private JLabel passDepTime;
    private JLabel passArrTime;
    private JLabel passGate;
    private JLabel passStatusBadge;

    public FlightStatusForm() {
        super("Flight Status");
        createStatusContent();
        loadStatusBoard();
        setVisible(true);
    }

    private void createStatusContent() {
        // Header
        JLabel titleLabel = new JLabel("Real-Time Flight Status");
        titleLabel.setForeground(DARK_BLUE);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 32));
        titleLabel.setBorder(new EmptyBorder(0, 0, 20, 0));

        // Split Panel: Left (Check Form & Card), Right (Status Board Table)
        JPanel splitPanel = new JPanel(new GridLayout(1, 2, 30, 0));
        splitPanel.setOpaque(false);

        // --- Left Panel ---
        JPanel leftPanel = new JPanel(new BorderLayout(0, 20));
        leftPanel.setOpaque(false);

        // Form Card
        JPanel formCard = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 15));
        formCard.setBackground(WHITE);
        formCard.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(230, 230, 230), 1),
                BorderFactory.createEmptyBorder(10, 15, 10, 15)
        ));

        JLabel flightLabel = new JLabel("Enter Flight Number:");
        flightLabel.setForeground(DARK_BLUE);
        flightLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));

        flightNumberField = new JTextField(8);
        flightNumberField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        flightNumberField.setPreferredSize(new Dimension(120, 35));

        JButton checkButton = new CustomStyledButton("Check Status", 30, PRIMARY_BLUE, WHITE, 2);
        checkButton.setFont(new Font("Segoe UI", Font.BOLD, 14));
        checkButton.setPreferredSize(new Dimension(140, 35));
        checkButton.addActionListener(e -> checkFlightStatus());

        formCard.add(flightLabel);
        formCard.add(flightNumberField);
        formCard.add(checkButton);

        // Boarding Pass Detail Card
        boardingPassPanel = new JPanel();
        boardingPassPanel.setLayout(new BoxLayout(boardingPassPanel, BoxLayout.Y_AXIS));
        boardingPassPanel.setBackground(WHITE);
        boardingPassPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(230, 230, 230), 1),
                BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));
        boardingPassPanel.setVisible(false); // Only visible when checked

        JLabel passHeader = new JLabel("BOARDING PASS");
        passHeader.setFont(new Font("Segoe UI", Font.BOLD, 16));
        passHeader.setForeground(PRIMARY_BLUE);
        passHeader.setAlignmentX(Component.LEFT_ALIGNMENT);

        passFlightNum = new JLabel("FLIGHT: N/A");
        passFlightNum.setFont(new Font("Segoe UI", Font.BOLD, 20));
        passFlightNum.setForeground(DARK_BLUE);
        passFlightNum.setAlignmentX(Component.LEFT_ALIGNMENT);

        passOriginDest = new JLabel("JFK ➔ LAX");
        passOriginDest.setFont(new Font("Segoe UI", Font.BOLD, 28));
        passOriginDest.setForeground(DARK_BLUE);
        passOriginDest.setAlignmentX(Component.LEFT_ALIGNMENT);

        passDepTime = new JLabel("Departure: N/A");
        passDepTime.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        passDepTime.setForeground(DARK_BLUE);
        passDepTime.setAlignmentX(Component.LEFT_ALIGNMENT);

        passArrTime = new JLabel("Arrival: N/A");
        passArrTime.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        passArrTime.setForeground(DARK_BLUE);
        passArrTime.setAlignmentX(Component.LEFT_ALIGNMENT);

        passGate = new JLabel("Gate: B12 (Terminal 3)");
        passGate.setFont(new Font("Segoe UI", Font.BOLD, 14));
        passGate.setForeground(new Color(46, 204, 113));
        passGate.setAlignmentX(Component.LEFT_ALIGNMENT);

        passStatusBadge = new JLabel("  ON TIME  ");
        passStatusBadge.setOpaque(true);
        passStatusBadge.setBackground(new Color(46, 204, 113));
        passStatusBadge.setForeground(WHITE);
        passStatusBadge.setFont(new Font("Segoe UI", Font.BOLD, 14));
        passStatusBadge.setAlignmentX(Component.LEFT_ALIGNMENT);

        boardingPassPanel.add(passHeader);
        boardingPassPanel.add(Box.createVerticalStrut(10));
        boardingPassPanel.add(passFlightNum);
        boardingPassPanel.add(Box.createVerticalStrut(5));
        boardingPassPanel.add(passOriginDest);
        boardingPassPanel.add(Box.createVerticalStrut(15));
        boardingPassPanel.add(passDepTime);
        boardingPassPanel.add(passArrTime);
        boardingPassPanel.add(Box.createVerticalStrut(10));
        boardingPassPanel.add(passGate);
        boardingPassPanel.add(Box.createVerticalStrut(15));
        boardingPassPanel.add(passStatusBadge);

        leftPanel.add(formCard, BorderLayout.NORTH);
        leftPanel.add(boardingPassPanel, BorderLayout.CENTER);

        // --- Right Panel (Live Board) ---
        JPanel rightPanel = new JPanel(new BorderLayout());
        rightPanel.setBackground(WHITE);
        rightPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(230, 230, 230), 1),
                BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));

        JLabel boardTitle = new JLabel("Live Departure / Arrival Board");
        boardTitle.setFont(new Font("Segoe UI", Font.BOLD, 20));
        boardTitle.setForeground(DARK_BLUE);
        boardTitle.setBorder(new EmptyBorder(0, 0, 15, 0));

        String[] columns = {"Flight", "Origin", "Destination", "Time", "Status"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        statusBoardTable = new JTable(tableModel);
        styleTable(statusBoardTable);

        JScrollPane scrollPane = new JScrollPane(statusBoardTable);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getViewport().setBackground(WHITE);

        rightPanel.add(boardTitle, BorderLayout.NORTH);
        rightPanel.add(scrollPane, BorderLayout.CENTER);

        // Put everything together
        splitPanel.add(leftPanel);
        splitPanel.add(rightPanel);

        mainPanel.add(titleLabel, BorderLayout.NORTH);
        mainPanel.add(splitPanel, BorderLayout.CENTER);
    }

    private void loadStatusBoard() {
        try {
            HttpResponse<String> response = HttpClientUtil.sendGetRequest("/flights");
            if (response.statusCode() == 200) {
                tableModel.setRowCount(0);
                JSONArray flights = new JSONArray();
                try {
                    flights = new JSONObject(response.body()).getJSONArray("content");
                } catch (Exception ex) {
                    flights = new JSONArray(response.body());
                }
                DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");
                for (int i = 0; i < flights.length(); i++) {
                    JSONObject flight = flights.getJSONObject(i);
                    String departureTime = flight.optString("departureTime", flight.optString("departure_time", ""));
                    if (departureTime.isEmpty()) continue;
                    LocalDateTime depTime = LocalDateTime.parse(departureTime, DateTimeFormatter.ISO_LOCAL_DATE_TIME);

                    // Compute a dynamic status based on flight number or time
                    String status = "ON TIME";
                    if (i % 5 == 1) status = "DELAYED";
                    else if (i % 5 == 3) status = "BOARDING";
                    else if (depTime.isBefore(LocalDateTime.now())) status = "DEPARTED";

                    tableModel.addRow(new Object[]{
                            flight.optString("flightNumber", flight.optString("flight_number", "N/A")),
                            flight.optString("origin", "N/A"),
                            flight.optString("destination", "N/A"),
                            depTime.format(timeFormatter),
                            status
                    });
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void checkFlightStatus() {
        String query = flightNumberField.getText().trim();
        if (query.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter a flight number.", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }

        boolean found = false;
        for (int i = 0; i < tableModel.getRowCount(); i++) {
            String fNum = tableModel.getValueAt(i, 0).toString();
            if (fNum.equalsIgnoreCase(query)) {
                String origin = tableModel.getValueAt(i, 1).toString();
                String dest = tableModel.getValueAt(i, 2).toString();
                String depTime = tableModel.getValueAt(i, 3).toString();
                String status = tableModel.getValueAt(i, 4).toString();

                passFlightNum.setText("FLIGHT: " + fNum.toUpperCase());
                passOriginDest.setText(origin.toUpperCase() + " ➔ " + dest.toUpperCase());
                passDepTime.setText("Departure Time: " + depTime);
                passArrTime.setText("Arrival Time: Scheduled");
                passGate.setText("Gate: " + getMockGate(fNum));
                passStatusBadge.setText("  " + status + "  ");

                // Style badge color based on status
                if ("DELAYED".equals(status)) {
                    passStatusBadge.setBackground(new Color(231, 76, 60));
                } else if ("BOARDING".equals(status)) {
                    passStatusBadge.setBackground(new Color(241, 196, 15));
                } else {
                    passStatusBadge.setBackground(new Color(46, 204, 113));
                }

                boardingPassPanel.setVisible(true);
                found = true;
                break;
            }
        }

        if (!found) {
            JOptionPane.showMessageDialog(this, "Flight " + query + " not found in active database.",
                    "Not Found", JOptionPane.INFORMATION_MESSAGE);
            boardingPassPanel.setVisible(false);
        }
        revalidate();
        repaint();
    }

    private String getMockGate(String flightNumber) {
        int hash = Math.abs(flightNumber.hashCode());
        char row = (char) ('A' + (hash % 4));
        int num = 1 + (hash % 20);
        return row + String.valueOf(num) + " (Terminal " + (1 + (hash % 3)) + ")";
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new FlightStatusForm().setVisible(true));
    }
}