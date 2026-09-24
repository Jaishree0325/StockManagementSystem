import React, { useState, useEffect } from 'react';
import Dashboard from './pages/Dashboard';

// --- AUTHENTICATION LOCK SCREEN GATING ---
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
    <div style={{ minHeight: '100vh', width: '100vw', background: 'radial-gradient(circle at top, #0d2e27, #061f1a)', display: 'flex', alignItems: 'center', justifyContent: 'center', fontFamily: 'system-ui, sans-serif', margin: 0, padding: 0 }}>
      <div style={{ backgroundColor: '#ffffff', padding: '40px', borderRadius: '16px', width: '100%', maxWidth: '400px', boxSizing: 'border-box', boxShadow: '0 20px 25px -5px rgba(0,0,0,0.3)' }}>
        <div style={{ textAlign: 'center', marginBottom: '24px' }}>
          <div style={{ fontSize: '48px', marginBottom: '12px' }}>🔒</div>
          <h2 style={{ fontSize: '24px', fontWeight: '800', color: '#0f172a', margin: 0 }}>System Authentication</h2>
        </div>
        {error && <div style={{ color: '#991b1b', background: '#fef2f2', padding: '10px', borderRadius: '6px', marginBottom: '16px', fontSize: '13px' }}>{error}</div>}
        <form onSubmit={handleLoginSubmit} style={{ display: 'flex', flexDirection: 'column', gap: '16px' }}>
          <input type="text" placeholder="Username" value={username} onChange={e => setUsername(e.target.value)} style={{ padding: '12px', borderRadius: '8px', border: '1px solid #cbd5e1' }} required />
          <input type="password" placeholder="Password" value={password} onChange={e => setPassword(e.target.value)} style={{ padding: '12px', borderRadius: '8px', border: '1px solid #cbd5e1' }} required />
          <button type="submit" style={{ background: '#0d6e5c', color: '#fff', border: 'none', padding: '14px', borderRadius: '8px', fontWeight: '600', cursor: 'pointer' }}>Verify & Unlock Dashboard</button>
        </form>
      </div>
    </div>
  );
}

