import express from 'express';
import mysql from 'mysql2';
import cors from 'cors';

const app = express();
app.use(cors());
app.use(express.json());

// Connected directly to your exact verified MySQL database configuration
const db = mysql.createConnection({
  host: 'localhost',
  user: 'root',
  password: '12345', 
  database: 'stock_management_db'
});

db.connect(err => {
  if (err) console.error('Database connection failed: ' + err.stack);
  else console.log('[🎉 SUCCESS] Complete Web API Engine connected to MySQL database!');
});

// 1. GLOBAL READ ENDPOINT: FETCH ALL RECORDS ACROSS ALL 6 MODULES
app.get('/api/data', (req, res) => {
  const qProd = 'SELECT product_id as id, sku, name, "General" as category, price, quantity as stock FROM products';
  const qSup = 'SELECT supplier_id as id, name, contact_person as contact, phone, email FROM suppliers';
  const qCats = 'SELECT category_id as id, name, description FROM categories';
  
  // Added remarks and explicit reason variables to the database query arrays
  const qEntries = 'SELECT COALESCE(entry_date, NOW()) as date, (SELECT name FROM products WHERE products.product_id = stock_entries.product_id LIMIT 1) as product, quantity as qty, (SELECT name FROM suppliers WHERE suppliers.supplier_id = stock_entries.supplier_id LIMIT 1) as supplier, remarks FROM stock_entries ORDER BY entry_id DESC';
  const qExits = 'SELECT COALESCE(exit_date, NOW()) as date, (SELECT name FROM products WHERE products.product_id = stock_exits.product_id LIMIT 1) as product, quantity as qty, reason FROM stock_exits ORDER BY exit_id DESC';
  const qAlerts = 'SELECT name as product, quantity as stock, min_threshold as threshold FROM products WHERE quantity <= min_threshold';

  db.query(qProd, (err, products) => {
    db.query(qSup, (err, suppliers) => {
      db.query(qCats, (err, categories) => {
        db.query(qEntries, (err, entries) => {
          db.query(qExits, (err, exits) => {
            db.query(qAlerts, (err, alerts) => {
              res.json({ products, suppliers, categories, entries, exits, alerts });
            });
          });
        });
      });
    });
  });
});


// 2. INSERT LOGISTICS STOCK ENTRY (UPDATED TO CAPTURE REMARKS COLUMN)
app.post('/api/entry', (req, res) => {
  const { product, qty, supplier, remarks } = req.body; // Captures remarks from the webpage input form
  
  const query = 'INSERT INTO stock_entries (product_id, supplier_id, quantity, remarks, entry_date) ' +
                'VALUES ((SELECT product_id FROM products WHERE name = ? LIMIT 1), ' +
                '(SELECT supplier_id FROM suppliers WHERE name = ? LIMIT 1), ?, ?, NOW())';
                
  db.query(query, [product, supplier, qty, remarks || 'Web Entry Ingestion'], (err) => {
    if (err) return res.status(500).json({ error: err.message });
    
    // Dynamically add the numbers to your core product register row
    db.query('UPDATE products SET quantity_in_stock = quantity_in_stock + ? WHERE name = ?', [qty, product], () => {
      res.json({ status: 'success' });
    });
  });
});

// 3. INSERT NEW SUPPLIER VENDOR REGISTER
app.post('/api/supplier', (req, res) => {
  const { name, contact, phone, email, address } = req.body;
  const query = 'INSERT INTO suppliers (name, contact_person, phone, email, address, created_at, updated_at) VALUES (?, ?, ?, ?, ?, NOW(), NOW())';
  db.query(query, [name, contact, phone, email, address], (err) => {
    if (err) return res.status(500).json({ error: err.message });
    res.json({ status: 'success' });
  });
});

// 4. INSERT NEW INVENTORY CATEGORY
app.post('/api/category', (req, res) => {
  const { name, description } = req.body;
  const query = 'INSERT INTO categories (name, description, created_at) VALUES (?, ?, NOW())';
  db.query(query, [name, description], (err) => {
    if (err) return res.status(500).json({ error: err.message });
    res.json({ status: 'success' });
  });
});

// 5. INSERT LOGISTICS STOCK ENTRY
app.post('/api/entry', (req, res) => {
  const { product, qty, supplier } = req.body;
  const query = 'INSERT INTO stock_entries (product_id, supplier_id, quantity, entry_date) VALUES ((SELECT product_id FROM products WHERE name = ? LIMIT 1), (SELECT supplier_id FROM suppliers WHERE name = ? LIMIT 1), ?, NOW())';
  db.query(query, [product, supplier, qty], (err) => {
    if (err) return res.status(500).json({ error: err.message });
    db.query('UPDATE products SET quantity_in_stock = quantity_in_stock + ? WHERE name = ?', [qty, product], () => res.json({ status: 'success' }));
  });
});

// 6. INSERT LOGISTICS STOCK EXIT
app.post('/api/exit', (req, res) => {
  const { product, qty, reason } = req.body;
  const query = 'INSERT INTO stock_exits (product_id, quantity, reason, exit_date) VALUES ((SELECT product_id FROM products WHERE name = ? LIMIT 1), ?, ?, NOW())';
  db.query(query, [product, qty, reason], (err) => {
    if (err) return res.status(500).json({ error: err.message });
    db.query('UPDATE products SET quantity_in_stock = quantity_in_stock - ? WHERE name = ?', [qty, product], () => res.json({ status: 'success' }));
  });
});

app.listen(8080, () => console.log('Full-Stack Web Tunnel Engine running on http://localhost:8080'));
