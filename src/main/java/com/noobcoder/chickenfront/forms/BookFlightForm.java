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

    private int currentPage = 0;
    private int totalPages = 1;
    private JButton prevButton;
    private JButton nextButton;
    private JLabel pageLabel;
    private JTable bookingsTable;
    private DefaultTableModel bookingsTableModel;

    public BookFlightForm() {
        super("Book a Flight");
        createBookingContent();
        loadAvailableFlights();
        loadUserBookings();
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

        JButton cancelBookingButton = new CustomStyledButton("Cancel a Booking", 30, new Color(231, 76, 60), WHITE, 2);
        cancelBookingButton.setFont(new Font("Segoe UI", Font.BOLD, 14));
        cancelBookingButton.setMaximumSize(new Dimension(370, 45));
        cancelBookingButton.setAlignmentX(Component.LEFT_ALIGNMENT);
        cancelBookingButton.addActionListener(e -> showCancelBookingDialog());

        confirmationLabel = new JLabel("", SwingConstants.LEFT);
        confirmationLabel.setForeground(DARK_BLUE);
        confirmationLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        confirmationLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        bookingCard.add(formTitle);
        bookingCard.add(Box.createVerticalStrut(20));
        bookingCard.add(fieldsPanel);
        bookingCard.add(Box.createVerticalStrut(25));
        bookingCard.add(bookButton);
        bookingCard.add(Box.createVerticalStrut(10));
        bookingCard.add(cancelBookingButton);
        bookingCard.add(Box.createVerticalStrut(15));
        bookingCard.add(confirmationLabel);

        leftPanel.add(bookingCard, BorderLayout.NORTH);

        // --- Right Panel (Stack of Two Cards: Flights Selection and My Bookings) ---
        JPanel rightPanel = new JPanel(new GridBagLayout());
        rightPanel.setOpaque(false);

        // -- Card 1: Available Flights selection --
        JPanel flightsCard = new JPanel(new BorderLayout(0, 10));
        flightsCard.setBackground(WHITE);
        flightsCard.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(230, 230, 230), 1),
                BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));

        JLabel selectTitle = new JLabel("Quick Select: Click a flight to auto-fill the form");
        selectTitle.setFont(new Font("Segoe UI", Font.BOLD, 16));
        selectTitle.setForeground(PRIMARY_BLUE);
        selectTitle.setBorder(new EmptyBorder(0, 0, 5, 0));

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

        JScrollPane flightsScrollPane = new JScrollPane(flightSelectionTable);
        flightsScrollPane.setBorder(BorderFactory.createEmptyBorder());
        flightsScrollPane.getViewport().setBackground(WHITE);

        // Pagination controls panel
        JPanel paginationPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 0));
        paginationPanel.setOpaque(false);

        prevButton = new CustomStyledButton("< Prev", 20, PRIMARY_BLUE, WHITE, 1);
        prevButton.setFont(new Font("Segoe UI", Font.BOLD, 12));
        prevButton.setPreferredSize(new Dimension(90, 30));
        prevButton.addActionListener(e -> {
            if (currentPage > 0) {
                currentPage--;
                loadAvailableFlights();
            }
        });

        nextButton = new CustomStyledButton("Next >", 20, PRIMARY_BLUE, WHITE, 1);
        nextButton.setFont(new Font("Segoe UI", Font.BOLD, 12));
        nextButton.setPreferredSize(new Dimension(90, 30));
        nextButton.addActionListener(e -> {
            if (currentPage < totalPages - 1) {
                currentPage++;
                loadAvailableFlights();
            }
        });

        pageLabel = new JLabel("Page 1 of 1");
        pageLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
        pageLabel.setForeground(DARK_BLUE);

        paginationPanel.add(prevButton);
        paginationPanel.add(pageLabel);
        paginationPanel.add(nextButton);

        flightsCard.add(selectTitle, BorderLayout.NORTH);
        flightsCard.add(flightsScrollPane, BorderLayout.CENTER);
        flightsCard.add(paginationPanel, BorderLayout.SOUTH);

        // -- Card 2: My Bookings --
        JPanel bookingsCard = new JPanel(new BorderLayout(0, 10));
        bookingsCard.setBackground(WHITE);
        bookingsCard.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(230, 230, 230), 1),
                BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));

        JLabel bookingsTitle = new JLabel("My Bookings");
        bookingsTitle.setFont(new Font("Segoe UI", Font.BOLD, 16));
        bookingsTitle.setForeground(PRIMARY_BLUE);
        bookingsTitle.setBorder(new EmptyBorder(0, 0, 5, 0));

        String[] bookingColumns = {"Booking ID", "Flight #", "Passenger", "Tickets", "Total Price", "Status", "Date"};
        bookingsTableModel = new DefaultTableModel(bookingColumns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        bookingsTable = new JTable(bookingsTableModel);
        styleTable(bookingsTable);

        JScrollPane bookingsScrollPane = new JScrollPane(bookingsTable);
        bookingsScrollPane.setBorder(BorderFactory.createEmptyBorder());
        bookingsScrollPane.getViewport().setBackground(WHITE);

        bookingsCard.add(bookingsTitle, BorderLayout.NORTH);
        bookingsCard.add(bookingsScrollPane, BorderLayout.CENTER);

        // Add cards to rightPanel with GridBagLayout
        GridBagConstraints rightGbc = new GridBagConstraints();
        rightGbc.gridx = 0;
        rightGbc.fill = GridBagConstraints.BOTH;
        rightGbc.weightx = 1.0;
        rightGbc.gridwidth = GridBagConstraints.REMAINDER;

        // Flights Card gets 45% of vertical space, My Bookings gets 55%
        rightGbc.gridy = 0;
        rightGbc.weighty = 0.45;
        rightPanel.add(flightsCard, rightGbc);

        rightGbc.gridy = 1;
        rightGbc.weighty = 0.55;
        rightGbc.insets = new Insets(20, 0, 0, 0); // Spacing between cards
        rightPanel.add(bookingsCard, rightGbc);

        // GridBagConstraints Setup for Left/Right in splitPanel
        gbc.gridx = 0;
        gbc.weightx = 0.35;
        gbc.insets = new Insets(0, 0, 0, 0);
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
            HttpResponse<String> response = HttpClientUtil.sendGetRequest("/flights/status?page=" + currentPage + "&size=5");
            if (response.statusCode() == 200) {
                tableModel.setRowCount(0);
                JSONArray flights;
                try {
                    JSONObject pageObj = new JSONObject(response.body());
                    flights = pageObj.getJSONArray("content");
                    totalPages = pageObj.optInt("totalPages", 1);
                    currentPage = pageObj.optInt("number", 0);
                } catch (Exception ex) {
                    flights = new JSONArray(response.body());
                    totalPages = 1;
                    currentPage = 0;
                }
                
                if (pageLabel != null) {
                    pageLabel.setText(String.format("Page %d of %d", currentPage + 1, totalPages));
                }
                if (prevButton != null) {
                    prevButton.setEnabled(currentPage > 0);
                }
                if (nextButton != null) {
                    nextButton.setEnabled(currentPage < totalPages - 1);
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

    private void loadUserBookings() {
        try {
            HttpResponse<String> response = HttpClientUtil.sendGetRequest("/bookings");
            if (response.statusCode() == 200) {
                bookingsTableModel.setRowCount(0);
                JSONArray bookings = new JSONArray(response.body());
                for (int i = 0; i < bookings.length(); i++) {
                    JSONObject booking = bookings.getJSONObject(i);
                    long bookingId = booking.optLong("bookingId", booking.optLong("booking_id", -1));
                    String flightNum = booking.optString("flightNumber", booking.optString("flight_number", "N/A"));
                    String passenger = booking.optString("passengerName", booking.optString("passenger_name", "N/A"));
                    int tickets = booking.optInt("numberOfTickets", booking.optInt("number_of_tickets", 0));
                    double totalPrice = booking.optDouble("totalPrice", booking.optDouble("total_price", 0.0));
                    String status = booking.optString("status", "CONFIRMED");
                    
                    String bookingDateStr = "N/A";
                    String fullDate = booking.optString("bookingDate", booking.optString("booking_date", ""));
                    if (!fullDate.isEmpty() && fullDate.length() >= 10) {
                        bookingDateStr = fullDate.substring(0, 10);
                    }

                    bookingsTableModel.addRow(new Object[]{
                        bookingId, flightNum, passenger, tickets, String.format("$%.2f", totalPrice), status, bookingDateStr
                    });
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
                loadUserBookings(); // Reload user bookings
            } else {
                confirmationLabel.setText("Booking failed: " + response.body());
                confirmationLabel.setForeground(Color.RED);
            }
        } catch (Exception e) {
            confirmationLabel.setText("Error: " + e.getMessage());
            confirmationLabel.setForeground(Color.RED);
        }
    }

    private void showCancelBookingDialog() {
        JTextField bookingIdField = new JTextField(15);
        bookingIdField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        
        JPanel inputPanel = new JPanel(new BorderLayout(5, 5));
        inputPanel.setOpaque(false);
        
        JLabel promptLabel = new JLabel("Enter Booking ID to cancel:");
        promptLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        promptLabel.setForeground(DARK_BLUE);
        
        inputPanel.add(promptLabel, BorderLayout.NORTH);
        inputPanel.add(bookingIdField, BorderLayout.CENTER);
        
        int result = JOptionPane.showConfirmDialog(
            this, 
            inputPanel, 
            "Cancel Booking", 
            JOptionPane.OK_CANCEL_OPTION, 
            JOptionPane.PLAIN_MESSAGE
        );
        
        if (result == JOptionPane.OK_OPTION) {
            String bookingIdText = bookingIdField.getText().trim();
            if (bookingIdText.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Booking ID cannot be empty.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            try {
                Long bookingId = Long.parseLong(bookingIdText);
                
                // Show confirmation before cancellation
                int confirm = JOptionPane.showConfirmDialog(
                    this,
                    "Are you sure you want to cancel Booking #" + bookingId + "?\nThis action cannot be undone.",
                    "Confirm Cancellation",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.WARNING_MESSAGE
                );
                
                if (confirm == JOptionPane.YES_OPTION) {
                    HttpResponse<String> response = HttpClientUtil.sendDeleteRequest("/bookings/" + bookingId);
                    if (response.statusCode() == 200) {
                        JOptionPane.showMessageDialog(
                            this, 
                            "Booking #" + bookingId + " has been successfully cancelled.", 
                            "Cancellation Successful", 
                            JOptionPane.INFORMATION_MESSAGE
                        );
                        confirmationLabel.setText("Booking #" + bookingId + " cancelled.");
                        confirmationLabel.setForeground(new Color(46, 204, 113));
                        loadAvailableFlights(); // Reload seats
                        loadUserBookings(); // Reload user bookings
                    } else {
                        String errorMsg = response.body();
                        if (errorMsg == null || errorMsg.trim().isEmpty()) {
                            errorMsg = "Booking ID not found or database constraint error.";
                        }
                        JOptionPane.showMessageDialog(
                            this, 
                            "Failed to cancel booking: " + errorMsg, 
                            "Cancellation Failed", 
                            JOptionPane.ERROR_MESSAGE
                        );
                        confirmationLabel.setText("Cancellation failed.");
                        confirmationLabel.setForeground(Color.RED);
                    }
                }
            } catch (NumberFormatException nfe) {
                JOptionPane.showMessageDialog(this, "Please enter a valid numeric Booking ID.", "Invalid Input", JOptionPane.ERROR_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error communicating with server: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new BookFlightForm().setVisible(true));
    }
}