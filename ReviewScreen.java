import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.sql.*;

public class ReviewScreen extends JPanel {
    private MainApp mainApp;
    private int technicianId;

    private JPanel reviewsContainer;
    private JTextField ratingField;
    private JTextArea commentArea;

    public ReviewScreen(MainApp mainApp, int technicianId) {
        this.mainApp = mainApp;
        this.technicianId = technicianId;

        setLayout(new BorderLayout(10, 10));
        setBorder(new EmptyBorder(10, 10, 10, 10));

        JLabel title = new JLabel("Αξιολογήσεις Τεχνικού", SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 20));
        add(title, BorderLayout.NORTH);

        // Review block container
        reviewsContainer = new JPanel();
        reviewsContainer.setLayout(new BoxLayout(reviewsContainer, BoxLayout.Y_AXIS));
        JScrollPane scrollPane = new JScrollPane(reviewsContainer);
        add(scrollPane, BorderLayout.CENTER);

        // Review submission panel
        JPanel reviewForm = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0; gbc.gridy = 0;
        reviewForm.add(new JLabel("Your Rating (1-5):"), gbc);
        gbc.gridx = 1;
        ratingField = new JTextField(5);
        reviewForm.add(ratingField, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        reviewForm.add(new JLabel("Your Comment:"), gbc);
        gbc.gridx = 1;
        commentArea = new JTextArea(3, 30);
        reviewForm.add(new JScrollPane(commentArea), gbc);

        JButton submitBtn = new JButton("Submit Review");
        gbc.gridx = 1; gbc.gridy = 2;
        reviewForm.add(submitBtn, gbc);

        // Back button
        JButton backBtn = new JButton("Back");
        backBtn.addActionListener(e -> mainApp.switchScreen(new TechnicianOptionsScreen(mainApp, technicianId)));

        JPanel southPanel = new JPanel(new BorderLayout());
        southPanel.add(reviewForm, BorderLayout.CENTER);
        southPanel.add(backBtn, BorderLayout.SOUTH);
        add(southPanel, BorderLayout.SOUTH);

        submitBtn.addActionListener(e -> submitReview());

        loadReviews();
        loadMyReview();
    }

