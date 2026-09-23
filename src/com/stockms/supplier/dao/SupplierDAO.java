package com.stockms.supplier.dao;

import com.stockms.common.db.DBConnection;
import com.stockms.common.exception.StockManagementException;
import com.stockms.common.util.DateUtil;
import com.stockms.supplier.model.Supplier;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for {@link Supplier}. Handles all raw SQL / JDBC
 * interaction with the `suppliers` table.
 */
public class SupplierDAO {

    private static final String SQL_INSERT =
            "INSERT INTO suppliers (name, contact_person, phone, email, address, created_at, updated_at) "
                    + "VALUES (?, ?, ?, ?, ?, ?, ?)";

    private static final String SQL_UPDATE =
            "UPDATE suppliers SET name = ?, contact_person = ?, phone = ?, email = ?, address = ?, "
                    + "updated_at = ? WHERE supplier_id = ?";

    private static final String SQL_DELETE =
            "DELETE FROM suppliers WHERE supplier_id = ?";

    private static final String SQL_FIND_BY_ID =
            "SELECT supplier_id, name, contact_person, phone, email, address, created_at, updated_at "
                    + "FROM suppliers WHERE supplier_id = ?";

    private static final String SQL_FIND_ALL =
            "SELECT supplier_id, name, contact_person, phone, email, address, created_at, updated_at "
                    + "FROM suppliers ORDER BY name ASC";

    private static final String SQL_SEARCH =
            "SELECT supplier_id, name, contact_person, phone, email, address, created_at, updated_at "
                    + "FROM suppliers WHERE name LIKE ? OR contact_person LIKE ? OR email LIKE ? "
                    + "ORDER BY name ASC";

    private static final String SQL_EXISTS_BY_NAME =
            "SELECT COUNT(*) FROM suppliers WHERE LOWER(name) = LOWER(?)";

    private static final String SQL_EXISTS_BY_NAME_EXCLUDING_ID =
            "SELECT COUNT(*) FROM suppliers WHERE LOWER(name) = LOWER(?) AND supplier_id <> ?";

    private static final String SQL_COUNT_STOCK_ENTRIES_FOR_SUPPLIER =
            "SELECT COUNT(*) FROM stock_entries WHERE supplier_id = ?";

