package com.stockms.product.service;

import com.stockms.common.constants.AppConstants;
import com.stockms.common.exception.StockManagementException;
import com.stockms.common.util.ValidationUtil;
import com.stockms.product.dao.CategoryDAO;
import com.stockms.product.model.Category;

import java.util.List;

/**
 * Business/service layer for {@link Category}. Validates input before
 * delegating persistence to {@link CategoryDAO}, so the UI layer never has
 * to know about SQL or duplicate-key checks directly.
 */
public class CategoryService {

    private final CategoryDAO categoryDAO;

    public CategoryService() {
        this.categoryDAO = new CategoryDAO();
    }

    /** Constructor allowing DAO injection, useful for unit testing with mocks. */
    public CategoryService(CategoryDAO categoryDAO) {
        this.categoryDAO = categoryDAO;
    }

    /**
     * Validates and creates a new category.
     *
     * @throws StockManagementException with ERR_VALIDATION if the name is
     *         missing/too long, or ERR_DUPLICATE if the name is already taken.
     */
    public Category addCategory(String name, String description) throws StockManagementException {
        validateName(name);
        if (description != null && description.length() > AppConstants.MAX_DESCRIPTION_LENGTH) {
            throw new StockManagementException(
                    "Description cannot exceed " + AppConstants.MAX_DESCRIPTION_LENGTH + " characters.",
                    StockManagementException.ERR_VALIDATION);
        }
        if (categoryDAO.existsByName(name.trim())) {
            throw new StockManagementException(
                    "A category named '" + name.trim() + "' already exists.",
                    StockManagementException.ERR_DUPLICATE);
        }
        Category category = new Category(name.trim(), description == null ? "" : description.trim());
        categoryDAO.insert(category);
        return category;
    }

    /**
     * Validates and updates an existing category.
     *
     * @throws StockManagementException with ERR_VALIDATION, ERR_DUPLICATE, or
     *         ERR_NOT_FOUND as appropriate.
     */
    public void updateCategory(int categoryId, String name, String description) throws StockManagementException {
        validateName(name);
        if (description != null && description.length() > AppConstants.MAX_DESCRIPTION_LENGTH) {
            throw new StockManagementException(
                    "Description cannot exceed " + AppConstants.MAX_DESCRIPTION_LENGTH + " characters.",
                    StockManagementException.ERR_VALIDATION);
        }
        Category existing = categoryDAO.findById(categoryId);
        if (existing == null) {
            throw new StockManagementException("Category with id " + categoryId + " was not found.",
                    StockManagementException.ERR_NOT_FOUND);
        }
        if (categoryDAO.existsByNameExcludingId(name.trim(), categoryId)) {
            throw new StockManagementException(
                    "A category named '" + name.trim() + "' already exists.",
                    StockManagementException.ERR_DUPLICATE);
        }
        existing.setName(name.trim());
        existing.setDescription(description == null ? "" : description.trim());
        categoryDAO.update(existing);
    }

    /**
     * Deletes a category. Delegates the referential-integrity guard (no
     * products still assigned to this category) to {@link CategoryDAO}.
     */
    public void deleteCategory(int categoryId) throws StockManagementException {
        categoryDAO.delete(categoryId);
    }

    /** Retrieves a category by id, throwing ERR_NOT_FOUND if it does not exist. */
    public Category getCategory(int categoryId) throws StockManagementException {
        Category category = categoryDAO.findById(categoryId);
        if (category == null) {
            throw new StockManagementException("Category with id " + categoryId + " was not found.",
                    StockManagementException.ERR_NOT_FOUND);
        }
        return category;
    }

    /** Returns every category, alphabetically sorted. */
    public List<Category> getAllCategories() throws StockManagementException {
        return categoryDAO.findAll();
    }

    private void validateName(String name) throws StockManagementException {
        if (ValidationUtil.isEmpty(name)) {
            throw new StockManagementException("Category name is required.",
                    StockManagementException.ERR_VALIDATION);
        }
        if (!ValidationUtil.isValidLength(name, AppConstants.MAX_NAME_LENGTH)) {
            throw new StockManagementException(
                    "Category name cannot exceed " + AppConstants.MAX_NAME_LENGTH + " characters.",
                    StockManagementException.ERR_VALIDATION);
        }
    }
}
