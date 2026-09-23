import React, { useState, useEffect } from 'react';
import Dashboard from './pages/Dashboard';

// --- SECURE SYSTEM AUTHENTICATION MODAL GATE ---
function LoginScreen({ onLoginSuccess }) {
  const [username, setUsername] = useState('admin');
  const [password, setPassword] = useState('');
  const [error, setError] = useState('');

  const handleLoginSubmit = (e) => {
    e.preventDefault();
    if (username === 'admin' && password === '12345') {
      onLoginSuccess();
    } else {
      setError('❌ Invalid Admin Credentials.');
    }
  };

  return (
    <div style={{ minHeight: '100vh', width: '100vw', background: 'radial-gradient(circle at top, #1e293b, #0f172a)', display: 'flex', alignItems: 'center', justifyContent: 'center', fontFamily: 'system-ui, sans-serif', margin: 0, padding: 0 }}>
      <div style={{ backgroundColor: '#ffffff', padding: '40px', borderRadius: '16px', width: '100%', maxWidth: '400px', boxSizing: 'border-box', boxShadow: '0 20px 25px -5px rgba(0,0,0,0.2)' }}>
        <div style={{ textAlign: 'center', marginBottom: '24px' }}>
          <div style={{ fontSize: '48px', marginBottom: '12px' }}>🔒</div>
          <h2 style={{ fontSize: '24px', fontWeight: '800', color: '#0f172a', margin: 0 }}>System Authentication</h2>
        </div>
        {error && <div style={{ color: '#991b1b', background: '#fef2f2', padding: '10px', borderRadius: '6px', marginBottom: '16px', fontSize: '13px' }}>{error}</div>}
        <form onSubmit={handleLoginSubmit} style={{ display: 'flex', flexDirection: 'column', gap: '16px' }}>
          <input type="text" placeholder="Username" value={username} onChange={e => setUsername(e.target.value)} style={{ padding: '12px', borderRadius: '8px', border: '1px solid #cbd5e1' }} required />
          <input type="password" placeholder="Password (123334)" value={password} onChange={e => setPassword(e.target.value)} style={{ padding: '12px', borderRadius: '8px', border: '1px solid #cbd5e1' }} required />
          <button type="submit" style={{ background: '#2563eb', color: '#fff', border: 'none', padding: '14px', borderRadius: '8px', fontWeight: '600', cursor: 'pointer' }}>Verify & Unlock Dashboard</button>
        </form>
      </div>
    </div>
  );
}