    /** Inserts a new supplier and populates the generated supplier_id back onto the given object. */
    public void insert(Supplier supplier) throws StockManagementException {
        LocalDateTime now = DateUtil.now();
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(SQL_INSERT, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, supplier.getName());
            ps.setString(2, supplier.getContactPerson());
            ps.setString(3, supplier.getPhone());
            ps.setString(4, supplier.getEmail());
            ps.setString(5, supplier.getAddress());
            ps.setTimestamp(6, DateUtil.toSqlTimestamp(now));
            ps.setTimestamp(7, DateUtil.toSqlTimestamp(now));

            int affected = ps.executeUpdate();
            if (affected == 0) {
                throw new StockManagementException("Creating supplier failed, no rows affected.",
                        StockManagementException.ERR_DATABASE);
            }
            try (ResultSet generatedKeys = ps.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    supplier.setSupplierId(generatedKeys.getInt(1));
                }
            }
            supplier.setCreatedAt(now);
            supplier.setUpdatedAt(now);
        } catch (SQLException ex) {
            throw new StockManagementException("Failed to insert supplier '" + supplier.getName() + "'.",
                    StockManagementException.ERR_DATABASE, ex);
        }
    }

    /** Updates every editable field of an existing supplier. */
    public void update(Supplier supplier) throws StockManagementException {
        LocalDateTime now = DateUtil.now();
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(SQL_UPDATE)) {

            ps.setString(1, supplier.getName());
            ps.setString(2, supplier.getContactPerson());
            ps.setString(3, supplier.getPhone());
            ps.setString(4, supplier.getEmail());
            ps.setString(5, supplier.getAddress());
            ps.setTimestamp(6, DateUtil.toSqlTimestamp(now));
            ps.setInt(7, supplier.getSupplierId());

            int affected = ps.executeUpdate();
            if (affected == 0) {
                throw new StockManagementException(
                        "Supplier with id " + supplier.getSupplierId() + " does not exist.",
                        StockManagementException.ERR_NOT_FOUND);
            }
            supplier.setUpdatedAt(now);
        } catch (SQLException ex) {
            throw new StockManagementException("Failed to update supplier '" + supplier.getName() + "'.",
                    StockManagementException.ERR_DATABASE, ex);
        }
    }

    /**
     * Deletes a supplier by id.
     *
     * @throws StockManagementException with ERR_BUSINESS_RULE if stock entry
     *         records still reference this supplier (referential integrity
     *         guard performed at the application level).
     */
    public void delete(int supplierId) throws StockManagementException {
        try (Connection conn = DBConnection.getConnection()) {
            try (PreparedStatement countPs = conn.prepareStatement(SQL_COUNT_STOCK_ENTRIES_FOR_SUPPLIER)) {
                countPs.setInt(1, supplierId);
                try (ResultSet rs = countPs.executeQuery()) {
                    if (rs.next() && rs.getInt(1) > 0) {
                        throw new StockManagementException(
                                "Cannot delete supplier: " + rs.getInt(1)
                                        + " stock entry record(s) reference this supplier.",
                                StockManagementException.ERR_BUSINESS_RULE);
                    }
                }
            } catch (SQLException lookupFailure) {
                // The stock_entries table does not exist yet at this stage of the project
                // (it is introduced in the Stock Entry module). Treat that specific case
                // as "no dependent records" rather than failing the delete.
                if (!isMissingTableError(lookupFailure)) {
                    throw lookupFailure;
                }
            }
            try (PreparedStatement deletePs = conn.prepareStatement(SQL_DELETE)) {
                deletePs.setInt(1, supplierId);
                int affected = deletePs.executeUpdate();
                if (affected == 0) {
                    throw new StockManagementException("Supplier with id " + supplierId + " does not exist.",
                            StockManagementException.ERR_NOT_FOUND);
                }
            }
        } catch (SQLException ex) {
            throw new StockManagementException("Failed to delete supplier with id " + supplierId + ".",
                    StockManagementException.ERR_DATABASE, ex);
        }
    }

    /** Retrieves a single supplier by its primary key, or null if not found. */
    public Supplier findById(int supplierId) throws StockManagementException {
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(SQL_FIND_BY_ID)) {

            ps.setInt(1, supplierId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? mapRow(rs) : null;
            }
        } catch (SQLException ex) {
            throw new StockManagementException("Failed to fetch supplier with id " + supplierId + ".",
                    StockManagementException.ERR_DATABASE, ex);
        }
    }

    /** Returns every supplier, ordered alphabetically by name. */
    public List<Supplier> findAll() throws StockManagementException {
        List<Supplier> suppliers = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(SQL_FIND_ALL);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                suppliers.add(mapRow(rs));
            }
        } catch (SQLException ex) {
            throw new StockManagementException("Failed to fetch supplier list.",
                    StockManagementException.ERR_DATABASE, ex);
        }
        return suppliers;
    }

    /** Searches suppliers whose name, contact person, or e-mail contains the given keyword. */
    public List<Supplier> searchByKeyword(String keyword) throws StockManagementException {
        List<Supplier> suppliers = new ArrayList<>();
        String likePattern = "%" + keyword + "%";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(SQL_SEARCH)) {

            ps.setString(1, likePattern);
            ps.setString(2, likePattern);
            ps.setString(3, likePattern);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    suppliers.add(mapRow(rs));
                }
            }
        } catch (SQLException ex) {
            throw new StockManagementException("Failed to search suppliers for keyword '" + keyword + "'.",
                    StockManagementException.ERR_DATABASE, ex);
        }
        return suppliers;
    }

    /** Returns true if a supplier with this name already exists (case-insensitive). */
    public boolean existsByName(String name) throws StockManagementException {
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(SQL_EXISTS_BY_NAME)) {

            ps.setString(1, name);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() && rs.getInt(1) > 0;
            }
        } catch (SQLException ex) {
            throw new StockManagementException("Failed to check for duplicate supplier name '" + name + "'.",
                    StockManagementException.ERR_DATABASE, ex);
        }
    }

    /** Returns true if a supplier with this name exists on a different supplier_id (used during edit/update). */
    public boolean existsByNameExcludingId(String name, int excludeSupplierId) throws StockManagementException {
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(SQL_EXISTS_BY_NAME_EXCLUDING_ID)) {

            ps.setString(1, name);
            ps.setInt(2, excludeSupplierId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() && rs.getInt(1) > 0;
            }
        } catch (SQLException ex) {
            throw new StockManagementException("Failed to check for duplicate supplier name '" + name + "'.",
                    StockManagementException.ERR_DATABASE, ex);
        }
    }

    /** MySQL error code 1146 = "Table ... doesn't exist". SQLState "42S02" is the standard equivalent. */
    private boolean isMissingTableError(SQLException ex) {
        return ex.getErrorCode() == 1146 || "42S02".equals(ex.getSQLState());
    }

    private Supplier mapRow(ResultSet rs) throws SQLException {
        Supplier supplier = new Supplier();
        supplier.setSupplierId(rs.getInt("supplier_id"));
        supplier.setName(rs.getString("name"));
        supplier.setContactPerson(rs.getString("contact_person"));
        supplier.setPhone(rs.getString("phone"));
        supplier.setEmail(rs.getString("email"));
        supplier.setAddress(rs.getString("address"));
        supplier.setCreatedAt(DateUtil.fromSqlTimestamp(rs.getTimestamp("created_at")));
        supplier.setUpdatedAt(DateUtil.fromSqlTimestamp(rs.getTimestamp("updated_at")));
        return supplier;
    }
}
