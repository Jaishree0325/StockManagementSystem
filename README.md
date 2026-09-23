# Stock Management System

A Java Swing desktop application (NetBeans Ant project layout) that helps
organizations maintain accurate records of available stock, incoming
materials, outgoing products, and supplier information.

## Project Status

This is an incremental build. Implemented so far:

| Module                | Status         |
|------------------------|---------------|
| Configuration / Baseline | ✅ Complete |
| Product Module          | ✅ Complete |
| Supplier Module         | ✅ Complete |
| Stock Entry Module      | ⏳ Not yet implemented |
| Stock Exit Module       | ⏳ Not yet implemented |
| Reports Module          | ⏳ Not yet implemented |
| Alerts Module           | ⏳ Not yet implemented |

## Prerequisites

- JDK 17 or later
- Apache Ant (bundled with NetBeans, or install separately)
- MySQL Server 8.x
- MySQL Connector/J JDBC driver (see `lib/README.txt`)

## Setup

1. **Database**: run `database/schema.sql` against your MySQL server. This
   creates the `stock_management_db` schema and the `categories`,
   `products`, and `suppliers` tables, and seeds a default "General"
   category.
2. **JDBC driver**: download MySQL Connector/J and place it at
   `lib/mysql-connector-j.jar` (see `lib/README.txt` for details).
3. **Connection settings**: defaults live in
   `src/com/stockms/common/constants/AppConstants.java`
   (`DB_URL`, `DB_USER`, `DB_PASSWORD`). Edit these to match your local
   MySQL credentials.

## Building & Running

This project opens directly in NetBeans (File → Open Project), or can be
built from the command line with Apache Ant:

```bash
ant clean jar     # produces dist/StockManagementSystem.jar
ant run           # builds (if needed) and launches the application
```

Or run the compiled JAR directly once built:

```bash
java -jar dist/StockManagementSystem.jar
```

## Using the Application

On launch, the main window opens with two tabs:

- **Products** — search, add, edit, and delete products; rows are
  highlighted red when quantity in stock has fallen to or below the
  reorder level. Categories are managed from the "Manage Categories..."
  button (also reachable from within the Add/Edit Product form).
- **Suppliers** — search, add, edit, and delete vendor records.

The **Modules** menu also lists the four modules planned for later steps
(Stock Entry, Stock Exit, Reports, Alerts); their menu items are disabled
until those modules are implemented.

## Architecture

Each module follows the same layered package structure:

```
com.stockms.<module>.model    # POJOs mapped to database rows
com.stockms.<module>.dao      # JDBC data access (PreparedStatement-based)
com.stockms.<module>.service  # Validation + business rules
com.stockms.<module>.ui       # Swing panels/dialogs
```

Shared infrastructure lives in `com.stockms.common`:
`db` (JDBC connection handling), `util` (date/validation helpers),
`exception` (a single application-wide checked exception type), and
`constants` (centralized configuration values).
