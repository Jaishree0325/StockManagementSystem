📦 Enterprise Full-Stack Stock Management System

    A dual-engine logistics infrastructure integrating a reactive Vite + React Web Dashboard and a high-performance Java Desktop Application Core. The entire architecture operates over a secure, decentralized network topology utilizing a lightweight Node.js Data Tunnel Gateway for immediate, live bidirectional synchronization with a central MySQL Relational Database Engine.

    🎨 System Architecture Flow Matrixtext

                    ┌────────────────────────────────────────────────────────┐
                    │                 CLIENT USER INTERFACES                 │
                    └───────────────────────────┬────────────────────────────┘
                                                │
                         ┌──────────────────────┴──────────────────────┐
                         ▼                                             ▼
                ┌──────────────────┐                         ┌──────────────────┐
                │  React Web UI    │                         │ Java Desktop App │
                │ (Vite Ecosystem) │                         │  (Swing Toolkit) │
                │  localhost:5173  │                         │   Native Frame   │
                └────────┬─────────┘                         └─────────┬────────┘
                         │                                             │
                         │ (JSON REST Requests)                        │ (Direct JDBC)
                         ▼                                             │
                ┌──────────────────┐                                   │
                │  Node.js Tunnel  │                                   │
                │ (Express Engine) │                                   │
                │  localhost:8080  │                                   │
                └────────┬─────────┘                                   │
                         │                                             │
                         │ (Asynchronous `mysql2` Handshake)           │
                         └──────────────────────┬──────────────────────┘
                                                │
                                                ▼
                                    ┌──────────────────────┐
                                    │ MySQL Database Server│
                                    │(stock_management_db) │
                                    │      Port: 3306      │
                                    └──────────────────────┘

    ✨ Production Core Features
        🔒 Secure Dual-Layer Authentication Gating: Features a secure front-end modal authentication lock screen checking designated administrative keys (admin / 123334) before generating persistent routing access.
        
        🌐 Modern Responsive Web Viewport: Built completely with high-fidelity, uncompressed React components, hosting a clean navigation top-bar and interactive metric summary data cards for quick business management visibility.
        
        💾 Bidirectional MySQL Hot Mirroring: Integrated with asynchronous polling intervals looping every 3,000ms. Any entry logged inside the browser or inside MySQL Workbench maps instantly into the database, updates the tracking total columns, and refreshes the browser counters live.
        
        🚨 Automatic Real-Time Alert Analytics: Enforces relational query boundary scans frequently checking available quantities against pre-configured item minimum limits. If an asset falls into a threshold breach, the UI overrides default rendering and flags row items instantly in a red hazard tint.
        
        💻 Native Desktop Standalone Framework: Includes a companion multi-module Java application running alongside the web components. Uses decoupled DAO patterns to deliver standard warehouse ledger capabilities even when unlinked from the front-end router.


    🛠️ Comprehensive Tech Stack

        Frontend & Layout Ecosystem
            Framework Core: React.js v18+ (Single Page Application Layout Engine)
            Compilation Wrapper: Vite Bundler Core (Hot Module Replacement Architecture)
            Styling Architecture: Declarative Embedded Inline Structural Layout Components
            
        Backend API Database Tunnel
            Runtime Environment: Node.js
            Routing Framework: ExpressJS HTTP Web Routing Framework
            Relational Connector: mysql2 Event-Driven Server Pipeline Driver
        
        Native Desktop Layer
            Language Environment: Java SE 8 Runtime Classpath Parameters
            Graphical Framework: Java Swing View Engine Toolkit
            Build Core Framework: Apache Ant & Classpath Descriptor Layout Rules
            
        Persistent Engine
            Relational Database Server: MySQL Server Engine v8.0 / v8.4 Relational Storage Schema
            
            
    🗄️ Relational Table Mapping Blueprint
        The persistence schema hosted inside stock_management_db uses hard relational boundaries mapped across six key tracking modules:
            categories: Formulates master indexing parent divisions for warehouse items.
            
            products: Stores master SKU assets, available volumes, virtual backward-compatible naming configurations, and structural pricing/unit costs.
            
            suppliers: Holds active vendor profile maps, corporate emails, and contact records.
            
            stock_entries: High-integrity append-only audit register capturing inward deliveries linked to unique table primary keys, along with clear logging transaction remarks.
            
            stock_exits: Logs outward warehouse departures, distribution volumes, and operational exit reasons.
            
            
    🚀 Quick Setup Installation Steps
        Prerequisites
            Ensure your development machine has the following tools installed:
                Node.js Runtime Engine (LTS Package, configured on system variables)
                Java Development Kit (JDK 8)
                MySQL Relational Database Instance (Configured on Port 3306)
                
                1. Initialize the Relational Databases Schema
                    Log into your local MySQL manager (e.g., MySQL Workbench) and execute the master initialization script:
                        
                        CREATE DATABASE IF NOT EXISTS stock_management_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
    USE stock_management_db;
                        
                        CREATE TABLE IF NOT EXISTS categories (category_id INT PRIMARY KEY AUTO_INCREMENT, name VARCHAR(100) NOT NULL UNIQUE, description VARCHAR(500), created_at DATETIME NOT NULL) ENGINE=InnoDB;
                        
                        CREATE TABLE IF NOT EXISTS products (product_id INT PRIMARY KEY AUTO_INCREMENT, sku VARCHAR(30) NOT NULL UNIQUE, name VARCHAR(100) NOT NULL, description VARCHAR(500), category_id INT NOT NULL, unit VARCHAR(20) NOT NULL DEFAULT 'pcs', unit_price DECIMAL(12,2) NOT NULL DEFAULT 0.00, quantity_in_stock INT NOT NULL DEFAULT 0, reorder_level INT NOT NULL DEFAULT 10, created_at DATETIME NOT NULL, updated_at DATETIME NOT NULL, CONSTRAINT fk_products_category FOREIGN KEY (category_id) REFERENCES categories(category_id) ON DELETE RESTRICT ON UPDATE CASCADE) ENGINE=InnoDB;
                        
                        CREATE TABLE IF NOT EXISTS suppliers (supplier_id INT PRIMARY KEY AUTO_INCREMENT, name VARCHAR(100) NOT NULL, contact_person VARCHAR(100), phone VARCHAR(20), email VARCHAR(100), address VARCHAR(250), created_at DATETIME NOT NULL, updated_at DATETIME NOT NULL) ENGINE=InnoDB;
                        
                        CREATE TABLE IF NOT EXISTS stock_entries (entry_id INT PRIMARY KEY AUTO_INCREMENT, product_id INT NOT NULL, supplier_id INT NOT NULL, quantity INT NOT NULL, entry_date DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP, remarks VARCHAR(500), CONSTRAINT fk_entries_product FOREIGN KEY (product_id) REFERENCES products(product_id) ON DELETE CASCADE ON UPDATE CASCADE, CONSTRAINT fk_entries_supplier FOREIGN KEY (supplier_id) REFERENCES suppliers(supplier_id) ON DELETE RESTRICT ON UPDATE CASCADE) ENGINE=InnoDB;
                        
                        CREATE TABLE IF NOT EXISTS stock_exits (exit_id INT PRIMARY KEY AUTO_INCREMENT, product_id INT NOT NULL, quantity INT NOT NULL, exit_date DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP, reason VARCHAR(255), CONSTRAINT fk_exits_product FOREIGN KEY (product_id) REFERENCES products(product_id) ON DELETE CASCADE ON UPDATE CASCADE) ENGINE=InnoDB;

                        -- Add mirrored mapping identifiers
                        ALTER TABLE products ADD COLUMN price DECIMAL(12,2) GENERATED ALWAYS AS (unit_price) STORED;
                        
                        ALTER TABLE products ADD COLUMN quantity INT GENERATED ALWAYS AS (quantity_in_stock) STORED;
                        
                        ALTER TABLE products ADD COLUMN min_threshold INT GENERATED ALWAYS AS (reorder_level) STORED;

                2. Configure and Fire Up the API Web Tunnel Gateway
                    Navigate to your web folder, build your packages, and turn on your server:
                        
                        cd frontend
                        npm install
                        node server.js

                    The endpoint channel will launch at http://localhost:8080/, outputting a green confirmation handshake notification confirming a secure connection to your MySQL schema.
                
                3. Deploy the Vite Responsive Front-End Dashboard View
                    In a secondary, separate split command window pane, execute the browser deployment script:
                        
                        cd frontend
                        npm run dev

                    Open your web browser of choice and step into http://localhost:5173/ to authenticate your administrator session and use the real-time full-stack widgets!
                    
                4. Execute the Local Companion Desktop Runner File
                    To boot the desktop application engine frame, head back into the root workspace folder path and execute the automated native Windows runner script:
                        
                        # Double-click via file explorer or run inside Command Prompt
                        run_app.bat
                    
                    Developed as a high-fidelity capstone engineering project for the Infosys Project Submission Framework.