package com.electronics.store.dao;

import com.electronics.store.model.Review;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import config.DatabaseConfig;

public class ReviewDao {
    private Connection getConnection() throws SQLException {
        return DriverManager.getConnection(DatabaseConfig.URL, DatabaseConfig.USER, DatabaseConfig.PASSWORD);
    }

    // Добавление отзыва
    public void save(Review review) {
        String sql = "INSERT INTO reviews (product_id, customer_id, rating, comment) VALUES (?, ?, ?, ?)";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setInt(1, review.getProductId());
            stmt.setInt(2, review.getCustomerId());
            stmt.setInt(3, review.getRating());
            stmt.setString(4, review.getComment());
            stmt.executeUpdate();
            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    review.setId(rs.getInt(1));
                }
            }
        } catch (SQLException e) {
            System.out.println("Ошибка при добавлении отзыва: " + e.getMessage());
        }
    }

    // Получение всех отзывов
    public List<Review> findAll() {
        List<Review> reviews = new ArrayList<>();
        String sql = "SELECT * FROM reviews";
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                Review review = new Review();
                review.setId(rs.getInt("id"));
                review.setProductId(rs.getInt("product_id"));
                review.setCustomerId(rs.getInt("customer_id"));
                review.setRating(rs.getInt("rating"));
                review.setComment(rs.getString("comment"));
                reviews.add(review);
            }
        } catch (SQLException e) {
            System.out.println("Ошибка при получении отзывов: " + e.getMessage());
        }
        return reviews;
    }

    // Обновление отзыва
    public void update(Review review) {
        String sql = "UPDATE reviews SET product_id = ?, customer_id = ?, rating = ?, comment = ? WHERE id = ?";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, review.getProductId());
            stmt.setInt(2, review.getCustomerId());
            stmt.setInt(3, review.getRating());
            stmt.setString(4, review.getComment());
            stmt.setInt(5, review.getId());
            int affectedRows = stmt.executeUpdate();
            if (affectedRows == 0) {
                System.out.println("Отзыв с ID " + review.getId() + " не найден!");
            }
        } catch (SQLException e) {
            System.out.println("Ошибка при обновлении отзыва: " + e.getMessage());
        }
    }

    // Удаление отзыва
    public void delete(int id) {
        String sql = "DELETE FROM reviews WHERE id = ?";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            int affectedRows = stmt.executeUpdate();
            if (affectedRows == 0) {
                System.out.println("Отзыв с ID " + id + " не найден!");
            }
        } catch (SQLException e) {
            System.out.println("Ошибка при удалении отзыва: " + e.getMessage());
        }
    }

    // Поиск отзыва по ID
    public Review findById(int id) {
        String sql = "SELECT * FROM reviews WHERE id = ?";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Review review = new Review();
                    review.setId(rs.getInt("id"));
                    review.setProductId(rs.getInt("product_id"));
                    review.setCustomerId(rs.getInt("customer_id"));
                    review.setRating(rs.getInt("rating"));
                    review.setComment(rs.getString("comment"));
                    return review;
                }
            }
        } catch (SQLException e) {
            System.out.println("Ошибка при поиске отзыва: " + e.getMessage());
        }
        return null;
    }
}