// --- MASTER APPLICATION WRAPPER CORE ROUTER ---
export default function App() {
  const [isAuthenticated, setIsAuthenticated] = useState(false);
  const [currentView, setCurrentView] = useState('dashboard');
  
  const [dbData, setDbData] = useState({ products: [], suppliers: [], categories: [] });

  const fetchDbData = () => {
    fetch('http://localhost:8080/api/data')
      .then(res => res.json())
      .then(data => {
        setDbData({
          products: data.products || [],
          suppliers: data.suppliers || [],
          categories: data.categories || []
        });
      })
      .catch(err => console.error("Database connection check active: ", err));
  };

  useEffect(() => {
    if (isAuthenticated) {
      fetchDbData();
      const loop = setInterval(fetchDbData, 3000);
      return () => clearInterval(loop);
    }
  }, [isAuthenticated]);

  if (!isAuthenticated) {
    return <LoginScreen onLoginSuccess={() => setIsAuthenticated(true)} />;
  }

  return (
    <div style={{ minHeight: '100vh', backgroundColor: '#f8fafc', display: 'flex', flexDirection: 'column', fontFamily: 'system-ui, sans-serif' }}>
      
      {/* Navbar Panel */}
      <nav style={{ backgroundColor: '#0f172a', color: '#ffffff', display: 'flex', justifyContent: 'space-between', alignItems: 'center', height: '64px', padding: '0 32px', position: 'sticky', top: 0, zIndex: 100 }}>
        <div style={{ display: 'flex', alignItems: 'center', gap: '32px' }}>
          <div style={{ fontWeight: '800', fontSize: '20px', color: '#38bdf8' }}>StockMS Web</div>
          <div style={{ display: 'flex', gap: '16px' }}>
            <button onClick={() => setCurrentView('dashboard')} style={{ padding: '8px 16px', borderRadius: '6px', border: 'none', cursor: 'pointer', backgroundColor: currentView === 'dashboard' ? '#1e293b' : 'transparent', color: '#fff', fontWeight: '500' }}>Dashboard</button>
            <button onClick={() => setCurrentView('products')} style={{ padding: '8px 16px', borderRadius: '6px', border: 'none', cursor: 'pointer', backgroundColor: currentView === 'products' ? '#1e293b' : 'transparent', color: '#fff', fontWeight: '500' }}>Products & Categories</button>
            <button onClick={() => setCurrentView('suppliers')} style={{ padding: '8px 16px', borderRadius: '6px', border: 'none', cursor: 'pointer', backgroundColor: currentView === 'suppliers' ? '#1e293b' : 'transparent', color: '#fff', fontWeight: '500' }}>Suppliers</button>
          </div>
        </div>
        <button onClick={() => { setIsAuthenticated(false); setCurrentView('dashboard'); }} style={{ padding: '8px 16px', color: '#f87171', backgroundColor: '#1e293b', border: '1px solid #334155', borderRadius: '6px', cursor: 'pointer', fontWeight: '500' }}>Logout</button>
      </nav>

      {/* Main Container Workspace */}
      <main style={{ flex: 1, width: '100%', maxWidth: '1280px', margin: '0 auto', padding: '32px', boxSizing: 'border-box' }}>
        {currentView === 'dashboard' && <Dashboard />}
        
        {/* PRODUCTS AND CATEGORIES MANAGEMENT MODULE */}
        {currentView === 'products' && (
          <div style={{ display: 'flex', flexDirection: 'column', gap: '32px' }}>
            
            <div style={{ backgroundColor: 'white', padding: '24px', borderRadius: '12px', border: '1px solid #e5e7eb' }}>
              <h3 style={{ margin: '0 0 16px 0', color: '#2563eb', fontWeight: '700' }}>📁 Create New Inventory Category</h3>
              <form onSubmit={(e) => {
                e.preventDefault();
                fetch('http://localhost:8080/api/category', {
                  method: 'POST',
                  headers: { 'Content-Type': 'application/json' },
                  body: JSON.stringify({ name: e.target.catName.value, description: e.target.catDesc.value })
                }).then(() => { alert("New Category Created!"); e.target.reset(); fetchDbData(); });
              }} style={{ display: 'grid', gridTemplateColumns: '1fr 2fr auto', gap: '16px', alignItems: 'center' }}>
                <input name="catName" placeholder="Category Name (e.g., Electronics)" style={{ padding: '12px', borderRadius: '6px', border: '1px solid #cbd5e1' }} required />
                <input name="catDesc" placeholder="Description Details" style={{ padding: '12px', borderRadius: '6px', border: '1px solid #cbd5e1' }} required />
                <button type="submit" style={{ padding: '12px 24px', background: '#2563eb', color: 'white', border: 'none', borderRadius: '6px', fontWeight: '600', cursor: 'pointer' }}>Save Category</button>
              </form>
            </div>

            <div style={{ backgroundColor: 'white', padding: '24px', borderRadius: '12px', border: '1px solid #e5e7eb' }}>
              <h3 style={{ margin: '0 0 16px 0', color: '#16a34a', fontWeight: '700' }}>📦 Add New Master Product SKU (With Price Cost)              </h3>
              <form onSubmit={(e) => {
                e.preventDefault();
                fetch('http://localhost:8080/api/product', {
                  method: 'POST',
                  headers: { 'Content-Type': 'application/json' },
                  body: JSON.stringify({
                    sku: e.target.pSku.value, 
                    name: e.target.pName.value, 
                    description: e.target.pDesc.value,
                    category_name: e.target.pCat.value, 
                    unit_price: e.target.pPrice.value, 
                    initial_stock: e.target.pStock.value
                  })
                }).then(() => { 
                  alert("Product Registered in MySQL!"); 
                  e.target.reset(); 
                  fetchDbData(); 
                });
              }} style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fit, minmax(180px, 1fr))', gap: '16px' }}>
                <input name="pSku" placeholder="SKU Code" style={{ padding: '12px', borderRadius: '6px', border: '1px solid #cbd5e1' }} required />
                <input name="pName" placeholder="Product Name" style={{ padding: '12px', borderRadius: '6px', border: '1px solid #cbd5e1' }} required />
                <input name="pDesc" placeholder="Description" style={{ padding: '12px', borderRadius: '6px', border: '1px solid #cbd5e1' }} />
                <input name="pCat" placeholder="Category Name (Must Exist)" style={{ padding: '12px', borderRadius: '6px', border: '1px solid #cbd5e1' }} required />
                <input name="pPrice" type="number" step="0.01" placeholder="Unit Item Cost ($)" style={{ padding: '12px', borderRadius: '6px', border: '1px solid #cbd5e1' }} required />
                <input name="pStock" type="number" placeholder="Initial Stock Qty" style={{ padding: '12px', borderRadius: '6px', border: '1px solid #cbd5e1' }} required />
                <button type="submit" style={{ padding: '12px', background: '#16a34a', color: 'white', border: 'none', borderRadius: '6px', fontWeight: '600', cursor: 'pointer' }}>Register Product</button>
              </form>
            </div>

            <div style={{ backgroundColor: 'white', padding: '24px', borderRadius: '12px', border: '1px solid #e5e7eb', boxShadow: '0 4px 6px -1px rgba(0,0,0,0.02)' }}>
              <h4 style={{ margin: '0 0 16px 0', fontSize: '18px', fontWeight: '700', color: '#1e293b' }}>Live MySQL Active Master Product Rows</h4>
              <table style={{ width: '100%', borderCollapse: 'collapse', border: '1px solid #e5e7eb' }}>
                <thead>
                  <tr style={{ backgroundColor: '#f8fafc', textAlign: 'left', borderBottom: '2px solid #e5e7eb' }}>
                    <th style={{ padding: '12px' }}>SKU</th>
                    <th style={{ padding: '12px' }}>Name</th>
                    <th style={{ padding: '12px' }}>Category</th>
                    <th style={{ padding: '12px' }}>Unit Price Cost</th>
                    <th style={{ padding: '12px' }}>Stock Balance</th>
                  </tr>
                </thead>
                <tbody>
                  {dbData.products.length === 0 ? (
                    <tr>
                      <td colSpan="5" style={{ padding: '16px', textAlign: 'center', color: '#64748b' }}>No product entries found in the database. Add an item above!</td>
                    </tr>
                  ) : (
                    dbData.products.map((p, i) => (
                      <tr key={i} style={{ borderBottom: '1px solid #e5e7eb' }}>
                        <td style={{ padding: '12px', fontWeight: '600' }}>{p.sku}</td>
                        <td style={{ padding: '12px' }}>{p.name}</td>
                        <td style={{ padding: '12px' }}>
                          <span style={{ background: '#f1f5f9', padding: '4px 8px', borderRadius: '4px', fontSize: '12px' }}>{p.category || 'General'}</span>
                        </td>
                        <td style={{ padding: '12px', fontWeight: '600', color: '#2563eb' }}>${parseFloat(p.price || 0).toFixed(2)}</td>
                        <td style={{ padding: '12px', fontWeight: '600', color: p.stock <= 5 ? '#ef4444' : '#10b981' }}>{p.stock} pcs</td>
                      </tr>
                    ))
                  )}
                </tbody>
              </table>
            </div>
          </div>
        )}

        {/* SUPPLIERS DIRECTORY MODULE PANEL */}
        {currentView === 'suppliers' && (
          <div style={{ display: 'flex', flexDirection: 'column', gap: '32px' }}>
            <div style={{ backgroundColor: 'white', padding: '24px', borderRadius: '12px', border: '1px solid #e5e7eb' }}>
              <h3 style={{ margin: '0 0 16px 0', color: '#ea580c', fontWeight: '700' }}>🏢 Register New Vendor / Manufacturer Profile</h3>
              <form onSubmit={(e) => {
                e.preventDefault();
                fetch('http://localhost:8080/api/supplier', {
                  method: 'POST',
                  headers: { 'Content-Type': 'application/json' },
                  body: JSON.stringify({
                    name: e.target.sName.value,
                    contact: e.target.sCont.value,
                    phone: e.target.sPhone.value,
                    email: e.target.sEmail.value,
                    address: e.target.sAddr.value
                  })
                }).then(() => { 
                  alert("New Vendor Registered in MySQL!"); 
                  e.target.reset(); 
                  fetchDbData(); 
                });
              }} style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fit, minmax(180px, 1fr))', gap: '16px' }}>
                <input name="sName" placeholder="Company Name" style={{ padding: '12px', borderRadius: '6px', border: '1px solid #cbd5e1' }} required />
                <input name="sCont" placeholder="Contact Person Name" style={{ padding: '12px', borderRadius: '6px', border: '1px solid #cbd5e1' }} required />
                <input name="sPhone" placeholder="Phone Line" style={{ padding: '12px', borderRadius: '6px', border: '1px solid #cbd5e1' }} required />
                <input name="sEmail" type="email" placeholder="Email Address" style={{ padding: '12px', borderRadius: '6px', border: '1px solid #cbd5e1' }} required />
                <input name="sAddr" placeholder="Physical Office Address" style={{ padding: '12px', borderRadius: '6px', border: '1px solid #cbd5e1' }} required />
                <button type="submit" style={{ padding: '12px 24px', background: '#ea580c', color: 'white', border: 'none', borderRadius: '6px', fontWeight: '600', cursor: 'pointer' }}>Save Vendor Row</button>
              </form>
            </div>

            <div style={{ backgroundColor: 'white', padding: '24px', borderRadius: '12px', border: '1px solid #e5e7eb' }}>
              <h4 style={{ margin: '0 0 16px 0', fontSize: '18px', fontWeight: '700', color: '#1e293b' }}>Live Registered Supplier Directory Registers</h4>
              <table style={{ width: '100%', borderCollapse: 'collapse', border: '1px solid #e5e7eb' }}>
                <thead>
                  <tr style={{ backgroundColor: '#f8fafc', textAlign: 'left', borderBottom: '2px solid #e5e7eb' }}>
                    <th style={{ padding: '12px' }}>Company Vendor</th>
                    <th style={{ padding: '12px' }}>Contact Person</th>
                    <th style={{ padding: '12px' }}>Phone Line</th>
                    <th style={{ padding: '12px' }}>Email</th>
                  </tr>
                </thead>
                <tbody>
                  {dbData.suppliers.length === 0 ? (
                    <tr>
                      <td colSpan="4" style={{ padding: '16px', textAlign: 'center', color: '#64748b' }}>No vendor profiles found. Add a supplier above!</td>
                    </tr>
                  ) : (
                    dbData.suppliers.map((s, i) => (
                      <tr key={i} style={{ borderBottom: '1px solid #e5e7eb' }}>
                        <td style={{ padding: '12px', fontWeight: '600' }}>{s.name}</td>
                        <td style={{ padding: '12px' }}>{s.contact}</td>
                        <td style={{ padding: '12px' }}>{s.phone}</td>
                        <td style={{ padding: '12px', color: '#2563eb' }}>{s.email}</td>
                      </tr>
                    ))
                  )}
                </tbody>
              </table>
            </div>
          </div>
        )}
      </main>
    </div>
  );
}