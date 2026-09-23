package com.stockms.product.service;

import com.stockms.common.constants.AppConstants;
import com.stockms.common.exception.StockManagementException;
import com.stockms.common.util.ValidationUtil;
import com.stockms.product.dao.CategoryDAO;
import com.stockms.product.dao.ProductDAO;
import com.stockms.product.model.Category;
import com.stockms.product.model.Product;

import java.math.BigDecimal;
import java.util.List;

/**
 * Business/service layer for {@link Product}. Owns all validation rules
 * (required fields, length limits, uniqueness of SKU, non-negative
 * quantities/prices, category existence) so that neither the DAO nor the
 * Swing UI layer needs to duplicate that logic.
 *
 * Also exposes {@link #increaseStock(int, int)} and
 * {@link #decreaseStock(int, int)}, which the Stock Entry and Stock Exit
 * modules call to keep {@code quantity_in_stock} accurate.
 */
public class ProductService {

    private final ProductDAO productDAO;
    private final CategoryDAO categoryDAO;

    public ProductService() {
        this.productDAO = new ProductDAO();
        this.categoryDAO = new CategoryDAO();
    }

    /** Constructor allowing DAO injection, useful for unit testing with mocks. */
    public ProductService(ProductDAO productDAO, CategoryDAO categoryDAO) {
        this.productDAO = productDAO;
        this.categoryDAO = categoryDAO;
    }

    /**
     * Validates and creates a new product.
     *
     * @throws StockManagementException ERR_VALIDATION for bad input,
     *         ERR_DUPLICATE if the SKU is taken, ERR_NOT_FOUND if the
     *         category does not exist.
     */
    public Product addProduct(String sku, String name, String description, int categoryId, String unit,
                               BigDecimal unitPrice, int quantityInStock, int reorderLevel)
            throws StockManagementException {

        validateCommonFields(sku, name, unitPrice, quantityInStock, reorderLevel);

        if (categoryDAO.findById(categoryId) == null) {
            throw new StockManagementException("Selected category does not exist.",
                    StockManagementException.ERR_NOT_FOUND);
        }
        if (productDAO.existsBySku(sku.trim())) {
            throw new StockManagementException("A product with SKU '" + sku.trim() + "' already exists.",
                    StockManagementException.ERR_DUPLICATE);
        }

        String resolvedUnit = ValidationUtil.isEmpty(unit) ? AppConstants.DEFAULT_UNIT : unit.trim();

        Product product = new Product(sku.trim(), name.trim(),
                description == null ? "" : description.trim(), categoryId, resolvedUnit,
                unitPrice, quantityInStock, reorderLevel);
        productDAO.insert(product);
        return product;
    }

    /**
     * Validates and updates every editable field of an existing product.
     *
     * @throws StockManagementException ERR_VALIDATION, ERR_DUPLICATE,
     *         ERR_NOT_FOUND as appropriate.
     */
    public void updateProduct(int productId, String sku, String name, String description, int categoryId,
                               String unit, BigDecimal unitPrice, int quantityInStock, int reorderLevel)
            throws StockManagementException {

        validateCommonFields(sku, name, unitPrice, quantityInStock, reorderLevel);

        Product existing = productDAO.findById(productId);
        if (existing == null) {
            throw new StockManagementException("Product with id " + productId + " was not found.",
                    StockManagementException.ERR_NOT_FOUND);
        }
        if (categoryDAO.findById(categoryId) == null) {
            throw new StockManagementException("Selected category does not exist.",
                    StockManagementException.ERR_NOT_FOUND);
        }
        if (productDAO.existsBySkuExcludingId(sku.trim(), productId)) {
            throw new StockManagementException("A product with SKU '" + sku.trim() + "' already exists.",
                    StockManagementException.ERR_DUPLICATE);
        }

        String resolvedUnit = ValidationUtil.isEmpty(unit) ? AppConstants.DEFAULT_UNIT : unit.trim();

        existing.setSku(sku.trim());
        existing.setName(name.trim());
        existing.setDescription(description == null ? "" : description.trim());
        existing.setCategoryId(categoryId);
        existing.setUnit(resolvedUnit);
        existing.setUnitPrice(unitPrice);
        existing.setQuantityInStock(quantityInStock);
        existing.setReorderLevel(reorderLevel);
        productDAO.update(existing);
    }

    /** Deletes a product by id. */
    public void deleteProduct(int productId) throws StockManagementException {
        productDAO.delete(productId);
    }

