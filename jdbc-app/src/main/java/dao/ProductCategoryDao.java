package com.electronics.store.dao;

import com.electronics.store.model.ProductCategory;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import config.DatabaseConfig;

public class ProductCategoryDao {
    private Connection getConnection() throws SQLException {
        return DriverManager.getConnection(DatabaseConfig.URL, DatabaseConfig.USER, DatabaseConfig.PASSWORD);
    }

    // Добавление связи товара с категорией
    public void addProductToCategory(int productId, int categoryId) {
        String sql = "INSERT INTO product_categories (product_id, category_id) VALUES (?, ?)";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, productId);
            stmt.setInt(2, categoryId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Ошибка при добавлении связи: " + e.getMessage());
        }
    }

    // Получение категорий товара
    public List<Integer> findCategoriesByProduct(int productId) {
        List<Integer> categories = new ArrayList<>();
        String sql = "SELECT category_id FROM product_categories WHERE product_id = ?";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, productId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    categories.add(rs.getInt("category_id"));
                }
            }
        } catch (SQLException e) {
            System.out.println("Ошибка при получении категорий: " + e.getMessage());
        }
        return categories;
    }
}