package rvt;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class DatabaseConnection {
    private static final String DB_URL = "jdbc:sqlite:products.db";

    public DatabaseConnection() {
        initSchema();
    }

    private Connection connect() throws SQLException {
        Connection conn = DriverManager.getConnection(DB_URL);
        try (PreparedStatement pragma = conn.prepareStatement("PRAGMA foreign_keys = ON")) {
            pragma.execute();
        }
        return conn;
    }

    private void initSchema() {
        String categorySql = "CREATE TABLE IF NOT EXISTS categories ("
                + "id INTEGER PRIMARY KEY AUTOINCREMENT, "
                + "name TEXT NOT NULL UNIQUE)";

        String productSql = "CREATE TABLE IF NOT EXISTS products ("
                + "id INTEGER PRIMARY KEY AUTOINCREMENT, "
                + "name TEXT NOT NULL, "
                + "price REAL NOT NULL CHECK(price > 0), "
                + "category_id INTEGER NOT NULL, "
                + "FOREIGN KEY(category_id) REFERENCES categories(id))";

        try (Connection conn = connect();
             Statement stmt = conn.createStatement()) {
            stmt.execute(categorySql);
            stmt.execute(productSql);
        } catch (SQLException e) {
            throw new RuntimeException("Schema init failed: " + e.getMessage(), e);
        }
    }

    public int addCategory(String name) {
        String sql = "INSERT INTO categories(name) VALUES(?)";
        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, name.trim());
            pstmt.executeUpdate();
            try (ResultSet rs = pstmt.getGeneratedKeys()) {
                return rs.next() ? rs.getInt(1) : -1;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to add category: " + e.getMessage(), e);
        }
    }

    public int addProduct(String name, double price, int categoryId) {
        String sql = "INSERT INTO products(name, price, category_id) VALUES(?, ?, ?)";
        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, name.trim());
            pstmt.setDouble(2, price);
            pstmt.setInt(3, categoryId);
            pstmt.executeUpdate();
            try (ResultSet rs = pstmt.getGeneratedKeys()) {
                return rs.next() ? rs.getInt(1) : -1;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to add product: " + e.getMessage(), e);
        }
    }

    public List<Category> findAllCategories() {
        String sql = "SELECT id, name FROM categories ORDER BY id";
        List<Category> categories = new ArrayList<>();

        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                categories.add(new Category(rs.getInt("id"), rs.getString("name")));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to read categories: " + e.getMessage(), e);
        }

        return categories;
    }

    public List<Product> findAllProducts() {
        String sql = "SELECT p.id, p.name, p.price, p.category_id, c.name AS category_name "
                + "FROM products p "
                + "JOIN categories c ON p.category_id = c.id "
                + "ORDER BY p.id";
        List<Product> products = new ArrayList<>();

        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                products.add(mapProduct(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to read products: " + e.getMessage(), e);
        }

        return products;
    }

    public List<Product> findProductsByCategoryId(int categoryId) {
        String sql = "SELECT p.id, p.name, p.price, p.category_id, c.name AS category_name "
                + "FROM products p "
                + "JOIN categories c ON p.category_id = c.id "
                + "WHERE c.id = ? "
                + "ORDER BY p.id";
        return queryProductsByPreparedStatement(categoryId, sql);
    }

    public List<Product> findProductsByCategoryName(String categoryName) {
        String sql = "SELECT p.id, p.name, p.price, p.category_id, c.name AS category_name "
                + "FROM products p "
                + "JOIN categories c ON p.category_id = c.id "
                + "WHERE c.name LIKE ? "
                + "ORDER BY p.id";
        return queryProductsByPreparedStatement("%" + categoryName.trim() + "%", sql);
    }

    public boolean hasCategory(int categoryId) {
        String sql = "SELECT 1 FROM categories WHERE id = ?";
        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, categoryId);
            try (ResultSet rs = pstmt.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to validate category: " + e.getMessage(), e);
        }
    }

    public boolean deleteProduct(int productId) {
        String sql = "DELETE FROM products WHERE id = ?";
        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, productId);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Failed to delete product: " + e.getMessage(), e);
        }
    }

    public boolean updateProduct(int productId, String name, double price, int categoryId) {
        String sql = "UPDATE products SET name = ?, price = ?, category_id = ? WHERE id = ?";
        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, name.trim());
            pstmt.setDouble(2, price);
            pstmt.setInt(3, categoryId);
            pstmt.setInt(4, productId);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Failed to update product: " + e.getMessage(), e);
        }
    }

    public Category findCategoryById(int categoryId) {
        String sql = "SELECT id, name FROM categories WHERE id = ?";
        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, categoryId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return new Category(rs.getInt("id"), rs.getString("name"));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to read category: " + e.getMessage(), e);
        }
        return null;
    }

    private List<Product> queryProductsByPreparedStatement(Object parameter, String sql) {
        List<Product> products = new ArrayList<>();
        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            if (parameter instanceof Integer integerParam) {
                pstmt.setInt(1, integerParam);
            } else {
                pstmt.setString(1, String.valueOf(parameter));
            }
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    products.add(mapProduct(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to read products: " + e.getMessage(), e);
        }
        return products;
    }

    private Product mapProduct(ResultSet rs) throws SQLException {
        return new Product(
                rs.getInt("id"),
                rs.getString("name"),
                rs.getDouble("price"),
                rs.getInt("category_id"),
                rs.getString("category_name")
        );
    }
}
