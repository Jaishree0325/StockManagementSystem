package com.stockms.supplier.service;

import com.stockms.common.constants.AppConstants;
import com.stockms.common.exception.StockManagementException;
import com.stockms.common.util.ValidationUtil;
import com.stockms.supplier.dao.SupplierDAO;
import com.stockms.supplier.model.Supplier;

import java.util.List;

/**
 * Business/service layer for {@link Supplier}. Validates input before
 * delegating persistence to {@link SupplierDAO}.
 */
public class SupplierService {

    private final SupplierDAO supplierDAO;

    public SupplierService() {
        this.supplierDAO = new SupplierDAO();
    }

    /** Constructor allowing DAO injection, useful for unit testing with mocks. */
    public SupplierService(SupplierDAO supplierDAO) {
        this.supplierDAO = supplierDAO;
    }

    /**
     * Validates and creates a new supplier.
     *
     * @throws StockManagementException ERR_VALIDATION for bad input,
     *         ERR_DUPLICATE if the name is already taken.
     */
    public Supplier addSupplier(String name, String contactPerson, String phone, String email, String address)
            throws StockManagementException {

        validateFields(name, contactPerson, phone, email, address);

        if (supplierDAO.existsByName(name.trim())) {
            throw new StockManagementException("A supplier named '" + name.trim() + "' already exists.",
                    StockManagementException.ERR_DUPLICATE);
        }

        Supplier supplier = new Supplier(name.trim(), nullSafeTrim(contactPerson), nullSafeTrim(phone),
                nullSafeTrim(email), nullSafeTrim(address));
        supplierDAO.insert(supplier);
        return supplier;
    }

    /**
     * Validates and updates an existing supplier.
     *
     * @throws StockManagementException ERR_VALIDATION, ERR_DUPLICATE, or
     *         ERR_NOT_FOUND as appropriate.
     */
    public void updateSupplier(int supplierId, String name, String contactPerson, String phone, String email,
                                String address) throws StockManagementException {

        validateFields(name, contactPerson, phone, email, address);

        Supplier existing = supplierDAO.findById(supplierId);
        if (existing == null) {
            throw new StockManagementException("Supplier with id " + supplierId + " was not found.",
                    StockManagementException.ERR_NOT_FOUND);
        }
        if (supplierDAO.existsByNameExcludingId(name.trim(), supplierId)) {
            throw new StockManagementException("A supplier named '" + name.trim() + "' already exists.",
                    StockManagementException.ERR_DUPLICATE);
        }

        existing.setName(name.trim());
        existing.setContactPerson(nullSafeTrim(contactPerson));
        existing.setPhone(nullSafeTrim(phone));
        existing.setEmail(nullSafeTrim(email));
        existing.setAddress(nullSafeTrim(address));
        supplierDAO.update(existing);
    }

    /** Deletes a supplier. Delegates the referential-integrity guard to {@link SupplierDAO}. */
    public void deleteSupplier(int supplierId) throws StockManagementException {
        supplierDAO.delete(supplierId);
    }

    /** Retrieves a supplier by id, throwing ERR_NOT_FOUND if it does not exist. */
    public Supplier getSupplier(int supplierId) throws StockManagementException {
        Supplier supplier = supplierDAO.findById(supplierId);
        if (supplier == null) {
            throw new StockManagementException("Supplier with id " + supplierId + " was not found.",
                    StockManagementException.ERR_NOT_FOUND);
        }
        return supplier;
    }

    /** Returns every supplier, alphabetically sorted by name. */
    public List<Supplier> getAllSuppliers() throws StockManagementException {
        return supplierDAO.findAll();
    }

    /** Searches suppliers by a free-text keyword against name, contact person, and e-mail. */
    public List<Supplier> searchSuppliers(String keyword) throws StockManagementException {
        if (ValidationUtil.isEmpty(keyword)) {
            return supplierDAO.findAll();
        }
        return supplierDAO.searchByKeyword(keyword.trim());
    }

    private void validateFields(String name, String contactPerson, String phone, String email, String address)
            throws StockManagementException {

        if (ValidationUtil.isEmpty(name) || !ValidationUtil.isValidLength(name, AppConstants.MAX_NAME_LENGTH)) {
            throw new StockManagementException(
                    "Supplier name is required and cannot exceed " + AppConstants.MAX_NAME_LENGTH + " characters.",
                    StockManagementException.ERR_VALIDATION);
        }
        if (ValidationUtil.isNotEmpty(contactPerson)
                && !ValidationUtil.isValidLength(contactPerson, AppConstants.MAX_NAME_LENGTH)) {
            throw new StockManagementException(
                    "Contact person cannot exceed " + AppConstants.MAX_NAME_LENGTH + " characters.",
                    StockManagementException.ERR_VALIDATION);
        }
        if (ValidationUtil.isNotEmpty(phone) && !ValidationUtil.isValidPhone(phone)) {
            throw new StockManagementException(
                    "Phone number format is invalid. Use digits, spaces, '+', '-', or parentheses only.",
                    StockManagementException.ERR_VALIDATION);
        }
        if (ValidationUtil.isNotEmpty(email) && !ValidationUtil.isValidEmail(email)) {
            throw new StockManagementException("E-mail address format is invalid.",
                    StockManagementException.ERR_VALIDATION);
        }
        if (ValidationUtil.isNotEmpty(address)
                && !ValidationUtil.isValidLength(address, AppConstants.MAX_ADDRESS_LENGTH)) {
            throw new StockManagementException(
                    "Address cannot exceed " + AppConstants.MAX_ADDRESS_LENGTH + " characters.",
                    StockManagementException.ERR_VALIDATION);
        }
    }

    private String nullSafeTrim(String value) {
        return value == null ? "" : value.trim();
    }
}