// --- MASTER APPLICATION WRAPPER CORE ROUTER ---
export default function App() {
  const [isAuthenticated, setIsAuthenticated] = useState(false);
  const [currentView, setCurrentView] = useState('dashboard');
  const [showProductForm, setShowProductForm] = useState(false);
  const [showSupplierForm, setShowSupplierForm] = useState(false);
  const [showCategoryForm, setShowCategoryForm] = useState(false);
  
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
    <div style={{ minHeight: '100vh', display: 'flex', fontFamily: 'system-ui, -apple-system, sans-serif', backgroundColor: '#fff0f3', margin: 0, padding: 0 }}>
      
      {/* 🌲 DEEP TEAL GREEN SIDEBAR MENU */}
      <aside style={{ width: '260px', backgroundColor: '#09473b', color: '#a3d1c6', display: 'flex', flexDirection: 'column', padding: '28px 18px', boxSizing: 'border-box', borderRight: '1px solid #063328' }}>
        <div style={{ color: '#ffffff', fontWeight: '800', fontSize: '24px', letterSpacing: '-0.5px', marginBottom: '6px' }}>StockMS</div>
        <div style={{ fontSize: '11px', color: '#5ba393', fontWeight: '700', textTransform: 'uppercase', marginBottom: '36px', letterSpacing: '0.5px' }}>Stocks Portal</div>
        
        <div style={{ display: 'flex', flexDirection: 'column', gap: '8px', flex: 1 }}>
          <button onClick={() => setCurrentView('dashboard')} style={{ width: '100%', border: 'none', padding: '12px 16px', borderRadius: '8px', cursor: 'pointer', textAlign: 'left', fontSize: '15px', fontWeight: '600', display: 'flex', alignItems: 'center', gap: '12px', backgroundColor: currentView === 'dashboard' ? '#0d6e5c' : 'transparent', color: currentView === 'dashboard' ? '#ffffff' : '#a3d1c6', transition: 'all 0.2s' }}>
            <span>📊</span> Dashboard
          </button>
          <button onClick={() => setCurrentView('products')} style={{ width: '100%', border: 'none', padding: '12px 16px', borderRadius: '8px', cursor: 'pointer', textAlign: 'left', fontSize: '15px', fontWeight: '600', display: 'flex', alignItems: 'center', gap: '12px', backgroundColor: currentView === 'products' ? '#0d6e5c' : 'transparent', color: currentView === 'products' ? '#ffffff' : '#a3d1c6', transition: 'all 0.2s' }}>
            <span>📦</span> Products & Categories
          </button>
          <button onClick={() => setCurrentView('suppliers')} style={{ width: '100%', border: 'none', padding: '12px 16px', borderRadius: '8px', cursor: 'pointer', textAlign: 'left', fontSize: '15px', fontWeight: '600', display: 'flex', alignItems: 'center', gap: '12px', backgroundColor: currentView === 'suppliers' ? '#0d6e5c' : 'transparent', color: currentView === 'suppliers' ? '#ffffff' : '#a3d1c6', transition: 'all 0.2s' }}>
            <span>🏢</span> Suppliers
          </button>
        </div>

        <button onClick={() => { setIsAuthenticated(false); setCurrentView('dashboard'); }} style={{ width: '100%', border: 'none', padding: '12px 16px', borderRadius: '8px', cursor: 'pointer', textAlign: 'left', fontSize: '15px', fontWeight: '600', color: '#ff8a8a', backgroundColor: 'transparent', display: 'flex', alignItems: 'center', gap: '12px' }}>
          <span>🚪</span> Exit Console
        </button>
      </aside>

      {/* 🌸 PALE PINK MAIN WORKSPACE CONTENT FRAME */}
      <main style={{ flex: 1, padding: '40px', boxSizing: 'border-box', overflowY: 'auto' }}>
        {currentView === 'dashboard' && <Dashboard />}

        {/* --- PRODUCTS AND CATEGORIES SCREEN VIEW --- */}
        {currentView === 'products' && (
          <div style={{ display: 'flex', flexDirection: 'column', gap: '32px' }}>
            <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', borderBottom: '1px solid #f0ccd4', paddingBottom: '20px' }}>
              <div>
                <span style={{ fontSize: '12px', color: '#0d6e5c', fontWeight: '700', textTransform: 'uppercase', letterSpacing: '0.5px' }}>OVERVIEW</span>
                <h1 style={{ fontSize: '32px', fontWeight: '800', color: '#111827', margin: '4px 0 0 0', letterSpacing: '-0.5px' }}>Product List</h1>
                <p style={{ color: '#6b5257', margin: '4px 0 0 0', fontSize: '15px' }}>Here is the latest structural activity across your products.</p>
              </div>
              <div style={{ display: 'flex', gap: '12px' }}>
                <button onClick={() => setShowCategoryForm(!showCategoryForm)} style={{ backgroundColor: '#ffffff', color: '#09473b', border: '1px solid #eccad3', padding: '10px 18px', borderRadius: '8px', cursor: 'pointer', fontWeight: '600', fontSize: '14px' }}>+ Add Category</button>
                <button onClick={() => setShowProductForm(!showProductForm)} style={{ backgroundColor: '#09473b', color: '#ffffff', border: 'none', padding: '10px 20px', borderRadius: '8px', cursor: 'pointer', fontWeight: '600', fontSize: '14px' }}>+ Add Product</button>
              </div>
            </div>

            {showCategoryForm && (
              <div style={{ backgroundColor: '#ffffff', padding: '24px', borderRadius: '12px', border: '1px solid #eccad3', boxShadow: '0 4px 6px rgba(0,0,0,0.02)' }}>
                <h3 style={{ margin: '0 0 16px 0', color: '#09473b', fontWeight: '700' }}>📁 Create New Category</h3>
                <form onSubmit={(e) => {
                  e.preventDefault();
                  fetch('http://localhost:8080/api/category', {
                    method: 'POST',
                    headers: { 'Content-Type': 'application/json' },
                    body: JSON.stringify({ name: e.target.catName.value, description: e.target.catDesc.value })
                  }).then(() => { alert("New Category Created!"); e.target.reset(); setShowCategoryForm(false); fetchDbData(); });
                }} style={{ display: 'grid', gridTemplateColumns: '1fr 2fr auto', gap: '16px', alignItems: 'center' }}>
                  <input name="catName" placeholder="Category Name" style={{ padding: '12px', borderRadius: '8px', border: '1px solid #eccad3', outline: 'none' }} required />
                  <input name="catDesc" placeholder="Description Details" style={{ padding: '12px', borderRadius: '8px', border: '1px solid #eccad3', outline: 'none' }} required />
                  <button type="submit" style={{ padding: '12px 24px', background: '#09473b', color: 'white', border: 'none', borderRadius: '8px', fontWeight: '600', cursor: 'pointer' }}>Save Category</button>
                </form>
              </div>
            )}

            {showProductForm && (
              <div style={{ backgroundColor: '#ffffff', padding: '24px', borderRadius: '12px', border: '1px solid #eccad3', boxShadow: '0 4px 6px rgba(0,0,0,0.02)' }}>
                <h3 style={{ margin: '0 0 16px 0', color: '#09473b', fontWeight: '700' }}>📦 Add New Product SKU</h3>
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
                    alert("Products Registered!"); 
                    e.target.reset(); 
                    setShowProductForm(false); 
                    fetchDbData(); 
                  });
                }} style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fit, minmax(180px, 1fr))', gap: '16px' }}>
                  <input name="pSku" placeholder="SKU Code" style={{ padding: '12px', borderRadius: '8px', border: '1px solid #eccad3', outline: 'none' }} required />
                  <input name="pName" placeholder="Product Name" style={{ padding: '12px', borderRadius: '8px', border: '1px solid #eccad3', outline: 'none' }} required />
                  <input name="pDesc" placeholder="Description" style={{ padding: '12px', borderRadius: '8px', border: '1px solid #eccad3', outline: 'none' }} />
                  <input name="pCat" placeholder="Category Profile" style={{ padding: '12px', borderRadius: '8px', border: '1px solid #eccad3', outline: 'none' }} required />
                  <input name="pPrice" type="number" step="0.01" placeholder="Price Cost ($)" style={{ padding: '12px', borderRadius: '8px', border: '1px solid #eccad3', outline: 'none' }} required />
                  <input name="pStock" type="number" placeholder="Initial Qty" style={{ padding: '12px', borderRadius: '8px', border: '1px solid #eccad3', outline: 'none' }} required />
                  <button type="submit" style={{ padding: '12px', background: '#09473b', color: 'white', border: 'none', borderRadius: '8px', fontWeight: '600', cursor: 'pointer' }}>Register Item</button>
                </form>
              </div>
            )}

            {/* 🎯 TABLE DESIGN WITH CRISP LAYOUT */}
            <div style={{ backgroundColor: '#ffffff', borderRadius: '12px', border: '1px solid #eccad3', overflow: 'hidden', boxShadow: '0 4px 10px rgba(0,0,0,0.02)' }}>
              <table style={{ width: '100%', borderCollapse: 'collapse', border: 'none' }}>
                <thead>
                  <tr style={{ backgroundColor: '#09473b', color: '#ffffff', textAlign: 'left' }}>
                    <th style={{ padding: '16px', fontSize: '14px', fontWeight: '600' }}>SKU Code</th>
                    <th style={{ padding: '16px', fontSize: '14px', fontWeight: '600' }}>Product Name</th>
                    <th style={{ padding: '16px', fontSize: '14px', fontWeight: '600' }}>Category</th>
                    <th style={{ padding: '16px', fontSize: '14px', fontWeight: '600' }}>Unit Cost</th>
                    <th style={{ padding: '16px', fontSize: '14px', fontWeight: '600' }}>Stock Balance</th>
                  </tr>
                </thead>
                <tbody>
                  {dbData.products.length === 0 ? (
                    <tr>
                      <td colSpan="5" style={{ padding: '24px', textAlign: 'center', color: '#6b5257' }}>No active product entries found in the database.</td>
                    </tr>
                  ) : (
                    dbData.products.map((p, i) => (
                      <tr key={i} style={{ borderBottom: '1px solid #f0ccd4', backgroundColor: i % 2 === 0 ? '#ffffff' : '#fff9fb' }}>
                        <td style={{ padding: '16px', fontWeight: '600', color: '#09473b' }}>{p.sku}</td>
                        <td style={{ padding: '16px', fontWeight: '500', color: '#111827' }}>{p.name}</td>
                        <td style={{ padding: '16px' }}>
                          <span style={{ backgroundColor: '#fff0f3', color: '#09473b', padding: '4px 10px', borderRadius: '6px', fontSize: '13px', fontWeight: '600', border: '1px solid #f0ccd4' }}>{p.category || 'General'}</span>
                        </td>
                        <td style={{ padding: '16px', fontWeight: '700', color: '#0d6e5c' }}>${parseFloat(p.price || 0).toFixed(2)}</td>
                        <td style={{ padding: '16px', fontWeight: '700', color: p.stock <= 5 ? '#ef4444' : '#09473b' }}>{p.stock} units</td>
                      </tr>
                    ))
                  )}
                </tbody>
              </table>
            </div>
          </div>
        )}

        {/* --- SUPPLIERS SECTION --- */}
        {currentView === 'suppliers' && (
          <div style={{ display: 'flex', flexDirection: 'column', gap: '32px' }}>
            <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', borderBottom: '1px solid #f0ccd4', paddingBottom: '20px' }}>
              <div>
                <span style={{ fontSize: '12px', color: '#0d6e5c', fontWeight: '700', textTransform: 'uppercase', letterSpacing: '0.5px' }}>OPERATIONS RECORD</span>
                <h1 style={{ fontSize: '32px', fontWeight: '800', color: '#111827', margin: '4px 0 0 0', letterSpacing: '-0.5px' }}>Registered Suppliers</h1>
                <p style={{ color: '#6b5257', margin: '4px 0 0 0', fontSize: '15px' }}>Manage partner details and business contacts across your network.</p>
              </div>
              <button onClick={() => setShowSupplierForm(!showSupplierForm)} style={{ backgroundColor: '#09473b', color: '#ffffff', border: 'none', padding: '10px 20px', borderRadius: '8px', cursor: 'pointer', fontWeight: '600', fontSize: '14px', boxShadow: '0 2px 4px rgba(0,0,0,0.05)' }}>+ Add Supplier</button>
            </div>

            {showSupplierForm && (
              <div style={{ backgroundColor: '#ffffff', padding: '24px', borderRadius: '12px', border: '1px solid #eccad3', boxShadow: '0 4px 6px rgba(0,0,0,0.02)' }}>
                <h3 style={{ margin: '0 0 16px 0', color: '#ea580c', fontWeight: '700' }}>🏢 Register New Vendor Profile</h3>
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
                  }).then(() => { alert("New Vendor Registered!"); e.target.reset(); setShowSupplierForm(false); fetchDbData(); });
                }} style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fit, minmax(180px, 1fr))', gap: '16px' }}>
                  <input name="sName" placeholder="Company Name" style={{ padding: '12px', borderRadius: '6px', border: '1px solid #cbd5e1' }} required />
                  <input name="sCont" placeholder="Contact Person" style={{ padding: '12px', borderRadius: '6px', border: '1px solid #cbd5e1' }} required />
                  <input name="sPhone" placeholder="Phone Line" style={{ padding: '12px', borderRadius: '6px', border: '1px solid #cbd5e1' }} required />
                  <input name="sEmail" type="email" placeholder="Email Address" style={{ padding: '12px', borderRadius: '6px', border: '1px solid #cbd5e1' }} required />
                  <input name="sAddr" placeholder="Office Address" style={{ padding: '12px', borderRadius: '6px', border: '1px solid #cbd5e1' }} required />
                  <button type="submit" style={{ padding: '12px 24px', background: '#09473b', color: 'white', border: 'none', borderRadius: '6px', fontWeight: '600', cursor: 'pointer' }}>Save Supplier</button>
                </form>
              </div>
            )}

            <div style={{ backgroundColor: '#ffffff', borderRadius: '12px', border: '1px solid #eccad3', overflow: 'hidden', boxShadow: '0 4px 10px rgba(0,0,0,0.02)' }}>
              <table style={{ width: '100%', borderCollapse: 'collapse', border: 'none' }}>
                <thead>
                  <tr style={{ backgroundColor: '#09473b', color: '#ffffff', textAlign: 'left' }}>
                    <th style={{ padding: '16px', fontSize: '14px', fontWeight: '600' }}>Company Vendor</th>
                    <th style={{ padding: '16px', fontSize: '14px', fontWeight: '600' }}>Contact Person</th>
                    <th style={{ padding: '16px', fontSize: '14px', fontWeight: '600' }}>Phone Line</th>
                    <th style={{ padding: '16px', fontSize: '14px', fontWeight: '600' }}>Email Address</th>
                  </tr>
                </thead>
                <tbody>
                  {dbData.suppliers.length === 0 ? (
                    <tr>
                      <td colSpan="4" style={{ padding: '24px', textAlign: 'center', color: '#6b5257' }}>No vendor profiles found.</td>
                    </tr>
                  ) : (
                    dbData.suppliers.map((s, i) => (
                      <tr key={i} style={{ borderBottom: '1px solid #f0ccd4', backgroundColor: i % 2 === 0 ? '#ffffff' : '#fff9fb' }}>
                        <td style={{ padding: '16px', fontWeight: '600', color: '#09473b' }}>{s.name}</td>
                        <td style={{ padding: '16px', color: '#111827' }}>{s.contact}</td>
                        <td style={{ padding: '16px', color: '#6b5257' }}>{s.phone}</td>
                        <td style={{ padding: '16px', color: '#0d6e5c', fontWeight: '500' }}>{s.email}</td>
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