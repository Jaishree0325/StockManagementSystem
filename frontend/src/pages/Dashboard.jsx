import React, { useState, useEffect } from 'react';

export default function Dashboard() {
  const [activeForm, setActiveForm] = useState(null);
  const [entryLogs, setEntryLogs] = useState([]);
  const [exitLogs, setExitLogs] = useState([]);
  const [alerts, setAlerts] = useState([]);

  const [entryProd, setEntryProd] = useState('');
  const [entryQty, setEntryQty] = useState('');
  const [entrySup, setEntrySup] = useState('');
  const [exitProd, setExitProd] = useState('');
  const [exitQty, setExitQty] = useState('');
  const [exitReason, setExitReason] = useState('');

  // Fetch running ledger history logs from your live MySQL Database via tunnel
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
    const interval = setInterval(fetchDatabaseData, 3000); // Auto-sync loops every 3 seconds!
    return () => clearInterval(interval);
  }, []);

  const handleAddEntry = (e) => {
    e.preventDefault();
    fetch('http://localhost:8080/api/entry', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ product: entryProd, qty: entryQty, supplier: entrySup })
    }).then(() => {
      alert("Stock Entry Ingested and Saved into MySQL Database!");
      setEntryProd(''); setEntryQty(''); setEntrySup('');
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
      alert("Stock Exit Processed and Decremented inside MySQL!");
      setExitProd(''); setExitQty(''); setExitReason('');
      fetchDatabaseData();
    });
  };

  return (
    <div style={{ padding: '32px', background: '#f8fafc', minHeight: '100vh', fontFamily: 'system-ui, sans-serif', width: '100%', boxSizing: 'border-box', display: 'flex', flexDirection: 'column', gap: '32px' }}>
      
      <div style={{ backgroundColor: '#ffffff', borderRadius: '12px', padding: '24px 32px', border: '1px solid #f3f4f6', display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
        <div>
          <h1 style={{ fontSize: '28px', fontWeight: '700', color: '#1f2937', margin: 0 }}>Overview Dashboard</h1>
          <p style={{ color: '#6b7280', marginTop: '4px', marginBottom: 0 }}>Connected Localhost Live Database Environment.</p>
        </div>
      </div>

      {/* Action Selection Controls */}
      <div style={{ backgroundColor: '#ffffff', borderRadius: '12px', border: '1px solid #f3f4f6', padding: '24px' }}>
        <h2 style={{ fontSize: '18px', fontWeight: '700', color: '#1f2937', margin: '0 0 20px 0' }}>Quick Actions Control</h2>
        <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fit, minmax(180px, 1fr))', gap: '16px' }}>
          <button onClick={() => setActiveForm('entry')} style={{ padding: '14px', borderRadius: '8px', border: '2px solid #bfdbfe', backgroundColor: '#ffffff', color: '#1d4ed8', fontWeight: '600', cursor: 'pointer' }}>📥 New Entry</button>
          <button onClick={() => setActiveForm('exit')} style={{ padding: '14px', borderRadius: '8px', border: '2px solid #c7d2fe', backgroundColor: '#ffffff', color: '#4338ca', fontWeight: '600', cursor: 'pointer' }}>📤 Stock Exit</button>
          <button onClick={() => setActiveForm('reports')} style={{ padding: '14px', borderRadius: '8px', border: '2px solid #a7f3d0', backgroundColor: '#ffffff', color: '#047857', fontWeight: '600', cursor: 'pointer' }}>📊 Reports</button>
          <button onClick={() => setActiveForm('alerts')} style={{ padding: '14px', borderRadius: '8px', border: '2px solid #fecaca', backgroundColor: '#ffffff', color: '#b91c1c', fontWeight: '600', cursor: 'pointer' }}>⚠️ Alerts ({alerts.length})</button>
        </div>
      </div>

            {/* Forms Execution Ingestion Interface */}
      {activeForm === 'entry' && (
        <div style={{ backgroundColor: '#ffffff', padding: '24px', borderRadius: '12px', border: '1px solid #f3f4f6' }}>
          <h3 style={{ margin: '0 0 16px 0', color: '#1d4ed8', fontWeight: '700' }}>📥 Incoming Logistics - Live Stock Entry</h3>
          <form onSubmit={handleAddEntry} style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fit, minmax(200px, 1fr))', gap: '16px' }}>
            <input type="text" placeholder="Product Name" value={entryProd} onChange={e => setEntryProd(e.target.value)} style={{ padding: '12px', borderRadius: '6px', border: '1px solid #d1d5db' }} required />
            <input type="number" placeholder="Quantity Ingested" value={entryQty} onChange={e => setEntryQty(e.target.value)} style={{ padding: '12px', borderRadius: '6px', border: '1px solid #d1d5db' }} required />
            <input type="text" placeholder="Supplier Company Profile Name" value={entrySup} onChange={e => setEntrySup(e.target.value)} style={{ padding: '12px', borderRadius: '6px', border: '1px solid #d1d5db' }} required />
            
            {/* NEW: Interactive Remarks Field Box */}
            <input type="text" placeholder="Transaction Remarks (e.g., Damaged item replacement)" value={entryProd === 'remarks' ? '' : window.entryRemarks || ''} onChange={e => { window.entryRemarks = e.target.value; setEntryProd(entryProd); }} style={{ padding: '12px', borderRadius: '6px', border: '1px solid #d1d5db' }} />
            
            <button type="submit" onClick={(e) => {
              e.preventDefault();
              fetch('http://localhost:8080/api/entry', {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify({ product: entryProd, qty: entryQty, supplier: entrySup, remarks: window.entryRemarks || 'Web Entry' })
              }).then(() => {
                alert("Stock Entry with Remarks saved into MySQL Database!");
                setEntryProd(''); setEntryQty(''); setEntrySup(''); window.entryRemarks = '';
                fetchDatabaseData();
              });
            }} style={{ padding: '12px', background: '#1d4ed8', color: 'white', border: 'none', borderRadius: '6px', fontWeight: '600', cursor: 'pointer' }}>Save Entry Log</button>
          </form>
        </div>
      )}


      {activeForm === 'exit' && (
        <div style={{ backgroundColor: '#ffffff', padding: '24px', borderRadius: '12px', border: '1px solid #f3f4f6' }}>
          <h3 style={{ margin: '0 0 16px 0', color: '#4338ca' }}>📤 Outgoing Logistics - Live Stock Exit</h3>
          <form onSubmit={handleAddExit} style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fit, minmax(200px, 1fr))', gap: '16px' }}>
            <input type="text" placeholder="Product Name" value={exitProd} onChange={e => setExitProd(e.target.value)} style={{ padding: '12px', borderRadius: '6px', border: '1px solid #d1d5db' }} required />
            <input type="number" placeholder="Quantity Dispatched" value={exitQty} onChange={e => setExitQty(e.target.value)} style={{ padding: '12px', borderRadius: '6px', border: '1px solid #d1d5db' }} required />
            <input type="text" placeholder="Reason" value={exitReason} onChange={e => setExitReason(e.target.value)} style={{ padding: '12px', borderRadius: '6px', border: '1px solid #d1d5db' }} required />
            <button type="submit" style={{ padding: '12px', background: '#4338ca', color: 'white', border: 'none', borderRadius: '6px', fontWeight: '600', cursor: 'pointer' }}>Log Stock Exit</button>
          </form>
        </div>
      )}

      {activeForm === 'reports' && (
        <div style={{ backgroundColor: '#ffffff', padding: '24px', borderRadius: '12px', border: '1px solid #f3f4f6' }}>
          <h3 style={{ margin: '0 0 16px 0', color: '#047857' }}>📊 Live MySQL Database Audit Ledger Reports</h3>
          <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fit, minmax(280px, 1fr))', gap: '20px' }}>
            <div>
              <h4 style={{ color: '#334155' }}>Recent Inward Entries (Live MySQL Rows)</h4>
              <ul style={{ paddingLeft: '20px', lineHeight: '2' }}>{entryLogs.map((log, i) => <li key={i}>{log.date} - <b>{log.product}</b> (+{log.qty} units) via {log.supplier}</li>)}</ul>
            </div>
            <div>
              <h4 style={{ color: '#334155' }}>Recent Outward Exits (Live MySQL Rows)</h4>
              <ul style={{ paddingLeft: '20px', lineHeight: '2' }}>{exitLogs.map((log, i) => <li key={i}>{log.date} - <b>{log.product}</b> (-{log.qty} units) - {log.reason}</li>)}</ul>
            </div>
          </div>
        </div>
      )}

      {activeForm === 'alerts' && (
        <div style={{ backgroundColor: '#ffffff', padding: '24px', borderRadius: '12px', border: '1px solid #f3f4f6' }}>
          <h3 style={{ margin: '0 0 16px 0', color: '#b91c1c' }}>⚠️ Active Database Low-Stock Warning Threshold Breaches</h3>
          <div style={{ display: 'flex', flexDirection: 'column', gap: '12px' }}>
            {alerts.length === 0 ? (
              <div style={{ color: '#16a34a', fontWeight: '500' }}>✓ All stock metrics optimal. No active reorder threshold warnings.</div>
            ) : (
              alerts.map((al, idx) => (
                <div key={idx} style={{ background: '#fee2e2', color: '#b91c1c', padding: '16px', borderRadius: '8px', border: '1px solid #fca5a5' }}>
                  📌 <b>CRITICAL BREACH:</b> {al.product} quantity is down to <b>{al.stock} pcs</b> (Reorder Level: {al.threshold} pcs).
                </div>
              ))
            )}
          </div>
        </div>
      )}

    </div>
  );
}