    private void loadReviews() {
        reviewsContainer.removeAll();

        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/TL", "root", "12345")) {
            String query = """
                SELECT u.name, r.rating, r.comment
                FROM reviews r
                JOIN users u ON r.client_id = u.id
                WHERE r.technician_id = ?
                ORDER BY r.rating DESC
            """;

            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setInt(1, technicianId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                String name = rs.getString("name");
                int rating = rs.getInt("rating");
                String comment = rs.getString("comment");

                JPanel reviewPanel = new JPanel();
                reviewPanel.setLayout(new BorderLayout());
                reviewPanel.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(Color.GRAY, 1),
                        new EmptyBorder(8, 10, 8, 10)
                ));
                reviewPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 120));

                // Top row: Name left, rating right
                JPanel topRow = new JPanel(new BorderLayout());
                JLabel nameLabel = new JLabel(name);
                nameLabel.setFont(new Font("Arial", Font.BOLD, 14));
                JLabel ratingLabel = new JLabel("Rating: " +rating + "/5", SwingConstants.RIGHT);
                ratingLabel.setFont(new Font("Arial", Font.PLAIN, 14));
                topRow.add(nameLabel, BorderLayout.WEST);
                topRow.add(ratingLabel, BorderLayout.EAST);

                reviewPanel.add(topRow, BorderLayout.NORTH);

                if (comment != null && !comment.trim().isEmpty()) {
                    JTextArea commentText = new JTextArea(comment);
                    commentText.setWrapStyleWord(true);
                    commentText.setLineWrap(true);
                    commentText.setEditable(false);
                    commentText.setOpaque(false);
                    commentText.setFont(new Font("Arial", Font.PLAIN, 13));
                    reviewPanel.add(commentText, BorderLayout.CENTER);
                }

                reviewsContainer.add(reviewPanel);
                reviewsContainer.add(Box.createVerticalStrut(10));
            }

            rs.close();
            stmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
            reviewsContainer.add(new JLabel("Σφάλμα κατά την φόρτωση αξιολογήσεων."));
        }

        revalidate();
        repaint();
    }

    private void loadMyReview() {
        int clientId = mainApp.getCurrentUserId();
        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/TL", "root", "12345")) {
            String query = "SELECT rating, comment FROM reviews WHERE client_id = ? AND technician_id = ?";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setInt(1, clientId);
            stmt.setInt(2, technicianId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                ratingField.setText(String.valueOf(rs.getInt("rating")));
                commentArea.setText(rs.getString("comment"));
            }

            rs.close();
            stmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void submitReview() {
        int clientId = mainApp.getCurrentUserId();
        String ratingText = ratingField.getText().trim();
        String comment = commentArea.getText().trim();

        int rating;
        try {
            rating = Integer.parseInt(ratingText);
            if (rating < 1 || rating > 5) throw new NumberFormatException();
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Η βαθμολογία πρέπει να είναι αριθμός από 1 έως 5.", "Invalid Rating", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/TL", "root", "12345")) {
            String check = "SELECT id FROM reviews WHERE client_id = ? AND technician_id = ?";
            PreparedStatement checkStmt = conn.prepareStatement(check);
            checkStmt.setInt(1, clientId);
            checkStmt.setInt(2, technicianId);
            ResultSet rs = checkStmt.executeQuery();

            if (rs.next()) {
                String update = "UPDATE reviews SET rating = ?, comment = ? WHERE id = ?";
                PreparedStatement updateStmt = conn.prepareStatement(update);
                updateStmt.setInt(1, rating);
                updateStmt.setString(2, comment);
                updateStmt.setInt(3, rs.getInt("id"));
                updateStmt.executeUpdate();
                updateStmt.close();
            } else {
                String insert = "INSERT INTO reviews (client_id, technician_id, rating, comment) VALUES (?, ?, ?, ?)";
                PreparedStatement insertStmt = conn.prepareStatement(insert);
                insertStmt.setInt(1, clientId);
                insertStmt.setInt(2, technicianId);
                insertStmt.setInt(3, rating);
                insertStmt.setString(4, comment);
                insertStmt.executeUpdate();
                insertStmt.close();
            }

            rs.close();
            checkStmt.close();

            JOptionPane.showMessageDialog(this, "Η αξιολόγηση αποθηκεύτηκε επιτυχώς.", "Success", JOptionPane.INFORMATION_MESSAGE);
            updateTechnicianRating(technicianId);
            loadReviews();
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Αποτυχία αποθήκευσης αξιολόγησης.", "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void updateTechnicianRating(int technicianId) {
        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/TL", "root", "12345")) {
            String getRatings = "SELECT rating FROM reviews WHERE technician_id = ? ORDER BY rating";
            PreparedStatement stmt = conn.prepareStatement(getRatings);
            stmt.setInt(1, technicianId);
            ResultSet rs = stmt.executeQuery();

            java.util.List<Integer> ratings = new java.util.ArrayList<>();
            while (rs.next()) {
                ratings.add(rs.getInt("rating"));
            }

            rs.close();
            stmt.close();

            if (!ratings.isEmpty()) {
                double median;
                int size = ratings.size();
                if (size % 2 == 1) {
                    median = ratings.get(size / 2);
                } else {
                    median = (ratings.get(size / 2 - 1) + ratings.get(size / 2)) / 2.0;
                }

                String update = "UPDATE technicians SET rating = ? WHERE user_id = ?";
                PreparedStatement updateStmt = conn.prepareStatement(update);
                updateStmt.setDouble(1, median);
                updateStmt.setInt(2, technicianId);
                updateStmt.executeUpdate();
                updateStmt.close();
            }

        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Failed to update technician rating.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}