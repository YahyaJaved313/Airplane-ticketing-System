package com.noobcoder.chickenfront.forms;

import com.noobcoder.chickenfront.util.HttpClientUtil;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.net.http.HttpResponse;
import org.json.JSONArray;
import org.json.JSONObject;

public class BookFlightForm extends BaseCustomerFrame {
    private JTextField flightNumberField;
    private JTextField passengerNameField;
    private JComboBox<String> genderCombo;
    private JTextField passportNumberField;
    private JSpinner ticketsSpinner;
    private JLabel confirmationLabel;
    private JButton bookButton;
    private JTable flightSelectionTable;
    private DefaultTableModel tableModel;

    public BookFlightForm() {
        super("Book a Flight");
        createBookingContent();
        loadAvailableFlights();
        setVisible(true);
    }

    private void createBookingContent() {
        // Header
        JLabel titleLabel = new JLabel("Book Your Flight");
        titleLabel.setForeground(DARK_BLUE);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 32));
        titleLabel.setBorder(new EmptyBorder(0, 0, 20, 0));

        // Main split panel
        JPanel splitPanel = new JPanel(new GridBagLayout());
        splitPanel.setOpaque(false);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weighty = 1.0;

        // --- Left Panel (Booking Card) ---
        JPanel leftPanel = new JPanel(new BorderLayout());
        leftPanel.setOpaque(false);
        leftPanel.setPreferredSize(new Dimension(420, 0)); // Fixed width to keep fields tight

        JPanel bookingCard = new JPanel();
        bookingCard.setLayout(new BoxLayout(bookingCard, BoxLayout.Y_AXIS));
        bookingCard.setBackground(WHITE);
        bookingCard.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(230, 230, 230), 1),
                BorderFactory.createEmptyBorder(25, 25, 25, 25)
        ));

        JLabel formTitle = new JLabel("Passenger Information");
        formTitle.setFont(new Font("Segoe UI", Font.BOLD, 18));
        formTitle.setForeground(DARK_BLUE);
        formTitle.setAlignmentX(Component.LEFT_ALIGNMENT);

        JPanel fieldsPanel = new JPanel(new GridLayout(5, 2, 15, 15));
        fieldsPanel.setOpaque(false);
        fieldsPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel flightNumberLabel = new JLabel("Flight Number:");
        flightNumberLabel.setForeground(DARK_BLUE);
        flightNumberLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        flightNumberField = new JTextField();
        flightNumberField.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        JLabel passengerNameLabel = new JLabel("Passenger Name:");
        passengerNameLabel.setForeground(DARK_BLUE);
        passengerNameLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        passengerNameField = new JTextField();
        passengerNameField.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        JLabel genderLabel = new JLabel("Gender:");
        genderLabel.setForeground(DARK_BLUE);
        genderLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        genderCombo = new JComboBox<>(new String[]{"Male", "Female", "Other"});
        genderCombo.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        JLabel passportNumberLabel = new JLabel("Passport Number:");
        passportNumberLabel.setForeground(DARK_BLUE);
        passportNumberLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        passportNumberField = new JTextField();
        passportNumberField.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        JLabel ticketsLabel = new JLabel("Number of Tickets:");
        ticketsLabel.setForeground(DARK_BLUE);
        ticketsLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        ticketsSpinner = new JSpinner(new SpinnerNumberModel(1, 1, 100, 1));
        ticketsSpinner.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        fieldsPanel.add(flightNumberLabel);
        fieldsPanel.add(flightNumberField);
        fieldsPanel.add(passengerNameLabel);
        fieldsPanel.add(passengerNameField);
        fieldsPanel.add(genderLabel);
        fieldsPanel.add(genderCombo);
        fieldsPanel.add(passportNumberLabel);
        fieldsPanel.add(passportNumberField);
        fieldsPanel.add(ticketsLabel);
        fieldsPanel.add(ticketsSpinner);

        bookButton = new CustomStyledButton("Proceed to Booking", 30, PRIMARY_BLUE, WHITE, 2);
        bookButton.setFont(new Font("Segoe UI", Font.BOLD, 14));
        bookButton.setMaximumSize(new Dimension(370, 45));
        bookButton.setAlignmentX(Component.LEFT_ALIGNMENT);
        bookButton.addActionListener(e -> bookFlight());

        confirmationLabel = new JLabel("", SwingConstants.LEFT);
        confirmationLabel.setForeground(DARK_BLUE);
        confirmationLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        confirmationLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        bookingCard.add(formTitle);
        bookingCard.add(Box.createVerticalStrut(20));
        bookingCard.add(fieldsPanel);
        bookingCard.add(Box.createVerticalStrut(25));
        bookingCard.add(bookButton);
        bookingCard.add(Box.createVerticalStrut(15));
        bookingCard.add(confirmationLabel);

        leftPanel.add(bookingCard, BorderLayout.NORTH);

        // --- Right Panel (Flight Selector Table) ---
        JPanel rightPanel = new JPanel(new BorderLayout());
        rightPanel.setBackground(WHITE);
        rightPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(230, 230, 230), 1),
                BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));

        JLabel selectTitle = new JLabel("Quick Select: Click a flight to auto-fill the form");
        selectTitle.setFont(new Font("Segoe UI", Font.BOLD, 16));
        selectTitle.setForeground(PRIMARY_BLUE);
        selectTitle.setBorder(new EmptyBorder(0, 0, 15, 0));

        String[] columns = {"Flight #", "Origin", "Destination", "Seats Available", "Price", "Status"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        flightSelectionTable = new JTable(tableModel);
        styleTable(flightSelectionTable);

        // Click row handler
        flightSelectionTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int row = flightSelectionTable.getSelectedRow();
                if (row != -1) {
                    String selectedFlight = flightSelectionTable.getValueAt(row, 0).toString();
                    flightNumberField.setText(selectedFlight);
                }
            }
        });

        JScrollPane scrollPane = new JScrollPane(flightSelectionTable);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getViewport().setBackground(WHITE);

        rightPanel.add(selectTitle, BorderLayout.NORTH);
        rightPanel.add(scrollPane, BorderLayout.CENTER);

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

    private void loadAvailableFlights() {
        try {
            HttpResponse<String> response = HttpClientUtil.sendGetRequest("/flights/status");
            if (response.statusCode() == 200) {
                tableModel.setRowCount(0);
                JSONArray flights;
                try {
                    flights = new JSONArray(response.body());
                } catch (Exception ex) {
                    flights = new JSONObject(response.body()).getJSONArray("content");
                }
                for (int i = 0; i < flights.length(); i++) {
                    JSONObject flight = flights.getJSONObject(i);
                    String flightNum = flight.optString("flightNumber", flight.optString("flight_number", "N/A"));
                    String origin = flight.optString("origin", "N/A");
                    String destination = flight.optString("destination", "N/A");
                    int availableSeats = flight.optInt("availableSeats", flight.optInt("available_seats", 100));
                    double price = flight.optDouble("price", 0.0);
                    String status = flight.optString("bookingStatus", flight.optString("booking_status", "AVAILABLE"));

                    tableModel.addRow(new Object[]{flightNum, origin, destination, availableSeats, String.format("$%.2f", price), status});
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void bookFlight() {
        String flightNumber = flightNumberField.getText().trim();
        String passengerName = passengerNameField.getText().trim();
        String gender = (String) genderCombo.getSelectedItem();
        String passportNumber = passportNumberField.getText().trim();
        int numberOfTickets = (Integer) ticketsSpinner.getValue();

        if (flightNumber.isEmpty() || passengerName.isEmpty() || gender == null || passportNumber.isEmpty()) {
            confirmationLabel.setText("Please fill in all fields.");
            confirmationLabel.setForeground(Color.RED);
            return;
        }

        // Retrieve flight details from the table for pre-booking confirmation & bill calculation
        String origin = "N/A";
        String destination = "N/A";
        double pricePerTicket = 0.0;
        boolean flightFound = false;

        for (int i = 0; i < tableModel.getRowCount(); i++) {
            String tableFlightNum = tableModel.getValueAt(i, 0).toString();
            if (tableFlightNum.equalsIgnoreCase(flightNumber)) {
                flightNumber = tableFlightNum; // keep consistent case
                origin = tableModel.getValueAt(i, 1).toString();
                destination = tableModel.getValueAt(i, 2).toString();
                String priceStr = tableModel.getValueAt(i, 4).toString().replace("$", "").trim();
                try {
                    pricePerTicket = Double.parseDouble(priceStr);
                } catch (Exception ex) {
                    pricePerTicket = 0.0;
                }
                flightFound = true;
                break;
            }
        }

        if (!flightFound) {
            JOptionPane.showMessageDialog(this, "The specified flight was not found in the loaded list.", "Invalid Flight Number", JOptionPane.ERROR_MESSAGE);
            confirmationLabel.setText("Invalid flight number.");
            confirmationLabel.setForeground(Color.RED);
            return;
        }

        double totalBill = pricePerTicket * numberOfTickets;

        // Show pre-booking confirmation with all details
        String confirmMsg = String.format(
            "Please confirm your booking details:\n\n" +
            "Flight Number: %s\n" +
            "Route: %s ➔ %s\n" +
            "Passenger Name: %s\n" +
            "Gender: %s\n" +
            "Passport Number: %s\n" +
            "Number of Tickets: %d\n" +
            "Price per Ticket: $%.2f\n\n" +
            "Total Bill: $%.2f\n\n" +
            "Do you want to proceed to payment?",
            flightNumber, origin, destination, passengerName, gender, passportNumber, numberOfTickets, pricePerTicket, totalBill
        );

        int confirmResult = JOptionPane.showConfirmDialog(this, confirmMsg, "Confirm Booking Details", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
        if (confirmResult != JOptionPane.YES_OPTION) {
            confirmationLabel.setText("Booking cancelled.");
            confirmationLabel.setForeground(Color.RED);
            return;
        }

        JComboBox<String> paymentMethodCombo = new JComboBox<>(new String[]{"Credit Card", "Debit Card", "PayPal", "Cash"});
        paymentMethodCombo.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        JTextField cardNumberField = new JTextField(10);
        cardNumberField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        JTextField expiryField = new JTextField(5);
        expiryField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        JTextField cvvField = new JTextField(3);
        cvvField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        JLabel cardNumberLabel = new JLabel("Card Number:");
        cardNumberLabel.setForeground(DARK_BLUE);
        cardNumberLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        JLabel expiryLabel = new JLabel("Expiry (MM/YY):");
        expiryLabel.setForeground(DARK_BLUE);
        expiryLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        JLabel cvvLabel = new JLabel("CVV:");
        cvvLabel.setForeground(DARK_BLUE);
        cvvLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        JLabel cashMessageLabel = new JLabel("Please pay at the counter.", SwingConstants.CENTER);
        cashMessageLabel.setForeground(DARK_BLUE);
        cashMessageLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        JPanel paymentPanel = new JPanel(new GridLayout(5, 2, 10, 10));
        paymentPanel.setBackground(BACKGROUND_COLOR);
        paymentPanel.add(new JLabel("Payment Method:"));
        paymentPanel.add(paymentMethodCombo);
        paymentPanel.add(cardNumberLabel);
        paymentPanel.add(cardNumberField);
        paymentPanel.add(expiryLabel);
        paymentPanel.add(expiryField);
        paymentPanel.add(cvvLabel);
        paymentPanel.add(cvvField);
        paymentPanel.add(new JLabel());
        paymentPanel.add(cashMessageLabel);

        cashMessageLabel.setVisible(false);
        paymentMethodCombo.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                if (e.getStateChange() == ItemEvent.SELECTED) {
                    String selectedMethod = (String) paymentMethodCombo.getSelectedItem();
                    boolean isCash = "Cash".equals(selectedMethod);
                    cardNumberLabel.setVisible(!isCash);
                    cardNumberField.setVisible(!isCash);
                    expiryLabel.setVisible(!isCash);
                    expiryField.setVisible(!isCash);
                    cvvLabel.setVisible(!isCash);
                    cvvField.setVisible(!isCash);
                    cashMessageLabel.setVisible(isCash);
                    paymentPanel.revalidate();
                    paymentPanel.repaint();
                }
            }
        });

        int paymentResult = JOptionPane.showConfirmDialog(this, paymentPanel, "Payment Details", JOptionPane.OK_CANCEL_OPTION);
        if (paymentResult != JOptionPane.OK_OPTION) {
            confirmationLabel.setText("Payment cancelled.");
            confirmationLabel.setForeground(Color.RED);
            return;
        }

        String paymentMethod = (String) paymentMethodCombo.getSelectedItem();
        if (!"Cash".equals(paymentMethod)) {
            String cardNumber = cardNumberField.getText();
            String expiry = expiryField.getText();
            String cvv = cvvField.getText();
            if (cardNumber.isEmpty() || expiry.isEmpty() || cvv.isEmpty()) {
                confirmationLabel.setText("Please fill in all payment details.");
                confirmationLabel.setForeground(Color.RED);
                return;
            }
        }

        try {
            String query = String.format("?flightNumber=%s&numberOfTickets=%d&passengerName=%s&gender=%s&passportNumber=%s&paymentMethod=%s",
                    java.net.URLEncoder.encode(flightNumber, java.nio.charset.StandardCharsets.UTF_8),
                    numberOfTickets,
                    java.net.URLEncoder.encode(passengerName, java.nio.charset.StandardCharsets.UTF_8),
                    java.net.URLEncoder.encode(gender, java.nio.charset.StandardCharsets.UTF_8),
                    java.net.URLEncoder.encode(passportNumber, java.nio.charset.StandardCharsets.UTF_8),
                    java.net.URLEncoder.encode(paymentMethod, java.nio.charset.StandardCharsets.UTF_8)
            );
            HttpResponse<String> response = HttpClientUtil.sendPostRequest("/bookings/book" + query, "");
            if (response.statusCode() == 200) {
                JSONObject booking = new JSONObject(response.body());
                confirmationLabel.setText("Booking successful!");
                confirmationLabel.setForeground(new Color(46, 204, 113));
                double totalPrice = booking.optDouble("totalPrice", booking.optDouble("total_price", 0.0));
                String bill = String.format(
                        "===== Flight Booking Bill =====\n" +
                                "Booking ID: %d\n" +
                                "Flight Number: %s\n" +
                                "Passenger Name: %s\n" +
                                "Gender: %s\n" +
                                "Passport Number: %s\n" +
                                "Number of Tickets: %d\n" +
                                "Payment Method: %s\n" +
                                "Status: %s\n" +
                                "Total Price: $%.2f\n" +
                                "Date: %s\n" +
                                "==============================",
                        booking.getLong("bookingId"),
                        flightNumber, passengerName, gender, passportNumber,
                        numberOfTickets,
                        paymentMethod, booking.getString("status"),
                        totalPrice,
                        booking.getString("bookingDate").substring(0, 10)
                );
                JOptionPane.showMessageDialog(this, bill, "Booking Confirmation", JOptionPane.INFORMATION_MESSAGE);
                loadAvailableFlights(); // Reload seats
            } else {
                confirmationLabel.setText("Booking failed: " + response.body());
                confirmationLabel.setForeground(Color.RED);
            }
        } catch (Exception e) {
            confirmationLabel.setText("Error: " + e.getMessage());
            confirmationLabel.setForeground(Color.RED);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new BookFlightForm().setVisible(true));
    }
}