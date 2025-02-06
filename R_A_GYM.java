import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
public class R_A_GYM extends JFrame {
    private JButton adminButton, trainerButton, traineeButton, gymButton;
    private Connection connection;
    public R_A_GYM() {
        setTitle("R&A Gym");
        setSize(400, 400);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
        try {
            connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/gym_diet", "root", "Ce50_Saltlake");
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Failed to connect to database", "Error", JOptionPane.ERROR_MESSAGE);
        }
        gymButton = new JButton("R&A Gym");
        gymButton.setBackground(new Color(0x00897B));
        gymButton.setForeground(Color.WHITE);
        gymButton.setFont(new Font("Arial", Font.BOLD, 26));
        gymButton.setFocusPainted(false);
        gymButton.setPreferredSize(new Dimension(100, 70));
        gymButton.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        add(gymButton, BorderLayout.NORTH);
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new GridLayout(3, 1, 10, 10));
        adminButton = createStyledButton("Admin", new Color(0x2196F3));
        trainerButton = createStyledButton("Trainer", new Color(0xFFC107));
        traineeButton = createStyledButton("Trainee", new Color(0xF44336));
        buttonPanel.add(adminButton);
        buttonPanel.add(trainerButton);
        buttonPanel.add(traineeButton);
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(20, 50, 30, 50));
        add(buttonPanel, BorderLayout.CENTER);
        gymButton.addActionListener(e -> showGymInfo());
        trainerButton.addActionListener(e -> showTrainerOptions());
        adminButton.addActionListener(e -> {
            JTextField usernameField = new JTextField();
            JPasswordField passwordField = new JPasswordField();
            Object[] message = {
                    "Username:", usernameField,
                    "Password:", passwordField
            };
            int option = JOptionPane.showConfirmDialog(null, message, "Admin Login", JOptionPane.OK_CANCEL_OPTION);
            if (option == JOptionPane.OK_OPTION) {
                String username = usernameField.getText();
                String password = new String(passwordField.getPassword());
                if ("Ansh Jindal".equals(username) && "Ansh123".equals(password)) {
                    JOptionPane.showMessageDialog(null, "Welcome, Ansh Jindal!");
                    showAdminInterface();  // Call the method to show admin interface
                } else if ("Ratna Durgesh Tiwari".equals(username) && "Ratna123".equals(password)) {
                    JOptionPane.showMessageDialog(null, "Welcome, Ratna Durgesh Tiwari!");
                    showAdminInterface();  // Call the method to show admin interface
                } else {
                    JOptionPane.showMessageDialog(null, "Invalid username or password", "Login Failed", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        traineeButton.addActionListener(e -> showTraineeOptions());
    }
    private JButton createStyledButton(String text, Color bgColor) {
        JButton button = new JButton(text);
        button.setBackground(bgColor);
        button.setForeground(Color.WHITE); // White text
        button.setFont(new Font("Arial", Font.BOLD, 18));
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20)); // Padding for the button
        return button;
    }
    private void showAdminInterface() {
        JFrame adminFrame = new JFrame("Admin Dashboard");
        adminFrame.setSize(400, 300);
        adminFrame.setLayout(new GridLayout(3, 1, 10, 10));  // Grid layout with spacing
        adminFrame.setLocationRelativeTo(null);
        JButton viewTrainersButton = createStyledButton("View Trainers", new Color(0x4CAF50)); // Green
        JButton viewMemberPlansButton = createStyledButton("View Member Plans", new Color(0x2196F3)); // Blue
        JButton launchProductButton = createStyledButton("Launch New Product", new Color(0xFFC107)); // Yellow
        adminFrame.add(viewTrainersButton);
        adminFrame.add(viewMemberPlansButton);
        adminFrame.add(launchProductButton);
        viewTrainersButton.addActionListener(e -> viewTrainers());
        viewMemberPlansButton.addActionListener(e -> viewMemberPlans());
        launchProductButton.addActionListener(e -> launchNewProduct());
        adminFrame.setVisible(true);
    }
    private void viewTrainers() {
        JFrame trainerFrame = new JFrame("View Trainers");
        trainerFrame.setSize(700, 500);
        trainerFrame.setLayout(new BorderLayout(10, 10));
        trainerFrame.setLocationRelativeTo(null);
        JLabel titleLabel = new JLabel("Trainers List", JLabel.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 22));
        titleLabel.setForeground(new Color(0x2196F3));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        DefaultListModel<String> trainerListModel = new DefaultListModel<>();
        JList<String> trainerList = new JList<>(trainerListModel);
        trainerList.setFont(new Font("Arial", Font.PLAIN, 16));
        trainerList.setSelectionBackground(new Color(0x4CAF50));  // Green background when selected
        trainerList.setSelectionForeground(Color.WHITE);  // White text when selected
        trainerList.setBorder(BorderFactory.createTitledBorder("Available Trainers"));
        JScrollPane scrollPane = new JScrollPane(trainerList);
        try {
            String query = "SELECT name FROM trainers";
            Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery(query);
            while (rs.next()) {
                trainerListModel.addElement(rs.getString("name"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error fetching trainers.");
        }
        JPanel detailsPanel = new JPanel();
        detailsPanel.setLayout(new BoxLayout(detailsPanel, BoxLayout.Y_AXIS));
        detailsPanel.setBorder(BorderFactory.createTitledBorder("Trainer Details"));
        detailsPanel.setBackground(Color.WHITE);
        JButton updateButton = createStyledButton("Update", new Color(0x2196F3));  // Blue button
        JButton deleteButton = createStyledButton("Delete", new Color(0xF44336));  // Red button
        trainerList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                String selectedTrainer = trainerList.getSelectedValue();
                if (selectedTrainer != null) {
                    displayTrainerDetails(selectedTrainer, detailsPanel, updateButton, deleteButton);
                }
            }
        });
        trainerFrame.add(titleLabel, BorderLayout.NORTH);
        trainerFrame.add(scrollPane, BorderLayout.WEST);
        trainerFrame.add(detailsPanel, BorderLayout.CENTER);
        trainerFrame.setVisible(true);
    }
    private void displayTrainerDetails(String trainerName, JPanel detailsPanel, JButton updateButton, JButton deleteButton) {
        detailsPanel.removeAll();
        detailsPanel.revalidate();
        detailsPanel.repaint();
        try {
            String query = "SELECT * FROM trainers WHERE name = ?";
            PreparedStatement pstmt = connection.prepareStatement(query);
            pstmt.setString(1, trainerName);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                String trainerDetails = "<html>Name: <b>" + rs.getString("name") + "</b><br>"
                        + "Age: <b>" + rs.getInt("age") + "</b><br>"
                        + "Gender: <b>" + rs.getString("gender") + "</b><br>"
                        + "Phone: <b>" + rs.getString("phone") + "</b><br>"
                        + "E-mail: <b>" + rs.getString("email") + "</b><br>"
                        + "Experience: <b>" + rs.getDouble("experience") + " years</b><br>"
                        + "Type: <b>" + rs.getString("type") + "</b><br>"
                        + "Salary: <b>₹" + rs.getDouble("salary") + "</b><br>"
                        + "Batches: <b>" + rs.getInt("batches") + "</b><br>"
                        + "Students per Batch: <b>" + rs.getInt("students_per_batch") + "</b><br>"
                        + "Current Students: <b>" + rs.getInt("starting_students") + "</b></html>";
                JLabel detailsLabel = new JLabel(trainerDetails);
                detailsLabel.setFont(new Font("Arial", Font.PLAIN, 16));
                detailsLabel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
                detailsPanel.add(detailsLabel);
                JLabel studentDetailsLabel = new JLabel("<html><br><strong>Students:</strong></html>");
                studentDetailsLabel.setFont(new Font("Arial", Font.BOLD, 16));
                studentDetailsLabel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
                detailsPanel.add(studentDetailsLabel);
                displayTrainerStudents(trainerName, detailsPanel);
                JPanel buttonPanel = new JPanel();
                buttonPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 20, 10));
                buttonPanel.add(updateButton);
                buttonPanel.add(deleteButton);
                detailsPanel.add(buttonPanel);
                updateButton.addActionListener(e -> updateTrainerDetails(trainerName));
                deleteButton.addActionListener(e -> deleteTrainer(trainerName, detailsPanel));
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error displaying trainer details.");
        }
    }
    private void displayTrainerStudents(String trainerName, JPanel detailsPanel) {
        try {
            String query = "SELECT name, phone, email FROM members WHERE chosen_trainer = ?";
            PreparedStatement pstmt = connection.prepareStatement(query);
            pstmt.setString(1, trainerName);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                String studentDetails = "Name: " + rs.getString("name") + ", Phone: " + rs.getString("phone")
                        + ", E-mail: " + rs.getString("email");
                JLabel studentLabel = new JLabel(studentDetails);
                detailsPanel.add(studentLabel);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error fetching students.");
        }
    }
    private void updateTrainerDetails(String trainerName) {
        JFrame updateFrame = new JFrame("Update Trainer Details");
        updateFrame.setSize(400, 500);
        updateFrame.setLayout(new GridBagLayout());
        updateFrame.setLocationRelativeTo(null);
        updateFrame.getContentPane().setBackground(new Color(0xECEFF1));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(10, 10, 10, 10);
        JLabel nameLabel = new JLabel("Name:");
        JTextField nameField = new JTextField(20);
        JLabel ageLabel = new JLabel("Age:");
        JTextField ageField = new JTextField(20);
        JLabel genderLabel = new JLabel("Gender:");
        JRadioButton maleRadio = new JRadioButton("Male");
        JRadioButton femaleRadio = new JRadioButton("Female");
        ButtonGroup genderGroup = new ButtonGroup();
        genderGroup.add(maleRadio);
        genderGroup.add(femaleRadio);
        JLabel phoneLabel = new JLabel("Phone Number:");
        JTextField phoneField = new JTextField(20);
        JLabel emailLabel = new JLabel("E-mail:");
        JTextField emailField = new JTextField(20);
        JLabel experienceLabel = new JLabel("Years of Experience:");
        JTextField experienceField = new JTextField(20);
        try {
            String query = "SELECT * FROM trainers WHERE name = ?";
            PreparedStatement pstmt = connection.prepareStatement(query);
            pstmt.setString(1, trainerName);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                nameField.setText(rs.getString("name"));
                ageField.setText(String.valueOf(rs.getInt("age")));
                if (rs.getString("gender").equalsIgnoreCase("Male")) {
                    maleRadio.setSelected(true);
                } else {
                    femaleRadio.setSelected(true);
                }
                phoneField.setText(rs.getString("phone"));
                emailField.setText(rs.getString("email"));
                experienceField.setText(String.valueOf(rs.getDouble("experience")));
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error fetching trainer details.");
            return;
        }
        gbc.gridx = 0;
        gbc.gridy = 0;
        updateFrame.add(nameLabel, gbc);
        gbc.gridx = 1;
        updateFrame.add(nameField, gbc);
        gbc.gridx = 0;
        gbc.gridy = 1;
        updateFrame.add(ageLabel, gbc);
        gbc.gridx = 1;
        updateFrame.add(ageField, gbc);
        gbc.gridx = 0;
        gbc.gridy = 2;
        updateFrame.add(genderLabel, gbc);
        gbc.gridx = 1;
        JPanel genderPanel = new JPanel();
        genderPanel.add(maleRadio);
        genderPanel.add(femaleRadio);
        updateFrame.add(genderPanel, gbc);
        gbc.gridx = 0;
        gbc.gridy = 3;
        updateFrame.add(phoneLabel, gbc);
        gbc.gridx = 1;
        updateFrame.add(phoneField, gbc);
        gbc.gridx = 0;
        gbc.gridy = 4;
        updateFrame.add(emailLabel, gbc);
        gbc.gridx = 1;
        updateFrame.add(emailField, gbc);
        gbc.gridx = 0;
        gbc.gridy = 5;
        updateFrame.add(experienceLabel, gbc);
        gbc.gridx = 1;
        updateFrame.add(experienceField, gbc);
        JButton submitButton = new JButton("Update Trainer");
        submitButton.setBackground(new Color(0x4CAF50));
        submitButton.setForeground(Color.WHITE);
        submitButton.setFocusPainted(false);
        submitButton.setFont(new Font("Arial", Font.BOLD, 16));
        gbc.gridx = 1;
        gbc.gridy = 6;
        updateFrame.add(submitButton, gbc);
        submitButton.addActionListener(e -> {
            String updatedName = nameField.getText();
            int updatedAge;
            double updatedExperience;
            String updatedPhone = phoneField.getText();
            String updatedEmail = emailField.getText();
            String updatedGender = maleRadio.isSelected() ? "Male" : "Female";
            try {
                updatedAge = Integer.parseInt(ageField.getText());
                updatedExperience = Double.parseDouble(experienceField.getText());
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(null, "Please enter valid numeric values for age and experience.");
                return;
            }
            try {
                String updateQuery = "UPDATE trainers SET name = ?, age = ?, gender = ?, phone = ?, email = ?, experience = ? WHERE name = ?";
                PreparedStatement updatePstmt = connection.prepareStatement(updateQuery);
                updatePstmt.setString(1, updatedName);
                updatePstmt.setInt(2, updatedAge);
                updatePstmt.setString(3, updatedGender);
                updatePstmt.setString(4, updatedPhone);
                updatePstmt.setString(5, updatedEmail);
                updatePstmt.setDouble(6, updatedExperience);
                updatePstmt.setString(7, trainerName);
                int rowsAffected = updatePstmt.executeUpdate();
                if (rowsAffected > 0) {
                    JOptionPane.showMessageDialog(null, "Trainer details updated successfully!");
                    updateFrame.dispose();  // Close the form after a successful update
                } else {
                    JOptionPane.showMessageDialog(null, "Failed to update trainer details.");
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(null, "Error updating trainer details.");
            }
        });
        updateFrame.setVisible(true);
    }
    private void deleteTrainer(String trainerName, JPanel detailsPanel) {
        int confirmation = JOptionPane.showConfirmDialog(null, "Are you sure you want to delete " + trainerName + "?",
                "Confirm Deletion", JOptionPane.YES_NO_OPTION);
        if (confirmation == JOptionPane.YES_OPTION) {
            try {
                String deleteTrainerQuery = "DELETE FROM trainers WHERE name = ?";
                PreparedStatement pstmt = connection.prepareStatement(deleteTrainerQuery);
                pstmt.setString(1, trainerName);
                pstmt.executeUpdate();
                String updateMembersQuery = "UPDATE members SET chosen_trainer = NULL WHERE chosen_trainer = ?";
                pstmt = connection.prepareStatement(updateMembersQuery);
                pstmt.setString(1, trainerName);
                pstmt.executeUpdate();
                JOptionPane.showMessageDialog(null, "Trainer " + trainerName + " deleted successfully!");
                detailsPanel.removeAll();
                detailsPanel.revalidate();
                detailsPanel.repaint();
            } catch (SQLException e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(null, "Error deleting trainer.");
            }
        }
    }
    private void viewMemberPlans() {
        JFrame memberFrame = new JFrame("Member Plans");
        memberFrame.setSize(800, 500);
        memberFrame.setLayout(new BorderLayout(20, 20));
        memberFrame.setLocationRelativeTo(null);
        JLabel titleLabel = new JLabel("Member Plans Overview", JLabel.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 28));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setOpaque(true);
        titleLabel.setBackground(new Color(0x2E7D32)); // Green background
        titleLabel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        String[] columnNames = {"Name", "Trainer Type", "Trainer", "Plan", "Cost (₹)", "Status"};
        Object[][] data = fetchMemberPlanData();
        JTable memberTable = new JTable(data, columnNames);
        memberTable.setFont(new Font("Arial", Font.PLAIN, 14));
        memberTable.setRowHeight(30);
        memberTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 16));
        memberTable.getTableHeader().setBackground(new Color(0x1B5E20)); // Dark Green
        memberTable.getTableHeader().setForeground(Color.WHITE);
        memberTable.setSelectionBackground(new Color(0x64B5F6)); // Light Blue background on selection
        memberTable.setSelectionForeground(Color.WHITE);
        JScrollPane scrollPane = new JScrollPane(memberTable);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        JPanel buttonPanel = new JPanel(new FlowLayout());
        JButton activateButton = new JButton("Activate");
        JButton deactivateButton = new JButton("Deactivate");
        activateButton.setBackground(new Color(0x43A047)); // Green
        activateButton.setForeground(Color.WHITE);
        activateButton.setFont(new Font("Arial", Font.BOLD, 16));
        activateButton.setFocusPainted(false);
        deactivateButton.setBackground(new Color(0xD32F2F)); // Red
        deactivateButton.setForeground(Color.WHITE);
        deactivateButton.setFont(new Font("Arial", Font.BOLD, 16));
        deactivateButton.setFocusPainted(false);
        buttonPanel.add(activateButton);
        buttonPanel.add(deactivateButton);
        activateButton.addActionListener(e -> {
            int selectedRow = memberTable.getSelectedRow();
            if (selectedRow != -1) {
                String memberName = (String) memberTable.getValueAt(selectedRow, 0);
                updateMemberStatus(memberName, "Active");
                refreshMemberTable(memberTable);
            }
        });
        deactivateButton.addActionListener(e -> {
            int selectedRow = memberTable.getSelectedRow();
            if (selectedRow != -1) {
                String memberName = (String) memberTable.getValueAt(selectedRow, 0);
                updateMemberStatus(memberName, "Inactive");
                refreshMemberTable(memberTable);
            }
        });
        memberFrame.add(titleLabel, BorderLayout.NORTH);
        memberFrame.add(scrollPane, BorderLayout.CENTER);
        memberFrame.add(buttonPanel, BorderLayout.SOUTH);
        memberFrame.setVisible(true);
    }
    private Object[][] fetchMemberPlanData() {
        List<Object[]> data = new ArrayList<>();
        try {
            String query = "SELECT name, trainer_type, trainer, plan, cost, status FROM status";
            Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery(query);
            while (rs.next()) {
                Object[] row = {
                        rs.getString("name"),
                        rs.getString("trainer_type"),
                        rs.getString("trainer"),
                        rs.getString("plan"),
                        rs.getDouble("cost"),
                        rs.getString("status")
                };
                data.add(row);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error fetching member details.");
        }
        return data.toArray(new Object[0][]);
    }
    private void updateMemberStatus(String memberName, String newStatus) {
        try {
            String query = "UPDATE status SET status = ? WHERE name = ?";
            PreparedStatement pstmt = connection.prepareStatement(query);
            pstmt.setString(1, newStatus);
            pstmt.setString(2, memberName);
            pstmt.executeUpdate();
            JOptionPane.showMessageDialog(null, "Member status updated to " + newStatus + "!");
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error updating member status.");
        }
    }
    private void refreshMemberTable(JTable memberTable) {
        Object[][] data = fetchMemberPlanData();
        for (int i = 0; i < data.length; i++) {
            for (int j = 0; j < data[i].length; j++) {
                memberTable.setValueAt(data[i][j], i, j);
            }
        }
    }
    private void launchNewProduct() {
        JFrame launchProductFrame = new JFrame("Launch New Product");
        launchProductFrame.setSize(400, 400);
        launchProductFrame.setLayout(new GridBagLayout());
        launchProductFrame.setLocationRelativeTo(null);
        launchProductFrame.getContentPane().setBackground(new Color(0xECEFF1));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(10, 10, 10, 10);
        JLabel productNameLabel = new JLabel("Product Name:");
        JTextField productNameField = new JTextField(20);
        JLabel productPriceLabel = new JLabel("Product Price:");
        JTextField productPriceField = new JTextField(20);
        JLabel productDescriptionLabel = new JLabel("Product Description:");
        JTextArea productDescriptionField = new JTextArea(10, 20);
        JButton launchButton = new JButton("Launch Product");
        launchButton.setBackground(new Color(0x4CAF50));
        launchButton.setForeground(Color.WHITE);
        launchButton.setFocusPainted(false);
        launchButton.setFont(new Font("Arial", Font.BOLD, 16));
        gbc.gridx = 0;
        gbc.gridy = 0;
        launchProductFrame.add(productNameLabel, gbc);
        gbc.gridx = 1;
        launchProductFrame.add(productNameField, gbc);
        gbc.gridx = 0;
        gbc.gridy = 1;
        launchProductFrame.add(productPriceLabel, gbc);
        gbc.gridx = 1;
        launchProductFrame.add(productPriceField, gbc);
        gbc.gridx = 0;
        gbc.gridy = 2;
        launchProductFrame.add(productDescriptionLabel, gbc);
        gbc.gridx = 1;
        launchProductFrame.add(new JScrollPane(productDescriptionField), gbc);
        gbc.gridx = 1;
        gbc.gridy = 3;
        launchProductFrame.add(launchButton, gbc);
        launchButton.addActionListener(e -> {
            String productName = productNameField.getText();
            double productPrice;
            try {
                productPrice = Double.parseDouble(productPriceField.getText());
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(null, "Please enter a valid price.");
                return;
            }
            String productDescription = productDescriptionField.getText();
            storeProductData(productName, productPrice, productDescription);
            launchProductFrame.dispose();
        });
        launchProductFrame.setVisible(true);
    }
    private void storeProductData(String productName, double productPrice, String productDescription) {
        try {
            String query = "INSERT INTO product (product_name, product_price, product_description) VALUES (?, ?, ?)";
            PreparedStatement pstmt = connection.prepareStatement(query);
            pstmt.setString(1, productName);
            pstmt.setDouble(2, productPrice);
            pstmt.setString(3, productDescription);
            pstmt.executeUpdate();
            JOptionPane.showMessageDialog(null, "Product launched successfully!");
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error launching product: " + e.getMessage());
        }
    }
    private void showGymInfo() {
        JFrame gymInfoFrame = new JFrame("R&A Gym Information");
        gymInfoFrame.setSize(500, 400);
        gymInfoFrame.setLocationRelativeTo(null); // Center the frame
        // Panel to display gym information
        JPanel infoPanel = new JPanel();
        infoPanel.setLayout(new BorderLayout());
        infoPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        // Information text area
        JTextArea gymInfoText = new JTextArea();
        gymInfoText.setText("R&A Gym is a modern styled gym where one can transform their body\n"
                + "through various diet and workout plans with the help of many experienced trainers.\n"
                + "Do not forget to go through our various products below.");
        gymInfoText.setFont(new Font("Serif", Font.ITALIC, 18));
        gymInfoText.setWrapStyleWord(true);
        gymInfoText.setLineWrap(true);
        gymInfoText.setEditable(false);
        gymInfoText.setOpaque(false);
        infoPanel.add(gymInfoText, BorderLayout.CENTER);
        // Products button at the bottom
        JButton productsButton = new JButton("Products");
        productsButton.setFont(new Font("Arial", Font.BOLD, 16));
        productsButton.setBackground(new Color(0x4CAF50)); // Green color
        productsButton.setForeground(Color.WHITE);
        productsButton.setFocusPainted(false);
        infoPanel.add(productsButton, BorderLayout.SOUTH);
        gymInfoFrame.add(infoPanel);
        gymInfoFrame.setVisible(true);
        productsButton.addActionListener(e -> {
            displayProducts();
        });
    }
    private void displayProducts() {
        JFrame productsFrame = new JFrame("Product Catalog");
        productsFrame.setSize(1000, 700);
        productsFrame.setLayout(new BorderLayout());
        productsFrame.setLocationRelativeTo(null);
        productsFrame.getContentPane().setBackground(new Color(240, 240, 240));
        JPanel productListPanel = new JPanel();
        productListPanel.setLayout(new BoxLayout(productListPanel, BoxLayout.Y_AXIS));
        productListPanel.setBackground(new Color(240, 240, 240));
        JScrollPane productScrollPane = new JScrollPane(productListPanel);
        productScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        productScrollPane.setBorder(BorderFactory.createEmptyBorder());
        JPanel titlePanel = new JPanel();
        titlePanel.setBackground(new Color(70, 130, 180));
        JLabel titleLabel = new JLabel("Our Products");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setForeground(Color.WHITE);
        titlePanel.add(titleLabel);
        try {
            String query = "SELECT product_id, product_name, product_price FROM product";
            try (Statement stmt = connection.createStatement();
                 ResultSet rs = stmt.executeQuery(query)) {
                while (rs.next()) {
                    int productId = rs.getInt("product_id");
                    String productName = rs.getString("product_name");
                    double productPrice = rs.getDouble("product_price");
                    JPanel productPanel = new JPanel(new BorderLayout());
                    productPanel.setBackground(Color.WHITE);
                    productPanel.setBorder(BorderFactory.createCompoundBorder(
                            new EmptyBorder(10, 10, 10, 10),
                            new LineBorder(new Color(200, 200, 200))
                    ));
                    JLabel productLabel = new JLabel(productName);
                    productLabel.setFont(new Font("Arial", Font.BOLD, 18));
                    JLabel priceLabel = new JLabel(String.format("₹%.2f", productPrice));
                    priceLabel.setFont(new Font("Arial", Font.PLAIN, 16));
                    priceLabel.setForeground(new Color(0, 128, 0));
                    JPanel infoPanel = new JPanel(new GridLayout(2, 1));
                    infoPanel.setBackground(Color.WHITE);
                    infoPanel.add(productLabel);
                    infoPanel.add(priceLabel);
                    JButton viewDescriptionButton = new JButton("View Details");
                    viewDescriptionButton.setBackground(new Color(70, 130, 180));
                    viewDescriptionButton.setForeground(Color.WHITE);
                    viewDescriptionButton.setFocusPainted(false);
                    viewDescriptionButton.addActionListener(e -> showProductDescription(productsFrame, productId, productName, productPrice));
                    productPanel.add(infoPanel, BorderLayout.CENTER);
                    productPanel.add(viewDescriptionButton, BorderLayout.EAST);
                    productListPanel.add(productPanel);
                    productListPanel.add(Box.createRigidArea(new Dimension(0, 10)));
                }
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(productsFrame, "Error retrieving products: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        }
        productsFrame.add(titlePanel, BorderLayout.NORTH);
        productsFrame.add(productScrollPane, BorderLayout.CENTER);
        productsFrame.setVisible(true);
    }
    private void showProductDescription(JFrame parentFrame, int productId, String productName, double productPrice) {
        JDialog descriptionDialog = new JDialog(parentFrame, "Product Details: " + productName, true);
        descriptionDialog.setSize(500, 400);
        descriptionDialog.setLayout(new BorderLayout());
        descriptionDialog.setLocationRelativeTo(parentFrame);
        try {
            String descriptionQuery = "SELECT product_description FROM product WHERE product_id = ?";
            try (PreparedStatement pstmt = connection.prepareStatement(descriptionQuery)) {
                pstmt.setInt(1, productId);
                try (ResultSet descriptionRs = pstmt.executeQuery()) {
                    if (descriptionRs.next()) {
                        String productDescription = descriptionRs.getString("product_description");
                        JTextArea descriptionTextArea = new JTextArea(productDescription, 10, 30);
                        descriptionTextArea.setEditable(false);
                        descriptionTextArea.setLineWrap(true);
                        descriptionTextArea.setWrapStyleWord(true);
                        descriptionTextArea.setFont(new Font("Arial", Font.PLAIN, 14));
                        descriptionTextArea.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
                        JScrollPane descriptionScrollPane = new JScrollPane(descriptionTextArea);
                        descriptionScrollPane.setBorder(BorderFactory.createEmptyBorder());
                        JPanel headerPanel = new JPanel(new BorderLayout());
                        headerPanel.setBackground(new Color(240, 240, 240));
                        headerPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
                        JLabel nameLabel = new JLabel(productName);
                        nameLabel.setFont(new Font("Arial", Font.BOLD, 20));
                        JLabel priceLabel = new JLabel(String.format("₹%.2f", productPrice));
                        priceLabel.setFont(new Font("Arial", Font.BOLD, 18));
                        priceLabel.setForeground(new Color(0, 128, 0));
                        headerPanel.add(nameLabel, BorderLayout.NORTH);
                        headerPanel.add(priceLabel, BorderLayout.SOUTH);
                        JButton orderButton = new JButton("Order Now");
                        orderButton.setBackground(new Color(0, 128, 0));
                        orderButton.setForeground(Color.WHITE);
                        orderButton.setFocusPainted(false);
                        orderButton.addActionListener(e -> showShippingDetailsDialog(descriptionDialog, productName, productPrice));
                        descriptionDialog.add(headerPanel, BorderLayout.NORTH);
                        descriptionDialog.add(descriptionScrollPane, BorderLayout.CENTER);
                        descriptionDialog.add(orderButton, BorderLayout.SOUTH);
                        descriptionDialog.setVisible(true);
                    }
                }
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(descriptionDialog, "Error retrieving product description: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    private void showShippingDetailsDialog(JDialog parentDialog, String productName, double productPrice) {
        JDialog shippingDetailsDialog = new JDialog(parentDialog, "Shipping Details", true);
        shippingDetailsDialog.setSize(400, 300);
        shippingDetailsDialog.setLayout(new BorderLayout());
        shippingDetailsDialog.setLocationRelativeTo(parentDialog);
        JPanel shippingDetailsPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 10, 5, 10);
        JTextField nameField = createLabeledTextField(shippingDetailsPanel, "Name:", gbc);
        JTextField addressField = createLabeledTextField(shippingDetailsPanel, "Address:", gbc);
        JTextField phoneNumberField = createLabeledTextField(shippingDetailsPanel, "Phone Number:", gbc);
        JButton payButton = new JButton("Proceed to Payment");
        payButton.setBackground(new Color(0, 128, 0));
        payButton.setForeground(Color.WHITE);
        payButton.setFocusPainted(false);
        payButton.addActionListener(e -> showBillDialog(shippingDetailsDialog, productName, productPrice,
                nameField.getText(), addressField.getText(), phoneNumberField.getText()));
        shippingDetailsDialog.add(shippingDetailsPanel, BorderLayout.CENTER);
        shippingDetailsDialog.add(payButton, BorderLayout.SOUTH);
        shippingDetailsDialog.setVisible(true);
    }
    private JTextField createLabeledTextField(JPanel panel, String labelText, GridBagConstraints gbc) {
        JLabel label = new JLabel(labelText);
        panel.add(label, gbc);
        JTextField textField = new JTextField(20);
        panel.add(textField, gbc);
        return textField;
    }
    private void showBillDialog(JDialog parentDialog, String productName, double productPrice,
                                String name, String address, String phoneNumber) {
        JDialog billDialog = new JDialog(parentDialog, "Order Summary", true);
        billDialog.setSize(400, 400);
        billDialog.setLayout(new BorderLayout());
        billDialog.setLocationRelativeTo(parentDialog);
        double shippingCharges = 120;
        double subtotal = productPrice + shippingCharges;
        double gst = subtotal * 0.12;
        double totalCost = subtotal + gst;
        JTextArea billTextArea = new JTextArea();
        billTextArea.setEditable(false);
        billTextArea.setFont(new Font("Monospaced", Font.PLAIN, 14));
        billTextArea.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        String billText = String.format(
                "Order Summary:\n\n" +
                        "Product: %s\n" +
                        "Price: ₹%.2f\n" +
                        "Shipping Charges: ₹%.2f\n" +
                        "Subtotal: ₹%.2f\n" +
                        "GST (12%%): ₹%.2f\n" +
                        "Total: ₹%.2f\n\n" +
                        "Shipping Details:\n" +
                        "Name: %s\n" +
                        "Address: %s\n" +
                        "Phone: %s",
                productName, productPrice, shippingCharges, subtotal, gst, totalCost,
                name, address, phoneNumber
        );
        billTextArea.setText(billText);
        JScrollPane billScrollPane = new JScrollPane(billTextArea);
        billScrollPane.setBorder(BorderFactory.createEmptyBorder());
        JButton confirmButton = new JButton("Confirm Order");
        confirmButton.setBackground(new Color(0, 128, 0));
        confirmButton.setForeground(Color.WHITE);
        confirmButton.setFocusPainted(false);
        confirmButton.addActionListener(e -> {
            JOptionPane.showMessageDialog(billDialog, "Your order has been confirmed. It will be shipped soon.", "Order Confirmation", JOptionPane.INFORMATION_MESSAGE);
            billDialog.dispose();
            parentDialog.dispose();
        });
        billDialog.add(billScrollPane, BorderLayout.CENTER);
        billDialog.add(confirmButton, BorderLayout.SOUTH);
        billDialog.setVisible(true);
    }
    private void showTrainerOptions() {
        JFrame trainerFrame = new JFrame("Trainer Options");
        trainerFrame.setSize(300, 200);
        trainerFrame.setLocationRelativeTo(null);
        trainerFrame.setLayout(new BorderLayout());
        JPanel trainerPanel = new JPanel();
        trainerPanel.setLayout(new GridLayout(2, 1, 10, 10));
        trainerPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        JButton joinButton = new JButton("Join");
        joinButton.setFont(new Font("Arial", Font.BOLD, 16));
        joinButton.setBackground(new Color(0x4CAF50));
        joinButton.setForeground(Color.WHITE);
        JButton loginButton = new JButton("Log in");
        loginButton.setFont(new Font("Arial", Font.BOLD, 16));
        loginButton.setBackground(new Color(0x2196F3)); // Blue
        loginButton.setForeground(Color.WHITE);
        trainerPanel.add(joinButton);
        trainerPanel.add(loginButton);
        trainerFrame.add(trainerPanel, BorderLayout.CENTER);
        joinButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showTermsAndConditions();
            }
        });
        loginButton.addActionListener(e -> trainerLogin());
        trainerFrame.setVisible(true);
    }
    private void showTermsAndConditions() {
        JCheckBox termsCheckBox = new JCheckBox("I hereby adhere to the terms and conditions of the R&A GYM. "
                + "I accept that I am a certified trainer and by wish agree to give my time and effort to the gym.");
        termsCheckBox.setFont(new Font("Arial", Font.PLAIN, 14));
        int option = JOptionPane.showConfirmDialog(null, termsCheckBox,
                "Terms and Conditions", JOptionPane.OK_CANCEL_OPTION, JOptionPane.INFORMATION_MESSAGE);
        if (option == JOptionPane.OK_OPTION && termsCheckBox.isSelected()) {
            showTrainerForm();
        } else {
            JOptionPane.showMessageDialog(null, "You must accept the terms and conditions to proceed.");
        }
    }
    private void trainerLogin() {
        String trainerName = JOptionPane.showInputDialog(null, "Enter your name:", "Trainer Login", JOptionPane.QUESTION_MESSAGE);
        if (trainerName != null && !trainerName.trim().isEmpty()) {
            displayTrainerDetails(trainerName);
        } else {
            JOptionPane.showMessageDialog(null, "Trainer name cannot be empty", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    private void displayTrainerDetails(String trainerName) {
        JFrame trainerFrame = new JFrame("Trainer Details - " + trainerName);
        trainerFrame.setSize(600, 500);
        trainerFrame.setLayout(new BorderLayout());
        trainerFrame.setLocationRelativeTo(null);

        // Panel for trainer details
        JPanel trainerPanel = new JPanel();
        trainerPanel.setLayout(new BoxLayout(trainerPanel, BoxLayout.Y_AXIS));
        trainerPanel.setBorder(BorderFactory.createTitledBorder("Trainer Information"));

        // Retrieve trainer details and students from the database
        try {
            String query = "SELECT trainer_id, name FROM trainers WHERE name = ?";
            PreparedStatement pstmt = connection.prepareStatement(query);
            pstmt.setString(1, trainerName);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                int trainerId = rs.getInt("trainer_id");

                // Display trainer's name and ID
                String trainerDetails = String.format("<html><b>Name:</b> %s<br><b>Trainer ID:</b> %d</html>", rs.getString("name"), trainerId);
                JLabel trainerLabel = new JLabel(trainerDetails);
                trainerPanel.add(trainerLabel);

                // Fetch and display the list of students for the trainer
                DefaultListModel<String> studentListModel = new DefaultListModel<>();
                JList<String> studentList = new JList<>(studentListModel);
                JScrollPane scrollPane = new JScrollPane(studentList);
                scrollPane.setBorder(BorderFactory.createTitledBorder("Students List"));
                trainerPanel.add(scrollPane);

                String studentQuery = "SELECT name FROM members WHERE chosen_trainer = ?";
                PreparedStatement studentPstmt = connection.prepareStatement(studentQuery);
                studentPstmt.setString(1, trainerName);
                ResultSet studentRs = studentPstmt.executeQuery();
                while (studentRs.next()) {
                    studentListModel.addElement(studentRs.getString("name"));
                }

                // Student selection action
                studentList.addListSelectionListener(e -> {
                    if (!e.getValueIsAdjusting()) {
                        String selectedStudent = studentList.getSelectedValue();
                        if (selectedStudent != null) {
                            displayStudentDetails(selectedStudent);
                        }
                    }
                });

            } else {
                JOptionPane.showMessageDialog(null, "Trainer not found.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error fetching trainer details.", "Database Error", JOptionPane.ERROR_MESSAGE);
        }

        trainerFrame.add(trainerPanel, BorderLayout.CENTER);
        trainerFrame.setVisible(true);
    }
    private void displayStudentDetails(String studentName) {
        JFrame studentFrame = new JFrame("Student Details - " + studentName);
        studentFrame.setSize(500, 600);
        studentFrame.setLayout(new BorderLayout());
        studentFrame.setLocationRelativeTo(null);

        JPanel studentPanel = new JPanel();
        studentPanel.setLayout(new BoxLayout(studentPanel, BoxLayout.Y_AXIS));
        studentPanel.setBorder(BorderFactory.createTitledBorder("Student Information"));

        // Retrieve student details from 'members' and 'status' tables
        try {
            String query = "SELECT m.name, m.age, m.phone, m.email, m.gender, m.weight, m.height, m.target, "
                    + "s.trainer_type, s.plan, s.cost, s.status "
                    + "FROM members m JOIN status s ON m.name = s.name WHERE m.name = ?";
            PreparedStatement pstmt = connection.prepareStatement(query);
            pstmt.setString(1, studentName);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                // Display student details
                String studentDetails = String.format(
                        "<html><b>Name:</b> %s<br><b>Age:</b> %d<br><b>Phone:</b> %s<br><b>Email:</b> %s<br>"
                                + "<b>Gender:</b> %s<br><b>Weight:</b> %.2f kg<br><b>Height:</b> %.2f cm<br>"
                                + "<b>Target:</b> %s<br><b>Trainer Type:</b> %s<br><b>Plan:</b> %s<br><b>Cost:</b> ₹%.2f<br>"
                                + "<b>Status:</b> %s</html>",
                        rs.getString("name"), rs.getInt("age"), rs.getString("phone"), rs.getString("email"),
                        rs.getString("gender"), rs.getDouble("weight"), rs.getDouble("height"), rs.getString("target"),
                        rs.getString("trainer_type"), rs.getString("plan"), rs.getDouble("cost"), rs.getString("status")
                );
                JLabel studentLabel = new JLabel(studentDetails);
                studentPanel.add(studentLabel);
            }

        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error fetching student details.", "Database Error", JOptionPane.ERROR_MESSAGE);
        }

        studentFrame.add(studentPanel, BorderLayout.CENTER);

        // Workout and Diet buttons
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout(FlowLayout.CENTER));

        JButton workoutButton = new JButton("Workout");
        workoutButton.setBackground(new Color(0x4CAF50));  // Green
        workoutButton.setForeground(Color.WHITE);
        buttonPanel.add(workoutButton);
        JButton dietButton = new JButton("Diet");
        dietButton.setBackground(new Color(0x2196F3));  // Blue
        dietButton.setForeground(Color.WHITE);
        buttonPanel.add(dietButton);
        workoutButton.addActionListener(e -> showWorkoutOptions(studentName));  // Opens workout options
        dietButton.addActionListener(e -> showDietOptions(studentName));  // Opens diet options
        studentFrame.add(buttonPanel, BorderLayout.SOUTH);
        studentFrame.setVisible(true);
    }
    private void showWorkoutOptions(String studentName) {
        String[] options = {"View Plan", "Edit Plan"};
        int choice = JOptionPane.showOptionDialog(null, "Choose an option for the Workout Plan:", "Workout Plan Options",
                JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE, null, options, options[0]);

        if (choice == 0) {  // View Plan
            showWorkoutPlans(studentName);
        } else if (choice == 1) {  // Edit Plan
            editWorkoutPlan(studentName);
        }
    }

    private void showDietOptions(String studentName) {
        String[] options = {"View Plan", "Edit Plan"};
        int choice = JOptionPane.showOptionDialog(null, "Choose an option for the Diet Plan:", "Diet Plan Options",
                JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE, null, options, options[0]);

        if (choice == 0) {  // View Plan
            showDietPlans(studentName);
        } else if (choice == 1) {  // Edit Plan
            editDietPlan(studentName);
        }
    }
    private void editWorkoutPlan(String studentName) {
        JFrame editFrame = new JFrame("Edit Workout Plan for " + studentName);
        editFrame.setSize(400, 500);
        editFrame.setLayout(new GridBagLayout());
        editFrame.setLocationRelativeTo(null);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(10, 10, 10, 10);

        // Labels and TextFields for each day
        JLabel trainerLabel = new JLabel("Trainer Name:");
        JTextField trainerField = new JTextField(20);
        JLabel mondayLabel = new JLabel("Monday:");
        JTextField mondayField = new JTextField(20);
        JLabel tuesdayLabel = new JLabel("Tuesday:");
        JTextField tuesdayField = new JTextField(20);
        JLabel wednesdayLabel = new JLabel("Wednesday:");
        JTextField wednesdayField = new JTextField(20);
        JLabel thursdayLabel = new JLabel("Thursday:");
        JTextField thursdayField = new JTextField(20);
        JLabel fridayLabel = new JLabel("Friday:");
        JTextField fridayField = new JTextField(20);
        JLabel saturdayLabel = new JLabel("Saturday:");
        JTextField saturdayField = new JTextField(20);

        // Fetch the current workout plan
        try {
            String query = "SELECT * FROM workout WHERE name = ?";
            PreparedStatement pstmt = connection.prepareStatement(query);
            pstmt.setString(1, studentName);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                trainerField.setText(rs.getString("trainer_name"));
                mondayField.setText(rs.getString("monday"));
                tuesdayField.setText(rs.getString("tuesday"));
                wednesdayField.setText(rs.getString("wednesday"));
                thursdayField.setText(rs.getString("thursday"));
                fridayField.setText(rs.getString("friday"));
                saturdayField.setText(rs.getString("saturday"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error fetching workout details.", "Database Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Add the fields to the form
        gbc.gridx = 0; gbc.gridy = 0; editFrame.add(trainerLabel, gbc);
        gbc.gridx = 1; editFrame.add(trainerField, gbc);
        gbc.gridx = 0; gbc.gridy = 1; editFrame.add(mondayLabel, gbc);
        gbc.gridx = 1; editFrame.add(mondayField, gbc);
        gbc.gridx = 0; gbc.gridy = 2; editFrame.add(tuesdayLabel, gbc);
        gbc.gridx = 1; editFrame.add(tuesdayField, gbc);
        gbc.gridx = 0; gbc.gridy = 3; editFrame.add(wednesdayLabel, gbc);
        gbc.gridx = 1; editFrame.add(wednesdayField, gbc);
        gbc.gridx = 0; gbc.gridy = 4; editFrame.add(thursdayLabel, gbc);
        gbc.gridx = 1; editFrame.add(thursdayField, gbc);
        gbc.gridx = 0; gbc.gridy = 5; editFrame.add(fridayLabel, gbc);
        gbc.gridx = 1; editFrame.add(fridayField, gbc);
        gbc.gridx = 0; gbc.gridy = 6; editFrame.add(saturdayLabel, gbc);
        gbc.gridx = 1; editFrame.add(saturdayField, gbc);

        // Submit button
        JButton submitButton = new JButton("Submit");
        submitButton.setBackground(new Color(0x4CAF50));
        submitButton.setForeground(Color.WHITE);
        gbc.gridx = 1; gbc.gridy = 7; editFrame.add(submitButton, gbc);

        submitButton.addActionListener(e -> {
            // Update the workout plan in the database
            try {
                String updateQuery = "UPDATE workout SET trainer_name = ?, monday = ?, tuesday = ?, wednesday = ?, thursday = ?, friday = ?, saturday = ? WHERE name = ?";
                PreparedStatement updatePstmt = connection.prepareStatement(updateQuery);
                updatePstmt.setString(1, trainerField.getText());
                updatePstmt.setString(2, mondayField.getText());
                updatePstmt.setString(3, tuesdayField.getText());
                updatePstmt.setString(4, wednesdayField.getText());
                updatePstmt.setString(5, thursdayField.getText());
                updatePstmt.setString(6, fridayField.getText());
                updatePstmt.setString(7, saturdayField.getText());
                updatePstmt.setString(8, studentName);
                updatePstmt.executeUpdate();

                JOptionPane.showMessageDialog(null, "Workout plan updated successfully!");

            } catch (SQLException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(null, "Error updating workout plan.", "Database Error", JOptionPane.ERROR_MESSAGE);
            }
            editFrame.dispose();
        });

        editFrame.setVisible(true);
    }
    private void editDietPlan(String studentName) {
        JFrame editFrame = new JFrame("Edit Diet Plan for " + studentName);
        editFrame.setSize(400, 500);
        editFrame.setLayout(new GridBagLayout());
        editFrame.setLocationRelativeTo(null);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(10, 10, 10, 10);

        // Labels and TextFields for each day
        JLabel trainerLabel = new JLabel("Trainer Name:");
        JTextField trainerField = new JTextField(20);
        JLabel mondayLabel = new JLabel("Monday:");
        JTextField mondayField = new JTextField(20);
        JLabel tuesdayLabel = new JLabel("Tuesday:");
        JTextField tuesdayField = new JTextField(20);
        JLabel wednesdayLabel = new JLabel("Wednesday:");
        JTextField wednesdayField = new JTextField(20);
        JLabel thursdayLabel = new JLabel("Thursday:");
        JTextField thursdayField = new JTextField(20);
        JLabel fridayLabel = new JLabel("Friday:");
        JTextField fridayField = new JTextField(20);
        JLabel saturdayLabel = new JLabel("Saturday:");
        JTextField saturdayField = new JTextField(20);
        JLabel sundayLabel = new JLabel("Sunday:");
        JTextField sundayField = new JTextField(20);

        // Fetch the current diet plan
        try {
            String query = "SELECT * FROM diet WHERE name = ?";
            PreparedStatement pstmt = connection.prepareStatement(query);
            pstmt.setString(1, studentName);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                trainerField.setText(rs.getString("trainer_name"));
                mondayField.setText(rs.getString("monday"));
                tuesdayField.setText(rs.getString("tuesday"));
                wednesdayField.setText(rs.getString("wednesday"));
                thursdayField.setText(rs.getString("thursday"));
                fridayField.setText(rs.getString("friday"));
                saturdayField.setText(rs.getString("saturday"));
                sundayField.setText(rs.getString("sunday"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error fetching diet details.", "Database Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Add the fields to the form
        gbc.gridx = 0; gbc.gridy = 0; editFrame.add(trainerLabel, gbc);
        gbc.gridx = 1; editFrame.add(trainerField, gbc);
        gbc.gridx = 0; gbc.gridy = 1; editFrame.add(mondayLabel, gbc);
        gbc.gridx = 1; editFrame.add(mondayField, gbc);
        gbc.gridx = 0; gbc.gridy = 2; editFrame.add(tuesdayLabel, gbc);
        gbc.gridx = 1; editFrame.add(tuesdayField, gbc);
        gbc.gridx = 0; gbc.gridy = 3; editFrame.add(wednesdayLabel, gbc);
        gbc.gridx = 1; editFrame.add(wednesdayField, gbc);
        gbc.gridx = 0; gbc.gridy = 4; editFrame.add(thursdayLabel, gbc);
        gbc.gridx = 1; editFrame.add(thursdayField, gbc);
        gbc.gridx = 0; gbc.gridy = 5; editFrame.add(fridayLabel, gbc);
        gbc.gridx = 1; editFrame.add(fridayField, gbc);
        gbc.gridx = 0; gbc.gridy = 6; editFrame.add(saturdayLabel, gbc);
        gbc.gridx = 1; editFrame.add(saturdayField, gbc);
        gbc.gridx = 0; gbc.gridy = 7; editFrame.add(sundayLabel, gbc);
        gbc.gridx = 1; editFrame.add(sundayField, gbc);

        // Submit button
        JButton submitButton = new JButton("Submit");
        submitButton.setBackground(new Color(0x4CAF50));  // Green button
        submitButton.setForeground(Color.WHITE);
        gbc.gridx = 1; gbc.gridy = 8; editFrame.add(submitButton, gbc);

        submitButton.addActionListener(e -> {
            // Update the diet plan in the database
            try {
                String updateQuery = "UPDATE diet SET trainer_name = ?, monday = ?, tuesday = ?, wednesday = ?, thursday = ?, friday = ?, saturday = ?, sunday = ? WHERE name = ?";
                PreparedStatement updatePstmt = connection.prepareStatement(updateQuery);
                updatePstmt.setString(1, trainerField.getText());
                updatePstmt.setString(2, mondayField.getText());
                updatePstmt.setString(3, tuesdayField.getText());
                updatePstmt.setString(4, wednesdayField.getText());
                updatePstmt.setString(5, thursdayField.getText());
                updatePstmt.setString(6, fridayField.getText());
                updatePstmt.setString(7, saturdayField.getText());
                updatePstmt.setString(8, sundayField.getText());
                updatePstmt.setString(9, studentName);
                updatePstmt.executeUpdate();

                JOptionPane.showMessageDialog(null, "Diet plan updated successfully!");

            } catch (SQLException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(null, "Error updating diet plan.", "Database Error", JOptionPane.ERROR_MESSAGE);
            }
            editFrame.dispose();
        });

        editFrame.setVisible(true);
    }
    private void showTrainerForm() {
        JFrame joinFrame = new JFrame("Join as Trainer");
        joinFrame.setSize(400, 500);
        joinFrame.setLayout(new GridBagLayout());
        joinFrame.setLocationRelativeTo(null); // Center the frame
        joinFrame.getContentPane().setBackground(new Color(0xECEFF1));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(10, 10, 10, 10);
        JLabel nameLabel = new JLabel("Name:");
        JTextField nameField = new JTextField(20);
        JLabel ageLabel = new JLabel("Age:");
        JTextField ageField = new JTextField(20);
        JLabel genderLabel = new JLabel("Gender:");
        JRadioButton maleRadio = new JRadioButton("Male");
        JRadioButton femaleRadio = new JRadioButton("Female");
        ButtonGroup genderGroup = new ButtonGroup();
        genderGroup.add(maleRadio);
        genderGroup.add(femaleRadio);
        JLabel phoneLabel = new JLabel("Phone Number:");
        JTextField phoneField = new JTextField(20);
        JLabel emailLabel = new JLabel("E-mail:");
        JTextField emailField = new JTextField(20);
        JLabel experienceLabel = new JLabel("Years of Experience:");
        JTextField experienceField = new JTextField(20);
        JButton enrollButton = new JButton("Enroll");
        enrollButton.setBackground(new Color(0x4CAF50));
        enrollButton.setForeground(Color.WHITE);
        enrollButton.setFocusPainted(false);
        enrollButton.setFont(new Font("Arial", Font.BOLD, 16));
        gbc.gridx = 0;
        gbc.gridy = 0;
        joinFrame.add(nameLabel, gbc);
        gbc.gridx = 1;
        joinFrame.add(nameField, gbc);
        gbc.gridx = 0;
        gbc.gridy = 1;
        joinFrame.add(ageLabel, gbc);
        gbc.gridx = 1;
        joinFrame.add(ageField, gbc);
        gbc.gridx = 0;
        gbc.gridy = 2;
        joinFrame.add(genderLabel, gbc);
        gbc.gridx = 1;
        JPanel genderPanel = new JPanel();
        genderPanel.add(maleRadio);
        genderPanel.add(femaleRadio);
        joinFrame.add(genderPanel, gbc);
        gbc.gridx = 0;
        gbc.gridy = 3;
        joinFrame.add(phoneLabel, gbc);
        gbc.gridx = 1;
        joinFrame.add(phoneField, gbc);
        gbc.gridx = 0;
        gbc.gridy = 4;
        joinFrame.add(emailLabel, gbc);
        gbc.gridx = 1;
        joinFrame.add(emailField, gbc);
        gbc.gridx = 0;
        gbc.gridy = 5;
        joinFrame.add(experienceLabel, gbc);
        gbc.gridx = 1;
        joinFrame.add(experienceField, gbc);
        gbc.gridx = 1;
        gbc.gridy = 6;
        joinFrame.add(enrollButton, gbc);
        enrollButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String name = nameField.getText();
                int age;
                double yearsOfExperience;
                String phone = phoneField.getText();
                String email = emailField.getText();
                String gender = maleRadio.isSelected() ? "Male" : "Female";
                try {
                    age = Integer.parseInt(ageField.getText());
                    yearsOfExperience = Double.parseDouble(experienceField.getText());
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(null, "Please enter valid numeric values for age and years of experience.");
                    return;
                }
                String trainerType;
                int batches, studentsPerBatch, startingStudents = 0;
                double salary;
                if (yearsOfExperience > 3) {
                    trainerType = "Personal Trainer";
                    batches = 8;
                    studentsPerBatch = 3;
                    salary = 9000 + (startingStudents * 600);
                } else {
                    trainerType = "General Trainer";
                    batches = 5;
                    studentsPerBatch = 8;
                    salary = 7500 + (startingStudents * 350);
                }
                int trainerId = generateTrainerId();
                storeTrainerData(trainerId, name, age, phone, email, gender, yearsOfExperience, trainerType, salary, batches, studentsPerBatch, startingStudents);
            }
        });
        joinFrame.setVisible(true);
    }
    private int generateTrainerId() {
        int trainerId = 1;
        try (Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/gym_diet", "root", "Ce50_Saltlake")) {
            String query = "SELECT MAX(trainer_id) FROM trainers";
            Statement stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery(query);
            if (rs.next()) {
                trainerId = rs.getInt(1) + 1;
            }
            stmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return trainerId;
    }
    private void storeTrainerData(int trainerId, String name, int age, String phone, String email, String gender, double experience, String type, double salary, int batches, int studentsPerBatch, int studentsCount) {
        try (Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/gym_diet", "root", "Ce50_Saltlake")) {
            String query = "INSERT INTO trainers (trainer_id, name, age, phone, email, gender, experience, type, salary, batches, students_per_batch, starting_students) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
            PreparedStatement stmt = con.prepareStatement(query);
            stmt.setInt(1, trainerId);
            stmt.setString(2, name);
            stmt.setInt(3, age);
            stmt.setString(4, phone);
            stmt.setString(5, email);
            stmt.setString(6, gender);
            stmt.setDouble(7, experience);
            stmt.setString(8, type);
            stmt.setDouble(9, salary);
            stmt.setInt(10, batches);
            stmt.setInt(11, studentsPerBatch);
            stmt.setInt(12, studentsCount);
            stmt.executeUpdate();
            JOptionPane.showMessageDialog(null, "Trainer enrolled successfully! Trainer ID: " + String.format("%03d", trainerId));
            stmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    private void showTraineeOptions() {
        JFrame traineeFrame = new JFrame("Trainee Options");
        traineeFrame.setSize(300, 200);
        traineeFrame.setLocationRelativeTo(null);
        traineeFrame.setLayout(new BorderLayout());
        JPanel traineePanel = new JPanel();
        traineePanel.setLayout(new GridLayout(2, 1, 10, 10));
        traineePanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        JButton transformButton = new JButton("Transform");
        transformButton.setFont(new Font("Arial", Font.BOLD, 16));
        transformButton.setBackground(new Color(0x4CAF50));
        transformButton.setForeground(Color.WHITE);
        JButton gymConnectButton = new JButton("Gym Connect");
        gymConnectButton.setFont(new Font("Arial", Font.BOLD, 16));
        gymConnectButton.setBackground(new Color(0x2196F3));
        gymConnectButton.setForeground(Color.WHITE);
        traineePanel.add(transformButton);
        traineePanel.add(gymConnectButton);
        traineeFrame.add(traineePanel, BorderLayout.CENTER);
        transformButton.addActionListener(e -> showTransformForm());
        gymConnectButton.addActionListener(e -> {
            String userName = JOptionPane.showInputDialog(null, "Enter your name:", "User Input", JOptionPane.QUESTION_MESSAGE);
            if (userName != null && !userName.trim().isEmpty()) {
                displayUserDetails(userName);
            } else {
                JOptionPane.showMessageDialog(null, "Name cannot be empty", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        traineeFrame.setVisible(true);
    }
    private void displayUserDetails(String userName) {
        JFrame userFrame = new JFrame("User Details - " + userName);
        userFrame.setSize(600, 500);
        userFrame.setLayout(new BorderLayout());
        userFrame.setLocationRelativeTo(null);

        // Panel to display user details
        JPanel detailsPanel = new JPanel();
        detailsPanel.setLayout(new BoxLayout(detailsPanel, BoxLayout.Y_AXIS));
        detailsPanel.setBorder(BorderFactory.createTitledBorder("User Information"));
        userFrame.add(detailsPanel, BorderLayout.CENTER);

        // Retrieve user details from the database
        try {
            String query = "SELECT members.name, members.age, members.phone, members.email, members.gender, "
                    + "members.weight, members.height, members.target, status.trainer, status.plan, status.cost "
                    + "FROM members JOIN status ON members.name = status.name WHERE members.name = ?";
            PreparedStatement pstmt = connection.prepareStatement(query);
            pstmt.setString(1, userName);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                String userDetails = String.format(
                        "<html>Name: <b>%s</b><br>Age: <b>%d</b><br>Phone: <b>%s</b><br>Email: <b>%s</b><br>"
                                + "Gender: <b>%s</b><br>Weight: <b>%.2f kg</b><br>Height: <b>%.2f cm</b><br>Target: <b>%s</b><br>"
                                + "Trainer: <b>%s</b><br>Plan: <b>%s</b><br>Cost: <b>₹%.2f</b></html>",
                        rs.getString("name"), rs.getInt("age"), rs.getString("phone"),
                        rs.getString("email"), rs.getString("gender"), rs.getDouble("weight"),
                        rs.getDouble("height"), rs.getString("target"), rs.getString("trainer"),
                        rs.getString("plan"), rs.getDouble("cost")
                );
                JLabel userLabel = new JLabel(userDetails);
                detailsPanel.add(userLabel);

                // Add "Feature" button below the details
                JButton featureButton = new JButton("Feature");
                featureButton.setBackground(new Color(0x2196F3));
                featureButton.setForeground(Color.WHITE);
                detailsPanel.add(featureButton);

                featureButton.addActionListener(fe -> showFeatureOptions(userName));
            } else {
                JOptionPane.showMessageDialog(null, "User not found.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error fetching user details.", "Database Error", JOptionPane.ERROR_MESSAGE);
        }

        userFrame.setVisible(true);
    }
    private void showFeatureOptions(String userName) {
        JFrame featureFrame = new JFrame("Feature Options - " + userName);
        featureFrame.setSize(400, 300);
        featureFrame.setLayout(new GridLayout(5, 1, 10, 10));
        featureFrame.setLocationRelativeTo(null);

        // Buttons for different features
        JButton updateButton = new JButton("Update");
        JButton deleteButton = new JButton("Delete");
        JButton workoutButton = new JButton("Workout");
        JButton dietButton = new JButton("Diet");
        JButton doctorButton = new JButton("Doctor");

        // Style buttons
        updateButton.setBackground(new Color(0x4CAF50)); // Green
        deleteButton.setBackground(new Color(0xF44336)); // Red
        workoutButton.setBackground(new Color(0xFFC107)); // Yellow
        dietButton.setBackground(new Color(0x2196F3)); // Blue
        doctorButton.setBackground(new Color(0x9C27B0)); // Purple

        updateButton.setForeground(Color.WHITE);
        deleteButton.setForeground(Color.WHITE);
        workoutButton.setForeground(Color.WHITE);
        dietButton.setForeground(Color.WHITE);
        doctorButton.setForeground(Color.WHITE);

        featureFrame.add(updateButton);
        featureFrame.add(deleteButton);
        featureFrame.add(workoutButton);
        featureFrame.add(dietButton);
        featureFrame.add(doctorButton);
        dietButton.addActionListener(e -> showDietPlans(userName));
        doctorButton.addActionListener(e -> showDoctorForm(userName));
        deleteButton.addActionListener(e -> deleteUser(userName));
        updateButton.addActionListener(e -> updateUserDetails(userName));
        workoutButton.addActionListener(e -> showWorkoutPlans(userName));
        featureFrame.setVisible(true);
    }
    private void showWorkoutPlans(String userName) {
        JFrame workoutFrame = new JFrame("Workout Plan for " + userName);
        workoutFrame.setSize(400, 500);
        workoutFrame.setLayout(new BorderLayout());
        workoutFrame.setLocationRelativeTo(null);

        // Create a panel to display the workout details
        JPanel workoutPanel = new JPanel();
        workoutPanel.setLayout(new BoxLayout(workoutPanel, BoxLayout.Y_AXIS));
        workoutPanel.setBorder(BorderFactory.createTitledBorder("Workout Plan"));

        // Retrieve the workout details from the 'workout' table
        try {
            String query = "SELECT * FROM workout WHERE name = ?";
            PreparedStatement pstmt = connection.prepareStatement(query);
            pstmt.setString(1, userName);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                // Display workout details for each day of the week
                String workoutDetails = String.format(
                        "<html><b>Trainer Name:</b> %s<br><b>Monday:</b> %s<br><b>Tuesday:</b> %s<br><b>Wednesday:</b> %s<br>" +
                                "<b>Thursday:</b> %s<br><b>Friday:</b> %s<br><b>Saturday:</b> %s</html>",
                        rs.getString("trainer_name"), rs.getString("monday"), rs.getString("tuesday"), rs.getString("wednesday"),
                        rs.getString("thursday"), rs.getString("friday"), rs.getString("saturday")
                );

                JLabel workoutLabel = new JLabel(workoutDetails);
                workoutLabel.setFont(new Font("Arial", Font.PLAIN, 14));
                workoutPanel.add(workoutLabel);

            } else {
                JOptionPane.showMessageDialog(null, "No workout plan found for " + userName, "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error fetching workout details.", "Database Error", JOptionPane.ERROR_MESSAGE);
        }
        workoutFrame.add(workoutPanel, BorderLayout.CENTER);
        JButton closeButton = new JButton("Close");
        closeButton.setBackground(new Color(0xF44336));  // Red color for the close button
        closeButton.setForeground(Color.WHITE);
        closeButton.addActionListener(e -> workoutFrame.dispose());
        workoutFrame.add(closeButton, BorderLayout.SOUTH);
        workoutFrame.setVisible(true);
    }
    private void showDietPlans(String userName) {
        JFrame dietFrame = new JFrame("Diet Plan for " + userName);
        dietFrame.setSize(400, 500);
        dietFrame.setLayout(new BorderLayout());
        dietFrame.setLocationRelativeTo(null);
        JPanel dietPanel = new JPanel();
        dietPanel.setLayout(new BoxLayout(dietPanel, BoxLayout.Y_AXIS));
        dietPanel.setBorder(BorderFactory.createTitledBorder("Diet Plan"));
        try {
            String query = "SELECT * FROM diet WHERE name = ?";
            PreparedStatement pstmt = connection.prepareStatement(query);
            pstmt.setString(1, userName);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                String dietDetails = String.format(
                        "<html><b>Trainer Name:</b> %s<br><b>Monday:</b> %s<br><b>Tuesday:</b> %s<br><b>Wednesday:</b> %s<br>" +
                                "<b>Thursday:</b> %s<br><b>Friday:</b> %s<br><b>Saturday:</b> %s<br><b>Sunday:</b> %s</html>",
                        rs.getString("trainer_name"), rs.getString("monday"), rs.getString("tuesday"), rs.getString("wednesday"),
                        rs.getString("thursday"), rs.getString("friday"), rs.getString("saturday"), rs.getString("sunday")
                );
                JLabel dietLabel = new JLabel(dietDetails);
                dietLabel.setFont(new Font("Arial", Font.PLAIN, 14));
                dietPanel.add(dietLabel);

            } else {
                JOptionPane.showMessageDialog(null, "No diet plan found for " + userName, "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error fetching diet details.", "Database Error", JOptionPane.ERROR_MESSAGE);
        }
        dietFrame.add(dietPanel, BorderLayout.CENTER);
        JButton closeButton = new JButton("Close");
        closeButton.setBackground(new Color(0xF44336));  // Red color for the close button
        closeButton.setForeground(Color.WHITE);
        closeButton.addActionListener(e -> dietFrame.dispose());
        dietFrame.add(closeButton, BorderLayout.SOUTH);

        dietFrame.setVisible(true);
    }
    private void updateUserDetails(String userName) {
        JFrame updateFrame = new JFrame("Update Details for " + userName);
        updateFrame.setSize(400, 500);
        updateFrame.setLayout(new GridBagLayout());
        updateFrame.setLocationRelativeTo(null);
        updateFrame.getContentPane().setBackground(new Color(0xECEFF1));  // Light background for the form

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(10, 10, 10, 10);

        // Labels and Fields for Member Details
        JLabel nameLabel = new JLabel("Name:");
        JTextField nameField = new JTextField(20);
        JLabel ageLabel = new JLabel("Age:");
        JTextField ageField = new JTextField(20);
        JLabel phoneLabel = new JLabel("Phone:");
        JTextField phoneField = new JTextField(20);
        JLabel emailLabel = new JLabel("Email:");
        JTextField emailField = new JTextField(20);
        JLabel genderLabel = new JLabel("Gender:");
        JRadioButton maleRadio = new JRadioButton("Male");
        JRadioButton femaleRadio = new JRadioButton("Female");
        ButtonGroup genderGroup = new ButtonGroup();
        genderGroup.add(maleRadio);
        genderGroup.add(femaleRadio);
        JLabel weightLabel = new JLabel("Weight (kg):");
        JTextField weightField = new JTextField(20);
        JLabel heightLabel = new JLabel("Height (cm):");
        JTextField heightField = new JTextField(20);
        JLabel targetLabel = new JLabel("Target:");
        String[] targets = {"Maintain Weight", "Gain Weight", "Lose Weight"};
        JComboBox<String> targetCombo = new JComboBox<>(targets);

        // Fetch current user details from the database
        try {
            String query = "SELECT * FROM members WHERE name = ?";
            PreparedStatement pstmt = connection.prepareStatement(query);
            pstmt.setString(1, userName);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                // Pre-fill form with existing data
                nameField.setText(rs.getString("name"));
                ageField.setText(String.valueOf(rs.getInt("age")));
                phoneField.setText(rs.getString("phone"));
                emailField.setText(rs.getString("email"));
                if (rs.getString("gender").equalsIgnoreCase("Male")) {
                    maleRadio.setSelected(true);
                } else {
                    femaleRadio.setSelected(true);
                }
                weightField.setText(String.valueOf(rs.getDouble("weight")));
                heightField.setText(String.valueOf(rs.getDouble("height")));
                targetCombo.setSelectedItem(rs.getString("target"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error fetching user details.", "Database Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Add fields to the form
        gbc.gridx = 0; gbc.gridy = 0; updateFrame.add(nameLabel, gbc);
        gbc.gridx = 1; updateFrame.add(nameField, gbc);
        gbc.gridx = 0; gbc.gridy = 1; updateFrame.add(ageLabel, gbc);
        gbc.gridx = 1; updateFrame.add(ageField, gbc);
        gbc.gridx = 0; gbc.gridy = 2; updateFrame.add(phoneLabel, gbc);
        gbc.gridx = 1; updateFrame.add(phoneField, gbc);
        gbc.gridx = 0; gbc.gridy = 3; updateFrame.add(emailLabel, gbc);
        gbc.gridx = 1; updateFrame.add(emailField, gbc);
        gbc.gridx = 0; gbc.gridy = 4; updateFrame.add(genderLabel, gbc);
        gbc.gridx = 1;
        JPanel genderPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        genderPanel.add(maleRadio);
        genderPanel.add(femaleRadio);
        updateFrame.add(genderPanel, gbc);
        gbc.gridx = 0; gbc.gridy = 5; updateFrame.add(weightLabel, gbc);
        gbc.gridx = 1; updateFrame.add(weightField, gbc);
        gbc.gridx = 0; gbc.gridy = 6; updateFrame.add(heightLabel, gbc);
        gbc.gridx = 1; updateFrame.add(heightField, gbc);
        gbc.gridx = 0; gbc.gridy = 7; updateFrame.add(targetLabel, gbc);
        gbc.gridx = 1; updateFrame.add(targetCombo, gbc);

        // Submit button
        JButton submitButton = new JButton("Update");
        submitButton.setBackground(new Color(0x4CAF50));  // Green button
        submitButton.setForeground(Color.WHITE);
        gbc.gridx = 1; gbc.gridy = 8; updateFrame.add(submitButton, gbc);

        submitButton.addActionListener(e -> {
            // Collect updated data from the form
            String newName = nameField.getText();
            int newAge = Integer.parseInt(ageField.getText());
            String newPhone = phoneField.getText();
            String newEmail = emailField.getText();
            String newGender = maleRadio.isSelected() ? "Male" : "Female";
            double newWeight = Double.parseDouble(weightField.getText());
            double newHeight = Double.parseDouble(heightField.getText());
            String newTarget = (String) targetCombo.getSelectedItem();

            // Update the user's details in the database
            updateUserData(userName, newName, newAge, newPhone, newEmail, newGender, newWeight, newHeight, newTarget);

            // Close the update form
            updateFrame.dispose();
        });

        updateFrame.setVisible(true);
    }
    private void updateUserData(String originalName, String newName, int newAge, String newPhone, String newEmail,
                                String newGender, double newWeight, double newHeight, String newTarget) {
        try {
            String updateQuery = "UPDATE members SET name = ?, age = ?, phone = ?, email = ?, gender = ?, "
                    + "weight = ?, height = ?, target = ? WHERE name = ?";
            PreparedStatement pstmt = connection.prepareStatement(updateQuery);
            pstmt.setString(1, newName);
            pstmt.setInt(2, newAge);
            pstmt.setString(3, newPhone);
            pstmt.setString(4, newEmail);
            pstmt.setString(5, newGender);
            pstmt.setDouble(6, newWeight);
            pstmt.setDouble(7, newHeight);
            pstmt.setString(8, newTarget);
            pstmt.setString(9, originalName);
            int rowsAffected = pstmt.executeUpdate();

            if (rowsAffected > 0) {
                JOptionPane.showMessageDialog(null, "Details updated successfully for " + newName);
            } else {
                JOptionPane.showMessageDialog(null, "Update failed.", "Error", JOptionPane.ERROR_MESSAGE);
            }

        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error updating user data.", "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    private void deleteUser(String userName) {
        int confirmation = JOptionPane.showConfirmDialog(null,
                "Are you sure you want to delete all records for " + userName + "?",
                "Confirm Deletion", JOptionPane.YES_NO_OPTION);

        if (confirmation == JOptionPane.YES_OPTION) {
            try {
                // Delete from workout table
                String deleteWorkoutQuery = "DELETE FROM workout WHERE name = ?";
                PreparedStatement pstmt1 = connection.prepareStatement(deleteWorkoutQuery);
                pstmt1.setString(1, userName);
                pstmt1.executeUpdate();

                // Delete from diet table
                String deleteDietQuery = "DELETE FROM diet WHERE name = ?";
                PreparedStatement pstmt2 = connection.prepareStatement(deleteDietQuery);
                pstmt2.setString(1, userName);
                pstmt2.executeUpdate();

                // Delete from status table
                String deleteStatusQuery = "DELETE FROM status WHERE name = ?";
                PreparedStatement pstmt3 = connection.prepareStatement(deleteStatusQuery);
                pstmt3.setString(1, userName);
                pstmt3.executeUpdate();

                // Delete from members table
                String deleteMemberQuery = "DELETE FROM members WHERE name = ?";
                PreparedStatement pstmt4 = connection.prepareStatement(deleteMemberQuery);
                pstmt4.setString(1, userName);
                pstmt4.executeUpdate();

                JOptionPane.showMessageDialog(null, "All information for " + userName + " has been successfully deleted.");

            } catch (SQLException e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(null, "Error deleting user data.", "Database Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    private void showDoctorForm(String userName) {
        JFrame doctorFormFrame = new JFrame("Doctor Consultation Form");
        doctorFormFrame.setSize(400, 600);
        doctorFormFrame.setLayout(new GridBagLayout());
        doctorFormFrame.setLocationRelativeTo(null);
        doctorFormFrame.getContentPane().setBackground(new Color(0xECEFF1));  // Light background for the form

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(10, 10, 10, 10);

        // Form Fields
        JLabel nameLabel = new JLabel("Name:");
        JTextField nameField = new JTextField(20);
        JLabel ageLabel = new JLabel("Age:");
        JTextField ageField = new JTextField(20);
        JLabel phoneLabel = new JLabel("Phone Number:");
        JTextField phoneField = new JTextField(20);
        JLabel genderLabel = new JLabel("Gender:");
        JRadioButton maleRadio = new JRadioButton("Male");
        JRadioButton femaleRadio = new JRadioButton("Female");
        ButtonGroup genderGroup = new ButtonGroup();
        genderGroup.add(maleRadio);
        genderGroup.add(femaleRadio);
        JLabel problemLabel = new JLabel("State your problem:");
        JTextArea problemArea = new JTextArea(5, 20);
        problemArea.setLineWrap(true);
        problemArea.setWrapStyleWord(true);
        JScrollPane problemScrollPane = new JScrollPane(problemArea);
        JLabel allergiesLabel = new JLabel("Allergies (if any):");
        JTextField allergiesField = new JTextField(20);

        // Add fields to the form
        gbc.gridx = 0; gbc.gridy = 0; doctorFormFrame.add(nameLabel, gbc);
        gbc.gridx = 1; doctorFormFrame.add(nameField, gbc);
        gbc.gridx = 0; gbc.gridy = 1; doctorFormFrame.add(ageLabel, gbc);
        gbc.gridx = 1; doctorFormFrame.add(ageField, gbc);
        gbc.gridx = 0; gbc.gridy = 2; doctorFormFrame.add(phoneLabel, gbc);
        gbc.gridx = 1; doctorFormFrame.add(phoneField, gbc);
        gbc.gridx = 0; gbc.gridy = 3; doctorFormFrame.add(genderLabel, gbc);
        gbc.gridx = 1;
        JPanel genderPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        genderPanel.add(maleRadio);
        genderPanel.add(femaleRadio);
        doctorFormFrame.add(genderPanel, gbc);
        gbc.gridx = 0; gbc.gridy = 4; doctorFormFrame.add(problemLabel, gbc);
        gbc.gridx = 1; doctorFormFrame.add(problemScrollPane, gbc);
        gbc.gridx = 0; gbc.gridy = 5; doctorFormFrame.add(allergiesLabel, gbc);
        gbc.gridx = 1; doctorFormFrame.add(allergiesField, gbc);

        // Submit button
        JButton submitButton = new JButton("Submit");
        submitButton.setBackground(new Color(0x4CAF50));  // Green button
        submitButton.setForeground(Color.WHITE);
        gbc.gridx = 1; gbc.gridy = 6; doctorFormFrame.add(submitButton, gbc);

        submitButton.addActionListener(e -> {
            String name = nameField.getText();
            int age = Integer.parseInt(ageField.getText());
            String phone = phoneField.getText();
            String gender = maleRadio.isSelected() ? "Male" : "Female";
            String problem = problemArea.getText();
            String allergies = allergiesField.getText();

            // Store data in the database
            storeDoctorData(name, age, phone, gender, problem, allergies);

            // Ask if the member had past problems
            int pastIssues = JOptionPane.showConfirmDialog(doctorFormFrame,
                    "Have there been any problems related to this member in the past?",
                    "Past Problems", JOptionPane.YES_NO_OPTION);
            if (pastIssues == JOptionPane.YES_OPTION) {
                String pastProblem = JOptionPane.showInputDialog(doctorFormFrame,
                        "Please state the past problem:",
                        "Past Problem", JOptionPane.QUESTION_MESSAGE);
                if (pastProblem != null && !pastProblem.isEmpty()) {
                    storePastProblemData(userName, pastProblem);
                }
            }

            // Show doctor details and confirmation
            displayDoctorDetails();

            // Close the form
            doctorFormFrame.dispose();
        });

        doctorFormFrame.setVisible(true);
    }
    private void storeDoctorData(String name, int age, String phone, String gender, String problem, String allergies) {
        try {
            String query = "INSERT INTO doctor (name, age, phone, gender, problem, allergies) VALUES (?, ?, ?, ?, ?, ?)";
            PreparedStatement pstmt = connection.prepareStatement(query);
            pstmt.setString(1, name);
            pstmt.setInt(2, age);
            pstmt.setString(3, phone);
            pstmt.setString(4, gender);
            pstmt.setString(5, problem);
            pstmt.setString(6, allergies);
            pstmt.executeUpdate();
            JOptionPane.showMessageDialog(null, "Doctor data stored successfully!");
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error storing doctor data", "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    private void storePastProblemData(String userName, String pastProblem) {
        try {
            String query = "INSERT INTO past_problems (user_name, problem_description) VALUES (?, ?)";
            PreparedStatement pstmt = connection.prepareStatement(query);
            pstmt.setString(1, userName);
            pstmt.setString(2, pastProblem);
            pstmt.executeUpdate();
            JOptionPane.showMessageDialog(null, "Past problem recorded successfully!");
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error storing past problem", "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    private void displayDoctorDetails() {
        String doctorDetails = "<html><b>Doctor Details</b><br>Name: Dr. Kumar<br>Phone Number: 9163315631</html>";
        JLabel doctorLabel = new JLabel(doctorDetails);
        doctorLabel.setFont(new Font("Arial", Font.BOLD, 14));
        doctorLabel.setForeground(new Color(0x4CAF50));  // Green text

        JOptionPane.showMessageDialog(null, doctorLabel, "Doctor Details", JOptionPane.INFORMATION_MESSAGE);
        JOptionPane.showMessageDialog(null, "Your problem has been reported. The doctor will contact you soon.",
                "Confirmation", JOptionPane.INFORMATION_MESSAGE);
    }
    private void showTransformForm() {
        JFrame transformFrame = new JFrame("Transform");
        transformFrame.setSize(400, 600);
        transformFrame.setLocationRelativeTo(null);
        transformFrame.setLayout(new BorderLayout());
        JPanel formPanel = new JPanel();
        formPanel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);
        JCheckBox swearBox = new JCheckBox("I swear to transform");
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        formPanel.add(swearBox, gbc);
        String[] labels = {"Name:", "Age:", "Phone Number:", "E-mail:", "Gender:", "Weight (kg):", "Height (cm):", "Target:"};
        JTextField[] fields = new JTextField[6];
        ButtonGroup genderGroup = new ButtonGroup();
        JRadioButton maleRadio = new JRadioButton("Male");
        JRadioButton femaleRadio = new JRadioButton("Female");
        genderGroup.add(maleRadio);
        genderGroup.add(femaleRadio);
        String[] targets = {"Maintain Weight", "Gain Weight", "Lose Weight"};
        JComboBox<String> targetCombo = new JComboBox<>(targets);
        for (int i = 0; i < labels.length; i++) {
            gbc.gridx = 0;
            gbc.gridy = i + 1;
            gbc.gridwidth = 1;
            formPanel.add(new JLabel(labels[i]), gbc);
            gbc.gridx = 1;
            if (i == 4) {
                JPanel genderPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
                genderPanel.add(maleRadio);
                genderPanel.add(femaleRadio);
                formPanel.add(genderPanel, gbc);
            } else if (i == 7) {
                formPanel.add(targetCombo, gbc);
            } else {
                fields[i > 4 ? i - 1 : i] = new JTextField(20);
                formPanel.add(fields[i > 4 ? i - 1 : i], gbc);
            }
        }
        JButton submitButton = new JButton("Submit");
        gbc.gridx = 0;
        gbc.gridy = labels.length + 1;
        gbc.gridwidth = 2;
        formPanel.add(submitButton, gbc);
        transformFrame.add(new JScrollPane(formPanel), BorderLayout.CENTER);
        submitButton.addActionListener(e -> {
            if (!swearBox.isSelected()) {
                JOptionPane.showMessageDialog(transformFrame, "Please swear to transform!");
                return;
            }
            String name = fields[0].getText();
            int age = Integer.parseInt(fields[1].getText());
            String phone = fields[2].getText();
            String email = fields[3].getText();
            String gender = maleRadio.isSelected() ? "Male" : "Female";
            double weight = Double.parseDouble(fields[4].getText());
            double height = Double.parseDouble(fields[5].getText());
            String target = (String) targetCombo.getSelectedItem();
            int option = JOptionPane.showOptionDialog(transformFrame,
                    "Are you new to the gym?",
                    "Gym Experience",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.QUESTION_MESSAGE,
                    null, null, null);
            boolean isNew = (option == JOptionPane.YES_OPTION);
            if (isNew) {
                JOptionPane.showMessageDialog(transformFrame,
                        "Since you are new to gym culture, we recommend you to go with our personal trainers until you turn pro.",
                        "Recommendation",
                        JOptionPane.INFORMATION_MESSAGE);
            }
            String[] plans = {"Monthly", "Quarterly", "Semi-Annual", "Annual"};
            String[] trainerTypes = {"Personal", "General"};
            JComboBox<String> planCombo = new JComboBox<>(plans);
            JComboBox<String> trainerCombo = new JComboBox<>(trainerTypes);
            JPanel planPanel = new JPanel(new GridLayout(4, 1));
            planPanel.add(new JLabel("Choose plan:"));
            planPanel.add(planCombo);
            planPanel.add(new JLabel("Choose trainer:"));
            planPanel.add(trainerCombo);
            planPanel.add(new JLabel("(Each member will be assisted by a trainer no matter pro or not)"));
            int result = JOptionPane.showConfirmDialog(transformFrame, planPanel, "Select Plan and Trainer", JOptionPane.OK_CANCEL_OPTION);
            if (result == JOptionPane.OK_OPTION) {
                String selectedPlan = (String) planCombo.getSelectedItem();
                String selectedTrainer = (String) trainerCombo.getSelectedItem();
                int cost = calculateCost(selectedPlan, selectedTrainer);
                double tax = cost * 0.18;
                int joiningFee = 1000;
                double totalCost = cost + tax + joiningFee;
                DecimalFormat df = new DecimalFormat("#.##");
                String bill = "Plan: " + selectedPlan + "\n" +
                        "Trainer: " + selectedTrainer + "\n" +
                        "Base Cost: ₹" + cost + "\n" +
                        "Tax (18%): ₹" + df.format(tax) + "\n" +
                        "Joining Fee: ₹" + joiningFee + "\n" +
                        "Total Cost: ₹" + df.format(totalCost);
                JOptionPane.showMessageDialog(transformFrame, bill, "Bill", JOptionPane.INFORMATION_MESSAGE);
                String chosenTrainer = chooseTrainer(selectedTrainer);
                if (chosenTrainer != null) {
                    storeMemberData(name, age, phone, email, gender, weight, height, target, selectedPlan, selectedTrainer, chosenTrainer, totalCost);
                    updateTrainerStudentCount(chosenTrainer);
                }
                transformFrame.dispose();
            }
        });
        transformFrame.setVisible(true);
    }
    private int calculateCost(String plan, String trainerType) {
        switch (plan) {
            case "Monthly":
                return trainerType.equals("Personal") ? 2800 : 1800;
            case "Quarterly":
                return trainerType.equals("Personal") ? 7560 : 4860;
            case "Semi-Annual":
                return trainerType.equals("Personal") ? 14280 : 9180;
            case "Annual":
                return trainerType.equals("Personal") ? 26880 : 17280;
            default:
                return 0;
        }
    }
    private String chooseTrainer(String trainerType) {
        List<String> availableTrainers = getAvailableTrainers(trainerType);
        if (availableTrainers.isEmpty()) {
            JOptionPane.showMessageDialog(null, "No " + trainerType + " trainers available at the moment.");
            return null;
        }
        String[] trainers = availableTrainers.toArray(new String[0]);
        String chosenTrainer = (String) JOptionPane.showInputDialog(null, "Choose a trainer:",
                "Trainer Selection", JOptionPane.QUESTION_MESSAGE, null, trainers, trainers[0]);
        return chosenTrainer;
    }
    private List<String> getAvailableTrainers(String trainerType) {
        List<String> trainers = new ArrayList<>();
        try {
            String query = "SELECT name FROM trainers WHERE type = ?";
            PreparedStatement pstmt = connection.prepareStatement(query);
            pstmt.setString(1, trainerType + " Trainer");
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                trainers.add(rs.getString("name"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return trainers;
    }
    private void updateTrainerStudentCount(String trainerName) {
        try {
            String updateStudentCountQuery = "UPDATE trainers SET starting_students = starting_students + 1 WHERE name = ?";
            PreparedStatement pstmt = connection.prepareStatement(updateStudentCountQuery);
            pstmt.setString(1, trainerName);
            pstmt.executeUpdate();
            String fetchTrainerDetailsQuery = "SELECT type, starting_students FROM trainers WHERE name = ?";
            pstmt = connection.prepareStatement(fetchTrainerDetailsQuery);
            pstmt.setString(1, trainerName);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                String trainerType = rs.getString("type");
                int startingStudents = rs.getInt("starting_students");
                double newSalary;
                if (trainerType.equals("Personal Trainer")) {
                    newSalary = 9000 + (startingStudents * 600);
                } else {
                    newSalary = 7500 + (startingStudents * 350);
                }
                String updateSalaryQuery = "UPDATE trainers SET salary = ? WHERE name = ?";
                pstmt = connection.prepareStatement(updateSalaryQuery);
                pstmt.setDouble(1, newSalary);
                pstmt.setString(2, trainerName);
                pstmt.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    private void storeMemberData(String name, int age, String phone, String email, String gender,
                                 double weight, double height, String target, String plan,
                                 String trainerType, String chosenTrainer, double totalCost) {
        try {
            String query = "INSERT INTO members (name, age, phone, email, gender, weight, height, target, plan, trainer_type, chosen_trainer, total_cost) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
            PreparedStatement pstmt = connection.prepareStatement(query);
            pstmt.setString(1, name);
            pstmt.setInt(2, age);
            pstmt.setString(3, phone);
            pstmt.setString(4, email);
            pstmt.setString(5, gender);
            pstmt.setDouble(6, weight);
            pstmt.setDouble(7, height);
            pstmt.setString(8, target);
            pstmt.setString(9, plan);
            pstmt.setString(10, trainerType);
            pstmt.setString(11, chosenTrainer);
            pstmt.setDouble(12, totalCost);
            pstmt.executeUpdate();
            JOptionPane.showMessageDialog(null, "Member data stored successfully!");
            insertMemberStatus(name, trainerType, chosenTrainer, plan, totalCost);
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error storing member data", "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    private void insertMemberStatus(String memberName, String trainerType, String trainerName, String plan, double cost) {
        String insertQuery = "INSERT INTO status (name, trainer_type, trainer, plan, cost) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement pstmt = connection.prepareStatement(insertQuery)) {
            pstmt.setString(1, memberName);
            pstmt.setString(2, trainerType);
            pstmt.setString(3, trainerName);
            pstmt.setString(4, plan);
            pstmt.setDouble(5, cost);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error inserting member status: " + e.getMessage());
        }
    }
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new R_A_GYM().setVisible(true));
    }
}
