package com.stockms.product.dao;

import com.stockms.common.db.DBConnection;
import com.stockms.common.exception.StockManagementException;
import com.stockms.common.util.DateUtil;
import com.stockms.product.model.Product;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for {@link Product}. Handles all raw SQL / JDBC
 * interaction with the `products` table, joined against `categories` for
 * read operations so callers get the category name without a second query.
 */
public class ProductDAO {

    private static final String SELECT_BASE =
            "SELECT p.product_id, p.sku, p.name, p.description, p.category_id, c.name AS category_name, "
                    + "p.unit, p.unit_price, p.quantity_in_stock, p.reorder_level, p.created_at, p.updated_at "
                    + "FROM products p LEFT JOIN categories c ON p.category_id = c.category_id ";

    private static final String SQL_INSERT =
            "INSERT INTO products (sku, name, description, category_id, unit, unit_price, "
                    + "quantity_in_stock, reorder_level, created_at, updated_at) "
                    + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

    private static final String SQL_UPDATE =
            "UPDATE products SET sku = ?, name = ?, description = ?, category_id = ?, unit = ?, "
                    + "unit_price = ?, quantity_in_stock = ?, reorder_level = ?, updated_at = ? "
                    + "WHERE product_id = ?";

    private static final String SQL_UPDATE_QUANTITY =
            "UPDATE products SET quantity_in_stock = quantity_in_stock + ?, updated_at = ? WHERE product_id = ?";

    private static final String SQL_DELETE =
            "DELETE FROM products WHERE product_id = ?";

    private static final String SQL_FIND_BY_ID =
            SELECT_BASE + "WHERE p.product_id = ?";

    private static final String SQL_FIND_BY_SKU =
            SELECT_BASE + "WHERE p.sku = ?";

    private static final String SQL_FIND_ALL =
            SELECT_BASE + "ORDER BY p.name ASC";

    private static final String SQL_SEARCH_BY_NAME =
            SELECT_BASE + "WHERE p.name LIKE ? OR p.sku LIKE ? ORDER BY p.name ASC";

    private static final String SQL_FIND_BY_CATEGORY =
            SELECT_BASE + "WHERE p.category_id = ? ORDER BY p.name ASC";

    private static final String SQL_FIND_LOW_STOCK =
            SELECT_BASE + "WHERE p.quantity_in_stock <= p.reorder_level ORDER BY p.quantity_in_stock ASC";

    private static final String SQL_EXISTS_BY_SKU =
            "SELECT COUNT(*) FROM products WHERE sku = ?";

    private static final String SQL_EXISTS_BY_SKU_EXCLUDING_ID =
            "SELECT COUNT(*) FROM products WHERE sku = ? AND product_id <> ?";

