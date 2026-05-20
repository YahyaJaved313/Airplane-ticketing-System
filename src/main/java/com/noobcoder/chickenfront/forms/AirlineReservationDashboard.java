package com.noobcoder.chickenfront.forms;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.net.http.HttpResponse;
import org.json.JSONArray;
import org.json.JSONObject;
import com.noobcoder.chickenfront.util.HttpClientUtil;

public class AirlineReservationDashboard extends BaseCustomerFrame {
    private JTable flightTable;

    public AirlineReservationDashboard() {
        super("Dashboard");
        createDashboardContent();
        populateFlightData();
        setVisible(true);
    }

    private void createDashboardContent() {
        JLabel headerLabel = new JLabel("Airline Ticket Reservation System");
        headerLabel.setFont(new Font("Segoe UI", Font.BOLD, 32));
        headerLabel.setForeground(DARK_BLUE);
        headerLabel.setBorder(new EmptyBorder(0, 0, 30, 0));

        JPanel tablePanel = createTablePanel();

        mainPanel.add(headerLabel, BorderLayout.NORTH);
        mainPanel.add(tablePanel, BorderLayout.CENTER);
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

        String[] columns = {"Flight Number", "Departure Time", "Arrival Time", "Origin", "Destination", "Price"};

        DefaultTableModel model = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        flightTable = new JTable(model);
        styleTable(flightTable);

        JScrollPane scrollPane = new JScrollPane(flightTable);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getViewport().setBackground(WHITE);

        panel.add(tableTitle, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }



    private void populateFlightData() {
        if (flightTable != null) {
            DefaultTableModel model = (DefaultTableModel) flightTable.getModel();
            model.setRowCount(0); // Clear existing rows

            try {
                HttpResponse<String> response = HttpClientUtil.sendGetRequest("/flights");
                System.out.println("API Response: " + response.body()); // Debug log
                if (response.statusCode() == 200) {
                    JSONArray flights = new JSONObject(response.body()).getJSONArray("content");
                    for (int i = 0; i < flights.length(); i++) {
                        JSONObject flight = flights.getJSONObject(i);
                        String flightNumber = flight.optString("flight_number", flight.optString("flightNumber", "N/A"));
                        String departureTime = flight.optString("departure_time", flight.optString("departureTime", "N/A"));
                        String arrivalTime = flight.optString("arrival_time", flight.optString("arrivalTime", "N/A"));
                        String origin = flight.optString("origin", "N/A");
                        String destination = flight.optString("destination", "N/A");
                        double price = flight.optDouble("price", 0.0);

                        model.addRow(new Object[]{flightNumber, departureTime, arrivalTime, origin, destination, String.format("$%.2f", price)});
                    }
                } else {
                    JOptionPane.showMessageDialog(this, "Failed to fetch flight data: HTTP " + response.statusCode(),
                            "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Error fetching flight data: " + e.getMessage(),
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
        } catch (Exception e) {
            // Use default look and feel
        }

        SwingUtilities.invokeLater(() -> {
            new AirlineReservationDashboard();
        });
    }
}