    /** Retrieves a product by id, throwing ERR_NOT_FOUND if it does not exist. */
    public Product getProduct(int productId) throws StockManagementException {
        Product product = productDAO.findById(productId);
        if (product == null) {
            throw new StockManagementException("Product with id " + productId + " was not found.",
                    StockManagementException.ERR_NOT_FOUND);
        }
        return product;
    }

    /** Retrieves a product by its SKU, throwing ERR_NOT_FOUND if it does not exist. */
    public Product getProductBySku(String sku) throws StockManagementException {
        Product product = productDAO.findBySku(sku);
        if (product == null) {
            throw new StockManagementException("Product with SKU '" + sku + "' was not found.",
                    StockManagementException.ERR_NOT_FOUND);
        }
        return product;
    }

    /** Returns every product, alphabetically sorted by name. */
    public List<Product> getAllProducts() throws StockManagementException {
        return productDAO.findAll();
    }

    /** Searches products by a free-text keyword against name and SKU. Empty keyword returns all products. */
    public List<Product> searchProducts(String keyword) throws StockManagementException {
        if (ValidationUtil.isEmpty(keyword)) {
            return productDAO.findAll();
        }
        return productDAO.searchByKeyword(keyword.trim());
    }

    /** Returns every product in the given category. */
    public List<Product> getProductsByCategory(int categoryId) throws StockManagementException {
        return productDAO.findByCategory(categoryId);
    }

    /** Returns every product currently at or below its reorder level. Used by the Alerts module. */
    public List<Product> getLowStockProducts() throws StockManagementException {
        return productDAO.findLowStockProducts();
    }

    /**
     * Increases a product's stock quantity. Called by the Stock Entry
     * module when incoming materials are logged.
     *
     * @throws StockManagementException ERR_VALIDATION if quantity is not
     *         positive, ERR_NOT_FOUND if the product does not exist.
     */
    public void increaseStock(int productId, int quantity) throws StockManagementException {
        if (quantity <= 0) {
            throw new StockManagementException("Quantity to add must be a positive number.",
                    StockManagementException.ERR_VALIDATION);
        }
        productDAO.adjustQuantity(productId, quantity);
    }

    /**
     * Decreases a product's stock quantity. Called by the Stock Exit module
     * when outgoing products/sales/usage are logged.
     *
     * @throws StockManagementException ERR_VALIDATION if quantity is not
     *         positive, ERR_BUSINESS_RULE if there is insufficient stock on
     *         hand, ERR_NOT_FOUND if the product does not exist.
     */
    public void decreaseStock(int productId, int quantity) throws StockManagementException {
        if (quantity <= 0) {
            throw new StockManagementException("Quantity to remove must be a positive number.",
                    StockManagementException.ERR_VALIDATION);
        }
        Product product = getProduct(productId);
        if (product.getQuantityInStock() < quantity) {
            throw new StockManagementException(
                    "Insufficient stock for '" + product.getName() + "': available "
                            + product.getQuantityInStock() + ", requested " + quantity + ".",
                    StockManagementException.ERR_BUSINESS_RULE);
        }
        productDAO.adjustQuantity(productId, -quantity);
    }

    private void validateCommonFields(String sku, String name, BigDecimal unitPrice,
                                       int quantityInStock, int reorderLevel) throws StockManagementException {
        if (!ValidationUtil.isValidSku(sku)) {
            throw new StockManagementException(
                    "SKU must be 3-" + AppConstants.MAX_SKU_LENGTH
                            + " alphanumeric characters (hyphens/underscores allowed after the first character).",
                    StockManagementException.ERR_VALIDATION);
        }
        if (ValidationUtil.isEmpty(name) || !ValidationUtil.isValidLength(name, AppConstants.MAX_NAME_LENGTH)) {
            throw new StockManagementException(
                    "Product name is required and cannot exceed " + AppConstants.MAX_NAME_LENGTH + " characters.",
                    StockManagementException.ERR_VALIDATION);
        }
        if (unitPrice == null || unitPrice.compareTo(BigDecimal.ZERO) < 0) {
            throw new StockManagementException("Unit price cannot be negative.",
                    StockManagementException.ERR_VALIDATION);
        }
        if (quantityInStock < AppConstants.MIN_STOCK_QUANTITY) {
            throw new StockManagementException("Quantity in stock cannot be negative.",
                    StockManagementException.ERR_VALIDATION);
        }
        if (reorderLevel < 0) {
            throw new StockManagementException("Reorder level cannot be negative.",
                    StockManagementException.ERR_VALIDATION);
        }
    }
}
