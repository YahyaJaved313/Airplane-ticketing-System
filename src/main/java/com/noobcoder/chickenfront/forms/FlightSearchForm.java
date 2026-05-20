package com.noobcoder.chickenfront.forms;

import com.noobcoder.chickenfront.util.HttpClientUtil;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.time.format.DateTimeFormatter;
import java.net.http.HttpResponse;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

public class FlightSearchForm extends BaseCustomerFrame {
    private JTextField departureField;
    private JTextField destinationField;
    private JSpinner datePicker;
    private JTable flightTable;
    private DefaultTableModel tableModel;
    private JButton searchButton;
    private JButton showAllButton;

    public FlightSearchForm() {
        super("Search Flights");
        createSearchContent();
        loadFlights();
        setVisible(true);
    }

    private void createSearchContent() {
        // Header Title
        JLabel titleLabel = new JLabel("Search Flights");
        titleLabel.setForeground(DARK_BLUE);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 32));
        titleLabel.setBorder(new EmptyBorder(0, 0, 20, 0));

        // Parameter Card Panel (to group search inputs horizontally without stretching)
        JPanel searchCard = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 15));
        searchCard.setBackground(WHITE);
        searchCard.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(230, 230, 230), 1),
                BorderFactory.createEmptyBorder(15, 20, 15, 20)
        ));

        JLabel departureLabel = new JLabel("Departure:");
        departureLabel.setForeground(DARK_BLUE);
        departureLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        departureField = new JTextField(12);
        departureField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        departureField.setPreferredSize(new Dimension(150, 35));

        JLabel destinationLabel = new JLabel("Destination:");
        destinationLabel.setForeground(DARK_BLUE);
        destinationLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        destinationField = new JTextField(12);
        destinationField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        destinationField.setPreferredSize(new Dimension(150, 35));

        JLabel dateLabel = new JLabel("Date:");
        dateLabel.setForeground(DARK_BLUE);
        dateLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));

        Date currentDate = Date.from(LocalDate.of(2025, 5, 26).atStartOfDay(ZoneId.systemDefault()).toInstant());
        datePicker = new JSpinner(new SpinnerDateModel(currentDate, null, null, java.util.Calendar.DAY_OF_MONTH));
        datePicker.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        datePicker.setPreferredSize(new Dimension(130, 35));
        JSpinner.DateEditor dateEditor = new JSpinner.DateEditor(datePicker, "yyyy-MM-dd");
        datePicker.setEditor(dateEditor);

        searchButton = new CustomStyledButton("Search", 30, PRIMARY_BLUE, WHITE, 2);
        searchButton.setFont(new Font("Segoe UI", Font.BOLD, 14));
        searchButton.setPreferredSize(new Dimension(110, 35));
        searchButton.addActionListener(e -> searchFlights());

        showAllButton = new CustomStyledButton("Show All", 30, PRIMARY_BLUE, WHITE, 2);
        showAllButton.setFont(new Font("Segoe UI", Font.BOLD, 14));
        showAllButton.setPreferredSize(new Dimension(110, 35));
        showAllButton.addActionListener(e -> showAllFlights());

        searchCard.add(departureLabel);
        searchCard.add(departureField);
        searchCard.add(destinationLabel);
        searchCard.add(destinationField);
        searchCard.add(dateLabel);
        searchCard.add(datePicker);
        searchCard.add(searchButton);
        searchCard.add(showAllButton);

        // Results Table Panel
        JPanel tablePanel = new JPanel(new BorderLayout());
        tablePanel.setBackground(WHITE);
        tablePanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(230, 230, 230), 1),
                BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));

        JLabel tableTitle = new JLabel("Available Flights");
        tableTitle.setFont(new Font("Segoe UI", Font.BOLD, 20));
        tableTitle.setForeground(DARK_BLUE);
        tableTitle.setBorder(new EmptyBorder(0, 0, 15, 0));

        String[] columns = {"Flight Number", "Origin", "Destination", "Date", "Dep Time", "Arr Time", "Price"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        flightTable = new JTable(tableModel);
        styleTable(flightTable); // Reuse base style!

        JScrollPane scrollPane = new JScrollPane(flightTable);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getViewport().setBackground(WHITE);

        tablePanel.add(tableTitle, BorderLayout.NORTH);
        tablePanel.add(scrollPane, BorderLayout.CENTER);

        // Assemble into mainPanel (defined in BaseCustomerFrame)
        JPanel topPanel = new JPanel(new BorderLayout(0, 20));
        topPanel.setOpaque(false);
        topPanel.add(titleLabel, BorderLayout.NORTH);
        topPanel.add(searchCard, BorderLayout.CENTER);

        mainPanel.add(topPanel, BorderLayout.NORTH);
        mainPanel.add(tablePanel, BorderLayout.CENTER);
    }

    private void loadFlights() {
        try {
            showAllFlights();
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error loading flights: " + e.getMessage());
        }
    }

    private void searchFlights() {
        String departure = departureField.getText().trim();
        String destination = destinationField.getText().trim();
        Date dateValue = (Date) datePicker.getValue();
        if (dateValue == null) {
            dateValue = new Date();
        }
        java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd");
        String date = sdf.format(dateValue);

        tableModel.setRowCount(0);

        try {
            String queryParams = "?origin=" + (departure.isEmpty() ? "" : URLEncoder.encode(departure, StandardCharsets.UTF_8)) +
                    "&destination=" + (destination.isEmpty() ? "" : URLEncoder.encode(destination, StandardCharsets.UTF_8)) +
                    "&date=" + (date.isEmpty() ? "" : URLEncoder.encode(date, StandardCharsets.UTF_8));
            String fullUrl = "/flights/search" + queryParams;

            HttpResponse<String> response = HttpClientUtil.sendGetRequest(fullUrl);

            if (response.statusCode() == 200) {
                JSONArray flights = new JSONArray();
                try {
                    flights = new JSONObject(response.body()).getJSONArray("content");
                } catch (Exception ex) {
                    flights = new JSONArray(response.body());
                }
                DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");
                for (int i = 0; i < flights.length(); i++) {
                    JSONObject flight = flights.getJSONObject(i);
                    String depTimeStr = flight.optString("departureTime", flight.optString("departure_time", ""));
                    String arrTimeStr = flight.optString("arrivalTime", flight.optString("arrival_time", ""));
                    LocalDateTime depTime = parseTimeSafely(depTimeStr);
                    LocalDateTime arrTime = parseTimeSafely(arrTimeStr);
                    tableModel.addRow(new Object[]{
                            flight.optString("flightNumber", flight.optString("flight_number", "N/A")),
                            flight.optString("origin", "N/A"),
                            flight.optString("destination", "N/A"),
                            depTime.toLocalDate(),
                            depTime.format(timeFormatter),
                            arrTime.format(timeFormatter),
                            String.format("$%.2f", flight.optDouble("price", 0.0))
                    });
                }
                JOptionPane.showMessageDialog(this, "Found " + tableModel.getRowCount() + " flight(s).");
            } else {
                JOptionPane.showMessageDialog(this, "Failed to search flights: Status " + response.statusCode());
            }
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
        }
    }

    private void showAllFlights() {
        tableModel.setRowCount(0);
        try {
            HttpResponse<String> response = HttpClientUtil.sendGetRequest("/flights");
            if (response.statusCode() == 200) {
                JSONArray flights = new JSONArray();
                try {
                    flights = new JSONObject(response.body()).getJSONArray("content");
                } catch (Exception ex) {
                    flights = new JSONArray(response.body());
                }
                DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");
                for (int i = 0; i < flights.length(); i++) {
                    JSONObject flight = flights.getJSONObject(i);
                    String depTimeStr = flight.optString("departureTime", flight.optString("departure_time", ""));
                    String arrTimeStr = flight.optString("arrivalTime", flight.optString("arrival_time", ""));
                    LocalDateTime depTime = parseTimeSafely(depTimeStr);
                    LocalDateTime arrTime = parseTimeSafely(arrTimeStr);
                    tableModel.addRow(new Object[]{
                            flight.optString("flightNumber", flight.optString("flight_number", "N/A")),
                            flight.optString("origin", "N/A"),
                            flight.optString("destination", "N/A"),
                            depTime.toLocalDate(),
                            depTime.format(timeFormatter),
                            arrTime.format(timeFormatter),
                            String.format("$%.2f", flight.optDouble("price", 0.0))
                    });
                }
            } else {
                JOptionPane.showMessageDialog(this, "Failed to load flights: Status " + response.statusCode());
            }
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
        }
    }

    private LocalDateTime parseTimeSafely(String timeStr) {
        if (timeStr == null || timeStr.trim().isEmpty()) {
            return LocalDateTime.now();
        }
        try {
            return LocalDateTime.parse(timeStr, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        } catch (DateTimeParseException e) {
            return LocalDateTime.now();
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new FlightSearchForm().setVisible(true);
        });
    }
}