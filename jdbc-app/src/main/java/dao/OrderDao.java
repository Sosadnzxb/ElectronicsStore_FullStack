package com.electronics.store.dao;

import com.electronics.store.model.Order;
import java.sql.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class OrderDao {
    private final String URL = "jdbc:postgresql://localhost:5432/electronics_store_db";
    private final String USER = "postgres";
    private final String PASSWORD = "password";

    private Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }

    public void save(Order order) {
        String sql = "INSERT INTO orders (customer_id, order_date, status) VALUES (?, ?, ?)";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setInt(1, order.getCustomerId());
            stmt.setDate(2, new java.sql.Date(order.getOrderDate().getTime()));
            stmt.setString(3, order.getStatus());
            stmt.executeUpdate();
            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    order.setId(rs.getInt(1));
                }
            }
        } catch (SQLException e) {
            System.out.println("Ошибка при добавлении заказа: " + e.getMessage());
        }
    }

    public List<Order> findAll() {
        List<Order> orders = new ArrayList<>();
        String sql = "SELECT * FROM orders";
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                Order order = new Order();
                order.setId(rs.getInt("id"));
                order.setCustomerId(rs.getInt("customer_id"));
                order.setOrderDate(rs.getDate("order_date"));
                order.setStatus(rs.getString("status"));
                orders.add(order);
            }
        } catch (SQLException e) {
            System.out.println("Ошибка при получении списка заказов: " + e.getMessage());
        }
        return orders;
    }

    public void update(Order order) {
        String sql = "UPDATE orders SET customer_id = ?, order_date = ?, status = ? WHERE id = ?";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, order.getCustomerId());
            stmt.setDate(2, new java.sql.Date(order.getOrderDate().getTime()));
            stmt.setString(3, order.getStatus());
            stmt.setInt(4, order.getId());
            stmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Ошибка при обновлении заказа: " + e.getMessage());
        }
    }

    public void delete(int id) {
        String sql = "DELETE FROM orders WHERE id = ?";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Ошибка при удалении заказа: " + e.getMessage());
        }
    }

    public Order findById(int id) {
        String sql = "SELECT * FROM orders WHERE id = ?";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Order order = new Order();
                    order.setId(rs.getInt("id"));
                    order.setCustomerId(rs.getInt("customer_id"));
                    order.setOrderDate(rs.getDate("order_date"));
                    order.setStatus(rs.getString("status"));
                    return order;
                }
            }
        } catch (SQLException e) {
            System.out.println("Ошибка при поиске заказа: " + e.getMessage());
        }
        return null;
    }
}