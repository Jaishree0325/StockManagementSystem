import React, { useState, useEffect } from 'react';

export default function Dashboard() {
  const [activeForm, setActiveForm] = useState('entry'); // Options: 'entry', 'exit', 'reports', 'alerts'
  const [entryLogs, setEntryLogs] = useState([]);
  const [exitLogs, setExitLogs] = useState([]);
  const [alerts, setAlerts] = useState([]);

  const [entryProd, setEntryProd] = useState('');
  const [entryQty, setEntryQty] = useState('');
  const [entrySup, setEntrySup] = useState('');
  const [entryRem, setEntryRem] = useState('');
  
  const [exitProd, setExitProd] = useState('');
  const [exitQty, setExitQty] = useState('');
  const [exitReason, setExitReason] = useState('');

  const fetchDatabaseData = () => {
    fetch('http://localhost:8080/api/data')
      .then(res => res.json())
      .then(data => {
        setEntryLogs(data.entries || []);
        setExitLogs(data.exits || []);
        setAlerts(data.alerts || []);
      }).catch(err => console.log(err));
  };

  useEffect(() => {
    fetchDatabaseData();
    const interval = setInterval(fetchDatabaseData, 3000);
    return () => clearInterval(interval);
  }, []);

  const handleAddEntry = (e) => {
    e.preventDefault();
    fetch('http://localhost:8080/api/entry', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ product: entryProd, qty: entryQty, supplier: entrySup, remarks: entryRem || 'Web Ingestion' })
    }).then(() => {
      alert("Stock Entry Saved Successfully!");
      setEntryProd(''); setEntryQty(''); setEntrySup(''); setEntryRem('');
      fetchDatabaseData();
    });
  };

  const handleAddExit = (e) => {
    e.preventDefault();
    fetch('http://localhost:8080/api/exit', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ product: exitProd, qty: exitQty, reason: exitReason })
    }).then(() => {
      alert("Stock Exit Logged and Updated!");
      setExitProd(''); setExitQty(''); setExitReason('');
      fetchDatabaseData();
    });
  };

  return (
    <div style={{ display: 'flex', flexDirection: 'column', gap: '36px', minHeight: '100%', boxSizing: 'border-box' }}>
      
      {/* Premium Organized Heading Banner */}
      <div style={{ borderBottom: '1px solid #eccad3', paddingBottom: '20px' }}>
        <span style={{ fontSize: '12px', color: '#0d6e5c', fontWeight: '700', textTransform: 'uppercase', letterSpacing: '0.5px' }}>OVERVIEW DASHBOARD</span>
        <h1 style={{ fontSize: '36px', fontWeight: '800', color: '#111827', margin: '4px 0 0 0', letterSpacing: '-0.8px' }}>Good morning, admin.</h1>
        <p style={{ color: '#6b5257', margin: '6px 0 0 0', fontSize: '16px' }}>Here is the latest dynamic activity overview across your inventory logistics database.</p>
      </div>

      {/* Modern Compact Quick Actions Button Menu Row */}
      <div style={{ display: 'flex', gap: '8px', borderBottom: '1px solid #eccad3', paddingBottom: '8px' }}>
        <button onClick={() => setActiveForm('entry')} style={{ border: 'none', padding: '10px 20px', borderRadius: '6px', cursor: 'pointer', fontWeight: '700', fontSize: '14px', backgroundColor: activeForm === 'entry' ? '#09473b' : 'transparent', color: activeForm === 'entry' ? '#ffffff' : '#09473b', transition: 'all 0.2s' }}>📥 Stock Entry</button>
        <button onClick={() => setActiveForm('exit')} style={{ border: 'none', padding: '10px 20px', borderRadius: '6px', cursor: 'pointer', fontWeight: '700', fontSize: '14px', backgroundColor: activeForm === 'exit' ? '#09473b' : 'transparent', color: activeForm === 'exit' ? '#ffffff' : '#09473b', transition: 'all 0.2s' }}>📤 Stock Exit</button>
        <button onClick={() => setActiveForm('reports')} style={{ border: 'none', padding: '10px 20px', borderRadius: '6px', cursor: 'pointer', fontWeight: '700', fontSize: '14px', backgroundColor: activeForm === 'reports' ? '#09473b' : 'transparent', color: activeForm === 'reports' ? '#ffffff' : '#09473b', transition: 'all 0.2s' }}>📊 Reports</button>
        <button onClick={() => setActiveForm('alerts')} style={{ border: 'none', padding: '10px 20px', borderRadius: '6px', cursor: 'pointer', fontWeight: '700', fontSize: '14px', backgroundColor: activeForm === 'alerts' ? '#09473b' : 'transparent', color: activeForm === 'alerts' ? '#ffffff' : '#09473b', transition: 'all 0.2s' }}>⚠️ Threshold Alerts ({alerts.length})</button>
      </div>

      {/* --- FORMS AND SUB-PANEL DYNAMIC VIEWS --- */}
      {activeForm === 'entry' && (
        <div style={{ backgroundColor: '#ffffff', padding: '32px', borderRadius: '12px', border: '1px solid #eccad3', boxShadow: '0 4px 6px rgba(0,0,0,0.01)' }}>
          <h3 style={{ margin: '0 0 20px 0', fontSize: '18px', fontWeight: '700', color: '#09473b' }}>Stock Entry Details</h3>
          <form onSubmit={handleAddEntry} style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fit, minmax(220px, 1fr))', gap: '20px' }}>
            <input type="text" placeholder="Product Name" value={entryProd} onChange={e => setEntryProd(e.target.value)} style={{ padding: '12px', borderRadius: '8px', border: '1px solid #eccad3', fontSize: '14px', outline: 'none' }} required />
            <input type="number" placeholder="Quantity Ingested" value={entryQty} onChange={e => setEntryQty(e.target.value)} style={{ padding: '12px', borderRadius: '8px', border: '1px solid #eccad3', fontSize: '14px', outline: 'none' }} required />
            <input type="text" placeholder="Supplier Company Name" value={entrySup} onChange={e => setEntrySup(e.target.value)} style={{ padding: '12px', borderRadius: '8px', border: '1px solid #eccad3', fontSize: '14px', outline: 'none' }} required />
            <input type="text" placeholder="Transaction Remarks/Memo" value={entryRem} onChange={e => setEntryRem(e.target.value)} style={{ padding: '12px', borderRadius: '8px', border: '1px solid #eccad3', fontSize: '14px', outline: 'none' }} />
            <button type="submit" style={{ padding: '12px', background: '#09473b', color: 'white', border: 'none', borderRadius: '8px', fontWeight: '700', cursor: 'pointer', fontSize: '14px' }}>Save Entry Record</button>
          </form>
        </div>
      )}

      {activeForm === 'exit' && (
        <div style={{ backgroundColor: '#ffffff', padding: '32px', borderRadius: '12px', border: '1px solid #eccad3', boxShadow: '0 4px 6px rgba(0,0,0,0.01)' }}>
          <h3 style={{ margin: '0 0 20px 0', fontSize: '18px', fontWeight: '700', color: '#09473b' }}>Stock Exit Details</h3>
          <form onSubmit={handleAddExit} style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fit, minmax(220px, 1fr))', gap: '20px' }}>
            <input type="text" placeholder="Product Name" value={exitProd} onChange={e => setExitProd(e.target.value)} style={{ padding: '12px', borderRadius: '8px', border: '1px solid #eccad3', fontSize: '14px', outline: 'none' }} required />
            <input type="number" placeholder="Quantity Dispatched" value={exitQty} onChange={e => setExitQty(e.target.value)} style={{ padding: '12px', borderRadius: '8px', border: '1px solid #eccad3', fontSize: '14px', outline: 'none' }} required />
            <input type="text" placeholder="Dispatch Reason (e.g. Broken / Sale)" value={exitReason} onChange={e => setExitReason(e.target.value)} style={{ padding: '12px', borderRadius: '8px', border: '1px solid #eccad3', fontSize: '14px', outline: 'none' }} required />
            <button type="submit" style={{ padding: '12px', background: '#09473b', color: 'white', border: 'none', borderRadius: '8px', fontWeight: '700', cursor: 'pointer', fontSize: '14px' }}>Save Exit Record</button>
          </form>
        </div>
      )}

      {activeForm === 'reports' && (
        <div style={{ backgroundColor: '#ffffff', padding: '32px', borderRadius: '12px', border: '1px solid #eccad3', boxShadow: '0 4px 6px rgba(0,0,0,0.01)' }}>
          <h3 style={{ margin: '0 0 24px 0', fontSize: '18px', fontWeight: '700', color: '#09473b' }}>Reports</h3>
          <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fit, minmax(320px, 1fr))', gap: '32px' }}>
            
            <div>
              <h4 style={{ color: '#4a1525', fontWeight: '700', marginBottom: '14px', borderBottom: '2px solid #fff0f3', paddingBottom: '6px' }}>📥 Recent Product Entry Shipments</h4>
              <ul style={{ paddingLeft: '16px', lineHeight: '2.2', color: '#4b5563', fontSize: '14px' }}>
                {entryLogs.length === 0 ? <li>No entries logged yet.</li> : entryLogs.map((log, i) => (
                  <li key={i} style={{ marginBottom: '8px' }}>
                    {log.date} - <b style={{ color: '#09473b' }}>{log.product}</b> (+{log.qty} units) via <i>{log.supplier || 'N/A'}</i>
                    <span style={{ display: 'block', fontSize: '12px', color: '#9ca3af', fontStyle: 'italic', paddingLeft: '8px' }}>💬 Memo: {log.remarks || 'Web Entry'}</span>
                  </li>
                ))}
              </ul>
            </div>

            <div>
              <h4 style={{ color: '#4a1525', fontWeight: '700', marginBottom: '14px', borderBottom: '2px solid #fff0f3', paddingBottom: '6px' }}>📤 Recent Outward Dispatches</h4>
              <ul style={{ paddingLeft: '16px', lineHeight: '2.2', color: '#4b5563', fontSize: '14px' }}>
                {exitLogs.length === 0 ? <li>No dispatch records found.</li> : exitLogs.map((log, i) => (
                  <li key={i} style={{ marginBottom: '8px' }}>
                    {log.date} - <b style={{ color: '#9d2449' }}>{log.product}</b> (-{log.qty} units)
                    <span style={{ display: 'block', fontSize: '12px', color: '#dc2626', fontWeight: '600', paddingLeft: '8px' }}>⚠️ Reason: {log.reason}</span>
                  </li>
                ))}
              </ul>
            </div>

          </div>
        </div>
      )}

      {activeForm === 'alerts' && (
                <div style={{ backgroundColor: '#ffffff', padding: '32px', borderRadius: '12px', border: '1px solid #eccad3', boxShadow: '0 4px 6px rgba(0,0,0,0.01)' }}>
          <h3 style={{ margin: '0 0 20px 0', fontSize: '18px', fontWeight: '700', color: '#b91c1c' }}>Inventory Threshold Alerts</h3>
          <div style={{ display: 'flex', flexDirection: 'column', gap: '12px' }}>
            {alerts.length === 0 ? (
              <div style={{ color: '#16a34a', fontWeight: '700', fontSize: '15px' }}>✓ All catalog stock balances satisfy system safety guidelines. No active warnings.</div>
            ) : (
              alerts.map((al, idx) => (
                <div key={idx} style={{ background: '#fef2f2', color: '#991b1b', padding: '16px', borderRadius: '8px', border: '1px solid #fca5a5', fontSize: '14px', fontWeight: '600' }}>
                  📌 <b>CRITICAL BREACH THRESHOLD:</b> {al.product} quantity is currently down to <span style={{ fontSize: '16px' }}>{al.stock} units</span> (Minimum required safe stock: {al.threshold} units).
                </div>
              ))
            )}
          </div>
        </div>
      )}

    </div>
  );
}