    /** Inserts a new product and populates the generated product_id back onto the given object. */
    public void insert(Product product) throws StockManagementException {
        LocalDateTime now = DateUtil.now();
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(SQL_INSERT, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, product.getSku());
            ps.setString(2, product.getName());
            ps.setString(3, product.getDescription());
            ps.setInt(4, product.getCategoryId());
            ps.setString(5, product.getUnit());
            ps.setBigDecimal(6, product.getUnitPrice());
            ps.setInt(7, product.getQuantityInStock());
            ps.setInt(8, product.getReorderLevel());
            ps.setTimestamp(9, DateUtil.toSqlTimestamp(now));
            ps.setTimestamp(10, DateUtil.toSqlTimestamp(now));

            int affected = ps.executeUpdate();
            if (affected == 0) {
                throw new StockManagementException("Creating product failed, no rows affected.",
                        StockManagementException.ERR_DATABASE);
            }
            try (ResultSet generatedKeys = ps.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    product.setProductId(generatedKeys.getInt(1));
                }
            }
            product.setCreatedAt(now);
            product.setUpdatedAt(now);
        } catch (SQLException ex) {
            throw new StockManagementException("Failed to insert product '" + product.getSku() + "'.",
                    StockManagementException.ERR_DATABASE, ex);
        }
    }

    /** Updates every editable field of an existing product (full replace, not a partial patch). */
    public void update(Product product) throws StockManagementException {
        LocalDateTime now = DateUtil.now();
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(SQL_UPDATE)) {

            ps.setString(1, product.getSku());
            ps.setString(2, product.getName());
            ps.setString(3, product.getDescription());
            ps.setInt(4, product.getCategoryId());
            ps.setString(5, product.getUnit());
            ps.setBigDecimal(6, product.getUnitPrice());
            ps.setInt(7, product.getQuantityInStock());
            ps.setInt(8, product.getReorderLevel());
            ps.setTimestamp(9, DateUtil.toSqlTimestamp(now));
            ps.setInt(10, product.getProductId());

            int affected = ps.executeUpdate();
            if (affected == 0) {
                throw new StockManagementException(
                        "Product with id " + product.getProductId() + " does not exist.",
                        StockManagementException.ERR_NOT_FOUND);
            }
            product.setUpdatedAt(now);
        } catch (SQLException ex) {
            throw new StockManagementException("Failed to update product '" + product.getSku() + "'.",
                    StockManagementException.ERR_DATABASE, ex);
        }
    }

    /**
     * Atomically adjusts a product's quantity_in_stock by the given delta
     * (positive for stock entries/additions, negative for stock exits/sales)
     * directly at the database level, avoiding read-modify-write races.
     *
     * @param productId the product to adjust.
     * @param delta     signed quantity change to apply.
     * @throws StockManagementException if the product does not exist.
     */
    public void adjustQuantity(int productId, int delta) throws StockManagementException {
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(SQL_UPDATE_QUANTITY)) {

            ps.setInt(1, delta);
            ps.setTimestamp(2, DateUtil.toSqlTimestamp(DateUtil.now()));
            ps.setInt(3, productId);

            int affected = ps.executeUpdate();
            if (affected == 0) {
                throw new StockManagementException("Product with id " + productId + " does not exist.",
                        StockManagementException.ERR_NOT_FOUND);
            }
        } catch (SQLException ex) {
            throw new StockManagementException(
                    "Failed to adjust stock quantity for product id " + productId + ".",
                    StockManagementException.ERR_DATABASE, ex);
        }
    }

    /** Deletes a product by id. */
    public void delete(int productId) throws StockManagementException {
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(SQL_DELETE)) {

            ps.setInt(1, productId);
            int affected = ps.executeUpdate();
            if (affected == 0) {
                throw new StockManagementException("Product with id " + productId + " does not exist.",
                        StockManagementException.ERR_NOT_FOUND);
            }
        } catch (SQLException ex) {
            throw new StockManagementException("Failed to delete product with id " + productId + ".",
                    StockManagementException.ERR_DATABASE, ex);
        }
    }

    /** Retrieves a single product by its primary key, or null if not found. */
    public Product findById(int productId) throws StockManagementException {
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(SQL_FIND_BY_ID)) {

            ps.setInt(1, productId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? mapRow(rs) : null;
            }
        } catch (SQLException ex) {
            throw new StockManagementException("Failed to fetch product with id " + productId + ".",
                    StockManagementException.ERR_DATABASE, ex);
        }
    }

    /** Retrieves a single product by its unique SKU, or null if not found. */
    public Product findBySku(String sku) throws StockManagementException {
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(SQL_FIND_BY_SKU)) {

            ps.setString(1, sku);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? mapRow(rs) : null;
            }
        } catch (SQLException ex) {
            throw new StockManagementException("Failed to fetch product with SKU '" + sku + "'.",
                    StockManagementException.ERR_DATABASE, ex);
        }
    }

    /** Returns every product, ordered alphabetically by name. */
    public List<Product> findAll() throws StockManagementException {
        List<Product> products = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(SQL_FIND_ALL);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                products.add(mapRow(rs));
            }
        } catch (SQLException ex) {
            throw new StockManagementException("Failed to fetch product list.",
                    StockManagementException.ERR_DATABASE, ex);
        }
        return products;
    }

    /** Searches products whose name or SKU contains the given keyword (case-insensitive substring match). */
    public List<Product> searchByKeyword(String keyword) throws StockManagementException {
        List<Product> products = new ArrayList<>();
        String likePattern = "%" + keyword + "%";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(SQL_SEARCH_BY_NAME)) {

            ps.setString(1, likePattern);
            ps.setString(2, likePattern);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    products.add(mapRow(rs));
                }
            }
        } catch (SQLException ex) {
            throw new StockManagementException("Failed to search products for keyword '" + keyword + "'.",
                    StockManagementException.ERR_DATABASE, ex);
        }
        return products;
    }

    /** Returns every product belonging to the given category. */
    public List<Product> findByCategory(int categoryId) throws StockManagementException {
        List<Product> products = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(SQL_FIND_BY_CATEGORY)) {

            ps.setInt(1, categoryId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    products.add(mapRow(rs));
                }
            }
        } catch (SQLException ex) {
            throw new StockManagementException(
                    "Failed to fetch products for category id " + categoryId + ".",
                    StockManagementException.ERR_DATABASE, ex);
        }
        return products;
    }

    /**
     * Returns every product whose quantity_in_stock has fallen to or below
     * its reorder_level, ordered by the most critically low first. Used by
     * the Alerts and Reports modules.
     */
    public List<Product> findLowStockProducts() throws StockManagementException {
        List<Product> products = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(SQL_FIND_LOW_STOCK);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                products.add(mapRow(rs));
            }
        } catch (SQLException ex) {
            throw new StockManagementException("Failed to fetch low-stock products.",
                    StockManagementException.ERR_DATABASE, ex);
        }
        return products;
    }

    /** Returns true if a product with this SKU already exists. */
    public boolean existsBySku(String sku) throws StockManagementException {
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(SQL_EXISTS_BY_SKU)) {

            ps.setString(1, sku);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() && rs.getInt(1) > 0;
            }
        } catch (SQLException ex) {
            throw new StockManagementException("Failed to check for duplicate SKU '" + sku + "'.",
                    StockManagementException.ERR_DATABASE, ex);
        }
    }

    /** Returns true if a product with this SKU exists on a different product_id (used during edit/update). */
    public boolean existsBySkuExcludingId(String sku, int excludeProductId) throws StockManagementException {
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(SQL_EXISTS_BY_SKU_EXCLUDING_ID)) {

            ps.setString(1, sku);
            ps.setInt(2, excludeProductId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() && rs.getInt(1) > 0;
            }
        } catch (SQLException ex) {
            throw new StockManagementException("Failed to check for duplicate SKU '" + sku + "'.",
                    StockManagementException.ERR_DATABASE, ex);
        }
    }

    private Product mapRow(ResultSet rs) throws SQLException {
        Product product = new Product();
        product.setProductId(rs.getInt("product_id"));
        product.setSku(rs.getString("sku"));
        product.setName(rs.getString("name"));
        product.setDescription(rs.getString("description"));
        product.setCategoryId(rs.getInt("category_id"));
        product.setCategoryName(rs.getString("category_name"));
        product.setUnit(rs.getString("unit"));
        BigDecimal unitPrice = rs.getBigDecimal("unit_price");
        product.setUnitPrice(unitPrice != null ? unitPrice : BigDecimal.ZERO);
        product.setQuantityInStock(rs.getInt("quantity_in_stock"));
        product.setReorderLevel(rs.getInt("reorder_level"));
        product.setCreatedAt(DateUtil.fromSqlTimestamp(rs.getTimestamp("created_at")));
        product.setUpdatedAt(DateUtil.fromSqlTimestamp(rs.getTimestamp("updated_at")));
        return product;
    }
}
