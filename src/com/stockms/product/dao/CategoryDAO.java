package com.stockms.product.dao;

import com.stockms.common.db.DBConnection;
import com.stockms.common.exception.StockManagementException;
import com.stockms.common.util.DateUtil;
import com.stockms.product.model.Category;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for {@link Category}. Handles all raw SQL / JDBC
 * interaction with the `categories` table.
 */
public class CategoryDAO {

    private static final String SQL_INSERT =
            "INSERT INTO categories (name, description, created_at) VALUES (?, ?, ?)";

    private static final String SQL_UPDATE =
            "UPDATE categories SET name = ?, description = ? WHERE category_id = ?";

    private static final String SQL_DELETE =
            "DELETE FROM categories WHERE category_id = ?";

    private static final String SQL_FIND_BY_ID =
            "SELECT category_id, name, description, created_at FROM categories WHERE category_id = ?";

    private static final String SQL_FIND_ALL =
            "SELECT category_id, name, description, created_at FROM categories ORDER BY name ASC";

    private static final String SQL_EXISTS_BY_NAME =
            "SELECT COUNT(*) FROM categories WHERE LOWER(name) = LOWER(?)";

    private static final String SQL_EXISTS_BY_NAME_EXCLUDING_ID =
            "SELECT COUNT(*) FROM categories WHERE LOWER(name) = LOWER(?) AND category_id <> ?";

    private static final String SQL_COUNT_PRODUCTS_IN_CATEGORY =
            "SELECT COUNT(*) FROM products WHERE category_id = ?";

    /**
     * Inserts a new category and populates the generated category_id back
     * onto the given object.
     */
    public void insert(Category category) throws StockManagementException {
        LocalDateTime now = DateUtil.now();
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(SQL_INSERT, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, category.getName());
            ps.setString(2, category.getDescription());
            ps.setTimestamp(3, DateUtil.toSqlTimestamp(now));

            int affected = ps.executeUpdate();
            if (affected == 0) {
                throw new StockManagementException("Creating category failed, no rows affected.",
                        StockManagementException.ERR_DATABASE);
            }
            try (ResultSet generatedKeys = ps.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    category.setCategoryId(generatedKeys.getInt(1));
                }
            }
            category.setCreatedAt(now);
        } catch (SQLException ex) {
            throw new StockManagementException("Failed to insert category '" + category.getName() + "'.",
                    StockManagementException.ERR_DATABASE, ex);
        }
    }

    /** Updates an existing category's name and description. */
    public void update(Category category) throws StockManagementException {
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(SQL_UPDATE)) {

            ps.setString(1, category.getName());
            ps.setString(2, category.getDescription());
            ps.setInt(3, category.getCategoryId());

            int affected = ps.executeUpdate();
            if (affected == 0) {
                throw new StockManagementException(
                        "Category with id " + category.getCategoryId() + " does not exist.",
                        StockManagementException.ERR_NOT_FOUND);
            }
        } catch (SQLException ex) {
            throw new StockManagementException("Failed to update category '" + category.getName() + "'.",
                    StockManagementException.ERR_DATABASE, ex);
        }
    }

    /**
     * Deletes a category by id.
     *
     * @throws StockManagementException with error code ERR_BUSINESS_RULE if
     *         products still reference this category (referential integrity
     *         guard performed at the application level in addition to any
     *         database foreign-key constraint).
     */
    public void delete(int categoryId) throws StockManagementException {
        try (Connection conn = DBConnection.getConnection()) {
            try (PreparedStatement countPs = conn.prepareStatement(SQL_COUNT_PRODUCTS_IN_CATEGORY)) {
                countPs.setInt(1, categoryId);
                try (ResultSet rs = countPs.executeQuery()) {
                    if (rs.next() && rs.getInt(1) > 0) {
                        throw new StockManagementException(
                                "Cannot delete category: " + rs.getInt(1)
                                        + " product(s) are still assigned to it.",
                                StockManagementException.ERR_BUSINESS_RULE);
                    }
                }
            }
            try (PreparedStatement deletePs = conn.prepareStatement(SQL_DELETE)) {
                deletePs.setInt(1, categoryId);
                int affected = deletePs.executeUpdate();
                if (affected == 0) {
                    throw new StockManagementException("Category with id " + categoryId + " does not exist.",
                            StockManagementException.ERR_NOT_FOUND);
                }
            }
        } catch (SQLException ex) {
            throw new StockManagementException("Failed to delete category with id " + categoryId + ".",
                    StockManagementException.ERR_DATABASE, ex);
        }
    }

    /** Retrieves a single category by its primary key, or null if not found. */
    public Category findById(int categoryId) throws StockManagementException {
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(SQL_FIND_BY_ID)) {

            ps.setInt(1, categoryId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapRow(rs);
                }
                return null;
            }
        } catch (SQLException ex) {
            throw new StockManagementException("Failed to fetch category with id " + categoryId + ".",
                    StockManagementException.ERR_DATABASE, ex);
        }
    }

    /** Returns all categories, ordered alphabetically by name. */
    public List<Category> findAll() throws StockManagementException {
        List<Category> categories = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(SQL_FIND_ALL);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                categories.add(mapRow(rs));
            }
        } catch (SQLException ex) {
            throw new StockManagementException("Failed to fetch category list.",
                    StockManagementException.ERR_DATABASE, ex);
        }
        return categories;
    }

    /** Returns true if a category with this name already exists (case-insensitive). */
    public boolean existsByName(String name) throws StockManagementException {
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(SQL_EXISTS_BY_NAME)) {

            ps.setString(1, name);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() && rs.getInt(1) > 0;
            }
        } catch (SQLException ex) {
            throw new StockManagementException("Failed to check for duplicate category name '" + name + "'.",
                    StockManagementException.ERR_DATABASE, ex);
        }
    }

    /** Returns true if a category with this name exists on a different category_id (used during edit/update). */
    public boolean existsByNameExcludingId(String name, int excludeCategoryId) throws StockManagementException {
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(SQL_EXISTS_BY_NAME_EXCLUDING_ID)) {

            ps.setString(1, name);
            ps.setInt(2, excludeCategoryId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() && rs.getInt(1) > 0;
            }
        } catch (SQLException ex) {
            throw new StockManagementException("Failed to check for duplicate category name '" + name + "'.",
                    StockManagementException.ERR_DATABASE, ex);
        }
    }

    private Category mapRow(ResultSet rs) throws SQLException {
        Category category = new Category();
        category.setCategoryId(rs.getInt("category_id"));
        category.setName(rs.getString("name"));
        category.setDescription(rs.getString("description"));
        category.setCreatedAt(DateUtil.fromSqlTimestamp(rs.getTimestamp("created_at")));
        return category;
    }
}
