// frontend/src/components/ExcelViewer.jsx
import React, { useState, useEffect } from 'react';
import { useLocation, useNavigate } from 'react-router-dom';
import './ExcelViewer.css';

const ExcelViewer = () => {
  const location = useLocation();
  const navigate = useNavigate();
  const [data, setData] = useState(null);
  const [error, setError] = useState('');
  const [loading, setLoading] = useState(true);
  const [refreshInterval, setRefreshInterval] = useState(5000); // 5 seconds

  const fetchExcelData = async (filename) => {
    try {
      const response = await fetch(`/api/excel/read/${filename}`);
      if (!response.ok) {
        throw new Error(`HTTP error! status: ${response.status}`);
      }
      const result = await response.json();
      setData(result);
      setError('');
    } catch (err) {
      setError('Error loading Excel data: ' + err.message);
      setData(null);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    const filename = location.state?.data?.filename;
    if (!filename) {
      navigate('/');
      return;
    }

    // Initial fetch
    fetchExcelData(filename);

    // Set up polling
    const intervalId = setInterval(() => {
      fetchExcelData(filename);
    }, refreshInterval);

    // Cleanup
    return () => clearInterval(intervalId);
  }, [location.state, navigate, refreshInterval]);

  const handleRefreshIntervalChange = (event) => {
    const newInterval = parseInt(event.target.value, 10);
    setRefreshInterval(newInterval);
  };

  if (loading) {
    return <div className="loading">Loading...</div>;
  }

  if (error) {
    return <div className="error-message">{error}</div>;
  }

  if (!data) {
    return <div className="error-message">No data available</div>;
  }

  return (
    <div className="excel-viewer">
      <div className="viewer-header">
        <h2>Excel Data Viewer</h2>
        <div className="viewer-controls">
          <select 
            value={refreshInterval} 
            onChange={handleRefreshIntervalChange}
            className="refresh-select"
          >
            <option value={2000}>Refresh: 2s</option>
            <option value={5000}>Refresh: 5s</option>
            <option value={10000}>Refresh: 10s</option>
            <option value={30000}>Refresh: 30s</option>
          </select>
          <button 
            className="back-button"
            onClick={() => navigate('/')}
          >
            Upload New File
          </button>
        </div>
      </div>

      <div className="file-info-panel">
        <h4>File Information</h4>
        <p>Filename: {location.state?.data?.filename}</p>
        <p>Last Updated: {new Date().toLocaleString()}</p>
      </div>

      <div className="table-container">
        <table>
          <thead>
            <tr>
              {data.headers.map((header, index) => (
                <th key={index}>{header}</th>
              ))}
            </tr>
          </thead>
          <tbody>
            {data.rows.map((row, rowIndex) => (
              <tr key={rowIndex}>
                {row.map((cell, cellIndex) => (
                  <td key={cellIndex}>{cell}</td>
                ))}
              </tr>
            ))}
          </tbody>
        </table>
      </div>

      <div className="table-info">
        <span>Total Rows: {data.rows.length}</span>
        <span>Total Columns: {data.headers.length}</span>
      </div>
    </div>
  );
};

export default ExcelViewer;
